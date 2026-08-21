# Success unwraps; the Try is the container

After interpolating `parsePort(raw)` into a string (which printed `Success(443)`), the learner switched the success branch to the inner `value`. That is the same `A` vs `Option[A]` distinction, now for `Try`. On retrieval they placed `Try` with thrown exceptions and `Either` with a chosen `Left` payload (here a `String`) plus a `Right` success type.

**Evidence**: the `describePort` fix, plus the explanation that `Either` is for non-exception reasons.

**Implications**: `scala-007` can replace `String` on `Left` with a dedicated error type without re-teaching `Try` vs `Either`.
