package day20

import cats.effect.IO
import cats.effect.IOApp
import cats.effect.Resource
import com.comcast.ip4s.ipv4
import com.comcast.ip4s.port
import io.circe.Codec
import org.http4s.EntityDecoder
import org.http4s.EntityEncoder
import org.http4s.HttpApp
import org.http4s.HttpRoutes
import org.http4s.Uri
import org.http4s.InvalidMessageBodyFailure
import org.http4s.MalformedMessageBodyFailure
import org.http4s.circe.jsonEncoderOf
import org.http4s.circe.jsonOf
import org.http4s.client.Client
import org.http4s.dsl.io.*
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.headers.Allow
import org.http4s.implicits.*
import org.http4s.server.Server

val rates: Map[String, Int] = Map("EUR" -> 108, "GBP" -> 127)

/** Cents per unit for a currency code, or None. Pure: no HTTP, no sockets. */
def rateOf(code: String): Option[Int] =
  rates.get(code)

/** Injected values. Tests construct this; they do not own the process environment. */
final case class DeskConfig(name: String, minQty: Int)

final case class RateOut(code: String, cents: Int) derives Codec.AsObject
final case class ConvertIn(code: String, qty: Int) derives Codec.AsObject
final case class ConvertOut(code: String, cents: Int) derives Codec.AsObject
final case class HealthOut(name: String) derives Codec.AsObject
final case class ApiError(error: String) derives Codec.AsObject

enum ConvertError:
  case InvalidQty
  case UnknownCode

given EntityDecoder[IO, ConvertIn] = jsonOf[IO, ConvertIn]
given EntityDecoder[IO, ConvertOut] = jsonOf[IO, ConvertOut]
given EntityDecoder[IO, RateOut] = jsonOf[IO, RateOut]
given EntityDecoder[IO, HealthOut] = jsonOf[IO, HealthOut]
given EntityDecoder[IO, ApiError] = jsonOf[IO, ApiError]
given EntityEncoder[IO, ConvertIn] = jsonEncoderOf[IO, ConvertIn]
given EntityEncoder[IO, ConvertOut] = jsonEncoderOf[IO, ConvertOut]
given EntityEncoder[IO, RateOut] = jsonEncoderOf[IO, RateOut]
given EntityEncoder[IO, HealthOut] = jsonEncoderOf[IO, HealthOut]
given EntityEncoder[IO, ApiError] = jsonEncoderOf[IO, ApiError]

/** Amount times rate, or a domain reason. `minQty` is a number, not an env lookup. */
def convertAmount(code: String, qty: Int, minQty: Int): Either[ConvertError, ConvertOut] =
  if qty < minQty then Left(ConvertError.InvalidQty)
  else
    rateOf(code) match
      case None        => Left(ConvertError.UnknownCode)
      case Some(cents) => Right(ConvertOut(code, cents * qty))

def convertRoutes(config: DeskConfig): HttpRoutes[IO] =
  HttpRoutes.of[IO] {
    case GET -> Root / "health" =>
      Ok(HealthOut(config.name))
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
          convertAmount(in.code, in.qty, config.minQty) match
            case Left(ConvertError.InvalidQty) =>
              UnprocessableContent(ApiError("invalid_qty"))
            case Left(ConvertError.UnknownCode) =>
              UnprocessableContent(ApiError("unknown_code"))
            case Right(out) =>
              Ok(out)
      }
    case _ -> Root / "health" =>
      MethodNotAllowed(Allow(GET))
    case _ -> Root / "rates" / _ =>
      MethodNotAllowed(Allow(GET))
    case _ -> Root / "convert" =>
      MethodNotAllowed(Allow(POST))
  }

def convertApp(config: DeskConfig): HttpApp[IO] =
  convertRoutes(config).orNotFound

/** Bind loopback and let the OS pick a free port. Building this does not listen yet. */
def serve(app: HttpApp[IO]): Resource[IO, Server] =
  EmberServerBuilder
    .default[IO]
    .withHost(ipv4"127.0.0.1")
    .withPort(port"0")
    .withHttpApp(app)
    .build

/** Acquire Ember server + client, run `run`, release both — including when `run` fails. */
def withLive[A](app: HttpApp[IO])(run: (Uri, Client[IO]) => IO[A]): IO[A] =
  Resource
    .both(serve(app), EmberClientBuilder.default[IO].build)
    .use { case (server, client) =>
      run(server.baseUri, client)
    }

object Day20 extends IOApp.Simple:
  val run: IO[Unit] =
    val app = convertApp(DeskConfig("quotes", 1))
    withLive(app) { (base, client) =>
      client.expect[HealthOut](base / "health").flatMap { health =>
        IO.println(s"$base health=${health.name}")
      }
    }
