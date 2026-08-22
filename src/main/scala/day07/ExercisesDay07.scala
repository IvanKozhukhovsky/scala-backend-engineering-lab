package day07

import scala.util.Failure
import scala.util.Success

// Independent exercise for scala-007.
// Replace each ??? yourself. Match every enum case by name — not case _, not String on Left.

enum TicketStatus:
  case Open, InReview, Done

/** "open", "in review", or "done". Name every case. */
def label(status: TicketStatus): String =
  status match {
    case TicketStatus.Open => "open"
    case TicketStatus.InReview => "in review"
    case TicketStatus.Done => "done"
  }

enum SignupError:
  case EmptyName
  case TooYoung(age: Int)

/** Right(trimmed name) when the trimmed name is non-empty and age is at least 18. EmptyName if the
  * trimmed name is empty (check this first). TooYoung(age) if age is below 18.
  */
def register(name: String, age: Int): Either[SignupError, String] =
  name.trim match
    case "" => Left(SignupError.EmptyName)
    case n if age < 18 => Left(SignupError.TooYoung(age))
    case n => Right(n)

/** "name is empty", or "too young: <age>". Name every case. */
def explain(error: SignupError): String =
  error match
    case SignupError.EmptyName => s"name is empty"
    case SignupError.TooYoung(age) => s"too young: ${age}"

@main
def exercisesDay07(): Unit =
  println("Implement the functions above, then run /review-exercise.")
    // Тесты для label
  println("=== Testing label ===")
  println(s"Open: ${label(TicketStatus.Open)}")         // expected: "open"
  println(s"InReview: ${label(TicketStatus.InReview)}") // expected: "in review"
  println(s"Done: ${label(TicketStatus.Done)}")         // expected: "done"

  // Тесты для register
  println("\n=== Testing register ===")
  val testCases = Seq(
    ("", 20, "Empty name"),                   // EmptyName
    ("   ", 20, "Whitespace name"),           // EmptyName
    ("Alice", 17, "Alice, 17"),               // TooYoung(17)
    ("Bob", 18, "Bob, 18"),                   // Right("Bob")
    ("Charlie", 25, "Charlie, 25")            // Right("Charlie")
  )

  for (name, age, description) <- testCases do
    val result = register(name, age)
    println(s"$description: ${result match
      case Left(err) => explain(err)
      case Right(name) => s"registered: $name"
    }")

  // Тесты для explain
  println("\n=== Testing explain ===")
  println(s"EmptyName: ${explain(SignupError.EmptyName)}")       // expected: "name is empty"
  println(s"TooYoung(15): ${explain(SignupError.TooYoung(15))}") // expected: "too young: 15"
