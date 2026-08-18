# GitHub Project Setup

The repository remains the source of truth for curriculum state; GitHub Projects is the public planning and visualization layer.

## Create the project

Create a user-level GitHub Project named **Scala Backend Engineering Roadmap** and attach this repository after publishing it.

Create three views:

1. **Board** — grouped by `Status`; use it for day-to-day movement.
2. **Curriculum** — table view sorted by lesson sequence; show phase, verification, and target date.
3. **Roadmap** — add only when you start assigning target dates to larger backend/capstone units.

## Fields

Use these fields:

- `Status`: Planned, Learning, Exercise, Verification, Done.
- `Phase`: Fundamentals, Domain Modeling, Testing & FP, Concurrency & Effects, Backend, Persistence, Production, Capstone.
- `Verification`: Not started, Pending, Verified.
- `Target date`: optional date.
- `Evidence`: short text or link to the relevant test, PR, or learning record.

## Issues

Create one issue per curriculum unit, not separate issues for lecture and exercise. The issue template in `.github/ISSUE_TEMPLATE/lesson.yml` contains the lifecycle checklist. This prevents the tracker from becoming noisier than the learning itself.

When a unit is verified:

1. Update `curriculum.json` with evidence.
2. Run `python3 scripts/progress.py check`.
3. Mark the GitHub Project item's `Verification` as `Verified` and `Status` as `Done`.
4. Close the lesson issue.

GitHub is intentionally not required for local progress, so Cursor can maintain the learning state even without GitHub account access.
