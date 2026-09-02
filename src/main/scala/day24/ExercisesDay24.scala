package day24

import cats.effect.IO
import cats.effect.Resource
import org.flywaydb.core.Flyway
import org.testcontainers.postgresql.PostgreSQLContainer

// Independent exercise for scala-024.
// Implement deskPg, withDeskPg, and migrateDeskPg yourself.
// deskPg: Resource.make that start()s a PostgreSQLContainer(quotesImage) and stop()s it.
// withDeskPg: deskPg.use, then run(getJdbcUrl, getUsername, getPassword).
// migrateDeskPg: Flyway.configure().dataSource(url, user, password).locations("classpath:db/migration").load().migrate()
// Wrap migrate in IO { }. Return migrationsExecuted. Credentials come from the container.
// Do not use jdbc:h2 or jdbc:tc:. Do not alias quotesPg / withQuotesPg.
// Do not CREATE TABLE in Scala. Do not put JDBC or Docker inside rateOf.

/** Acquire a disposable Postgres. Building the Resource does not start Docker. */
def deskPg: Resource[IO, PostgreSQLContainer] =
  Resource.make(IO {
    val pg = new PostgreSQLContainer(quotesImage)
    pg.start()
    pg
  })(pg => IO { pg.stop() })

/** Start Postgres, pass JDBC coordinates into `run`, stop — including when `run` fails. */
def withDeskPg[A](run: (String, String, String) => IO[A]): IO[A] =
  deskPg.use(pg => run(pg.getJdbcUrl(), pg.getUsername(), pg.getPassword()))

/** Apply the versioned SQL on a live catalog. Building this IO does not migrate. */
def migrateDeskPg(url: String, user: String, password: String): IO[Int] =
  IO {
    Flyway
      .configure()
      .dataSource(url, user, password)
      .locations("classpath:db/migration")
      .load()
      .migrate()
      .migrationsExecuted
  }
