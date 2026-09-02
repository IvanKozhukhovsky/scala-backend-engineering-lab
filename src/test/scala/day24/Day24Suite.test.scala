package day24

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.syntax.all.*
import org.testcontainers.postgresql.PostgreSQLContainer
import org.typelevel.doobie.implicits.*

import munit.FunSuite

class Day24Suite extends FunSuite:
  test("quotesContainer is not running until start"):
    val pg = quotesContainer
    assert(!pg.isRunning)

  test("rateOf stays a pure Map lookup"):
    assertEquals(rateOf("EUR"), Some(108))
    assertEquals(rateOf("USD"), None)

  test("findRate SQL still uses a placeholder"):
    val sql = findRate("EUR").sql
    assert(sql.contains("?"))
    assert(!sql.contains("EUR"))

  test("live jdbc url is postgresql, not H2 and not jdbc:tc"):
    val url = withQuotesPg { (url, _, _) => IO.pure(url) }.unsafeRunSync()
    assert(url.startsWith("jdbc:postgresql://"), url)
    assert(!url.startsWith("jdbc:h2"), url)
    assert(!url.startsWith("jdbc:tc:"), url)

  test("Flyway then repository find returns EUR on live Postgres"):
    val (n, row, version) = withQuotesPg { (url, user, password) =>
      val xa = postgresXa(url, user, password)
      for
        n <- migrateQuotesPg(url, user, password)
        _ <- seedRates.transact(xa)
        row <- RateRepository(xa).find("EUR")
        version <- postgresVersion.unique.transact(xa)
      yield (n, row, version)
    }.unsafeRunSync()
    assertEquals(n, 2)
    assertEquals(row, Some(RateRow("EUR", 108)))
    assert(version.contains("PostgreSQL"), version)

  test("two transacts leave the first insert after the second fails"):
    val found = withQuotesPg { (url, user, password) =>
      val xa = postgresXa(url, user, password)
      migrateQuotesPg(url, user, password) *>
        insertRate(RateRow("EUR", 108)).run.transact(xa) *>
        insertRate(RateRow("EUR", 100)).run.transact(xa).attempt *>
        findRate("EUR").option.transact(xa)
    }.unsafeRunSync()
    assertEquals(found, Some(RateRow("EUR", 108)))

  test("quotesPg still stops the container when the inner IO fails"):
    var ref: Option[PostgreSQLContainer] = None
    intercept[RuntimeException] {
      quotesPg
        .use { pg =>
          ref = Some(pg)
          IO.raiseError(new RuntimeException("boom"))
        }
        .unsafeRunSync()
    }
    assertEquals(ref.map(_.isRunning), Some(false))
