# Run repository quality gates

Run `python3 scripts/verify.py`. Report each gate separately: curriculum consistency, formatting, and tests. Do not claim success for a gate that could not execute.

If every gate passed, use the `github-learning-board` skill:
1. Run `python3 scripts/github_board.py sync-body` for verified units that still have an open `learning` issue.
2. `issue_write` the generated body so lifecycle checkboxes and evidence match `curriculum.json`.
3. Then close those issues with `state_reason=completed` (Done).

Do not close an issue for a unit that is not `verified`. Do not change GitHub if a quality gate failed. Do not leave acceptance checkboxes empty on a Done card.
