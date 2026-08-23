# Teaching Notes

- Prefer a short title plus one observable outcome before expanding a lesson or a next-unit report.
- Do not skip curriculum units merely because later code appears to use the concept.
- Prefer retrieval questions before re-explaining material that was already covered.
- For an exercise, request or inspect the learner's attempt before offering a complete implementation.
- Give the smallest useful hint first. Escalate to a full explanation only when needed or explicitly requested.
- A file existing is evidence of an attempt, not proof of mastery.
- Verification should use objective evidence where possible: tests, compiler output, a code review, or an explanation by the learner.
- Before marking a unit verified, run `scala-cli fmt --check` as well as focused tests. If formatting fails, do not rewrite the learner file; hint them to run `scala-cli fmt` on it (or align the `=>` in the reported match).
- MUnit is pinned at `1.0.4`. `munit-scalacheck` has no `1.0.4`; the 1.0-line pin is `1.0.0`.
- `scala-011` teaches purity at function scale: core returns values, shell does I/O. Package/module boundaries wait for `scala-012`. Do not introduce `IO` / Cats Effect (`scala-014`).
