# Identify the next learning unit

Read `MISSION.md`, `curriculum.json`, `NOTES.md`, and `learning-records/`.

Identify the earliest curriculum unit whose `verificationStatus` is not `verified`.

If an earlier completed exercise is awaiting verification:
- do not start new material;
- report that unit as the required next action;
- instruct the learner to run `/review-exercise`.

If all preceding units are verified and the next unit has not been started:
- report its curriculum ID, title, phase, and objective;
- instruct the learner to create or activate its GitHub learning issue;
- instruct the learner to invoke `/teach` explicitly for that unit.

Do not teach the new unit inside this command.

Do not mark lesson, exercise, or verification states complete without evidence.
