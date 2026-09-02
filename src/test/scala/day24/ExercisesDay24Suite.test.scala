package day24

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.syntax.all.*
import org.testcontainers.postgresql.PostgreSQLContainer
import org.typelevel.doobie.*
import org.typelevel.doobie.implicits.*

import munit.FunSuite

class ExercisesDay24Suite extends FunSuite:
  test("deskPg is running during use and stopped after"):
    var ref: Option[PostgreSQLContainer] = None
    val running = deskPg
      .use { pg =>
        ref = Some(pg)
        IO.pure(pg.isRunning)
      }
      .unsafeRunSync()
    assertEquals(running, true)
    assertEquals(ref.map(_.isRunning), Some(false))

  test("live jdbc url is postgresql, not H2 and not jdbc:tc"):
    val url = withDeskPg { (url, _, _) => IO.pure(url) }.unsafeRunSync()
    assert(url.startsWith("jdbc:postgresql://"), url)
    assert(!url.startsWith("jdbc:h2"), url)
    assert(!url.startsWith("jdbc:tc:"), url)

  test("migrateDeskPg then bookFills returns both rows on live Postgres"):
    val xaOf = (url: String, user: String, password: String) =>
      Transactor.fromDriverManager[IO](
        driver = "org.postgresql.Driver",
        url = url,
        user = user,
        password = password,
        logHandler = None
      )
    val eur = FillRow("EUR", 10, 1080)
    val gbp = FillRow("GBP", 2, 254)
    val (n, found, version) = withDeskPg { (url, user, password) =>
      val xa = xaOf(url, user, password)
      for
        n <- migrateDeskPg(url, user, password)
        _ <- bookFills(eur, gbp).transact(xa)
        found <- (findFill("EUR").option.transact(xa), findFill("GBP").option.transact(xa)).tupled
        version <- sql"select version()".query[String].unique.transact(xa)
      yield (n, found, version)
    }.unsafeRunSync()
    assertEquals(n, 2)
    assertEquals(found, (Some(eur), Some(gbp)))
    assert(version.contains("PostgreSQL"), version)

  test("duplicate second fill in one transact rolls back the first"):
    val xaOf = (url: String, user: String, password: String) =>
      Transactor.fromDriverManager[IO](
        driver = "org.postgresql.Driver",
        url = url,
        user = user,
        password = password,
        logHandler = None
      )
    val found = withDeskPg { (url, user, password) =>
      val xa = xaOf(url, user, password)
      migrateDeskPg(url, user, password) *>
        bookFills(FillRow("EUR", 10, 1080), FillRow("EUR", 1, 100)).transact(xa).attempt *>
        findFill("EUR").option.transact(xa)
    }.unsafeRunSync()
    assertEquals(found, None)

  test("rateOf stays a pure Map lookup"):
    assertEquals(rateOf("EUR"), Some(108))
    assertEquals(rateOf("USD"), None)
