package day17

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.http4s.Method
import org.http4s.Request
import org.http4s.Status
import org.http4s.implicits.*

import munit.FunSuite

class ExercisesDay17Suite extends FunSuite:
  private def statusOf(req: Request[IO]): Status =
    quoteApp.run(req).unsafeRunSync().status

  private def bodyOf(req: Request[IO]): String =
    quoteApp.run(req).flatMap(_.as[String]).unsafeRunSync()

  test("GET /quotes/EUR returns the cents as text"):
    val req = Request[IO](Method.GET, uri"/quotes/EUR")
    assertEquals(statusOf(req), Status.Ok)
    assertEquals(bodyOf(req), "108")

  test("GET /quotes/JPY is 404 when the code is unknown"):
    val req = Request[IO](Method.GET, uri"/quotes/JPY")
    assertEquals(statusOf(req), Status.NotFound)

  test("GET /quotes lists known codes sorted"):
    val req = Request[IO](Method.GET, uri"/quotes")
    assertEquals(statusOf(req), Status.Ok)
    assertEquals(bodyOf(req), "EUR,GBP")

  test("POST /quotes/EUR is 405"):
    val req = Request[IO](Method.POST, uri"/quotes/EUR")
    assertEquals(statusOf(req), Status.MethodNotAllowed)

  test("DELETE /quotes is 405"):
    val req = Request[IO](Method.DELETE, uri"/quotes")
    assertEquals(statusOf(req), Status.MethodNotAllowed)

  test("unknown path is 404"):
    val req = Request[IO](Method.GET, uri"/nope")
    assertEquals(statusOf(req), Status.NotFound)
