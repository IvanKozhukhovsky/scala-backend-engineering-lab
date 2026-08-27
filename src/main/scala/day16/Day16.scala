package day16

import cats.effect.IO
import cats.effect.IOApp
import fs2.Pure
import fs2.Stream

val rates: Map[String, Int] = Map("EUR" -> 108, "GBP" -> 127)

/** Cents per unit for a currency code, or None. Pure: no IO. */
def rateOf(code: String): Option[Int] =
  rates.get(code)

/** Pure stream of codes from a finite list. Building this does not look anything up. */
def codesFrom(list: List[String]): Stream[Pure, String] =
  Stream.emits(list)

/** Describe looking up each code as the stream is compiled. */
def rateStream(codes: Stream[Pure, String]): Stream[IO, Option[Int]] =
  codes.evalMap(code => IO(rateOf(code)))

/** Run the stream at the edge, logging each lookup result. */
def printRates: IO[Unit] =
  rateStream(codesFrom(List("EUR", "GBP", "JPY")))
    .evalTap(maybe => IO.println(s"rate=$maybe"))
    .compile
    .drain

object Day16 extends IOApp.Simple:
  val run: IO[Unit] = printRates
