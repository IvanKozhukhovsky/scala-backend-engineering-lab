# A green test can still be a tautology

The learner saw `assertEquals(parseLimit("0"), parseLimit("0"))` pass and then discarded it: both sides call the function under test, so the assertion cannot disagree with a bug. Expected values stay spec literals such as `Left(LimitError.OutOfRange(0))`, not a second call to `parseLimit`.

**Evidence**: independent `ExercisesDay09Suite` (seven tests pass); retrieval that a tautology stays green; the tautology test was removed.

**Implications**: `scala-010` can teach properties/invariants without re-explaining “expected must not be the code under test.” Do not treat a passing suite as sufficient when the assertions are circular.
