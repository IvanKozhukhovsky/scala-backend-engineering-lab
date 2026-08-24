# Agent Instructions

This repository is both a Scala learning workspace and a public software-engineering portfolio project.

## Core contract

- Preserve the distinction between learning and code generation. Do not silently solve independent exercises for the learner.
- Read `MISSION.md`, `curriculum.json`, and relevant `learning-records/` before deciding what should be taught next.
- Use primary documentation listed in `RESOURCES.md` for version-sensitive or ecosystem claims.
- Treat `curriculum.json` as the machine-readable source of truth for progress. A source file proves an attempt, not mastery.
- A curriculum unit is only complete when `verificationStatus` is `verified` with evidence.
- Prefer the smallest useful feedback loop: compiler, focused test, then broader integration checks.
- Do not add frameworks merely to demonstrate sophistication. Introduce dependencies only when the curriculum reaches the relevant problem.
- Keep public documentation in English. Explanations to the learner may be in Russian.

## Quality gates

Before claiming code work is complete, run `python3 scripts/verify.py` when the local toolchain permits it. If a gate cannot run, state exactly which gate was not executed.

## Repository boundaries

- Learner exercises live in `src/main/scala/dayXX/ExercisesDayXX.scala` during the fundamentals phase.
- Automated tests live under `src/test/scala/`.
- Teaching artifacts live in `lessons/`, `reference/`, and `learning-records/`.
- AI/Cursor configuration lives under `.cursor/` and `.agents/`.
- GitHub learning-board workflow: `.agents/skills/github-learning-board/SKILL.md` and `docs/github-project-setup.md`. `/next-lesson`, `/progress`, and `/verify` keep `learning` issues and `githubIssue` in sync.
