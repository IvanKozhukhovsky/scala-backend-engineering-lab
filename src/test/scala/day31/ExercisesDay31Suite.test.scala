package day31

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.http4s.MediaType
import org.http4s.Method
import org.http4s.Request
import org.http4s.Status
import org.http4s.headers.Allow
import org.http4s.headers.`Content-Type`
import org.http4s.implicits.*
import org.typelevel.doobie.implicits.*

import day30.ApiError
import day30.DeskConfig
import day30.HealthOut
import day30.OrderError
import day30.OrderOut
import day30.RateOut
import day30.given
import day30.rateOf

import munit.FunSuite

class ExercisesDay31Suite extends FunSuite:
  private val config = DeskConfig("desk", 1)
  private def app = deskApp(config, memoryLookup(Map("EUR" -> 99, "GBP" -> 127)))
  private def down = deskApp(config, unavailableLookup)

  private def jsonPost(body: String): Request[IO] =
    Request[IO](Method.POST, uri"/orders")
      .withEntity(body)
      .withContentType(`Content-Type`(MediaType.application.json))

  private def statusOf(target: org.http4s.HttpApp[IO], req: Request[IO]): Status =
    target.run(req).unsafeRunSync().status

  private def asApiError(target: org.http4s.HttpApp[IO], req: Request[IO]): ApiError =
    target.run(req).flatMap(_.as[ApiError]).unsafeRunSync()

  private def allowOf(target: org.http4s.HttpApp[IO], req: Request[IO]): Option[Set[Method]] =
    target.run(req).unsafeRunSync().headers.get[Allow].map(_.methods)

  test("orderFrom uses the passed cents, not rateOf"):
    assertEquals(orderFrom("EUR", 10, 1, Some(99)), Right(OrderOut("EUR", 10, 990)))
    assertEquals(rateOf("EUR"), Some(108))

  test("orderFrom trims then rejects a blank code"):
    assertEquals(orderFrom("  ", 10, 1, Some(99)), Left(OrderError.InvalidCode))

  test("orderFrom rejects qty below minQty"):
    assertEquals(orderFrom("EUR", 0, 1, Some(99)), Left(OrderError.InvalidQty))

  test("orderFrom rejects a missing rate"):
    assertEquals(orderFrom("JPY", 10, 1, None), Left(OrderError.UnknownCode))

  test("GET /health returns the injected name without lookup"):
    val req = Request[IO](Method.GET, uri"/health")
    assertEquals(statusOf(down, req), Status.Ok)
    assertEquals(down.run(req).flatMap(_.as[HealthOut]).unsafeRunSync(), HealthOut("desk"))

  test("GET /orders/EUR uses the injected lookup, not rateOf"):
    val req = Request[IO](Method.GET, uri"/orders/EUR")
    assertEquals(statusOf(app, req), Status.Ok)
    assertEquals(app.run(req).flatMap(_.as[RateOut]).unsafeRunSync(), RateOut("EUR", 99))

  test("GET /orders/JPY is 404 not_found"):
    val req = Request[IO](Method.GET, uri"/orders/JPY")
    assertEquals(statusOf(app, req), Status.NotFound)
    assertEquals(asApiError(app, req), ApiError("not_found"))

  test("GET /orders/EUR when lookup fails is 503 unavailable"):
    val req = Request[IO](Method.GET, uri"/orders/EUR")
    assertEquals(statusOf(down, req), Status.ServiceUnavailable)
    assertEquals(asApiError(down, req), ApiError("unavailable"))

  test("POST /orders uses injected cents"):
    val req = jsonPost("""{"code":"EUR","qty":2}""")
    assertEquals(statusOf(app, req), Status.Ok)
    assertEquals(
      app.run(req).flatMap(_.as[OrderOut]).unsafeRunSync(),
      OrderOut("EUR", 2, 198)
    )

  test("POST /orders unknown code is 422 unknown_code"):
    val req = jsonPost("""{"code":"JPY","qty":2}""")
    assertEquals(statusOf(app, req), Status.UnprocessableContent)
    assertEquals(asApiError(app, req), ApiError("unknown_code"))

  test("POST /orders when lookup fails is 503 unavailable"):
    val req = jsonPost("""{"code":"EUR","qty":2}""")
    assertEquals(statusOf(down, req), Status.ServiceUnavailable)
    assertEquals(asApiError(down, req), ApiError("unavailable"))

  test("POST /orders with broken JSON is 400 malformed_json"):
    val req = jsonPost("{not json")
    assertEquals(statusOf(app, req), Status.BadRequest)
    assertEquals(asApiError(app, req), ApiError("malformed_json"))

  test("POST /orders missing qty is 422 invalid_json"):
    val req = jsonPost("""{"code":"EUR"}""")
    assertEquals(statusOf(app, req), Status.UnprocessableContent)
    assertEquals(asApiError(app, req), ApiError("invalid_json"))

  test("POST /health is 405 with Allow GET"):
    val req = Request[IO](Method.POST, uri"/health")
    assertEquals(statusOf(app, req), Status.MethodNotAllowed)
    assertEquals(allowOf(app, req), Some(Set(Method.GET)))

  test("H2 migrate and seed then GET /orders/EUR is 108"):
    val name = "day31-ex-h2"
    val xa = day23.h2Xa(name)
    val wired = deskApp(config, deskLookup(xa))
    val req = Request[IO](Method.GET, uri"/orders/EUR")
    val out =
      (day23.migrateQuotes(day23.quotesUrl(name)) *>
        day23.seedRates.transact(xa) *>
        wired.run(req).flatMap(_.as[RateOut])).unsafeRunSync()
    assertEquals(out, RateOut("EUR", 108))
