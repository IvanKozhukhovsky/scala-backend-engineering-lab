package day08

// Independent exercise for scala-008.
// Replace each ??? yourself.
// Do not put show on Ticket, User, or Status.
// Do not use case _ on Status. Do not call toString inside shown.

trait Labelled:
  def label: String

case class Ticket(id: Int, title: String) extends Labelled:
  /** "#12 Fix login" for Ticket(12, "Fix login"). */
  def label: String = s"#${id} ${title}"

extension (raw: String)
  /** Trimmed text, or None when the trimmed text is empty. */
  def asNonEmpty: Option[String] =
    val trimmed = raw.trim()
    if trimmed == "" then None
    else Option(trimmed)

enum Status:
  case Open, Closed

/** "open" or "closed". Name every case. */
given Showable[Status] with
  extension (s: Status)
    def show: String =
      s match
        case Status.Open   => "open"
        case Status.Closed => "closed"

case class User(name: String, age: Int)

/** "Ada, 36" for User("Ada", 36). */
given Showable[User] with
  extension (u: User)
    def show: String =
      s"${u.name}, ${u.age}"

/** Delegate to the type-class method. Do not call toString. */
def shown[A](a: A)(using Showable[A]): String =
  a.show

@main
def exercisesDay08(): Unit =
  println("Implement the functions above, then run /review-exercise.")
  println(s"Ticket: ${Ticket(12, "Fix login").label}")
  println(s"Non-empty: ${"  ada  ".asNonEmpty}")
  println(s"Empty: ${"   ".asNonEmpty}")
  println(s"Status: ${shown(Status.Open)}")
  println(s"User: ${shown(User("Ada", 36))}")
