package day09

// Independent exercise for scala-009.
// Do not change ExercisesDay09.scala.
// Write tests for parseLimit:
// - names describe behavior
// - assertEquals(obtained, expected) with literal expected values
// - cover empty/whitespace, non-integer, below 1, above 100, 1, 100, and a trimmed success
// Replace the placeholder.

class ExercisesDay09Suite extends munit.FunSuite:
  // test("replace this placeholder with behavior-oriented tests"):
  //   ???

  test("empty input"):
    assertEquals(parseLimit("  "), Left(LimitError.EmptyInput))

  test("non-integer input"):
    assertEquals(parseLimit(" a "), Left(LimitError.NotAnInteger("a")))

  test("input int value below 1"):
    assertEquals(parseLimit("0"), Left(LimitError.OutOfRange(0)))

  test("input int value above 100"):
    assertEquals(parseLimit("101"), Left(LimitError.OutOfRange(101)))

  test("input int value equal 1"):
    assertEquals(parseLimit("1"), Right(1))

  test("input int value equal 100"):
    assertEquals(parseLimit("100"), Right(100))

  test("input int succes trimmed"):
    assertEquals(parseLimit(" 50 "), Right(50))
