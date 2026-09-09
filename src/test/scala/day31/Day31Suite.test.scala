package day31

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.http4s.HttpApp
import org.http4s.MediaType
import org.http4s.Method
import org.http4s.Request
import org.http4s.Status
import org.http4s.headers.`Content-Type`
import org.http4s.implicits.*
import org.typelevel.doobie.implicits.*

import java.nio.file.Files
import java.nio.file.Path

import day29.adrOf
import day30.ApiError
import day30.ConvertError
import day30.ConvertOut
import day30.HealthOut
import day30.QuotesConfig
import day30.RateOut
import day30.given
import day30.rateOf

import munit.FunSuite

class Day31Suite extends FunSuite:
  private val config = QuotesConfig("quotes", 1)
  private val catalog = quotesApp(config, memoryLookup(Map("EUR" -> 99, "GBP" -> 127)))
  private val down = quotesApp(config, unavailableLookup)

  private def jsonPost(body: String): Request[IO] =
    Request[IO](Method.POST, uri"/convert")
      .withEntity(body)
      .withContentType(`Content-Type`(MediaType.application.json))

  private def statusOf(app: HttpApp[IO], req: Request[IO]): Status =
    app.run(req).unsafeRunSync().status

  private def asApiError(app: HttpApp[IO], req: Request[IO]): ApiError =
    app.run(req).flatMap(_.as[ApiError]).unsafeRunSync()

  test("rateOf stays a pure Map lookup"):
    assertEquals(rateOf("EUR"), Some(108))
    assertEquals(rateOf("JPY"), None)

  test("convertAmount uses the passed cents, not rateOf"):
    assertEquals(convertAmount("EUR", 10, 1, Some(99)), Right(ConvertOut("EUR", 990)))
    assertEquals(convertAmount("EUR", 0, 1, Some(108)), Left(ConvertError.InvalidQty))
    assertEquals(convertAmount("JPY", 10, 1, None), Left(ConvertError.UnknownCode))

  test("GET /rates/EUR uses the injected lookup, not rateOf"):
    val req = Request[IO](Method.GET, uri"/rates/EUR")
    assertEquals(statusOf(catalog, req), Status.Ok)
    assertEquals(catalog.run(req).flatMap(_.as[RateOut]).unsafeRunSync(), RateOut("EUR", 99))
    assertEquals(rateOf("EUR"), Some(108))

  test("GET /rates/JPY is 404 with not_found"):
    val req = Request[IO](Method.GET, uri"/rates/JPY")
    assertEquals(statusOf(catalog, req), Status.NotFound)
    assertEquals(asApiError(catalog, req), ApiError("not_found"))

  test("GET /rates/EUR when lookup fails is 503 unavailable"):
    val req = Request[IO](Method.GET, uri"/rates/EUR")
    assertEquals(statusOf(down, req), Status.ServiceUnavailable)
    assertEquals(asApiError(down, req), ApiError("unavailable"))

  test("POST /convert uses injected cents"):
    val req = jsonPost("""{"code":"EUR","qty":10}""")
    assertEquals(statusOf(catalog, req), Status.Ok)
    assertEquals(
      catalog.run(req).flatMap(_.as[ConvertOut]).unsafeRunSync(),
      ConvertOut("EUR", 990)
    )

  test("POST /convert unknown code is 422 unknown_code"):
    val req = jsonPost("""{"code":"JPY","qty":10}""")
    assertEquals(statusOf(catalog, req), Status.UnprocessableContent)
    assertEquals(asApiError(catalog, req), ApiError("unknown_code"))

  test("POST /convert when lookup fails is 503 unavailable"):
    val req = jsonPost("""{"code":"EUR","qty":10}""")
    assertEquals(statusOf(down, req), Status.ServiceUnavailable)
    assertEquals(asApiError(down, req), ApiError("unavailable"))

  test("GET /health does not call lookup"):
    val req = Request[IO](Method.GET, uri"/health")
    assertEquals(statusOf(down, req), Status.Ok)
    assertEquals(down.run(req).flatMap(_.as[HealthOut]).unsafeRunSync(), HealthOut("quotes"))

  test("H2 migrate and seed then GET /rates/EUR is 108"):
    val name = "day31-h2"
    val xa = day23.h2Xa(name)
    val app = quotesApp(config, quotesLookup(xa))
    val req = Request[IO](Method.GET, uri"/rates/EUR")
    val out =
      (day23.migrateQuotes(day23.quotesUrl(name)) *>
        day23.seedRates.transact(xa) *>
        app.run(req).flatMap(_.as[RateOut])).unsafeRunSync()
    assertEquals(out, RateOut("EUR", 108))
    assertEquals(statusOf(app, Request[IO](Method.GET, uri"/rates/JPY")), Status.NotFound)

  test("live Postgres GET /rates/EUR is 108"):
    val out = day24
      .withQuotesPg { (url, user, password) =>
        val xa = day24.postgresXa(url, user, password)
        val app = quotesApp(config, quotesLookup(xa))
        val req = Request[IO](Method.GET, uri"/rates/EUR")
        day24.migrateQuotesPg(url, user, password) *>
          day23.seedRates.transact(xa) *>
          app.run(req).flatMap(_.as[RateOut])
      }
      .unsafeRunSync()
    assertEquals(out, RateOut("EUR", 108))

  test("adrOf is the ADR 3 twin"):
    val file = Files.readString(Path.of("docs", "adr", "0003-persistence-failure-is-503.md"))
    assertEquals(adrOf(quotesAdr0003), file)
    assertEquals(quotesAdr0003.number, 3)
    assert(file.contains("503"), file)
    assert(!file.contains("rateOf"), file)
