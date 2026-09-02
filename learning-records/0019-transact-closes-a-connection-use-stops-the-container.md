# transact closes a Connection; use stops the container

The learner first treated `Resource.make` as extra success/failure paths for transactions. They then named the split: after `insertFill(...).run.transact(xa)` inside `withDeskPg`, Postgres is still running; `pg.stop()` runs when `use` ends. That is why a `jdbc:tc:` URL that stops on last-connection-close fights `transact`, while `deskPg` can host several sessions.

**Evidence**: retrieval after `ExercisesDay24Suite`; `Resource.make` around `PostgreSQLContainer.start` / `stop`.

**Implications**: Do not re-teach container vs JDBC lifetime unless a later URL uses `jdbc:tc:` or stops Postgres inside `transact`. App Dockerfiles (`scala-025`) are a different image — not this test harness.
