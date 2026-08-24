#!/usr/bin/env python3
from __future__ import annotations

import argparse
import json
import re
from pathlib import Path
import sys

ROOT = Path(__file__).resolve().parents[1]
CURRICULUM = ROOT / "curriculum.json"

ALLOWED = {
    "lessonStatus": {"planned", "inProgress", "completed"},
    "exerciseStatus": {"notStarted", "inProgress", "completed"},
    "verificationStatus": {"notStarted", "pending", "verified"},
}
ISSUE_URL_RE = re.compile(r"^https://github\.com/([^/]+)/([^/]+)/issues/\d+$")


def load() -> dict:
    return json.loads(CURRICULUM.read_text(encoding="utf-8"))


def save(data: dict) -> None:
    CURRICULUM.write_text(json.dumps(data, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")


def lessons(data: dict):
    for phase in data["phases"]:
        for lesson in phase["lessons"]:
            yield phase, lesson


def validate(data: dict) -> list[str]:
    errors: list[str] = []
    ids: set[str] = set()
    sequences: list[int] = []

    for phase, lesson in lessons(data):
        lesson_id = lesson.get("id")
        if not lesson_id:
            errors.append(f"Lesson without id in {phase.get('id', '<unknown phase>')}")
            continue
        if lesson_id in ids:
            errors.append(f"Duplicate lesson id: {lesson_id}")
        ids.add(lesson_id)

        sequence = lesson.get("sequence")
        if not isinstance(sequence, int):
            errors.append(f"{lesson_id}: sequence must be an integer")
        else:
            sequences.append(sequence)

        for field, allowed in ALLOWED.items():
            if lesson.get(field) not in allowed:
                errors.append(f"{lesson_id}: invalid {field}={lesson.get(field)!r}")

        if lesson.get("exerciseStatus") == "completed" and lesson.get("lessonStatus") != "completed":
            errors.append(f"{lesson_id}: exercise cannot be completed before the lesson")
        if lesson.get("verificationStatus") == "verified" and lesson.get("exerciseStatus") != "completed":
            errors.append(f"{lesson_id}: verification requires a completed exercise")
        if lesson.get("verificationStatus") == "verified":
            evidence_types = {item.get("type") for item in lesson.get("evidence", []) if isinstance(item, dict)}
            if not evidence_types.intersection({"test", "review", "learningRecord", "demo"}):
                errors.append(f"{lesson_id}: verified status requires verification evidence")

        issue = lesson.get("githubIssue")
        if issue is not None:
            if not isinstance(issue, str) or not ISSUE_URL_RE.match(issue):
                errors.append(f"{lesson_id}: githubIssue must be a GitHub issue URL or null")
            else:
                match = ISSUE_URL_RE.match(issue)
                github = data.get("github")
                if isinstance(github, dict) and match:
                    owner, repo = match.group(1), match.group(2)
                    if owner != github.get("owner") or repo != github.get("repo"):
                        errors.append(
                            f"{lesson_id}: githubIssue must belong to {github.get('owner')}/{github.get('repo')}"
                        )

    github = data.get("github")
    if github is not None:
        if not isinstance(github, dict):
            errors.append("github must be an object")
        else:
            for key in ("owner", "repo", "label", "assignee"):
                if not github.get(key):
                    errors.append(f"github.{key} is required")
            expected_repo = f"{github.get('owner')}/{github.get('repo')}"
            if data.get("repository") and data.get("repository") != expected_repo:
                errors.append("github.owner/repo must match repository")

    if sequences and sorted(sequences) != list(range(1, len(sequences) + 1)):
        errors.append("Lesson sequence numbers must be contiguous and start at 1")

    if data.get("ordered", False):
        found_open = False
        for _, lesson in sorted(lessons(data), key=lambda pair: pair[1]["sequence"]):
            is_verified = lesson["verificationStatus"] == "verified"
            if not is_verified:
                found_open = True
            elif found_open:
                errors.append(
                    f"{lesson['id']}: a later lesson is verified while an earlier lesson is still unverified"
                )

    return errors


def progress_bar(done: int, total: int, width: int = 24) -> str:
    filled = round(width * done / total) if total else 0
    return "[" + "#" * filled + "-" * (width - filled) + "]"


def show(data: dict) -> None:
    all_lessons = [lesson for _, lesson in lessons(data)]
    verified = sum(l["verificationStatus"] == "verified" for l in all_lessons)
    attempted = sum(l["exerciseStatus"] == "completed" for l in all_lessons)
    taught = sum(l["lessonStatus"] == "completed" for l in all_lessons)

    print(f"Scala Backend Engineering Lab: {verified}/{len(all_lessons)} verified")
    print(progress_bar(verified, len(all_lessons)), f"{verified / len(all_lessons):.0%}")
    print(f"Lessons completed: {taught}; exercises completed: {attempted}; verified: {verified}")
    print()

    for phase in data["phases"]:
        print(phase["title"])
        for lesson in phase["lessons"]:
            status = lesson["verificationStatus"]
            marker = "✓" if status == "verified" else "~" if lesson["exerciseStatus"] == "completed" else "·"
            issue = lesson.get("githubIssue")
            issue_part = ""
            if isinstance(issue, str) and "/issues/" in issue:
                issue_part = f" issue=#{issue.rsplit('/', 1)[-1]}"
            print(
                f"  {marker} {lesson['id']} {lesson['title']} | "
                f"lesson={lesson['lessonStatus']} exercise={lesson['exerciseStatus']} verify={status}{issue_part}"
            )
        print()

    next_item = next((l for l in all_lessons if l["verificationStatus"] != "verified"), None)
    if next_item:
        if next_item["exerciseStatus"] == "completed":
            print(f"Next action: verify {next_item['id']} — {next_item['title']}")
        elif next_item["lessonStatus"] == "completed":
            print(f"Next action: complete the independent exercise for {next_item['id']} — {next_item['title']}")
        else:
            print(f"Next action: start {next_item['id']} — {next_item['title']}")
    else:
        print("All curriculum units are verified.")


def parse_evidence(value: str) -> dict:
    if ":" not in value:
        raise ValueError("Evidence must use type:value, for example test:src/test/scala/day01/Day01Suite.test.scala")
    kind, payload = value.split(":", 1)
    if kind not in {"source", "test", "review", "learningRecord", "demo"}:
        raise ValueError(f"Unsupported evidence type: {kind}")
    return {"type": kind, "path": payload} if kind != "demo" else {"type": kind, "description": payload}


def set_status(data: dict, lesson_id: str, field: str, value: str, evidence: str | None) -> None:
    if field not in ALLOWED:
        raise ValueError(f"Field must be one of: {', '.join(ALLOWED)}")
    if value not in ALLOWED[field]:
        raise ValueError(f"Invalid {field} value: {value}")

    target = None
    for _, lesson in lessons(data):
        if lesson["id"] == lesson_id:
            target = lesson
            break
    if target is None:
        raise ValueError(f"Unknown lesson id: {lesson_id}")

    if evidence:
        item = parse_evidence(evidence)
        if item not in target["evidence"]:
            target["evidence"].append(item)

    target[field] = value
    errors = validate(data)
    if errors:
        raise ValueError("Invalid transition:\n- " + "\n- ".join(errors))
    save(data)


def main() -> int:
    parser = argparse.ArgumentParser(description="Validate and display learning progress.")
    sub = parser.add_subparsers(dest="command")
    sub.add_parser("show")
    sub.add_parser("check")
    setter = sub.add_parser("set")
    setter.add_argument("lessonId")
    setter.add_argument("field", choices=sorted(ALLOWED))
    setter.add_argument("value")
    setter.add_argument("--evidence")
    args = parser.parse_args()

    data = load()

    if args.command in (None, "show"):
        errors = validate(data)
        if errors:
            print("Tracker validation failed:", file=sys.stderr)
            for error in errors:
                print(f"- {error}", file=sys.stderr)
            return 1
        show(data)
        return 0

    if args.command == "check":
        errors = validate(data)
        if errors:
            print("Tracker validation failed:")
            for error in errors:
                print(f"- {error}")
            return 1
        print("curriculum.json is consistent")
        return 0

    if args.command == "set":
        try:
            set_status(data, args.lessonId, args.field, args.value, args.evidence)
        except ValueError as exc:
            print(exc, file=sys.stderr)
            return 2
        show(load())
        return 0

    return 2


if __name__ == "__main__":
    raise SystemExit(main())
