# H2 mem dies with the last connection

The learner named why `DB_CLOSE_DELAY=-1` is on `fillH2Xa`: each `transact` closes its JDBC connection; without the delay, H2 drops the in-memory catalog, so a second `transact` would not see the INSERT.

**Evidence**: retrieval after `ExercisesDay22Suite`; `jdbc:h2:mem:$name;DB_CLOSE_DELAY=-1`.

**Implications**: Do not re-teach the delay flag unless a later H2 URL omits it. Unique `name` is a different catalog per test — not the same as keeping one mem DB alive. Flyway / real Postgres (`scala-023` / `scala-024`) will not use this H2 lifetime trick.
