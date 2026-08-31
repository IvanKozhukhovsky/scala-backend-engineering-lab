package day22

import cats.effect.IO
import cats.effect.IOApp
import cats.effect.Resource
import com.zaxxer.hikari.HikariConfig
import org.typelevel.doobie.*
import org.typelevel.doobie.hikari.HikariTransactor
import org.typelevel.doobie.implicits.*

val rates: Map[String, Int] = Map("EUR" -> 108, "GBP" -> 127)

/** Cents per unit for a currency code, or None. Pure: no SQL, no JDBC. */
def rateOf(code: String): Option[Int] =
  rates.get(code)

/** One catalog row. Field order is the SELECT / INSERT column order. */
final case class RateRow(code: String, cents: Int)

/** A booked conversion. Used by the independent exercise. */
final case class FillRow(code: String, qty: Int, cents: Int)

/** Documented schema. Constructing this string does not create a table. */
val rateDdl: String =
  """CREATE TABLE rate (
    |  code  VARCHAR NOT NULL PRIMARY KEY,
    |  cents INTEGER NOT NULL
    |)""".stripMargin

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

def createRateTable: ConnectionIO[Int] =
  sql"""CREATE TABLE rate (
    code  VARCHAR NOT NULL PRIMARY KEY,
    cents INTEGER NOT NULL
  )""".update.run

/** One ConnectionIO program: one `transact` is one transaction. */
def seedRates: ConnectionIO[Unit] =
  for
    _ <- createRateTable
    _ <- insertRate(RateRow("EUR", 108)).run
    _ <- insertRate(RateRow("GBP", 127)).run
  yield ()

/** In-process JDBC. Unique `name` so tests do not share a catalog. `DB_CLOSE_DELAY=-1` keeps the
  * mem database after each connection closes.
  */
def h2Xa(name: String): Transactor[IO] =
  Transactor.fromDriverManager[IO](
    driver = "org.h2.Driver",
    url = s"jdbc:h2:mem:$name;DB_CLOSE_DELAY=-1",
    user = "sa",
    password = "",
    logHandler = None
  )

/** Production-shaped pool config. Setting fields does not connect. */
def postgresHikariConfig(url: String, user: String, password: String): HikariConfig =
  val config = new HikariConfig()
  config.setDriverClassName("org.postgresql.Driver")
  config.setJdbcUrl(url)
  config.setUsername(user)
  config.setPassword(password)
  config

/** Production pool. `Resource.use` starts Hikari and may open a connection. */
def postgresPool(
    url: String,
    user: String,
    password: String
): Resource[IO, HikariTransactor[IO]] =
  HikariTransactor.fromHikariConfig[IO](postgresHikariConfig(url, user, password))

/** JDBC rim: transact Day 21's Query0 / Update0. Domain stays `rateOf`. */
final class RateRepository(xa: Transactor[IO]):
  def find(code: String): IO[Option[RateRow]] =
    findRate(code).option.transact(xa)

  def insert(row: RateRow): IO[Int] =
    insertRate(row).run.transact(xa)

object Day22 extends IOApp.Simple:
  val run: IO[Unit] =
    val xa = h2Xa("day22-main")
    val repo = RateRepository(xa)
    for
      _ <- seedRates.transact(xa)
      row <- repo.find("EUR")
      _ <- IO.println(row)
    yield ()
