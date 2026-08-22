package day07

import scala.util.{Failure, Success, Try}

enum Role:
  case Admin, Member, Guest

def describeRole(role: Role): String =
  role match
    case Role.Admin  => "full access"
    case Role.Member => "limited access"
    case Role.Guest  => "read only"

enum PortError:
  case NotAnInteger(raw: String)
  case OutOfRange(port: Int)

def validPort(port: Int): Either[PortError, Int] =
  if port >= 1 && port <= 65535 then Right(port)
  else Left(PortError.OutOfRange(port))

def readPort(raw: String): Either[PortError, Int] =
  Try(raw.trim.toInt) match
    case Failure(_) => Left(PortError.NotAnInteger(raw.trim))
    case Success(n) => validPort(n)

def describePortError(error: PortError): String =
  error match
    case PortError.NotAnInteger(raw) => s"not an integer: $raw"
    case PortError.OutOfRange(port)  => s"out of range: $port"

@main
def day07(): Unit =
  println(s"Admin: ${describeRole(Role.Admin)}")
  println(s"Guest: ${describeRole(Role.Guest)}")
  println(s"Valid port: ${readPort("443")}")
  println(s"Not an integer: ${readPort("foo")}")
  println(s"Out of range: ${readPort("0")}")
  println(s"Explain range: ${describePortError(PortError.OutOfRange(0))}")
