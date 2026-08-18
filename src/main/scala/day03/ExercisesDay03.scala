package day03

// Goal: obtain the names of users who are at least 18 years old and practice collection operations.

case class User(name: String, age: Int)

def adults(users: List[User]): List[User] =
  users.filter(_.age >= 18)

def adultNames(users: List[User]): List[String] =
  adults(users).map(_.name)

def firstAdultOver30(users: List[User]): Option[User] =
  adults(users).find(_.age > 30)

def hasMinors(users: List[User]): Boolean =
  users.exists(_.age < 18)

def allUsersOlderThan10(users: List[User]): Boolean =
  users.forall(_.age > 10)

def sumAges(users: List[User]): Int =
  users.foldLeft(0)((acc, value) => acc + value.age)

def flattenGroups(groups: List[List[String]]): List[String] =
  groups.flatten

def flatMapGroups(groups: List[List[String]]): List[String] =
  groups.flatMap(identity)

@main
def exercisesDay03(): Unit =
  val users =
    List(
      "Alice:23",
      "Bob:17",
      "Charlie:35",
      "David:15",
      "Eve:29"
    )

  val parsedUsers =
    users.map { raw =>
      val parts = raw.split(":")
      User(
        name = parts(0),
        age = parts(1).toInt
      )
    }
  println(s"Users: $parsedUsers")

  println(s"Users aged 18 or older: ${adults(parsedUsers)}")

  println(s"Names of users aged 18 or older: ${adultNames(parsedUsers)}")

  println(s"First user over 30: ${firstAdultOver30(parsedUsers)}")

  println(s"Has minors: ${hasMinors(parsedUsers)}")

  println(s"All users older than 10: ${allUsersOlderThan10(parsedUsers)}")

  println(s"Sum of ages: ${sumAges(parsedUsers)}")

  val groups =
    List(
      List("Alice", "Bob"),
      List("Charlie"),
      List("David", "Eve")
    )

  println(s"Flattened groups (method 1): ${flattenGroups(groups)}")

  println(s"Flattened groups (method 2): ${flatMapGroups(groups)}")
