package day13

import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

val rates: Map[String, Int] = Map("EUR" -> 108, "GBP" -> 127)

/** Cents per unit for a currency code, or None. Pure: no Future. */
def rateOf(code: String): Option[Int] =
  rates.get(code)

/** Look up the rate on the given ExecutionContext. */
def fetchRate(code: String)(using ExecutionContext): Future[Option[Int]] =
  Future {
    rateOf(code)
  }

/** Eventual amount times rate, or None if the code is unknown. */
def fetchConverted(code: String, amount: Int)(using ExecutionContext): Future[Option[Int]] =
  fetchRate(code).map(maybeRate => maybeRate.map(_ * amount))

@main
def day13(): Unit =
  import ExecutionContext.Implicits.global
  val got = Await.result(fetchConverted("EUR", 10), 1.second)
  println(got)
