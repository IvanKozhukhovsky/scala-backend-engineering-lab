package day16

import cats.effect.unsafe.implicits.global

import munit.FunSuite

class ExercisesDay16Suite extends FunSuite:
  test("lines splits trims and drops blanks"):
    val got = lines(" EUR \n\nGBP \n ").compile.toList
    assertEquals(got, List("EUR", "GBP"))

  test("knownRates skips unknown codes"):
    val got = knownRates(List("EUR", "JPY", "GBP")).compile.toList.unsafeRunSync()
    assertEquals(got, List(108, 127))

  test("totalConverted sums amount times each known rate"):
    val got = totalConverted(List("EUR", "JPY", "GBP"), 10).unsafeRunSync()
    assertEquals(got, 2350)

  test("totalConverted is zero when no code is known"):
    assertEquals(totalConverted(List("JPY", "USD"), 10).unsafeRunSync(), 0)
