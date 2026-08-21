package day06

import scala.util.Try
import scala.util.Success
import scala.util.Failure

// Independent exercise for scala-006.
// Replace each ??? yourself. Do not call .get and do not throw to the caller.

/** Parse a TCP port from a string. Use Try so a bad number becomes a Failure. */
def parsePort(raw: String): Try[Int] =
  Try(raw.trim.toInt)

/** "port <n>" on Success, or "invalid: <message>" on Failure. Use match. */
def describePort(raw: String): String =
  parsePort(raw) match
    case Success(value)     => s"port ${{ value }}"
    case Failure(exception) => s"invalid: ${exception.getMessage}"

/** Right(port) if the port is between 1 and 65535 inclusive; otherwise Left with a reason. */
def validPort(port: Int): Either[String, Int] =
  if port >= 1 && port <= 65535 then Right(port)
  else Left(s"Port is not in distance 1 <= port <= 65535")

/** Parse, then validate. A Failure message and an out-of-range port both become Left. */
def readPort(raw: String): Either[String, Int] =
  parsePort(raw) match
    case Failure(exception) => Left(exception.getMessage)
    case Success(value)     => validPort(value)

@main
def exercisesDay06(): Unit =
  println("Implement the functions above, then run /review-exercise.")

  println(s"Valid parse port: ${{ parsePort("999") }}")
  println(s"Valid parse port with gaps: ${{ parsePort(" 999  ") }}")
  println(s"Invalid parse port: ${{ parsePort("9a9") }}")
  println(s"Valid describe port: ${{ describePort("999") }}")
  println(s"Invalid describe port: ${{ describePort("9a9") }}")
  println(s"Valid validation port: ${{ validPort(999) }}")
  println(s"Invalid less than 1 validation port: ${{ validPort(0) }}")
  println(s"Invalid higher than 65535 validation port: ${{ validPort(65536) }}")
  println(s"Valid read port: ${{ readPort("999") }}")
  println(s"Invalid read port: ${{ readPort("9a9") }}")
