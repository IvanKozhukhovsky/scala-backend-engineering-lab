package day23

import cats.effect.IO
import cats.effect.IOApp
import org.flywaydb.core.Flyway
import org.typelevel.doobie.*
import org.typelevel.doobie.implicits.*

val rates: Map[String, Int] = Map("EUR" -> 108, "GBP" -> 127)

/** Cents per unit for a currency code, or None. Pure: no SQL, no JDBC, no Flyway. */
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

/** Same JDBC URL Flyway and the Transactor must share. `DB_CLOSE_DELAY=-1` keeps H2 mem after
  * close.
  */
def quotesUrl(name: String): String =
  s"jdbc:h2:mem:$name;DB_CLOSE_DELAY=-1"

/** In-process JDBC. Unique `name` so tests do not share a catalog. */
def h2Xa(name: String): Transactor[IO] =
  Transactor.fromDriverManager[IO](
    driver = "org.h2.Driver",
    url = quotesUrl(name),
    user = "sa",
    password = "",
    logHandler = None
  )

/** Apply pending versioned SQL. `configure` / `load` do not migrate; `migrate` does. Idempotent. */
def migrateQuotes(url: String): IO[Int] =
  IO {
    Flyway
      .configure()
      .dataSource(url, "sa", "")
      .locations("classpath:db/migration")
      .load()
      .migrate()
      .migrationsExecuted
  }

/** Production-shaped Flyway. `load` does not connect. Do not call `migrate` without a live
  * Postgres.
  */
def postgresFlyway(url: String, user: String, password: String): Flyway =
  Flyway
    .configure()
    .dataSource(url, user, password)
    .locations("classpath:db/migration")
    .load()

/** Two writes, one program. One `transact` is one transaction; a failed second insert rolls back
  * the first.
  */
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

object Day23 extends IOApp.Simple:
  val run: IO[Unit] =
    val name = "day23-main"
    val xa = h2Xa(name)
    val repo = RateRepository(xa)
    for
      _ <- migrateQuotes(quotesUrl(name))
      _ <- seedRates.transact(xa)
      row <- repo.find("EUR")
      _ <- IO.println(row)
    yield ()
