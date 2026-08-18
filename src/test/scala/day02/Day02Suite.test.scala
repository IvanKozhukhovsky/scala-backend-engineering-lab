package day02

class Day02Suite extends munit.FunSuite:
  test("square and isEven model simple functions"):
    assertEquals(square(6), 36)
    assert(isEven(10))
    assert(!isEven(7))

  test("applyOperation accepts behavior as a value"):
    assertEquals(applyOperation(10, _ * 3), 30)

  test("warm temperatures start at 20 degrees Celsius"):
    assert(!isWarm(19))
    assert(isWarm(20))

  test("Celsius is converted to Fahrenheit"):
    assert(math.abs(toFahrenheit(0) - 32.0) < 0.000001)
    assert(math.abs(toFahrenheit(100) - 212.0) < 0.000001)
