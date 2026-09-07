package day30

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.http4s.MediaType
import org.http4s.Method
import org.http4s.Request
import org.http4s.Status
import org.http4s.headers.Allow
import org.http4s.headers.`Content-Type`
import org.http4s.implicits.*

import munit.FunSuite

class ExercisesDay30Suite extends FunSuite:
  private val config = DeskConfig("desk", 1)
  private def app = deskApp(config)

  private def jsonPost(body: String): Request[IO] =
    Request[IO](Method.POST, uri"/orders")
      .withEntity(body)
      .withContentType(`Content-Type`(MediaType.application.json))

  private def statusOf(req: Request[IO]): Status =
    app.run(req).unsafeRunSync().status

  private def asApiError(req: Request[IO]): ApiError =
    app.run(req).flatMap(_.as[ApiError]).unsafeRunSync()

  private def allowOf(req: Request[IO]): Option[Set[Method]] =
    app.run(req).unsafeRunSync().headers.get[Allow].map(_.methods)

  test("orderOf multiplies a known rate"):
    assertEquals(orderOf("EUR", 10, 1), Right(OrderOut("EUR", 10, 1080)))

  test("orderOf trims then rejects a blank code"):
    assertEquals(orderOf("  ", 10, 1), Left(OrderError.InvalidCode))

  test("orderOf rejects qty below minQty"):
    assertEquals(orderOf("EUR", 0, 1), Left(OrderError.InvalidQty))

  test("orderOf rejects an unknown code"):
    assertEquals(orderOf("JPY", 10, 1), Left(OrderError.UnknownCode))

  test("GET /health returns the injected name"):
    val req = Request[IO](Method.GET, uri"/health")
    assertEquals(statusOf(req), Status.Ok)
    assertEquals(app.run(req).flatMap(_.as[HealthOut]).unsafeRunSync(), HealthOut("desk"))

  test("GET /orders/EUR returns JSON cents"):
    val req = Request[IO](Method.GET, uri"/orders/EUR")
    assertEquals(statusOf(req), Status.Ok)
    assertEquals(app.run(req).flatMap(_.as[RateOut]).unsafeRunSync(), RateOut("EUR", 108))

  test("GET /orders/JPY is 404 not_found"):
    val req = Request[IO](Method.GET, uri"/orders/JPY")
    assertEquals(statusOf(req), Status.NotFound)
    assertEquals(asApiError(req), ApiError("not_found"))

  test("POST /orders returns qty and cents"):
    val req = jsonPost("""{"code":"GBP","qty":2}""")
    assertEquals(statusOf(req), Status.Ok)
    assertEquals(
      app.run(req).flatMap(_.as[OrderOut]).unsafeRunSync(),
      OrderOut("GBP", 2, 254)
    )

  test("POST /orders blank code is 422 invalid_code"):
    val req = jsonPost("""{"code":"  ","qty":2}""")
    assertEquals(statusOf(req), Status.UnprocessableContent)
    assertEquals(asApiError(req), ApiError("invalid_code"))

  test("POST /orders unknown code is 422 unknown_code"):
    val req = jsonPost("""{"code":"JPY","qty":2}""")
    assertEquals(statusOf(req), Status.UnprocessableContent)
    assertEquals(asApiError(req), ApiError("unknown_code"))

  test("POST /orders with broken JSON is 400 malformed_json"):
    val req = jsonPost("{not json")
    assertEquals(statusOf(req), Status.BadRequest)
    assertEquals(asApiError(req), ApiError("malformed_json"))

  test("POST /orders missing qty is 422 invalid_json"):
    val req = jsonPost("""{"code":"EUR"}""")
    assertEquals(statusOf(req), Status.UnprocessableContent)
    assertEquals(asApiError(req), ApiError("invalid_json"))

  test("POST /health is 405 with Allow GET"):
    val req = Request[IO](Method.POST, uri"/health")
    assertEquals(statusOf(req), Status.MethodNotAllowed)
    assertEquals(allowOf(req), Some(Set(Method.GET)))

  test("GET /orders is 405 with Allow POST"):
    val req = Request[IO](Method.GET, uri"/orders")
    assertEquals(statusOf(req), Status.MethodNotAllowed)
    assertEquals(allowOf(req), Some(Set(Method.POST)))

  test("rateOf stays a pure Map lookup"):
    assertEquals(rateOf("GBP"), Some(127))
    assertEquals(rateOf("JPY"), None)
