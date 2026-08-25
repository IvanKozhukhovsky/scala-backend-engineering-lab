package day14

import cats.effect.IO
import cats.effect.unsafe.implicits.global

class Day14Suite extends munit.FunSuite:
  test("rateOf finds EUR without an IO"):
    assertEquals(rateOf("EUR"), Some(108))
    assertEquals(rateOf("ZZZ"), None)

  test("building an IO does not run the body"):
    var ran = false
    val io = IO {
      ran = true
      42
    }
    assertEquals(ran, false)
    assertEquals(io.unsafeRunSync(), 42)
    assertEquals(ran, true)

  test("the same IO value runs again when sequenced twice"):
    var n = 0
    val tick = IO { n += 1 }
    val program = for
      _ <- tick
      _ <- tick
    yield ()
    program.unsafeRunSync()
    assertEquals(n, 2)

  test("lookupRate finds EUR"):
    assertEquals(lookupRate("EUR").unsafeRunSync(), Some(108))

  test("lookupRate is None when the code is missing"):
    assertEquals(lookupRate("ZZZ").unsafeRunSync(), None)

  test("convert multiplies amount by the rate"):
    assertEquals(convert("EUR", 10).unsafeRunSync(), Some(1080))
    assertEquals(convert("GBP", 1).unsafeRunSync(), Some(127))

  test("convert is None when the code is missing"):
    assertEquals(convert("ZZZ", 10).unsafeRunSync(), None)
