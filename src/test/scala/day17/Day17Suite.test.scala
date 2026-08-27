package day17

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.http4s.Method
import org.http4s.Request
import org.http4s.Status
import org.http4s.implicits.*

import munit.FunSuite

class Day17Suite extends FunSuite:
  private def statusOf(req: Request[IO]): Status =
    runRequest(req).unsafeRunSync().status

  private def bodyOf(req: Request[IO]): String =
    runRequest(req).flatMap(_.as[String]).unsafeRunSync()

  test("GET /rates/EUR returns the cents as text"):
    val req = Request[IO](Method.GET, uri"/rates/EUR")
    assertEquals(statusOf(req), Status.Ok)
    assertEquals(bodyOf(req), "108")

  test("GET /rates/JPY is 404 when the code is unknown"):
    val req = Request[IO](Method.GET, uri"/rates/JPY")
    assertEquals(statusOf(req), Status.NotFound)

  test("GET /health returns ok"):
    val req = Request[IO](Method.GET, uri"/health")
    assertEquals(statusOf(req), Status.Ok)
    assertEquals(bodyOf(req), "ok")

  test("POST /rates/EUR is 405 because only GET is allowed"):
    val req = Request[IO](Method.POST, uri"/rates/EUR")
    assertEquals(statusOf(req), Status.MethodNotAllowed)

  test("unknown path is 404"):
    val req = Request[IO](Method.GET, uri"/missing")
    assertEquals(statusOf(req), Status.NotFound)

  test("rateOf stays pure"):
    assertEquals(rateOf("EUR"), Some(108))
    assertEquals(rateOf("USD"), None)
