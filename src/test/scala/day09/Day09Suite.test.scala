package day09

class Day09Suite extends munit.FunSuite:
  test("ten percent off 1000 cents is 900"):
    assertEquals(discounted(1000, 10), 900)

  test("zero percent leaves the amount"):
    assertEquals(discounted(250, 0), 250)

  test("half of 5 cents truncates to 2"):
    assertEquals(discounted(5, 50), 2)
