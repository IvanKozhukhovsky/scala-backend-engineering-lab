package day23

import cats.effect.unsafe.implicits.global
import cats.syntax.all.*
import org.typelevel.doobie.implicits.*

import munit.FunSuite

class ExercisesDay23Suite extends FunSuite:
  test("migrateDesk applies two versioned migrations"):
    val n = migrateDesk(quotesUrl("day23-ex-mig")).unsafeRunSync()
    assertEquals(n, 2)

  test("second migrateDesk on the same catalog is a no-op"):
    val url = quotesUrl("day23-ex-idemp")
    assertEquals(migrateDesk(url).unsafeRunSync(), 2)
    assertEquals(migrateDesk(url).unsafeRunSync(), 0)

  test("building migrateDesk does not create the fill table"):
    val xa = h2Xa("day23-ex-lazy")
    val io = migrateDesk(quotesUrl("day23-ex-lazy"))
    intercept[Exception] {
      findFill("EUR").option.transact(xa).unsafeRunSync()
    }
    io.unsafeRunSync()
    assertEquals(findFill("EUR").option.transact(xa).unsafeRunSync(), None)

  test("bookFills then find returns both rows"):
    val name = "day23-ex-book"
    val xa = h2Xa(name)
    val eur = FillRow("EUR", 10, 1080)
    val gbp = FillRow("GBP", 2, 254)
    val found = (migrateDesk(quotesUrl(name)) *>
      bookFills(eur, gbp).transact(xa) *>
      (findFill("EUR").option.transact(xa), findFill("GBP").option.transact(xa)).tupled)
      .unsafeRunSync()
    assertEquals(found, (Some(eur), Some(gbp)))

  test("duplicate second fill in one transact rolls back the first"):
    val name = "day23-ex-rollback"
    val xa = h2Xa(name)
    migrateDesk(quotesUrl(name)).unsafeRunSync()
    intercept[Exception] {
      bookFills(FillRow("EUR", 10, 1080), FillRow("EUR", 1, 100)).transact(xa).unsafeRunSync()
    }
    assertEquals(findFill("EUR").option.transact(xa).unsafeRunSync(), None)

  test("rateOf stays a pure Map lookup"):
    assertEquals(rateOf("EUR"), Some(108))
    assertEquals(rateOf("USD"), None)
