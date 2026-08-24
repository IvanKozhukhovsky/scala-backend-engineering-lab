# Import is the dependency arrow

The learner first described the arrow as core → shell (value flow: a `Stock` comes back, then `println`). After correction they named the dependence: the shell file writes `import day12.core`; core does not import shell. The file that writes `import` is the one that depends.

**Evidence**: `ExercisesDay12Suite` (five tests pass); `reserve` in `day12.core` with no shell import; retrieval that the shell depends on core.

**Implications**: `scala-013` can treat Futures as shell-side asynchrony without re-teaching package direction. If a later unit names a capability as a trait, put the trait in core so the import still points shell → core.
