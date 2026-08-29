package day18

import cats.effect.IO
import cats.effect.IOApp
import io.circe.Codec
import org.http4s.EntityDecoder
import org.http4s.EntityEncoder
import org.http4s.HttpApp
import org.http4s.HttpRoutes
import org.http4s.InvalidMessageBodyFailure
import org.http4s.MalformedMessageBodyFailure
import org.http4s.Request
import org.http4s.Response
import org.http4s.circe.jsonEncoderOf
import org.http4s.circe.jsonOf
import org.http4s.dsl.io.*
import org.http4s.headers.Allow
import org.http4s.implicits.*

val rates: Map[String, Int] = Map("EUR" -> 108, "GBP" -> 127)

/** Cents per unit for a currency code, or None. Pure: no HTTP, no JSON. */
def rateOf(code: String): Option[Int] =
  rates.get(code)

final case class RateOut(code: String, cents: Int) derives Codec.AsObject
final case class ConvertIn(code: String, qty: Int) derives Codec.AsObject
final case class ConvertOut(code: String, cents: Int) derives Codec.AsObject
final case class ApiError(error: String) derives Codec.AsObject

enum ConvertError:
  case InvalidQty
  case UnknownCode

given EntityDecoder[IO, ConvertIn] = jsonOf[IO, ConvertIn]
given EntityDecoder[IO, ConvertOut] = jsonOf[IO, ConvertOut]
given EntityDecoder[IO, RateOut] = jsonOf[IO, RateOut]
given EntityDecoder[IO, ApiError] = jsonOf[IO, ApiError]
given EntityEncoder[IO, ConvertOut] = jsonEncoderOf[IO, ConvertOut]
given EntityEncoder[IO, RateOut] = jsonEncoderOf[IO, RateOut]
given EntityEncoder[IO, ApiError] = jsonEncoderOf[IO, ApiError]

/** Amount times rate, or a domain reason. Pure: HTTP only maps the Either. */
def convertAmount(code: String, qty: Int): Either[ConvertError, ConvertOut] =
  if qty < 1 then Left(ConvertError.InvalidQty)
  else
    rateOf(code) match
      case None        => Left(ConvertError.UnknownCode)
      case Some(cents) => Right(ConvertOut(code, cents * qty))

/** GET /rates/:code as JSON. POST /convert with a JSON body. */
def convertRoutes: HttpRoutes[IO] =
  HttpRoutes.of[IO] {
    case GET -> Root / "rates" / code =>
      rateOf(code) match
        case Some(cents) => Ok(RateOut(code, cents))
        case None        => NotFound(ApiError("not_found"))
    case req @ POST -> Root / "convert" =>
      req.attemptAs[ConvertIn].value.flatMap {
        case Left(_: MalformedMessageBodyFailure) =>
          BadRequest(ApiError("malformed_json"))
        case Left(_: InvalidMessageBodyFailure) =>
          UnprocessableContent(ApiError("invalid_json"))
        case Left(_) =>
          BadRequest(ApiError("malformed_json"))
        case Right(in) =>
          convertAmount(in.code, in.qty) match
            case Left(ConvertError.InvalidQty) =>
              UnprocessableContent(ApiError("invalid_qty"))
            case Left(ConvertError.UnknownCode) =>
              UnprocessableContent(ApiError("unknown_code"))
            case Right(out) =>
              Ok(out)
      }
    case _ -> Root / "rates" / _ =>
      MethodNotAllowed(Allow(GET))
    case _ -> Root / "convert" =>
      MethodNotAllowed(Allow(POST))
  }

/** Unmatched paths become 404. Building this does not bind a port. */
def convertApp: HttpApp[IO] =
  convertRoutes.orNotFound

def runRequest(req: Request[IO]): IO[Response[IO]] =
  convertApp.run(req)

object Day18 extends IOApp.Simple:
  private def line(req: Request[IO]): IO[String] =
    runRequest(req).flatMap { res =>
      res.as[String].map(body => s"${res.status.code} $body")
    }

  val run: IO[Unit] =
    for
      a <- line(Request[IO](GET, uri"/rates/EUR"))
      _ <- IO.println(a)
      b <- line(
        Request[IO](POST, uri"/convert")
          .withEntity("""{"code":"EUR","qty":10}""")
      )
      _ <- IO.println(b)
    yield ()
