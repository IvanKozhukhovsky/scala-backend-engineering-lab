#!/usr/bin/env python3
"""Plan and record GitHub learning-board issue links from curriculum.json."""

from __future__ import annotations

import argparse
import json
import re
import sys
from pathlib import Path
from typing import Any, Iterator

ROOT = Path(__file__).resolve().parents[1]
CURRICULUM = ROOT / "curriculum.json"
LESSON_ID_RE = re.compile(r"scala-\d{3}")
ISSUE_URL_RE = re.compile(r"^https://github\.com/([^/]+)/([^/]+)/issues/(\d+)$")


def load() -> dict[str, Any]:
    return json.loads(CURRICULUM.read_text(encoding="utf-8"))


def save(data: dict[str, Any]) -> None:
    CURRICULUM.write_text(json.dumps(data, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")


def github_cfg(data: dict[str, Any]) -> dict[str, Any]:
    cfg = data.get("github")
    if not isinstance(cfg, dict):
        raise ValueError("curriculum.json is missing the github configuration object")
    for key in ("owner", "repo", "label", "assignee"):
        if not cfg.get(key):
            raise ValueError(f"curriculum.json github.{key} is required")
    return cfg


def lessons(data: dict[str, Any]) -> Iterator[tuple[dict[str, Any], dict[str, Any]]]:
    for phase in data["phases"]:
        for lesson in phase["lessons"]:
            yield phase, lesson


def find_lesson(data: dict[str, Any], lesson_id: str) -> dict[str, Any]:
    for _, lesson in lessons(data):
        if lesson["id"] == lesson_id:
            return lesson
    raise ValueError(f"Unknown lesson id: {lesson_id}")


def next_unverified(data: dict[str, Any]) -> tuple[dict[str, Any], dict[str, Any]] | None:
    ordered = sorted(lessons(data), key=lambda pair: pair[1]["sequence"])
    for phase, lesson in ordered:
        if lesson["verificationStatus"] != "verified":
            return phase, lesson
    return None


LIFECYCLE_ITEMS: tuple[tuple[str, str], ...] = (
    ("lesson", "Lesson completed"),
    ("retrieval", "Retrieval / explanation attempted without notes"),
    ("exercise", "Independent exercise completed"),
    ("verification", "Focused verification executed"),
    ("curriculum", "curriculum.json updated with evidence"),
    ("learningRecord", "Learning record added if a non-trivial insight was demonstrated"),
)


def issue_title(lesson: dict[str, Any]) -> str:
    return f"[LEARN] {lesson['id']} — {lesson['title']}"


def evidence_types(lesson: dict[str, Any]) -> set[str]:
    return {item.get("type") for item in lesson.get("evidence", []) if isinstance(item, dict)}


def lifecycle_state(lesson: dict[str, Any]) -> dict[str, bool]:
    lesson_done = lesson.get("lessonStatus") == "completed"
    exercise_done = lesson.get("exerciseStatus") == "completed"
    verified = lesson.get("verificationStatus") == "verified"
    has_record = "learningRecord" in evidence_types(lesson)
    has_evidence = bool(lesson.get("evidence"))
    return {
        "lesson": lesson_done,
        "retrieval": lesson_done,
        "exercise": exercise_done,
        "verification": verified,
        "curriculum": verified or (exercise_done and has_evidence),
        "learningRecord": has_record,
    }


def evidence_markdown(lesson: dict[str, Any]) -> str:
    lines: list[str] = []
    for item in lesson.get("evidence", []):
        if not isinstance(item, dict):
            continue
        kind = item.get("type", "evidence")
        path = item.get("path") or item.get("description")
        if path:
            lines.append(f"- `{kind}`: `{path}`")
    if lines:
        return "\n".join(lines)
    if lesson.get("verificationStatus") == "verified":
        return "Verification is recorded in `curriculum.json`."
    return "Record tests, review notes, or learning records after the independent attempt."


def mark(checked: bool) -> str:
    return "[x]" if checked else "[ ]"


def issue_body(phase: dict[str, Any], lesson: dict[str, Any], outcome: str | None = None) -> str:
    checks = lifecycle_state(lesson)
    boxes = "\n".join(
        f"- {mark(checks[key])} {label}" for key, label in LIFECYCLE_ITEMS
    )
    return (
        f"<!-- curriculum-id: {lesson['id']} -->\n"
        f"### Curriculum ID\n"
        f"{lesson['id']}\n\n"
        f"### Phase\n"
        f"{phase['id']} — {phase['title']}\n\n"
        f"### Observable outcome\n"
        f"{outcome or lesson['title']}\n\n"
        f"### Learning lifecycle\n"
        f"{boxes}\n\n"
        f"### Evidence\n"
        f"{evidence_markdown(lesson)}\n"
    )


def find_phase_and_lesson(data: dict[str, Any], lesson_id: str) -> tuple[dict[str, Any], dict[str, Any]]:
    for phase, lesson in lessons(data):
        if lesson["id"] == lesson_id:
            return phase, lesson
    raise ValueError(f"Unknown lesson id: {lesson_id}")


def intended_board(data: dict[str, Any], lesson: dict[str, Any]) -> str:
    if lesson.get("verificationStatus") == "verified":
        return "done"
    current = next_unverified(data)
    if current and current[1]["id"] == lesson["id"]:
        return "in_progress"
    return "todo"


def sync_payload(data: dict[str, Any], lesson_ids: list[str] | None = None) -> list[dict[str, Any]]:
    cfg = github_cfg(data)
    selected: list[tuple[dict[str, Any], dict[str, Any]]]
    if lesson_ids:
        selected = [find_phase_and_lesson(data, lesson_id) for lesson_id in lesson_ids]
    else:
        selected = [
            (phase, lesson)
            for phase, lesson in lessons(data)
            if lesson.get("githubIssue")
        ]

    payloads: list[dict[str, Any]] = []
    for phase, lesson in selected:
        url = lesson.get("githubIssue")
        if not url:
            continue
        payloads.append(
            {
                "id": lesson["id"],
                "issueNumber": parse_issue_url(url, cfg),
                "htmlUrl": url,
                "board": intended_board(data, lesson),
                "checks": lifecycle_state(lesson),
                "issueTitle": issue_title(lesson),
                "issueBody": issue_body(phase, lesson),
            }
        )
    return payloads


def parse_issue_url(url: str, cfg: dict[str, Any] | None = None) -> int:
    match = ISSUE_URL_RE.match(url)
    if not match:
        raise ValueError(f"Not a GitHub issue URL: {url}")
    owner, repo, number = match.group(1), match.group(2), int(match.group(3))
    if cfg and (owner != cfg["owner"] or repo != cfg["repo"]):
        raise ValueError(
            f"Issue URL must belong to {cfg['owner']}/{cfg['repo']}: {url}"
        )
    return number


def lesson_id_from_title(title: str) -> str | None:
    match = LESSON_ID_RE.search(title)
    return match.group(0) if match else None


def plan(data: dict[str, Any]) -> dict[str, Any]:
    cfg = github_cfg(data)
    current = next_unverified(data)
    if current is None:
        return {
            "github": cfg,
            "reason": "complete",
            "nextUnit": None,
            "phase": None,
            "activateLessonId": None,
            "createLessonIds": [],
            "existingIssueUrls": {},
        }

    phase, lesson = current
    phase_lessons = list(phase["lessons"])
    existing = {
        item["id"]: item.get("githubIssue")
        for item in phase_lessons
        if item.get("githubIssue")
    }
    missing = [item["id"] for item in phase_lessons if not item.get("githubIssue")]
    awaiting = lesson["exerciseStatus"] == "completed"
    if awaiting:
        reason = "awaiting_verification"
    elif missing:
        reason = "create_phase_issues"
    else:
        reason = "activate_existing"

    return {
        "github": cfg,
        "reason": reason,
        "nextUnit": {
            "id": lesson["id"],
            "title": lesson["title"],
            "sequence": lesson["sequence"],
            "lessonStatus": lesson["lessonStatus"],
            "exerciseStatus": lesson["exerciseStatus"],
            "verificationStatus": lesson["verificationStatus"],
            "githubIssue": lesson.get("githubIssue"),
        },
        "phase": {
            "id": phase["id"],
            "title": phase["title"],
            "lessons": [
                {
                    "id": item["id"],
                    "title": item["title"],
                    "verificationStatus": item["verificationStatus"],
                    "githubIssue": item.get("githubIssue"),
                    "issueTitle": issue_title(item),
                    "issueBody": issue_body(phase, item),
                    "checks": lifecycle_state(item),
                    "board": intended_board(data, item),
                }
                for item in phase_lessons
            ],
        },
        "activateLessonId": None if awaiting else lesson["id"],
        "createLessonIds": [] if awaiting else missing,
        "existingIssueUrls": existing,
    }


def set_issues(pairs: list[tuple[str, str]]) -> dict[str, Any]:
    data = load()
    cfg = github_cfg(data)
    written: dict[str, str] = {}
    for lesson_id, url in pairs:
        parse_issue_url(url, cfg)
        lesson = find_lesson(data, lesson_id)
        lesson["githubIssue"] = url
        written[lesson_id] = url
    save(data)
    return written


def cmd_plan(_: argparse.Namespace) -> int:
    json.dump(plan(load()), sys.stdout, indent=2, ensure_ascii=False)
    sys.stdout.write("\n")
    return 0


def cmd_sync_body(args: argparse.Namespace) -> int:
    try:
        payload = sync_payload(load(), args.lesson_ids or None)
    except ValueError as exc:
        print(exc, file=sys.stderr)
        return 2
    json.dump(payload, sys.stdout, indent=2, ensure_ascii=False)
    sys.stdout.write("\n")
    return 0


def cmd_set_issue(args: argparse.Namespace) -> int:
    values = args.pairs
    if len(values) < 2 or len(values) % 2 != 0:
        print("set-issue expects ID URL pairs, for example: scala-013 https://github.com/org/repo/issues/15", file=sys.stderr)
        return 2
    pairs = [(values[i], values[i + 1]) for i in range(0, len(values), 2)]
    try:
        written = set_issues(pairs)
    except ValueError as exc:
        print(exc, file=sys.stderr)
        return 2
    json.dump(written, sys.stdout, indent=2, ensure_ascii=False)
    sys.stdout.write("\n")
    return 0


def main() -> int:
    parser = argparse.ArgumentParser(description="Plan GitHub learning-board actions from curriculum.json.")
    sub = parser.add_subparsers(dest="command", required=True)
    sub.add_parser("plan", help="Print the next-lesson board plan as JSON")
    setter = sub.add_parser("set-issue", help="Record GitHub issue URLs on curriculum units")
    setter.add_argument("pairs", nargs="+", help="Lesson ID and issue URL pairs")
    sync = sub.add_parser("sync-body", help="Print issue bodies with lifecycle checkboxes derived from curriculum.json")
    sync.add_argument("lesson_ids", nargs="*", help="Lesson IDs to sync; default is every unit with githubIssue")
    args = parser.parse_args()
    if args.command == "plan":
        return cmd_plan(args)
    if args.command == "set-issue":
        return cmd_set_issue(args)
    if args.command == "sync-body":
        return cmd_sync_body(args)
    return 2


if __name__ == "__main__":
    raise SystemExit(main())
