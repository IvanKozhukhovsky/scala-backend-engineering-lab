# Learning Protocol

The repository combines a teaching workspace with an engineering tracker. They serve different purposes and must not be conflated.

## Teaching state

The installed `teach` skill uses `MISSION.md`, `RESOURCES.md`, `NOTES.md`, `lessons/`, `reference/`, `assets/`, and `learning-records/`. These artifacts answer what to teach, why it matters, and what durable understanding has been demonstrated.

## Operational progress

`curriculum.json` answers whether each ordered unit has been exposed, attempted independently, and verified. Its three independent status fields are deliberate:

- `lessonStatus=completed` means the lesson or equivalent instruction was completed.
- `exerciseStatus=completed` means an independent attempt exists.
- `verificationStatus=verified` means the result was checked with evidence.

A unit may therefore legitimately be `completed / completed / pending`. The four imported fundamentals units start in exactly that state because source attempts exist but this repository has not yet recorded verification evidence for the learner's understanding.

## Verification evidence

Acceptable evidence types in `curriculum.json` are `test`, `review`, `learningRecord`, and `demo`. Automated tests verify behavior but do not by themselves prove conceptual understanding; for important concepts, pair them with a short explanation or learning record.

## Commands

Use `python3 scripts/progress.py show` to display progress and the next required action. Use `python3 scripts/progress.py check` in CI. Status changes can be made through `python3 scripts/progress.py set` so invalid transitions are rejected.

The curriculum is ordered by default. This is the anti-skip mechanism: later verification is rejected while an earlier unit is still unverified. If the learning plan genuinely changes, edit the curriculum deliberately rather than bypassing the invariant.
