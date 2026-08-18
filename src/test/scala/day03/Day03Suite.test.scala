package day03

class Day03Suite extends munit.FunSuite:
  private val users =
    List(
      User("Alice", 23),
      User("Bob", 17),
      User("Charlie", 35),
      User("David", 15),
      User("Eve", 29)
    )

  test("adult names are users aged 18 or older"):
    assertEquals(adultNames(users), List("Alice", "Charlie", "Eve"))
    assertEquals(adultNames(List(User("Kai", 18), User("Bob", 17))), List("Kai"))

  test("find returns the first adult over 30 as Option"):
    assertEquals(firstAdultOver30(users), Some(User("Charlie", 35)))
    assertEquals(firstAdultOver30(List(User("Alice", 23), User("Bob", 17))), None)

  test("exists reports whether any user is a minor"):
    assertEquals(hasMinors(users), true)
    assertEquals(hasMinors(List(User("Alice", 23), User("Eve", 29))), false)

  test("forall reports whether every user is older than 10"):
    assertEquals(allUsersOlderThan10(users), true)
    assertEquals(allUsersOlderThan10(List(User("Alice", 23), User("Sam", 10))), false)

  test("flatMap flattens nested name groups"):
    val groups =
      List(
        List("Alice", "Bob"),
        List("Charlie"),
        List("David", "Eve")
      )
    val expected = List("Alice", "Bob", "Charlie", "David", "Eve")
    assertEquals(flatMapGroups(groups), expected)
    assertEquals(flattenGroups(groups), expected)

  test("foldLeft sums ages"):
    assertEquals(sumAges(users), 119)
