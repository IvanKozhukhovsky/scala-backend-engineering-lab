# Changelog

## 2026-08-24 — GitHub learning board commands

- `/next-lesson` creates `learning` issues for a new phase (Todo), assigns the first unit (In Progress), and stores URLs in `githubIssue`.
- `/progress` reconciles GitHub cards with verified curriculum units and asks before resolving leftover open issues.
- `/verify` closes the working `learning` issue after quality gates when the unit is verified (Done).
- Lifecycle acceptance checkboxes are derived from `curriculum.json` via `scripts/github_board.py sync-body` and written with GitHub MCP; they are not ticked by hand.
- Added `scripts/github_board.py`, the `github-learning-board` skill, and Project workflow documentation.

## 2026-08-18 — Repository engineering baseline

- Consolidated four local Scala CLI lesson folders into one publishable project.
- Removed generated `.scala-build` / `.bsp` state from the distributable repository.
- Added root Scala/JVM configuration, MUnit tests, Scalafmt configuration, and GitHub Actions CI.
- Added an ordered machine-readable curriculum and deterministic progress validation.
- Added Cursor rules, commands, portable skills, and an independent Scala reviewer subagent.
- Added stateful teaching workspace files and documentation for installing Matt Pocock's `teach` skill.
- Added GitHub Project workflow, AI transparency policy, security policy, and portfolio-readiness gates.
