package day18

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.http4s.MediaType
import org.http4s.Method
import org.http4s.Request
import org.http4s.Status
import org.http4s.headers.`Content-Type`
import org.http4s.implicits.*

import munit.FunSuite

class Day18Suite extends FunSuite:
  private def jsonPost(body: String): Request[IO] =
    Request[IO](Method.POST, uri"/convert")
      .withEntity(body)
      .withContentType(`Content-Type`(MediaType.application.json))

  private def statusOf(req: Request[IO]): Status =
    runRequest(req).unsafeRunSync().status

  private def asApiError(req: Request[IO]): ApiError =
    runRequest(req).flatMap(_.as[ApiError]).unsafeRunSync()

  test("GET /rates/EUR returns JSON cents"):
    val req = Request[IO](Method.GET, uri"/rates/EUR")
    assertEquals(statusOf(req), Status.Ok)
    assertEquals(
      runRequest(req).flatMap(_.as[RateOut]).unsafeRunSync(),
      RateOut("EUR", 108)
    )

  test("GET /rates/JPY is 404 with not_found"):
    val req = Request[IO](Method.GET, uri"/rates/JPY")
    assertEquals(statusOf(req), Status.NotFound)
    assertEquals(asApiError(req), ApiError("not_found"))

  test("POST /convert multiplies rate by qty"):
    val req = jsonPost("""{"code":"EUR","qty":10}""")
    assertEquals(statusOf(req), Status.Ok)
    assertEquals(
      runRequest(req).flatMap(_.as[ConvertOut]).unsafeRunSync(),
      ConvertOut("EUR", 1080)
    )

  test("POST /convert with qty < 1 is 422 invalid_qty"):
    val req = jsonPost("""{"code":"EUR","qty":0}""")
    assertEquals(statusOf(req), Status.UnprocessableContent)
    assertEquals(asApiError(req), ApiError("invalid_qty"))

  test("POST /convert unknown code is 422 unknown_code"):
    val req = jsonPost("""{"code":"JPY","qty":10}""")
    assertEquals(statusOf(req), Status.UnprocessableContent)
    assertEquals(asApiError(req), ApiError("unknown_code"))

  test("POST /convert with broken JSON is 400 malformed_json"):
    val req = jsonPost("{not json")
    assertEquals(statusOf(req), Status.BadRequest)
    assertEquals(asApiError(req), ApiError("malformed_json"))

  test("POST /convert missing qty is 422 invalid_json"):
    val req = jsonPost("""{"code":"EUR"}""")
    assertEquals(statusOf(req), Status.UnprocessableContent)
    assertEquals(asApiError(req), ApiError("invalid_json"))

  test("GET /convert is 405"):
    val req = Request[IO](Method.GET, uri"/convert")
    assertEquals(statusOf(req), Status.MethodNotAllowed)

  test("convertAmount stays pure"):
    assertEquals(convertAmount("EUR", 10), Right(ConvertOut("EUR", 1080)))
    assertEquals(convertAmount("EUR", 0), Left(ConvertError.InvalidQty))
    assertEquals(convertAmount("JPY", 10), Left(ConvertError.UnknownCode))

  test("rateOf stays pure"):
    assertEquals(rateOf("EUR"), Some(108))
    assertEquals(rateOf("USD"), None)
