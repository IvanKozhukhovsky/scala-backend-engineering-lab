package day10

import munit.ScalaCheckSuite
import org.scalacheck.Gen
import org.scalacheck.Prop.{forAll, propBoolean}

class Day10Suite extends ScalaCheckSuite:
  test("clamp 150 into 1 to 100 is 100"):
    assertEquals(clamp(150, 1, 100), 100)

  property("when lo <= hi, clamp stays in [lo, hi]"):
    forAll { (value: Int, lo: Int, hi: Int) =>
      (lo <= hi) ==> {
        val got = clamp(value, lo, hi)
        got >= lo && got <= hi
      }
    }

  property("a value already in [lo, hi] is unchanged"):
    forAll(Gen.choose(-1000, 1000), Gen.choose(0, 2000), Gen.choose(0, 2000)) {
      (lo, width, rawOffset) =>
        val hi = lo + width
        val value = lo + math.min(rawOffset, width)
        assertEquals(clamp(value, lo, hi), value)
    }
