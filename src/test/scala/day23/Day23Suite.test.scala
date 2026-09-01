package day23

import cats.effect.unsafe.implicits.global
import cats.syntax.all.*
import org.typelevel.doobie.implicits.*

import munit.FunSuite

class Day23Suite extends FunSuite:
  test("postgresFlyway load records locations without opening Postgres"):
    val flyway = postgresFlyway(
      "jdbc:postgresql://127.0.0.1:5432/quotes",
      "quotes",
      "secret"
    )
    val locations = flyway.getConfiguration.getLocations.map(_.toString)
    assert(locations.exists(_.contains("db/migration")))
    assert(flyway.getConfiguration.getDataSource != null)

  test("rateOf stays a pure Map lookup"):
    assertEquals(rateOf("EUR"), Some(108))
    assertEquals(rateOf("USD"), None)

  test("findRate SQL still uses a placeholder"):
    val sql = findRate("EUR").sql
    assert(sql.contains("?"))
    assert(!sql.contains("EUR"))

  test("building migrateQuotes does not create the rate table"):
    val xa = h2Xa("day23-lazy")
    val io = migrateQuotes(quotesUrl("day23-lazy"))
    intercept[Exception] {
      findRate("EUR").option.transact(xa).unsafeRunSync()
    }
    assertEquals(io.unsafeRunSync(), 2)
    assertEquals(findRate("EUR").option.transact(xa).unsafeRunSync(), None)

  test("second migrateQuotes on the same catalog is a no-op"):
    val url = quotesUrl("day23-idemp")
    assertEquals(migrateQuotes(url).unsafeRunSync(), 2)
    assertEquals(migrateQuotes(url).unsafeRunSync(), 0)

  test("seedRates then repository find returns the EUR row"):
    val name = "day23-find"
    val xa = h2Xa(name)
    val row =
      (migrateQuotes(quotesUrl(name)) *> seedRates.transact(xa) *> RateRepository(xa).find("EUR"))
        .unsafeRunSync()
    assertEquals(row, Some(RateRow("EUR", 108)))

  test("duplicate second insert in one transact rolls back the first"):
    val name = "day23-rollback"
    val xa = h2Xa(name)
    migrateQuotes(quotesUrl(name)).unsafeRunSync()
    intercept[Exception] {
      bookRates(RateRow("EUR", 108), RateRow("EUR", 100)).transact(xa).unsafeRunSync()
    }
    assertEquals(findRate("EUR").option.transact(xa).unsafeRunSync(), None)

  test("two transacts leave the first insert after the second fails"):
    val name = "day23-two-tx"
    val xa = h2Xa(name)
    migrateQuotes(quotesUrl(name)).unsafeRunSync()
    insertRate(RateRow("EUR", 108)).run.transact(xa).unsafeRunSync()
    intercept[Exception] {
      insertRate(RateRow("EUR", 100)).run.transact(xa).unsafeRunSync()
    }
    assertEquals(findRate("EUR").option.transact(xa).unsafeRunSync(), Some(RateRow("EUR", 108)))
