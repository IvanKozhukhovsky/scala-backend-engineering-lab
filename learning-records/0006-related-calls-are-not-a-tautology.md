# Related calls are not a tautology

The learner distinguished `pageOffset(page + 1, pageSize) == pageOffset(page, pageSize) + pageSize` from calling the same function with the same arguments twice: the inputs differ, and `pageSize` is an independent spec value. A constant `0` implementation would fail the step property.

**Evidence**: independent `ExercisesDay10Suite` (three properties pass); retrieval that the two sides are different page inputs related by page size.

**Implications**: Later round-trips such as `decode(encode(x)) == x` also call production code on both sides. Do not treat that shape as a tautology; the arguments are related, not identical.
