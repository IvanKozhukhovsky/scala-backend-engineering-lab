# A finished transact is already committed

The learner first described the one-`transact` path (second insert fails, the whole program rolls back). They then named the other arrangement: after `insertFill(first).run.transact(xa)` returns, that write is already committed; a PK failure in a second `transact` rolls back only the second insert, so `findFill("EUR")` still sees the first row.

**Evidence**: retrieval after `ExercisesDay23Suite`; `bookFills` stays `ConnectionIO` so the tests place one `transact`.

**Implications**: Do not re-teach commit-vs-rollback unless a later method transacts each write. Live Postgres (`scala-024`) uses the same JDBC rule: two `transact` calls are two commits.
