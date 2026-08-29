package day19

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.http4s.MediaType
import org.http4s.Method
import org.http4s.Request
import org.http4s.Status
import org.http4s.headers.`Content-Type`
import org.http4s.implicits.*
import org.typelevel.log4cats.noop.NoOpLogger
import org.typelevel.log4cats.testing.StructuredTestingLogger

import munit.FunSuite

class ExercisesDay19Suite extends FunSuite:
  private val config = OrderConfig("lab", 10)
  private val silent = NoOpLogger.impl[IO]

  private def jsonPost(body: String): Request[IO] =
    Request[IO](Method.POST, uri"/orders")
      .withEntity(body)
      .withContentType(`Content-Type`(MediaType.application.json))

  private def statusOf(req: Request[IO], cfg: OrderConfig = config): Status =
    orderApp(cfg, silent).run(req).unsafeRunSync().status

  private def asApiError(req: Request[IO], cfg: OrderConfig = config): ApiError =
    orderApp(cfg, silent).run(req).flatMap(_.as[ApiError]).unsafeRunSync()

  test("orderConfig loads a shop name and a positive maxQty"):
    val cfg = orderConfig.load[IO].unsafeRunSync()
    assert(cfg.shopName.nonEmpty)
    assert(cfg.maxQty >= 1)

  test("orderOf multiplies a known rate"):
    assertEquals(orderOf("EUR", 10, 20), Right(OrderOut("EUR", 10, 1080)))

  test("orderOf rejects qty < 1"):
    assertEquals(orderOf("EUR", 0, 20), Left(OrderError.InvalidQty))

  test("orderOf rejects qty above maxQty"):
    assertEquals(orderOf("EUR", 21, 20), Left(OrderError.ExcessQty))

  test("orderOf rejects an unknown code"):
    assertEquals(orderOf("JPY", 2, 20), Left(OrderError.UnknownCode))

  test("GET /health returns the configured shop name"):
    val cfg = OrderConfig("desk", 10)
    val req = Request[IO](Method.GET, uri"/health")
    assertEquals(statusOf(req, cfg), Status.Ok)
    assertEquals(
      orderApp(cfg, silent).run(req).flatMap(_.as[HealthOut]).unsafeRunSync(),
      HealthOut("desk")
    )

  test("GET /orders/EUR returns JSON cents"):
    val req = Request[IO](Method.GET, uri"/orders/EUR")
    assertEquals(statusOf(req), Status.Ok)
    assertEquals(
      orderApp(config, silent).run(req).flatMap(_.as[RateOut]).unsafeRunSync(),
      RateOut("EUR", 108)
    )

  test("GET /orders/JPY is 404 not_found"):
    val req = Request[IO](Method.GET, uri"/orders/JPY")
    assertEquals(statusOf(req), Status.NotFound)
    assertEquals(asApiError(req), ApiError("not_found"))

  test("POST /orders returns qty and cents"):
    val req = jsonPost("""{"code":"GBP","qty":2}""")
    assertEquals(statusOf(req), Status.Ok)
    assertEquals(
      orderApp(config, silent).run(req).flatMap(_.as[OrderOut]).unsafeRunSync(),
      OrderOut("GBP", 2, 254)
    )

  test("POST /orders qty above maxQty is 422 excess_qty"):
    val req = jsonPost("""{"code":"EUR","qty":11}""")
    assertEquals(statusOf(req), Status.UnprocessableContent)
    assertEquals(asApiError(req), ApiError("excess_qty"))

  test("POST /orders with broken JSON is 400 malformed_json"):
    val req = jsonPost("{not json")
    assertEquals(statusOf(req), Status.BadRequest)
    assertEquals(asApiError(req), ApiError("malformed_json"))

  test("POST /orders missing qty is 422 invalid_json"):
    val req = jsonPost("""{"code":"EUR"}""")
    assertEquals(statusOf(req), Status.UnprocessableContent)
    assertEquals(asApiError(req), ApiError("invalid_json"))

  test("DELETE /orders is 405"):
    val req = Request[IO](Method.DELETE, uri"/orders")
    assertEquals(statusOf(req), Status.MethodNotAllowed)

  test("POST /orders success logs structured code and op"):
    val logger = StructuredTestingLogger.impl[IO]()
    val req = jsonPost("""{"code":"EUR","qty":2}""")
    orderApp(config, logger).run(req).unsafeRunSync()
    val infos = logger.logged.unsafeRunSync().collect { case m: StructuredTestingLogger.INFO =>
      m
    }
    assert(
      infos.exists(m =>
        m.message == "ok" && m.ctx.get("code").contains("EUR") && m.ctx.get("op").contains("order")
      )
    )
