package day13

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

class ExercisesDay13Suite extends munit.FunSuite:
  test("unitPrice finds WIDGET without a Future"):
    assertEquals(unitPrice("WIDGET"), Some(250))
    assertEquals(unitPrice("GADGET"), Some(400))
    assertEquals(unitPrice("NOPE"), None)

  test("fetchUnitPrice finds WIDGET"):
    assertEquals(Await.result(fetchUnitPrice("WIDGET"), 1.second), Some(250))

  test("fetchUnitPrice is None when the sku is missing"):
    assertEquals(Await.result(fetchUnitPrice("NOPE"), 1.second), None)

  test("fetchLineTotal multiplies unit cents by qty"):
    assertEquals(Await.result(fetchLineTotal("WIDGET", 3), 1.second), Some(750))
    assertEquals(Await.result(fetchLineTotal("GADGET", 1), 1.second), Some(400))

  test("fetchLineTotal is None for an unknown sku even when qty is valid"):
    assertEquals(Await.result(fetchLineTotal("NOPE", 3), 1.second), None)

  test("non-positive qty is None even when the sku is known"):
    assertEquals(Await.result(fetchLineTotal("WIDGET", 0), 1.second), None)
    assertEquals(Await.result(fetchLineTotal("WIDGET", -1), 1.second), None)
