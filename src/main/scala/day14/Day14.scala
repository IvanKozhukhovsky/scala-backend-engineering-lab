package day14

import cats.effect.IO
import cats.effect.IOApp

val rates: Map[String, Int] = Map("EUR" -> 108, "GBP" -> 127)

/** Cents per unit for a currency code, or None. Pure: no IO. */
def rateOf(code: String): Option[Int] =
  rates.get(code)

/** Describe looking up the rate. Building this value does not run the lookup. */
def lookupRate(code: String): IO[Option[Int]] =
  IO(rateOf(code))

/** Amount times rate, or None if the code is unknown. Still only a description. */
def convert(code: String, amount: Int): IO[Option[Int]] =
  lookupRate(code).map(maybeRate => maybeRate.map(_ * amount))

object Day14 extends IOApp.Simple:
  val run: IO[Unit] =
    for
      got <- convert("EUR", 10)
      _ <- IO.println(got)
    yield ()
