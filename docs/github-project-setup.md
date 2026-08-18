# GitHub Project Setup

The repository remains the source of truth for curriculum state. GitHub Projects is the public execution and visualization layer.

## Project

Create a user-level GitHub Project named **Scala Backend Engineering Roadmap** and link this repository to it.

Start with a single **Board** view grouped by `Status`.

Use three statuses only:

* `Todo` — the unit is queued but active work has not started.
* `In Progress` — this is the unit currently being learned, exercised, or verified.
* `Done` — the unit has been verified and its issue has been closed.

Keep work in progress small. Normally, only one learning unit should be `In Progress`.

Do not create GitHub issues for the entire curriculum in advance. Create an issue when a curriculum unit becomes active or is one of the immediately pending verification units.

A **Curriculum** table view may be added later when the number of completed and planned units becomes large enough that a board is no longer sufficient.

A **Roadmap** view should only be added when target dates or larger backend/capstone milestones start to matter.

## Fields

The only required project field is:

* `Status`: `Todo`, `In Progress`, `Done`.

Do not duplicate `lessonStatus`, `exerciseStatus`, or `verificationStatus` as GitHub Project fields. Those states belong to `curriculum.json` and the learning-unit issue checklist.

Optional fields may be introduced later when they provide real value:

* `Phase`
* `Target date`
* `Evidence`

Avoid adding fields only for documentation purposes.

## Issues

Create one issue per active curriculum unit, not separate issues for the lesson, exercise, and verification stages.

The issue template in `.github/ISSUE_TEMPLATE/lesson.yml` contains the lifecycle checklist.

Each learning-unit issue should use the `learning` label so that it can be automatically added to the project.

The issue represents the complete lifecycle of one curriculum unit:

1. Learn or revisit the material.
2. Attempt retrieval or explanation without relying on notes.
3. Complete the independent exercise.
4. Run focused verification.
5. Record verification evidence in `curriculum.json`.
6. Run `python3 scripts/progress.py check`.
7. Complete the issue checklist.
8. Close the issue as completed.

When work begins on a unit, move it from `Todo` to `In Progress`.

A unit moves to `Done` only after its verification state in `curriculum.json` is `verified`.

Closing the issue should normally move the corresponding project item to `Done` through GitHub Project automation.

## Source of truth

`curriculum.json` is authoritative for learning progress.

GitHub Projects visualizes current work but must not become a second independent source of curriculum state.

If GitHub Project state and `curriculum.json` disagree, update GitHub to reflect `curriculum.json`, not the other way around.

If a curriculum unit contains a `githubIssue` field, store the corresponding GitHub issue URL there for traceability.

GitHub is intentionally not required for local progress, so Cursor can maintain and verify the learning state without GitHub account access.
