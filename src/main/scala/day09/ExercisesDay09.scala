package day09

import scala.util.{Failure, Success, Try}

// Independent exercise for scala-009: do not change this file.
// The skill is writing tests in ExercisesDay09Suite.test.scala.

enum LimitError:
  case EmptyInput
  case NotAnInteger(raw: String)
  case OutOfRange(value: Int)

/** Trim, then parse an integer in 1 to 100 inclusive.
  *
  * Empty after trim: EmptyInput. Not an integer: NotAnInteger with the trimmed text. Integer
  * outside 1..100: OutOfRange with that integer.
  */
def parseLimit(raw: String): Either[LimitError, Int] =
  raw.trim match
    case ""      => Left(LimitError.EmptyInput)
    case trimmed =>
      Try(trimmed.toInt) match
        case Failure(_)                     => Left(LimitError.NotAnInteger(trimmed))
        case Success(n) if n < 1 || n > 100 => Left(LimitError.OutOfRange(n))
        case Success(n)                     => Right(n)

@main
def exercisesDay09(): Unit =
  println(
    "Do not change this file. Write tests in src/test/scala/day09/ExercisesDay09Suite.test.scala"
  )
