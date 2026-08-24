# Show learning progress

Use the `learning-progress` skill, then the `github-learning-board` skill.

Validate `curriculum.json`, summarize completed lessons, independent exercises, pending verification, and the next required action. Do not infer mastery from file presence alone.

Run `python3 scripts/github_board.py sync-body` and update each listed GitHub issue body so lifecycle checkboxes follow curriculum status. Then compare open/closed issues with `verificationStatus`. If cards are still open when they should be Done, or closed when the unit is not verified, ask the learner what to do. Do not close or reopen silently.
