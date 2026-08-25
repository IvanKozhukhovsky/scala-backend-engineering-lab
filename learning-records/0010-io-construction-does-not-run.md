# IO construction does not run

The learner explained that `lookupUnitPrice("WIDGET")` only builds a description in memory, and that `unsafeRunSync` is what executes it and yields the `Option[Int]`, contrasting that with eager `Future` which starts immediately. The floor for later sessions: compose `IO` values without running them; execution stays at the edge.

**Evidence**: `ExercisesDay14Suite` (six tests pass); formatted `ExercisesDay14.scala` with `IO.pure(None)` on the non-positive qty branch; retrieval contrasting description vs eager start.

**Implications**: `scala-015` can introduce fibers / `Resource` / cancelation without re-teaching that `IO` is lazy and not memoized like `Future`.
