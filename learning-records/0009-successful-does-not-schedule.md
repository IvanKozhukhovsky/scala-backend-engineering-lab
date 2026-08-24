# Future.successful does not schedule

The learner kept two `fetchLineTotal` shapes and explained that the `map` path waits for `fetchUnitPrice` to complete, then unpacks the `Option[Int]`, while `Future.successful(None)` does not go through that resolve. The floor for later sessions: `Future { }` / `map` run on the `ExecutionContext`; `Future.successful` is already completed and does not start the lookup.

**Evidence**: `ExercisesDay13Suite` (six tests pass); formatted `ExercisesDay13.scala`; retrieval that solution 1 unpacks after the Future resolves.

**Implications**: `scala-014` can contrast eager `Future` with lazy `IO` without re-teaching the placeholder or `using ExecutionContext`.
