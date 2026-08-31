package day22

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.syntax.all.*
import org.typelevel.doobie.implicits.*

import munit.FunSuite

class ExercisesDay22Suite extends FunSuite:
  private def withFill[A](name: String)(run: FillRepository => IO[A]): A =
    val xa = fillH2Xa(name)
    val repo = FillRepository(xa)
    val ddl =
      sql"""CREATE TABLE fill (
        code  VARCHAR NOT NULL PRIMARY KEY,
        qty   INTEGER NOT NULL,
        cents INTEGER NOT NULL
      )""".update.run
    (ddl.transact(xa) *> run(repo)).unsafeRunSync()

  test("fill repository insert then find returns the row"):
    val row = FillRow("EUR", 10, 1080)
    val found = withFill("day22-ex-find") { repo =>
      repo.insert(row) *> repo.find("EUR")
    }
    assertEquals(found, Some(row))

  test("fill repository find of a missing code is None"):
    val found = withFill("day22-ex-miss")(_.find("JPY"))
    assertEquals(found, None)

  test("insert then find sees the row across two transacts"):
    val row = FillRow("GBP", 2, 254)
    val found = withFill("day22-ex-round") { repo =>
      repo.insert(row) *> repo.find("GBP")
    }
    assertEquals(found, Some(row))

  test("hostile code round-trips as data, not as SQL"):
    val row = FillRow("x'; drop table fill; --", 1, 100)
    val found = withFill("day22-ex-hostile") { repo =>
      repo.insert(row) *> repo.find(row.code)
    }
    assertEquals(found, Some(row))

  test("rateOf stays a pure Map lookup"):
    assertEquals(rateOf("EUR"), Some(108))
    assertEquals(rateOf("USD"), None)
