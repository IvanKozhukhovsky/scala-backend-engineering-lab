package day24

import cats.effect.IO
import cats.effect.IOApp
import cats.effect.Resource
import org.flywaydb.core.Flyway
import org.testcontainers.postgresql.PostgreSQLContainer
import org.typelevel.doobie.*
import org.typelevel.doobie.implicits.*

val rates: Map[String, Int] = Map("EUR" -> 108, "GBP" -> 127)

/** Cents per unit for a currency code, or None. Pure: no SQL, no JDBC, no Docker. */
def rateOf(code: String): Option[Int] =
  rates.get(code)

/** One catalog row. Field order is the SELECT / INSERT column order. */
final case class RateRow(code: String, cents: Int)

/** A booked conversion. Used by the independent exercise. */
final case class FillRow(code: String, qty: Int, cents: Int)

/** Lookup by primary key. Building this does not talk to a database. */
def findRate(code: String): Query0[RateRow] =
  sql"select code, cents from rate where code = $code".query[RateRow]

/** Insert one row. Values become `?` placeholders, not concatenated text. */
def insertRate(row: RateRow): Update0 =
  sql"insert into rate (code, cents) values (${row.code}, ${row.cents})".update

/** Lookup a booked fill. Building this does not talk to a database. */
def findFill(code: String): Query0[FillRow] =
  sql"select code, qty, cents from fill where code = $code".query[FillRow]

/** Insert one fill row. Column order must match FillRow: code, qty, cents. */
def insertFill(row: FillRow): Update0 =
  sql"insert into fill (code, qty, cents) values (${row.code}, ${row.qty}, ${row.cents})".update

/** Engine fingerprint. H2's version() string does not contain `PostgreSQL`. */
def postgresVersion: Query0[String] =
  sql"select version()".query[String]

/** Image from the Testcontainers Postgres module catalog. Constructing a container does not pull
  * it.
  */
val quotesImage: String = "postgres:16-alpine"

/** Disposable Postgres. Building this object does not start Docker. */
def quotesContainer: PostgreSQLContainer =
  new PostgreSQLContainer(quotesImage)

/** Acquire starts and waits until Postgres is ready; release stops. Same shape as Ember `Resource`.
  */
def quotesPg: Resource[IO, PostgreSQLContainer] =
  Resource.make(IO {
    val pg = quotesContainer
    pg.start()
    pg
  })(pg => IO { pg.stop() })

/** Live JDBC. DriverManager is still the test transactor; the URL now comes from a running
  * container.
  */
def postgresXa(url: String, user: String, password: String): Transactor[IO] =
  Transactor.fromDriverManager[IO](
    driver = "org.postgresql.Driver",
    url = url,
    user = user,
    password = password,
    logHandler = None
  )

/** Apply pending versioned SQL to a live catalog. Credentials come from the container, not `sa`. */
def migrateQuotesPg(url: String, user: String, password: String): IO[Int] =
  IO {
    Flyway
      .configure()
      .dataSource(url, user, password)
      .locations("classpath:db/migration")
      .load()
      .migrate()
      .migrationsExecuted
  }

/** Start Postgres, pass JDBC coordinates into `run`, stop — including when `run` fails. */
def withQuotesPg[A](run: (String, String, String) => IO[A]): IO[A] =
  quotesPg.use { pg =>
    run(pg.getJdbcUrl, pg.getUsername, pg.getPassword)
  }

/** Two fill inserts, one program. The independent exercise reuses this; it does not re-teach
  * `transact`.
  */
def bookFills(first: FillRow, second: FillRow): ConnectionIO[Unit] =
  for
    _ <- insertFill(first).run
    _ <- insertFill(second).run
  yield ()

/** Two writes, one program. One `transact` is one transaction. */
def bookRates(first: RateRow, second: RateRow): ConnectionIO[Unit] =
  for
    _ <- insertRate(first).run
    _ <- insertRate(second).run
  yield ()

/** Seed after Flyway has created `rate`. No CREATE TABLE here. */
def seedRates: ConnectionIO[Unit] =
  bookRates(RateRow("EUR", 108), RateRow("GBP", 127))

/** JDBC rim for a single-row find. Multi-step writes stay ConnectionIO so callers place `transact`.
  */
final class RateRepository(xa: Transactor[IO]):
  def find(code: String): IO[Option[RateRow]] =
    findRate(code).option.transact(xa)

object Day24 extends IOApp.Simple:
  val run: IO[Unit] =
    withQuotesPg { (url, user, password) =>
      val xa = postgresXa(url, user, password)
      for
        _ <- migrateQuotesPg(url, user, password)
        _ <- seedRates.transact(xa)
        row <- RateRepository(xa).find("EUR")
        _ <- IO.println(row)
      yield ()
    }
