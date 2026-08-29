package day19

import cats.Show
import cats.effect.IO
import cats.effect.IOApp
import cats.syntax.all.*
import ciris.*
import io.circe.Codec
import org.http4s.EntityDecoder
import org.http4s.EntityEncoder
import org.http4s.HttpApp
import org.http4s.HttpRoutes
import org.http4s.InvalidMessageBodyFailure
import org.http4s.MalformedMessageBodyFailure
import org.http4s.Request
import org.http4s.circe.jsonEncoderOf
import org.http4s.circe.jsonOf
import org.http4s.dsl.io.*
import org.http4s.headers.Allow
import org.http4s.implicits.*
import org.typelevel.log4cats.StructuredLogger
import org.typelevel.log4cats.noop.NoOpLogger

given Show[String] = Show.fromToString

val rates: Map[String, Int] = Map("EUR" -> 108, "GBP" -> 127)

/** Cents per unit for a currency code, or None. Pure: no HTTP, no env, no logger. */
def rateOf(code: String): Option[Int] =
  rates.get(code)

/** Values loaded once at the rim. The core never sees env names or Secret. */
final case class QuoteConfig(
    serviceName: String,
    minQty: Int,
    apiToken: Secret[String]
)

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
given EntityEncoder[IO, ConvertOut] = jsonEncoderOf[IO, ConvertOut]
given EntityEncoder[IO, RateOut] = jsonEncoderOf[IO, RateOut]
given EntityEncoder[IO, HealthOut] = jsonEncoderOf[IO, HealthOut]
given EntityEncoder[IO, ApiError] = jsonEncoderOf[IO, ApiError]

/** Description of config. Loading is an effect; this value is not loaded yet. */
def quoteConfig: ConfigValue[Effect, QuoteConfig] =
  (
    env("QUOTE_SERVICE_NAME").or(prop("quote.serviceName")).as[String].default("quotes"),
    env("QUOTE_MIN_QTY").or(prop("quote.minQty")).as[Int].default(1),
    env("QUOTE_API_TOKEN").or(prop("quote.apiToken")).secret.default(Secret("dev-token"))
  ).parMapN(QuoteConfig.apply)

def loadQuoteConfig: IO[QuoteConfig] =
  quoteConfig.load[IO]

/** Amount times rate, or a domain reason. `minQty` is a number, not an env lookup. */
def convertAmount(code: String, qty: Int, minQty: Int): Either[ConvertError, ConvertOut] =
  if qty < minQty then Left(ConvertError.InvalidQty)
  else
    rateOf(code) match
      case None        => Left(ConvertError.UnknownCode)
      case Some(cents) => Right(ConvertOut(code, cents * qty))

/** HTTP shell: maps Either, logs key-value context, never unwraps apiToken into a body. */
def convertRoutes(config: QuoteConfig, logger: StructuredLogger[IO]): HttpRoutes[IO] =
  HttpRoutes.of[IO] {
    case GET -> Root / "health" =>
      Ok(HealthOut(config.serviceName))
    case GET -> Root / "rates" / code =>
      rateOf(code) match
        case Some(cents) =>
          logger.info(Map("code" -> code, "op" -> "get_rate"))("ok") *>
            Ok(RateOut(code, cents))
        case None =>
          logger.info(Map("code" -> code, "op" -> "get_rate"))("not_found") *>
            NotFound(ApiError("not_found"))
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
              logger.info(Map("code" -> in.code, "op" -> "convert"))("invalid_qty") *>
                UnprocessableContent(ApiError("invalid_qty"))
            case Left(ConvertError.UnknownCode) =>
              logger.info(Map("code" -> in.code, "op" -> "convert"))("unknown_code") *>
                UnprocessableContent(ApiError("unknown_code"))
            case Right(out) =>
              logger.info(Map("code" -> in.code, "op" -> "convert"))("ok") *>
                Ok(out)
      }
    case _ -> Root / "health" =>
      MethodNotAllowed(Allow(GET))
    case _ -> Root / "rates" / _ =>
      MethodNotAllowed(Allow(GET))
    case _ -> Root / "convert" =>
      MethodNotAllowed(Allow(POST))
  }

/** Unmatched paths become 404. Building this does not bind a port. */
def convertApp(config: QuoteConfig, logger: StructuredLogger[IO]): HttpApp[IO] =
  convertRoutes(config, logger).orNotFound

object Day19 extends IOApp.Simple:
  private val logger = NoOpLogger.impl[IO]

  val run: IO[Unit] =
    for
      config <- loadQuoteConfig
      res <- convertApp(config, logger).run(Request[IO](GET, uri"/health"))
      body <- res.as[String]
      _ <- IO.println(s"${res.status.code} $body")
    yield ()
