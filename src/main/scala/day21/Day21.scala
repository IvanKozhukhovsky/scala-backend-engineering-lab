package day21

import cats.effect.IO
import cats.effect.IOApp
import org.typelevel.doobie.*
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

object Day21 extends IOApp.Simple:
  val run: IO[Unit] =
    IO.println(findRate("EUR").sql)
