package day19

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import ciris.*
import org.http4s.MediaType
import org.http4s.Method
import org.http4s.Request
import org.http4s.Status
import org.http4s.headers.`Content-Type`
import org.http4s.implicits.*
import org.typelevel.log4cats.noop.NoOpLogger
import org.typelevel.log4cats.testing.StructuredTestingLogger

import munit.FunSuite

class Day19Suite extends FunSuite:
  private val config = QuoteConfig("quotes", 1, Secret("test-token"))
  private val silent = NoOpLogger.impl[IO]

  private def jsonPost(body: String): Request[IO] =
    Request[IO](Method.POST, uri"/convert")
      .withEntity(body)
      .withContentType(`Content-Type`(MediaType.application.json))

  private def statusOf(req: Request[IO], cfg: QuoteConfig = config): Status =
    convertApp(cfg, silent).run(req).unsafeRunSync().status

  private def asApiError(req: Request[IO], cfg: QuoteConfig = config): ApiError =
    convertApp(cfg, silent).run(req).flatMap(_.as[ApiError]).unsafeRunSync()

  test("Secret.toString does not contain the raw token"):
    val secret = Secret("super-secret-token")
    assertEquals(secret.value, "super-secret-token")
    assert(!secret.toString.contains("super-secret-token"))

  test("a missing Ciris key is Left"):
    val missing = prop("day19.ciris.missing.key.never.set.zzz").as[Int]
    assert(missing.attempt[IO].unsafeRunSync().isLeft)

  test("GET /health returns the configured name and not the token"):
    val cfg = QuoteConfig("desk", 1, Secret("test-token"))
    val req = Request[IO](Method.GET, uri"/health")
    val res = convertApp(cfg, silent).run(req).unsafeRunSync()
    assertEquals(res.status, Status.Ok)
    assertEquals(
      convertApp(cfg, silent).run(req).flatMap(_.as[HealthOut]).unsafeRunSync(),
      HealthOut("desk")
    )
    val body = convertApp(cfg, silent).run(req).flatMap(_.as[String]).unsafeRunSync()
    assert(!body.contains("test-token"))

  test("GET /rates/EUR returns JSON cents"):
    val req = Request[IO](Method.GET, uri"/rates/EUR")
    assertEquals(statusOf(req), Status.Ok)
    assertEquals(
      convertApp(config, silent).run(req).flatMap(_.as[RateOut]).unsafeRunSync(),
      RateOut("EUR", 108)
    )

  test("GET /rates/JPY is 404 with not_found"):
    val req = Request[IO](Method.GET, uri"/rates/JPY")
    assertEquals(statusOf(req), Status.NotFound)
    assertEquals(asApiError(req), ApiError("not_found"))

  test("POST /convert respects minQty from config"):
    val cfg = QuoteConfig("quotes", 5, Secret("test-token"))
    val req = jsonPost("""{"code":"EUR","qty":4}""")
    assertEquals(statusOf(req, cfg), Status.UnprocessableContent)
    assertEquals(asApiError(req, cfg), ApiError("invalid_qty"))

  test("POST /convert multiplies rate by qty"):
    val req = jsonPost("""{"code":"EUR","qty":10}""")
    assertEquals(statusOf(req), Status.Ok)
    assertEquals(
      convertApp(config, silent).run(req).flatMap(_.as[ConvertOut]).unsafeRunSync(),
      ConvertOut("EUR", 1080)
    )

  test("GET /rates/EUR logs structured code and op"):
    val logger = StructuredTestingLogger.impl[IO]()
    val req = Request[IO](Method.GET, uri"/rates/EUR")
    convertApp(config, logger).run(req).unsafeRunSync()
    val infos = logger.logged.unsafeRunSync().collect { case m: StructuredTestingLogger.INFO =>
      m
    }
    assert(
      infos.exists(m =>
        m.message == "ok" && m.ctx.get("code").contains("EUR") && m.ctx
          .get("op")
          .contains(
            "get_rate"
          )
      )
    )

  test("convertAmount stays pure and uses minQty"):
    assertEquals(convertAmount("EUR", 10, 1), Right(ConvertOut("EUR", 1080)))
    assertEquals(convertAmount("EUR", 4, 5), Left(ConvertError.InvalidQty))
    assertEquals(convertAmount("JPY", 10, 1), Left(ConvertError.UnknownCode))

  test("rateOf stays pure"):
    assertEquals(rateOf("EUR"), Some(108))
    assertEquals(rateOf("USD"), None)
