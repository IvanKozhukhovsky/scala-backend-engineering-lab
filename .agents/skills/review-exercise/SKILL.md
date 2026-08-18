---
name: review-exercise
description: Review a learner-authored Scala exercise attempt without replacing the learning process. Use when the user submits an exercise, asks whether their solution is correct, or wants feedback before marking a lesson verified.
---

# Review Exercise

## Preconditions

- Inspect the learner's existing attempt first.
- Read the corresponding curriculum unit and the lesson/exercise requirement.
- Do not produce a complete replacement solution unless explicitly requested after feedback.

## Review loop

1. Restate the observable behavior the exercise is meant to demonstrate.
2. Compile or run the smallest relevant test when possible.
3. If it fails, explain the first root issue and give the smallest useful hint.
4. If it passes, review correctness at boundaries and then idiomatic Scala appropriate to the learner's current phase.
5. Ask at least one short retrieval/explanation question when understanding cannot be established from the code alone.
6. Prefer an independent `scala-reviewer` subagent for substantial exercises or capstone changes.
7. Mark `verificationStatus=verified` only after evidence exists. Record that evidence in `curriculum.json` and add a learning record only for non-trivial demonstrated understanding.

## Guardrails

- A more advanced abstraction is not automatically a better answer.
- Do not turn stylistic preference into a correctness defect.
- Do not rewrite working learner code merely to make it look AI-generated or maximally concise.
