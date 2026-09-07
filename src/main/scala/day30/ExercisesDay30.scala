package day30

import cats.effect.IO
import io.circe.Codec
import org.http4s.EntityDecoder
import org.http4s.EntityEncoder
import org.http4s.HttpApp
import org.http4s.HttpRoutes
import org.http4s.InvalidMessageBodyFailure
import org.http4s.MalformedMessageBodyFailure
import org.http4s.circe.jsonEncoderOf
import org.http4s.circe.jsonOf
import org.http4s.headers.Allow
import org.http4s.dsl.io.*

// Independent exercise for scala-030.
// Implement orderOf and deskRoutes yourself. deskApp is already orNotFound.
// The HTTP oracle is docs/capstone/desk.openapi.yaml plus RFC 9110 405 + Allow
// for methods the document does not list. ADR 2: unknown POST code is 422.
//
// Keep rateOf / orderOf pure — no HTTP, JDBC, env, logs, or metrics inside them.
// Do not alias quotesRoutes / quotesApp.
// Do not start Flyway, bind a port, or call unsafeRunSync / println inside
// orderOf / deskRoutes.
// Do not change Day30.scala or the quotes OpenAPI file.
//
// orderOf:
//   Trim `code`. Empty after trim → InvalidCode. qty < minQty → InvalidQty.
//   Unknown rate → UnknownCode. Otherwise cents = rate * qty.
//
// deskRoutes(config):
//   GET /health → 200 HealthOut(config.name)
//   GET /orders/:code → 200 RateOut or 404 not_found
//   POST /orders OrderIn JSON → 200 OrderOut, or:
//     malformed JSON → 400 malformed_json
//     missing/wrong fields → 422 invalid_json
//     InvalidQty → 422 invalid_qty
//     InvalidCode → 422 invalid_code
//     UnknownCode → 422 unknown_code
//   Other methods on those URIs → 405 with Allow (GET on /health and
//     /orders/:code; POST on /orders).
// Work one red test at a time. Do not copy quotesRoutes and rename paths.

final case class DeskConfig(name: String, minQty: Int)

final case class OrderIn(code: String, qty: Int) derives Codec.AsObject
final case class OrderOut(code: String, qty: Int, cents: Int) derives Codec.AsObject

enum OrderError:
  case InvalidQty
  case InvalidCode
  case UnknownCode

given EntityDecoder[IO, OrderIn] = jsonOf[IO, OrderIn]
given EntityDecoder[IO, OrderOut] = jsonOf[IO, OrderOut]
given EntityEncoder[IO, OrderOut] = jsonEncoderOf[IO, OrderOut]

/** Trim `code`, then InvalidCode / InvalidQty / UnknownCode, or OrderOut. Pure. */
def orderOf(code: String, qty: Int, minQty: Int): Either[OrderError, OrderOut] =
  val trimmedCode = code.trim
  if trimmedCode.isEmpty then Left(OrderError.InvalidCode)
  else if qty < minQty then Left(OrderError.InvalidQty)
  else
    rateOf(trimmedCode) match
      case Some(cents) => Right(OrderOut(trimmedCode, qty, cents * qty))
      case None        => Left(OrderError.UnknownCode)

/** Desk HTTP surface for docs/capstone/desk.openapi.yaml. Does not bind a port. */
def deskRoutes(config: DeskConfig): HttpRoutes[IO] =
  HttpRoutes.of[IO] {
    case GET -> Root / "health" =>
      Ok(HealthOut(config.name))
    case GET -> Root / "orders" / code =>
      rateOf(code) match
        case Some(cents) => Ok(RateOut(code, cents))
        case None        => NotFound(ApiError("not_found"))
    case req @ POST -> Root / "orders" =>
      req.attemptAs[OrderIn].value.flatMap {
        case Left(_: MalformedMessageBodyFailure) =>
          BadRequest(ApiError("malformed_json"))
        case Left(_: InvalidMessageBodyFailure) =>
          UnprocessableContent(ApiError("invalid_json"))
        case Left(_) =>
          BadRequest(ApiError("malformed_json"))
        case Right(orderIn) =>
          orderOf(orderIn.code, orderIn.qty, config.minQty) match
            case Left(OrderError.InvalidCode) => UnprocessableContent(ApiError("invalid_code"))
            case Left(OrderError.InvalidQty)  => UnprocessableContent(ApiError("invalid_qty"))
            case Left(OrderError.UnknownCode) => UnprocessableContent(ApiError("unknown_code"))
            case Right(orderOut)              => Ok(orderOut)
      }
    case _ -> Root / "health" =>
      MethodNotAllowed(Allow(GET))
    case _ -> Root / "orders" =>
      MethodNotAllowed(Allow(POST))
    case _ -> Root / "orders" / _ =>
      MethodNotAllowed(Allow(GET))
  }

def deskApp(config: DeskConfig): HttpApp[IO] =
  deskRoutes(config).orNotFound
