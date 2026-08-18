package day04

class Day04Suite extends munit.FunSuite:
  test("describeUser uses pattern matching with guards"):
    assertEquals(describeUser(User("Bob", 17)), "Bob is a minor")
    assertEquals(describeUser(User("Alice", 23)), "Alice is an adult")
    assertEquals(describeUser(User("Grace", 70)), "Grace is a senior")

  test("classifyNumber handles zero, sign and parity"):
    assertEquals(classifyNumber(0), "zero")
    assertEquals(classifyNumber(-3), "negative")
    assertEquals(classifyNumber(8), "positive even")
    assertEquals(classifyNumber(7), "positive odd")

  test("describeProduct classifies domain values"):
    assertEquals(describeProduct(Product("Laptop", 1200.0, "electronics")), "premium electronics")
    assertEquals(describeProduct(Product("Mouse", 25.0, "electronics")), "electronics")
    assertEquals(describeProduct(Product("Scala Guide", 40.0, "books")), "book")
    assertEquals(describeProduct(Product("Desk", 200.0, "furniture")), "other")
