package day04

class Day04Suite extends munit.FunSuite:
  test("describeUser uses pattern matching with guards"):
    assertEquals(describeUser(User("Bob", 17)), "Bob is a minor")
    assertEquals(describeUser(User("Kai", 18)), "Kai is an adult")
    assertEquals(describeUser(User("Alice", 23)), "Alice is an adult")
    assertEquals(describeUser(User("Nora", 64)), "Nora is an adult")
    assertEquals(describeUser(User("Grace", 65)), "Grace is a senior")
    assertEquals(describeUser(User("Grace", 70)), "Grace is a senior")

  test("classifyNumber handles zero, sign and parity"):
    assertEquals(classifyNumber(0), "zero")
    assertEquals(classifyNumber(-3), "negative")
    assertEquals(classifyNumber(-2), "negative")
    assertEquals(classifyNumber(8), "positive even")
    assertEquals(classifyNumber(7), "positive odd")

  test("describeProduct classifies domain values"):
    assertEquals(describeProduct(Product("Laptop", 1200.0, "electronics")), "premium electronics")
    assertEquals(describeProduct(Product("TV", 1000.0, "electronics")), "premium electronics")
    assertEquals(describeProduct(Product("Monitor", 350.0, "electronics")), "electronics")
    assertEquals(describeProduct(Product("Mouse", 25.0, "electronics")), "electronics")
    assertEquals(describeProduct(Product("Scala Guide", 40.0, "books")), "book")
    assertEquals(describeProduct(Product("Desk", 200.0, "furniture")), "other")

  test("parseCoordinates destructures tuple points"):
    assertEquals(parseCoordinates(Nil), Nil)
    assertEquals(
      parseCoordinates(List((0, 0), (10, 5), (-2, 4), (3, -7))),
      List("x = 0, y = 0", "x = 10, y = 5", "x = -2, y = 4", "x = 3, y = -7")
    )
