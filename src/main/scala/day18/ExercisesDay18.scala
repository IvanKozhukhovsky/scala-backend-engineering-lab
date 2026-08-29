package day18

import cats.effect.IO
import io.circe.Codec
import org.http4s.EntityDecoder
import org.http4s.EntityEncoder
import org.http4s.HttpApp
import org.http4s.HttpRoutes
import org.http4s.circe.jsonEncoderOf
import org.http4s.circe.jsonOf
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.http4s.MalformedMessageBodyFailure
import org.http4s.InvalidMessageBodyFailure
import org.http4s.headers.Allow

// Independent exercise for scala-018.
// Implement orderOf and orderRoutes yourself.
// Keep rateOf / orderOf pure — do not put catalog rules only inside Ok { }.
// Do not start Ember, bind a port, or call unsafeRunSync / println inside those two.

final case class OrderIn(code: String, qty: Int) derives Codec.AsObject
final case class OrderOut(code: String, qty: Int, cents: Int) derives Codec.AsObject

enum OrderError:
  case InvalidQty
  case InvalidCode
  case UnknownCode

given EntityDecoder[IO, OrderIn] = jsonOf[IO, OrderIn]
given EntityDecoder[IO, OrderOut] = jsonOf[IO, OrderOut]
given EntityEncoder[IO, OrderOut] = jsonEncoderOf[IO, OrderOut]

/** Trim `code`. Empty after trim → InvalidCode. qty < 1 → InvalidQty. Unknown rate → UnknownCode.
  * Otherwise cents = rate * qty.
  */
def orderOf(code: String, qty: Int): Either[OrderError, OrderOut] =
  val trimmedCode = code.trim
  if trimmedCode.isEmpty then Left(OrderError.InvalidCode)
  else if qty < 1 then Left(OrderError.InvalidQty)
  else
    rateOf(trimmedCode) match
      case None        => Left(OrderError.UnknownCode)
      case Some(cents) => Right(OrderOut(trimmedCode, qty, cents * qty))

/** GET /orders/:code → 200 Rate-style JSON `{"code","cents"}` using RateOut, or 404 `not_found`.
  * POST /orders with OrderIn JSON → 200 OrderOut, or the JSON error contract from the lesson:
  * malformed JSON → 400 `malformed_json`; missing/wrong fields → 422 `invalid_json`; InvalidQty →
  * 422 `invalid_qty`; InvalidCode → 422 `invalid_code`; UnknownCode → 422 `unknown_code`. Use
  * `UnprocessableContent` for 422 (http4s name since 0.23.31). Other methods on those URIs → 405.
  */
def orderRoutes: HttpRoutes[IO] =
  HttpRoutes.of[IO] {
    case GET -> Root / "orders" / code =>
      rateOf(code) match
        case None        => NotFound(ApiError("not_found"))
        case Some(cents) => Ok(RateOut(code, cents))
    case req @ POST -> Root / "orders" =>
      req.attemptAs[OrderIn].value.flatMap {
        case Left(_: MalformedMessageBodyFailure) =>
          BadRequest(ApiError("malformed_json"))
        case Left(_: InvalidMessageBodyFailure) =>
          UnprocessableContent(ApiError("invalid_json"))
        case Left(_) =>
          BadRequest(ApiError("malformed_json"))
        case Right(in) =>
          orderOf(in.code, in.qty) match
            case Left(OrderError.InvalidQty) =>
              UnprocessableContent(ApiError("invalid_qty"))
            case Left(OrderError.InvalidCode) =>
              UnprocessableContent(ApiError("invalid_code"))
            case Left(OrderError.UnknownCode) =>
              UnprocessableContent(ApiError("unknown_code"))
            case Right(out) =>
              Ok(out)
      }
    case _ -> Root / "orders" / _ =>
      MethodNotAllowed(Allow(GET))
    case _ -> Root / "orders" =>
      MethodNotAllowed(Allow(POST))
  }

/** Turn unmatched paths into 404. Do not bind a port. */
def orderApp: HttpApp[IO] =
  orderRoutes.orNotFound
