package day04

case class User(name: String, age: Int)

def describeUser(user: User): String =
  user match
    case User(name, age) if age < 18 =>
      s"$name is a minor"
    case User(name, age) if age < 65 =>
      s"$name is an adult"
    case User(name, _) =>
      s"$name is a senior"

def classifyNumber(value: Int): String =
  value match
    case 0 =>
      "zero"
    case x if x < 0 =>
      "negative"
    case x if x % 2 == 0 =>
      "positive even"
    case _ =>
      "positive odd"

@main
def day04(): Unit =
  val users =
    List(
      User("Alice", 23),
      User("Bob", 17),
      User("Charlie", 35),
      User("David", 15),
      User("Eve", 29)
    )

  val adultNames =
    users
      .filter(_.age >= 18)
      .map(_.name)

  val descriptions = users.map(describeUser)

  val alice = User("Alice", 23)
  val olderAlice = alice.copy(age = 24)

  println(s"Adults: $adultNames")
  println(s"Descriptions: $descriptions")
  println(s"Alice: $alice")
  println(s"Older Alice: $olderAlice")
  println(s"Equal: ${alice == olderAlice}")
