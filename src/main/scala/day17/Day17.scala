package day17

import cats.effect.IO
import cats.effect.IOApp
import org.http4s.HttpApp
import org.http4s.HttpRoutes
import org.http4s.Request
import org.http4s.Response
import org.http4s.dsl.io.*
import org.http4s.headers.Allow
import org.http4s.implicits.*

val rates: Map[String, Int] = Map("EUR" -> 108, "GBP" -> 127)

/** Cents per unit for a currency code, or None. Pure: no HTTP. */
def rateOf(code: String): Option[Int] =
  rates.get(code)

/** GET /rates/:code and GET /health. Other methods on /rates/:code are 405. */
def rateRoutes: HttpRoutes[IO] =
  HttpRoutes.of[IO] {
    case GET -> Root / "rates" / code =>
      rateOf(code) match
        case Some(n) => Ok(n.toString)
        case None    => NotFound()
    case GET -> Root / "health" =>
      Ok("ok")
    case _ -> Root / "rates" / _ =>
      MethodNotAllowed(Allow(GET))
  }

/** Unmatched paths become 404. Building this does not bind a port. */
def rateApp: HttpApp[IO] =
  rateRoutes.orNotFound

/** Run one request against the app. Still only a description until unsafeRun / IOApp. */
def runRequest(req: Request[IO]): IO[Response[IO]] =
  rateApp.run(req)

object Day17 extends IOApp.Simple:
  private def line(req: Request[IO]): IO[String] =
    runRequest(req).flatMap { res =>
      res.as[String].map(body => s"${res.status.code} $body")
    }

  val run: IO[Unit] =
    for
      a <- line(Request[IO](GET, uri"/rates/EUR"))
      _ <- IO.println(a)
      b <- line(Request[IO](GET, uri"/rates/JPY"))
      _ <- IO.println(b)
      c <- line(Request[IO](POST, uri"/rates/EUR"))
      _ <- IO.println(c)
    yield ()
