package day16

import cats.effect.unsafe.implicits.global

import munit.FunSuite

class Day16Suite extends FunSuite:
  test("codesFrom emits every code in order"):
    val got = codesFrom(List("EUR", "GBP")).compile.toList
    assertEquals(got, List("EUR", "GBP"))

  test("rateStream looks up each code when compiled"):
    val got =
      rateStream(codesFrom(List("EUR", "JPY", "GBP"))).compile.toList
        .unsafeRunSync()
    assertEquals(got, List(Some(108), None, Some(127)))

  test("rateOf stays pure"):
    assertEquals(rateOf("EUR"), Some(108))
    assertEquals(rateOf("USD"), None)
