package day03

// Goal: obtain the names of users who are at least 18 years old and practice collection operations.
@main
def exercisesDay03(): Unit =
  case class User(name: String, age: Int)

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

  val adults = parsedUsers.filter(_.age >= 18)
  println(s"Users aged 18 or older: $adults")

  val adultNames = adults.map(_.name)
  println(s"Names of users aged 18 or older: $adultNames")

  val firstAdultOver30 = adults.find(_.age > 30)
  println(s"First user over 30: $firstAdultOver30")

  val hasMinors = parsedUsers.exists(_.age < 18)
  println(s"Has minors: $hasMinors")

  val allUsersOlderThan10 = parsedUsers.forall(_.age > 10)
  println(s"All users older than 10: $allUsersOlderThan10")

  val sumAges = parsedUsers.foldLeft(0)((acc, value) => acc + value.age)
  println(s"Sum of ages: $sumAges")

  val groups =
    List(
      List("Alice", "Bob"),
      List("Charlie"),
      List("David", "Eve")
    )

  val groupsList1 = groups.flatten
  println(s"Flattened groups (method 1): $groupsList1")

  val groupsList2 = groups.flatMap(identity)
  println(s"Flattened groups (method 2): $groupsList2")
