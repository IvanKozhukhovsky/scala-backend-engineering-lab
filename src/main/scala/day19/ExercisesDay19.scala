package day19

import cats.effect.IO
import ciris.*
import io.circe.Codec
import org.http4s.EntityDecoder
import org.http4s.EntityEncoder
import org.http4s.HttpApp
import org.http4s.HttpRoutes
import org.http4s.circe.jsonEncoderOf
import org.http4s.circe.jsonOf
import org.http4s.dsl.io.*
import org.http4s.headers.Allow
import org.typelevel.log4cats.StructuredLogger
import cats.implicits.*
import org.http4s.MalformedMessageBodyFailure
import org.http4s.InvalidMessageBodyFailure

// Independent exercise for scala-019.
// Implement orderConfig, orderOf, and orderRoutes yourself.
// Keep rateOf / orderOf pure — no env, no Ciris load, no logger inside those functions.
// Pass maxQty as Int. Pass shopName into HealthOut on GET /health.
// Do not start Ember, bind a port, or call unsafeRunSync / println inside those three.
// Do not log api tokens or put secrets in response bodies.

final case class OrderConfig(shopName: String, maxQty: Int)
final case class OrderIn(code: String, qty: Int) derives Codec.AsObject
final case class OrderOut(code: String, qty: Int, cents: Int) derives Codec.AsObject

enum OrderError:
  case InvalidQty
  case ExcessQty
  case UnknownCode

given EntityDecoder[IO, OrderIn] = jsonOf[IO, OrderIn]
given EntityDecoder[IO, OrderOut] = jsonOf[IO, OrderOut]
given EntityEncoder[IO, OrderOut] = jsonEncoderOf[IO, OrderOut]

/** `ORDER_SHOP_NAME` or `order.shopName`, default `"lab"`. `ORDER_MAX_QTY` or `order.maxQty`,
  * default `10`.
  */
def orderConfig: ConfigValue[Effect, OrderConfig] =
  (
    env("ORDER_SHOP_NAME").or(prop("order.shopName")).as[String].default("lab"),
    env("ORDER_MAX_QTY").or(prop("order.maxQty")).as[Int].default(10)
  ).parMapN(OrderConfig.apply)

/** qty < 1 → InvalidQty. qty > maxQty → ExcessQty. Unknown rate → UnknownCode. Else cents = rate *
  * qty.
  */
def orderOf(code: String, qty: Int, maxQty: Int): Either[OrderError, OrderOut] =
  if qty < 1 then Left(OrderError.InvalidQty)
  else if qty > maxQty then Left(OrderError.ExcessQty)
  else
    rateOf(code) match
      case None       => Left(OrderError.UnknownCode)
      case Some(rate) => Right(OrderOut(code, qty, rate * qty))

/** GET /health → 200 HealthOut with config.shopName. GET /orders/:code → 200 RateOut or 404
  * `not_found`. POST /orders with OrderIn JSON → 200 OrderOut, or the JSON error contract from Day
  * 18, plus ExcessQty → 422 `excess_qty`. On successful POST, log INFO with ctx keys `code` and
  * `op`=`order` and message `ok`. Other methods on those URIs → 405.
  */
def orderRoutes(config: OrderConfig, logger: StructuredLogger[IO]): HttpRoutes[IO] =
  HttpRoutes.of[IO] {
    case GET -> Root / "health" =>
      Ok(HealthOut(config.shopName))
    case GET -> Root / "orders" / code =>
      rateOf(code) match
        case None =>
          NotFound(ApiError("not_found"))
        case Some(cents) =>
          Ok(RateOut(code, cents))
    case req @ POST -> Root / "orders" =>
      req.attemptAs[OrderIn].value.flatMap {
        case Left(_: MalformedMessageBodyFailure) =>
          BadRequest(ApiError("malformed_json"))
        case Left(_: InvalidMessageBodyFailure) =>
          UnprocessableContent(ApiError("invalid_json"))
        case Left(_) =>
          BadRequest(ApiError("malformed_json"))
        case Right(in) =>
          orderOf(in.code, in.qty, config.maxQty) match
            case Left(OrderError.InvalidQty) =>
              UnprocessableContent(ApiError("invalid_qty"))
            case Left(OrderError.ExcessQty) =>
              UnprocessableContent(ApiError("excess_qty"))
            case Left(OrderError.UnknownCode) =>
              UnprocessableContent(ApiError("unknown_code"))
            case Right(out) =>
              logger.info(Map("code" -> in.code, "op" -> "order"))("ok") *>
                Ok(out)
      }
    case _ -> Root / "health" =>
      MethodNotAllowed(Allow(GET))
    case _ -> Root / "orders" / _ =>
      MethodNotAllowed(Allow(GET))
    case _ -> Root / "orders" =>
      MethodNotAllowed(Allow(POST))
  }

/** Turn unmatched paths into 404. Do not bind a port. */
def orderApp(config: OrderConfig, logger: StructuredLogger[IO]): HttpApp[IO] =
  orderRoutes(config, logger).orNotFound
