package day13

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

class Day13Suite extends munit.FunSuite:
  test("rateOf finds EUR without a Future"):
    assertEquals(rateOf("EUR"), Some(108))
    assertEquals(rateOf("ZZZ"), None)

  test("fetchRate finds EUR"):
    assertEquals(Await.result(fetchRate("EUR"), 1.second), Some(108))

  test("fetchRate is None when the code is missing"):
    assertEquals(Await.result(fetchRate("ZZZ"), 1.second), None)

  test("fetchConverted multiplies amount by the rate"):
    assertEquals(Await.result(fetchConverted("EUR", 10), 1.second), Some(1080))
    assertEquals(Await.result(fetchConverted("GBP", 1), 1.second), Some(127))

  test("fetchConverted is None when the code is missing"):
    assertEquals(Await.result(fetchConverted("ZZZ", 10), 1.second), None)
