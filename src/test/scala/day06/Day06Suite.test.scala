package day06

import scala.util.{Failure, Success}

class Day06Suite extends munit.FunSuite:
  test("parseInt succeeds for a trimmed integer"):
    assertEquals(parseInt(" 7 "), Success(7))

  test("parseInt fails for a non-integer"):
    assert(parseInt("foo").isFailure)
    parseInt("foo") match
      case Failure(ex) => assert(ex.isInstanceOf[NumberFormatException])
      case Success(_)  => fail("expected Failure")

  test("describeParse names success and failure"):
    assertEquals(describeParse("7"), "parsed 7")
    assert(describeParse("foo").startsWith("failed: "))

  test("requirePositive is Right only for n > 0"):
    assertEquals(requirePositive(3), Right(3))
    assertEquals(requirePositive(0), Left("0 is not positive"))
    assertEquals(requirePositive(-1), Left("-1 is not positive"))

  test("parsePositive turns parse and domain failures into Left"):
    assertEquals(parsePositive("8"), Right(8))
    assertEquals(parsePositive("0"), Left("0 is not positive"))
    assert(parsePositive("foo").isLeft)

  test("parsePort succeeds for a trimmed integer"):
    assertEquals(parsePort(" 443 "), Success(443))

  test("parsePort fails for a non-integer"):
    assert(parsePort("9a9").isFailure)

  test("describePort formats the unwrapped port number"):
    assertEquals(describePort("443"), "port 443")
    assertEquals(describePort(" 80 "), "port 80")

  test("describePort prefixes parse failures with invalid"):
    assert(describePort("9a9").startsWith("invalid: "))

  test("validPort accepts the inclusive 1 to 65535 range"):
    assertEquals(validPort(1), Right(1))
    assertEquals(validPort(65535), Right(65535))
    assertEquals(validPort(443), Right(443))

  test("validPort rejects ports outside the range"):
    assert(validPort(0).isLeft)
    assert(validPort(65536).isLeft)

  test("readPort is Right only for a parsable in-range port"):
    assertEquals(readPort("443"), Right(443))
    assertEquals(readPort("1"), Right(1))
    assertEquals(readPort("65535"), Right(65535))

  test("readPort is Left for a bad parse or an out-of-range port"):
    assert(readPort("9a9").isLeft)
    assert(readPort("0").isLeft)
    assert(readPort("65536").isLeft)
