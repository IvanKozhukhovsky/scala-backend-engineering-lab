package day06

import scala.util.{Failure, Success, Try}

def parseInt(raw: String): Try[Int] =
  Try(raw.trim.toInt)

def describeParse(raw: String): String =
  parseInt(raw) match
    case Success(n)  => s"parsed $n"
    case Failure(ex) => s"failed: ${ex.getMessage}"

def requirePositive(n: Int): Either[String, Int] =
  if n > 0 then Right(n)
  else Left(s"$n is not positive")

def parsePositive(raw: String): Either[String, Int] =
  parseInt(raw) match
    case Failure(ex) => Left(ex.getMessage)
    case Success(n)  => requirePositive(n)

@main
def day06(): Unit =
  println(s"Parsed: ${parseInt("7")}")
  println(s"Not an int: ${parseInt("foo")}")
  println(s"Describe 7: ${describeParse("7")}")
  println(s"Describe foo: ${describeParse("foo")}")
  println(s"Positive: ${requirePositive(3)}")
  println(s"Not positive: ${requirePositive(0)}")
  println(s"Parse positive 8: ${parsePositive("8")}")
  println(s"Parse positive 0: ${parsePositive("0")}")
  println(s"Parse positive foo: ${parsePositive("foo")}")
  println(s"Map on Left: ${Left[String, Int]("nope").map(_ * 2)}")
