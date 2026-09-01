package day23

import cats.effect.IO
import org.typelevel.doobie.*
import org.flywaydb.core.Flyway

// Independent exercise for scala-023.
// Implement migrateDesk and bookFills yourself.
// migrateDesk: Flyway.configure().dataSource(url, "sa", "").locations("classpath:db/migration").load().migrate()
// Wrap migrate in IO { } so constructing the IO does not talk to JDBC.
// Return migrationsExecuted. Do not CREATE TABLE in Scala. Do not call postgresFlyway.migrate.
// bookFills: compose insertFill(first).run and insertFill(second).run in ConnectionIO.
// Do not transact inside bookFills — the tests place the boundary.
// Do not put JDBC inside rateOf. Do not start Testcontainers.

/** Apply the versioned SQL on `url`. Building this IO does not migrate. */
def migrateDesk(url: String): IO[Int] =
  IO {
    Flyway
      .configure()
      .dataSource(url, "sa", "")
      .locations("classpath:db/migration")
      .load()
      .migrate()
      .migrationsExecuted
  }

/** Two fill inserts, one program. A failed second insert must not leave the first row. */
def bookFills(first: FillRow, second: FillRow): ConnectionIO[Unit] =
  for
    _ <- insertFill(first).run
    _ <- insertFill(second).run
  yield ()
