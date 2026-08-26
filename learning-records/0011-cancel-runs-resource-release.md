# Cancel runs Resource release

The learner explained that canceling a fiber inside `withLease`'s `use` still runs the lease release finalizer, and that `withHeartbeat` cancels the pulse because leaving `heartbeat.background`'s `Resource` scope runs that resource's release. The floor for later sessions: treat cancelation and `Resource` finalizers as one lifecycle, not separate cleanup paths.

**Evidence**: `ExercisesDay15Suite` (five tests pass); formatted `ExercisesDay15.scala` with `Resource.make` / `.use` and `.background`; retrieval tying cancel to release and `background` scope exit to heartbeat cancel.

**Implications**: `scala-016` (FS2) can assume structured acquire/release and cooperative cancel without re-teaching bare `start` vs `background`.
