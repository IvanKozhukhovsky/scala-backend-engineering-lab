package day01

class Day01Suite extends munit.FunSuite:
  test("multiply returns the product"):
    assertEquals(multiply(7, 6), 42)

  test("temperature classification covers the boundaries"):
    assertEquals(classifyTemperature(-0.1), "freezing")
    assertEquals(classifyTemperature(0.0), "cool")
    assertEquals(classifyTemperature(19.9), "cool")
    assertEquals(classifyTemperature(20.0), "warm")

  test("age exercise classification covers all categories"):
    assertEquals(classifyAgeCategory(17), "a minor")
    assertEquals(classifyAgeCategory(18), "an adult")
    assertEquals(classifyAgeCategory(64), "an adult")
    assertEquals(classifyAgeCategory(65), "a senior")

  test("temperature exercise classification covers all categories"):
    assertEquals(classifyTemperatureCategory(-1), "freezing")
    assertEquals(classifyTemperatureCategory(0), "cold")
    assertEquals(classifyTemperatureCategory(20), "warm")
    assertEquals(classifyTemperatureCategory(30), "hot")
