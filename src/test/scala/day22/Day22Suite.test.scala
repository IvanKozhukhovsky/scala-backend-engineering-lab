package day22

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.syntax.all.*
import org.typelevel.doobie.implicits.*

import munit.FunSuite

class Day22Suite extends FunSuite:
  private def withRepo[A](name: String)(use: RateRepository => IO[A]): A =
    val xa = h2Xa(name)
    val repo = RateRepository(xa)
    (seedRates.transact(xa) *> use(repo)).unsafeRunSync()

  test("postgresHikariConfig names the Postgres driver and jdbc url"):
    val cfg = postgresHikariConfig(
      "jdbc:postgresql://127.0.0.1:5432/quotes",
      "quotes",
      "secret"
    )
    assertEquals(cfg.getDriverClassName, "org.postgresql.Driver")
    assertEquals(cfg.getJdbcUrl, "jdbc:postgresql://127.0.0.1:5432/quotes")
    assertEquals(cfg.getUsername, "quotes")

  test("rateOf stays a pure Map lookup"):
    assertEquals(rateOf("EUR"), Some(108))
    assertEquals(rateOf("USD"), None)

  test("findRate SQL still uses a placeholder"):
    val sql = findRate("EUR").sql
    assert(sql.contains("?"))
    assert(!sql.contains("EUR"))

  test("repository find after seed returns the EUR row"):
    val row = withRepo("day22-find")(_.find("EUR"))
    assertEquals(row, Some(RateRow("EUR", 108)))

  test("repository find of a missing code is None"):
    val row = withRepo("day22-miss")(_.find("JPY"))
    assertEquals(row, None)

  test("insert then find sees the row across two transacts"):
    val row = RateRow("USD", 100)
    val found = withRepo("day22-round") { repo =>
      repo.insert(row) *> repo.find("USD")
    }
    assertEquals(found, Some(row))

  test("building h2Xa does not create the rate table"):
    val xa = h2Xa("day22-noddl")
    intercept[Exception] {
      findRate("EUR").option.transact(xa).unsafeRunSync()
    }
