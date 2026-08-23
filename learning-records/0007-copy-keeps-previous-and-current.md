# Copy keeps previous and current

The learner treated in-place mutation as overwriting the input, and `copy` as keeping both the previous invoice and the next one so a test can still see the original. The clock belongs in `printApply`; `applyCoupon` stays a function of values so a test can pass a frozen `now`.

**Evidence**: `ExercisesDay11Suite` (five tests pass); retrieval that mutation loses the prior state and that `currentTimeMillis` is shell I/O.

**Implications**: `scala-012` can move to modules and dependency direction without re-teaching purity. When time or I/O appears, pass it into the core as data.
