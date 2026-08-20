package day05

// Independent exercise for scala-005.
// Replace each ??? yourself. Do not call .get and do not use null.

case class Account(id: String, owner: String, email: Option[String])

/** First account with this id, or None if nobody matches. */
def findAccount(accounts: List[Account], id: String): Option[Account] =
  accounts.find(_.id == id)

/** The owner, or "unknown" when the account is missing. */
def ownerName(account: Option[Account]): String =
  account match
    case Some(value) => value.owner
    case None        => "unknown"

/** The substring after the first '@', keeping None if the email is missing. If the string contains
  * no '@', return Some of the whole string.
  */
def emailDomain(email: Option[String]): Option[String] =
  email match
    case Some(str) =>
      val atIndex = str.indexOf("@")
      if (atIndex != -1) Some(str.substring(atIndex + 1)) else email
    case None => None

/** "Hello, <owner>" when present, otherwise "Hello, guest". Use match. */
def greeting(account: Option[Account]): String =
  account match
    case Some(found) => s"Hello, ${found.owner}"
    case None        => "Hello, guest"

@main
def exercisesDay05(): Unit =
  println("Implement the functions above, then run /review-exercise.")

  val users =
    List(
      Account("1", "Ada", Some("ada@example.com")),
      Account("2", "Grace", None),
      Account("3", "Bob", Some("ada@example.com"))
    )

  println(s"Found account: ${findAccount(users, "3")}")
  println(s"Not found account: ${findAccount(users, "4")}")
  println(s"Found owner: ${ownerName(Some(Account("1", "Ada", Some("ada@example.com"))))}")
  println(s"Not found owner: ${ownerName(None)}")
  println(s"Exists emailDomain: ${{ emailDomain(Some("123@example.com")) }}")
  println(s"Exists two emailDomain: ${{ emailDomain(Some("123@321@example.com")) }}")
  println(s"Non-exists emailDomain: ${{ emailDomain(Some("123example.com")) }}")
  println(
    s"Greeting for owner: ${{ greeting(Some(Account("1", "Ada", Some("ada@example.com")))) }}"
  )
  println(s"Greeting for guest: ${{ greeting(None) }}")
