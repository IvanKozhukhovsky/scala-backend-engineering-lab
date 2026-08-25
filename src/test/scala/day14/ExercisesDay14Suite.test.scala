package day14

import cats.effect.unsafe.implicits.global

class ExercisesDay14Suite extends munit.FunSuite:
  test("unitPrice finds WIDGET without an IO"):
    assertEquals(unitPrice("WIDGET"), Some(250))
    assertEquals(unitPrice("GADGET"), Some(400))
    assertEquals(unitPrice("NOPE"), None)

  test("lookupUnitPrice finds WIDGET"):
    assertEquals(lookupUnitPrice("WIDGET").unsafeRunSync(), Some(250))

  test("lookupUnitPrice is None when the sku is missing"):
    assertEquals(lookupUnitPrice("NOPE").unsafeRunSync(), None)

  test("lineTotal multiplies unit cents by qty"):
    assertEquals(lineTotal("WIDGET", 3).unsafeRunSync(), Some(750))
    assertEquals(lineTotal("GADGET", 1).unsafeRunSync(), Some(400))

  test("lineTotal is None for an unknown sku even when qty is valid"):
    assertEquals(lineTotal("NOPE", 3).unsafeRunSync(), None)

  test("non-positive qty is None even when the sku is known"):
    assertEquals(lineTotal("WIDGET", 0).unsafeRunSync(), None)
    assertEquals(lineTotal("WIDGET", -1).unsafeRunSync(), None)
