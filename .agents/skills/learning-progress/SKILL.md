---
name: learning-progress
description: Track, validate, and summarize the Scala curriculum. Use when the user asks what has been completed, what remains, whether a lesson is verified, or what should happen next.
---

# Learning Progress

Use `curriculum.json` as the source of truth for operational progress and `learning-records/` as evidence of durable understanding.

## Workflow

1. Run `python3 scripts/progress.py check` before interpreting progress.
2. If GitHub MCP is available, use the `github-learning-board` skill to compare `learning` issues with `githubIssue` and ask the learner about leftover open cards.
3. Read the earliest curriculum unit whose `verificationStatus` is not `verified`.
4. Distinguish three states explicitly: lesson exposure, independent exercise completion, and verification.
5. Do not promote a unit to `verified` from file existence alone.
6. Verification evidence may be a focused automated test, independent review, a learning record, or a concrete demonstration.
7. When changing a status, use `python3 scripts/progress.py set` when practical so transition invariants are checked.
8. If the ordered curriculum would be skipped, explain the reason and update the curriculum intentionally rather than silently jumping ahead.

## Output

Report: current phase, verified units, exercises awaiting verification, GitHub board mismatches, next required action, and any blocker. Keep the report concise unless the user asks for a detailed learning review.
