---
name: github-learning-board
description: Synchronize GitHub learning issues and the Todo / In Progress / Done board with curriculum.json. Use when running /next-lesson, /progress, or /verify, or when creating, assigning, or closing learning-labeled issues.
---

# GitHub Learning Board

`curriculum.json` is authoritative. GitHub is a view. Use GitHub MCP plus `python3 scripts/github_board.py`.

The GitHub MCP plugin can create, assign, comment, update bodies, and close issues. It cannot set Project Status. Columns therefore follow issue events, with one-time Project workflows (`docs/github-project-setup.md`):

| Status | GitHub issue event |
|---|---|
| Todo | open, label `learning`, unassigned |
| In Progress | assigned to `github.assignee` |
| Done | closed with `state_reason=completed` |

Do not ask the learner to drag cards or tick lifecycle boxes. Derive both from `curriculum.json`.

## Tooling

- Plan: `python3 scripts/github_board.py plan`
- Record URLs: `python3 scripts/github_board.py set-issue scala-013 URL`
- Checkbox/evidence body: `python3 scripts/github_board.py sync-body [scala-012 ...]`
- Local check: `python3 scripts/progress.py check`
- MCP: `list_issues`, `search_issues`, `issue_read`, `issue_write`, `add_issue_comment`

Lifecycle boxes (exact labels):

1. Lesson completed ← `lessonStatus=completed`
2. Retrieval / explanation attempted without notes ← same (teach contract includes retrieval)
3. Independent exercise completed ← `exerciseStatus=completed`
4. Focused verification executed ← `verificationStatus=verified`
5. curriculum.json updated with evidence ← verified, or exercise completed with `evidence`
6. Learning record added if a non-trivial insight was demonstrated ← evidence type `learningRecord`

## Sync issue body

Whenever curriculum status or evidence changes, and before closing:

1. `python3 scripts/github_board.py sync-body <id>`
2. `issue_write` `method: "update"` with that `issueBody` (and `issueNumber`)

Never close a learning issue while its lifecycle boxes still contradict `curriculum.json`.

## `/next-lesson`

Do not teach. Do not mark lesson, exercise, or verification complete.

1. `python3 scripts/progress.py check` and `python3 scripts/github_board.py plan`.
2. `list_issues` `labels: ["learning"]` (open; closed if checking leftovers). Match `githubIssue` or `scala-0xx` in the title.
3. If `reason` is `awaiting_verification`, stop. Instruct `/review-exercise`.
4. Open `learning` issues for already-`verified` units block a new phase. Send the learner to `/progress` or `/verify`.
5. If `reason` is `create_phase_issues` and older `learning` issues are closed:
   - `search_issues` first.
   - `issue_write` `method: "create"` for each `createLessonIds` id: `issueTitle` / `issueBody` from the plan, `labels: ["learning"]`, no assignees (Todo, empty boxes).
   - `python3 scripts/github_board.py set-issue` with each HTML URL.
6. If `reason` is `activate_existing` or after create:
   - `issue_write` `method: "update"`: `assignees: [github.assignee]` (In Progress).
   - `sync-body` + `issue_write` body (partial boxes if the lesson already started).
   - `add_issue_comment` that `/next-lesson` activated this unit.
   - Later phase issues stay unassigned (Todo).
7. Report id, title, phase, URL. Instruct `/teach`.

Exactly one unit In Progress.

## `/progress`

1. Follow `learning-progress` for curriculum state.
2. `python3 scripts/github_board.py sync-body` and `issue_write` the returned bodies onto every listed issue. This is mechanical; do not ask.
3. Compare open/closed with `verificationStatus`. Ask the learner only about leftovers (verified+open, unverified+closed, stale open cards).
4. Summarize board vs curriculum.

## `/verify`

1. `python3 scripts/verify.py`. If a gate fails or cannot run, do not change GitHub.
2. After gates pass, `sync-body` for **verified** units with an open `learning` issue, `issue_write` the body, then `state: "closed"`, `state_reason: "completed"`. Comment that `/verify` moved the card to Done.
3. Do not close an unverified unit. Point to `/review-exercise` if needed.

## `/review-exercise`

After writing `verified` (or completed exercise) into `curriculum.json`, `sync-body` that unit and `issue_write` the body. Do not close the issue.

## Guards

- Always use the `learning` label.
- Write `githubIssue` immediately after create.
- Do not invent issue numbers.
- If MCP is unavailable, print planned titles/bodies and do not pretend the board changed.
