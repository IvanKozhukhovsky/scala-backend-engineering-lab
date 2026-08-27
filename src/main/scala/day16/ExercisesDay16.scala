package day16

import cats.effect.IO
import fs2.Pure
import fs2.Stream

// Independent exercise for scala-016.
// Implement lines, knownRates, and totalConverted yourself.
// Do not call unsafeRunSync, compile.*, or println inside those three.
// Do not import scala.concurrent.Future.

/** Non-empty trimmed lines from a multi-line string. Pure: no IO. */
def lines(text: String): Stream[Pure, String] =
  Stream.emits(
    text.linesIterator
      .map(_.trim)
      .filter(_.nonEmpty)
      .toList
  )

/** Emit the cents-per-unit rate for each code that exists in `rates`. Skip unknown codes. */
def knownRates(codes: List[String]): Stream[IO, Int] =
  Stream
    .emits(codes)
    .evalMap(code => IO(rateOf(code)))
    .unNone

/** Sum `amount * rate` for every known code in `codes`. Returns 0 when none are known. */
def totalConverted(codes: List[String], amount: Int): IO[Int] =
  knownRates(codes).map(_ * amount).compile.fold(0)(_ + _)
