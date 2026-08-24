package day12.core

class Day12Suite extends munit.FunSuite:
  test("priceOf finds the matching sku"):
    val items = List(Item("A", 499), Item("B", 1200))
    assertEquals(priceOf(items, "B"), Some(1200))

  test("priceOf is None when the sku is missing"):
    assertEquals(priceOf(List(Item("A", 499)), "Z"), None)
