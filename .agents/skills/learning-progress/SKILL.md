---
name: learning-progress
description: Track, validate, and summarize the Scala curriculum. Use when the user asks what has been completed, what remains, whether a lesson is verified, or what should happen next.
---

# Learning Progress

Use `curriculum.json` as the source of truth for operational progress and `learning-records/` as evidence of durable understanding.

## Workflow

1. Run `python3 scripts/progress.py check` before interpreting progress.
2. Read the earliest curriculum unit whose `verificationStatus` is not `verified`.
3. Distinguish three states explicitly: lesson exposure, independent exercise completion, and verification.
4. Do not promote a unit to `verified` from file existence alone.
5. Verification evidence may be a focused automated test, independent review, learning record, or concrete demonstration.
6. When changing a status, use `python3 scripts/progress.py set` when practical so transition invariants are checked.
7. If the ordered curriculum would be skipped, explain the reason and update the curriculum intentionally rather than silently jumping ahead.

## Output

Report: current phase, verified units, exercises awaiting verification, next required action, and any blocker. Keep the report concise unless the user asks for a detailed learning review.
