package day17

import cats.effect.IO
import org.http4s.HttpApp
import org.http4s.HttpRoutes
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.http4s.Request
import org.http4s.headers.Allow

// Independent exercise for scala-017.
// Implement quoteRoutes yourself. Keep rateOf pure — do not put the catalog inside Ok { }.
// Do not start Ember, bind a port, or introduce JSON codecs.
// Do not call unsafeRunSync or println inside quoteRoutes / quoteApp.

/** GET /quotes/:code → 200 with the cents as text, or 404 if unknown. GET /quotes → 200 with known
  * codes sorted and joined by commas (example: "EUR,GBP"). Any other method on /quotes or
  * /quotes/:code → 405. Unmatched paths are 404 via quoteApp.
  */
def quoteRoutes: HttpRoutes[IO] =
  HttpRoutes.of[IO] {
    case GET -> Root / "quotes" / code =>
      rateOf(code) match
        case Some(n) => Ok(n.toString)
        case None    => NotFound()
    case GET -> Root / "quotes" =>
      Ok(rates.keys.toList.sorted.mkString(","))
    case _ -> Root / "quotes" / code =>
      MethodNotAllowed(Allow(GET))
    case _ -> Root / "quotes" =>
      MethodNotAllowed(Allow(GET))
  }

/** Turn unmatched paths into 404. Do not bind a port. */
def quoteApp: HttpApp[IO] =
  quoteRoutes.orNotFound
