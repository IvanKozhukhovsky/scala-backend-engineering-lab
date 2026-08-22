package day10

import munit.ScalaCheckSuite
import org.scalacheck.Gen
import org.scalacheck.Prop.forAll

// Independent exercise for scala-010.
// Do not change ExercisesDay10.scala.
// Write properties for pageOffset:
// - names describe the invariant
// - do not restate (page - 1) * pageSize as the expected value
// - generate page and pageSize with Gen.choose(1, 1000) so overflow and "gave up" stay out of scope
// Cover: offset is non-negative; page 1 starts at 0; the next page starts one pageSize later.
// Replace the placeholder.

class ExercisesDay10Suite extends ScalaCheckSuite:
  property("page 1 starts at 0"):
    forAll(Gen.choose(1, 1000)) { pageSize =>
      pageOffset(1, pageSize) == 0
    }

  property("offset is non-negative"):
    forAll(Gen.choose(1, 1000), Gen.choose(1, 1000)) { (page, pageSize) =>
      val offset = pageOffset(page, pageSize)
      offset >= 0
    }

  property("the next page starts one pageSize later"):
    forAll(Gen.choose(1, 1000), Gen.choose(1, 1000)) { (page, pageSize) =>
      pageOffset(page + 1, pageSize) == pageOffset(page, pageSize) + pageSize
    }
