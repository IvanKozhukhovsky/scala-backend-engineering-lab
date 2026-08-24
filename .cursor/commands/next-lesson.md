# Identify the next learning unit

Use the `github-learning-board` skill together with `MISSION.md`, `curriculum.json`, `NOTES.md`, and `learning-records/`.

Identify the earliest curriculum unit whose `verificationStatus` is not `verified`.

If an earlier completed exercise is awaiting verification:
- do not start new material;
- do not create or advance GitHub cards;
- report that unit as the required next action;
- instruct the learner to run `/review-exercise`.

Otherwise run the skill's `/next-lesson` workflow:
- if previous-phase `learning` issues are still open, do not open a new phase; send the learner to `/progress` or `/verify`;
- when entering a new phase and all older `learning` issues are closed, create one `learning` issue per unit in that phase (Todo), write each URL to `githubIssue`, and assign the first unverified unit (In Progress);
- when the phase issues already exist, assign the next unverified unit (In Progress), sync its lifecycle checkboxes from `curriculum.json`, and leave the rest in Todo;
- report the unit id, title, phase, objective, and issue URL;
- instruct the learner to invoke `/teach` explicitly for that unit.

Do not teach the new unit inside this command.

Do not mark lesson, exercise, or verification states complete without evidence.
