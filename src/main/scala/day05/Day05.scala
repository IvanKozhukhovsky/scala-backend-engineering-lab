package day05

case class User(id: String, name: String, email: Option[String])

def findUser(users: List[User], id: String): Option[User] =
  users.find(_.id == id)

def displayName(user: Option[User]): String =
  user match
    case Some(found) => found.name
    case None        => "guest"

def localPart(email: Option[String]): Option[String] =
  email.map(_.takeWhile(_ != '@'))

def primaryEmail(users: List[User], id: String): Option[String] =
  for
    user <- findUser(users, id)
    email <- user.email
  yield email

@main
def day05(): Unit =
  val users =
    List(
      User("1", "Ada", Some("ada@example.com")),
      User("2", "Grace", None)
    )

  println(s"Found: ${findUser(users, "1")}")
  println(s"Missing: ${findUser(users, "9")}")
  println(s"Name of missing user: ${displayName(findUser(users, "9"))}")
  println(s"Local parts: ${users.map(user => localPart(user.email))}")
  println(s"Ada email: ${primaryEmail(users, "1")}")
  println(s"Grace email: ${primaryEmail(users, "2")}")
  println(s"Unknown email: ${primaryEmail(users, "9")}")
  println(s"From Java-style null: ${Option[String](null)}")
  println(s"Fallback: ${findUser(users, "9").map(_.name).getOrElse("guest")}")
