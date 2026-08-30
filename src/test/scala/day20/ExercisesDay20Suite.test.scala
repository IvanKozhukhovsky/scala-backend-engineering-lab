package day20

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.http4s.Method
import org.http4s.Request
import org.http4s.Status
import org.http4s.client.UnexpectedStatus
import org.http4s.headers.Date
import org.http4s.implicits.*

import munit.FunSuite

class ExercisesDay20Suite extends FunSuite:
  private val app = convertApp(DeskConfig("desk", 1))

  test("orderServe binds loopback and a real port"):
    val uri = orderServe(app).use(server => IO.pure(server.baseUri)).unsafeRunSync()
    val authority = uri.authority.get
    assertEquals(authority.host.renderString, "127.0.0.1")
    assert(authority.port.exists(_ > 0))

  test("live GET /health returns the injected name and a Date header"):
    val (health, date) = withOrderLive(app) { (base, client) =>
      client.run(Request[IO](Method.GET, base / "health")).use { res =>
        res.as[HealthOut].map(body => (body, res.headers.get[Date]))
      }
    }.unsafeRunSync()
    assertEquals(health, HealthOut("desk"))
    assert(date.isDefined)

  test("live GET /rates/EUR returns JSON cents"):
    val out = withOrderLive(app) { (base, client) =>
      client.expect[RateOut](base / "rates" / "EUR")
    }.unsafeRunSync()
    assertEquals(out, RateOut("EUR", 108))

  test("live GET missing rate is 404; expect fails"):
    val status = withOrderLive(app) { (base, client) =>
      client.statusFromUri(base / "rates" / "JPY")
    }.unsafeRunSync()
    assertEquals(status, Status.NotFound)
    intercept[UnexpectedStatus] {
      withOrderLive(app) { (base, client) =>
        client.expect[RateOut](base / "rates" / "JPY")
      }.unsafeRunSync()
    }

  test("live POST /convert multiplies rate by qty"):
    val out = withOrderLive(app) { (base, client) =>
      val req = Request[IO](Method.POST, base / "convert").withEntity(ConvertIn("GBP", 2))
      client.expect[ConvertOut](req)
    }.unsafeRunSync()
    assertEquals(out, ConvertOut("GBP", 254))

  test("live POST qty below minQty is 422 invalid_qty"):
    val (status, err) = withOrderLive(app) { (base, client) =>
      val req = Request[IO](Method.POST, base / "convert").withEntity(ConvertIn("EUR", 0))
      client.run(req).use { res =>
        res.as[ApiError].map(body => (res.status, body))
      }
    }.unsafeRunSync()
    assertEquals(status, Status.UnprocessableContent)
    assertEquals(err, ApiError("invalid_qty"))

  test("withOrderLive still releases the port when the client call fails"):
    intercept[UnexpectedStatus] {
      withOrderLive(app) { (base, client) =>
        client.expect[RateOut](base / "rates" / "JPY")
      }.unsafeRunSync()
    }
    val uri = orderServe(app).use(server => IO.pure(server.baseUri)).unsafeRunSync()
    assert(uri.authority.flatMap(_.port).exists(_ > 0))
