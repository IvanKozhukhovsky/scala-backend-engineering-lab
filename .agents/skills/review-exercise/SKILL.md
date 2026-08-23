---
name: review-exercise
description: Review a learner-authored Scala exercise attempt without replacing the learning process. Use when the user submits an exercise, asks whether their solution is correct, or wants feedback before marking a lesson verified.
---

# Review Exercise

## Preconditions

- Inspect the learner's existing attempt first.
- Read the corresponding curriculum unit and the lesson/exercise requirement.
- Identify the concept that is currently being learned.
- Do not produce a complete replacement solution unless explicitly requested after feedback.
- Do not treat existing source code as proof of mastery.

## Review loop

1. Restate the observable behaviour the exercise is meant to demonstrate.
2. Identify the narrowest appropriate verification seam.
3. Determine the appropriate verification oracle: automated test, review, learning record, demo, or a combination.
4. Compile or run the smallest relevant verification when possible. Also run `scala-cli fmt --check` before treating the unit as ready to verify. Focused tests are not a substitute for the formatting gate CI runs.
5. If verification fails, explain the first root issue and give the smallest useful hint. If `fmt --check` fails, do not silently reformat learner-owned code; point at the diff and tell the learner to run `scala-cli fmt` (or make the one-line alignment the checker shows).
6. If verification passes, review correctness at boundaries and then idiomatic Scala appropriate to the learner's current phase.
7. Ask at least one short retrieval or explanation question when conceptual understanding cannot be established from the learner-authored code alone.
8. Prefer an independent `scala-reviewer` subagent for substantial exercises or capstone changes.
9. Mark `verificationStatus=verified` only after sufficient evidence exists.
10. Record the evidence in `curriculum.json`.
11. Add a learning record only for non-trivial demonstrated understanding.

Passing automated tests alone does not prove learning mastery when the curriculum unit is intended to teach a concept that the learner should be able to explain or apply independently.

## Guardrails

- A more advanced abstraction is not automatically a better answer.
- Do not turn stylistic preference into a correctness defect.
- Do not rewrite working learner code merely to make it look AI-generated or maximally concise.
- Do not change learner-owned implementation merely to make verification pass.
- Do not use a subagent to bypass learner/verifier ownership rules.
- Missing automated tests do not automatically mean that automated testing is the correct verification oracle.

## Verification seam

Before verification, identify:

1. the observable behaviour being assessed;
2. the narrowest appropriate public seam;
3. whether that seam already exists;
4. whether automated testing is an appropriate oracle.

Do not test console output merely because the exercise is currently implemented inside `@main`.

If automated verification is appropriate and a suitable seam already exists:

1. create or extend focused tests when those tests are verifier-owned;
2. test observable behaviour rather than implementation details;
3. use independent expected values;
4. include meaningful boundary and edge cases;
5. prefer fast unit-level tests over broader integration tests when both validate the same learning objective.

If automated verification is appropriate but no suitable test seam exists:

1. classify the required refactoring using the ownership rules below;
2. do not create an artificial test around an unsuitable interface;
3. establish the appropriate seam first;
4. create focused tests only after the seam exists.

If automated testing is not the appropriate oracle, use another explicit evidence type such as review, learning record, or demo.

## Learner and verifier ownership

Keep learner implementation and verification infrastructure separate.

### Learner-owned code

Code that demonstrates the concept currently being learned is learner-owned.

Examples include:

- exercise implementations;
- domain logic written as part of the lesson;
- collection transformations when collections are being learned;
- pattern matching when pattern matching is being learned;
- error handling when error modelling is being learned;
- concurrency or effect code when those concepts are being learned;
- architectural decisions when architecture is the learning objective.

Do not implement or rewrite learner-owned code merely to make verification pass.

When learner-owned code lacks a suitable test seam:

1. identify the smallest required refactoring;
2. explain why the seam is needed;
3. describe the observable interface that should result;
4. ask the learner to perform the change;
5. review the learner's change afterward;
6. continue verification only when an appropriate seam exists.

Do not provide the completed implementation unless the learner explicitly asks for the solution after attempting it.

### Verifier-owned code

When testing is not itself the learning objective, verification infrastructure may be created and modified by the agent.

Verifier-owned code includes:

- focused test suites;
- additional boundary and edge-case assertions;
- test fixtures;
- deterministic test data;
- verification helpers;
- verification scripts.

Verifier-owned tests must:

- evaluate observable behaviour through an appropriate public seam;
- use independent expected values;
- avoid reproducing the implementation as the expected result;
- avoid coupling to private implementation details;
- remain focused on the learning objective.

Creating verifier-owned tests does not give permission to rewrite learner-owned source code.

### When testing is the learning objective

If the current curriculum unit explicitly teaches testing, TDD, property-based testing, test architecture, mocking, or related concepts, tests become learner-owned code.

In those units:

- the learner writes the tests;
- the learner selects or justifies the tested behaviour when that is part of the learning objective;
- the agent reviews the tests;
- the agent may identify missing cases or weak assertions;
- the agent must not silently add the missing solution.

Verification infrastructure outside the learner's actual testing assignment may still remain verifier-owned when clearly separated.

## Testability refactoring

A missing test seam does not automatically give the verifier permission to edit learner-owned source code.

Classify the required change first.

If the refactoring changes, demonstrates, or exercises the concept currently being learned, the learner performs it.

If the change is purely mechanical infrastructure unrelated to the learning objective, the agent may perform it, but must state what will change before editing learner-owned files.

Prefer extracting existing behaviour over rewriting it.

A testability refactoring should:

- preserve existing behaviour;
- expose the smallest useful public seam;
- avoid introducing unnecessary abstractions;
- avoid solving additional parts of the exercise;
- remain appropriate to the learner's current Scala level.

## Verification evidence

Use explicit evidence rather than subjective confidence.

Typical evidence types are:

- `test` — deterministic automated behaviour verification;
- `review` — focused independent code or design review;
- `learningRecord` — demonstrated conceptual understanding worth preserving;
- `demo` — observable system-level behaviour.

A curriculum unit may require more than one evidence type.

For concept-focused programming lessons, a strong default is:

- observable behaviour verified by tests when appropriate;
- learner-authored implementation reviewed;
- short retrieval or explanation demonstrating understanding.

Do not mark a unit verified merely because source code exists or because an automated suite passes.
