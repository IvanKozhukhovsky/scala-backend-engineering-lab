package day31

import cats.effect.IO
import org.http4s.HttpApp
import org.http4s.HttpRoutes
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.http4s.InvalidMessageBodyFailure
import org.http4s.MalformedMessageBodyFailure
import org.http4s.headers.Allow
import org.typelevel.doobie.Transactor

import day30.DeskConfig
import day30.OrderError
import day30.OrderIn
import day30.OrderOut
import day30.HealthOut
import day30.ApiError
import day30.RateOut
import day30.given

// Independent exercise for scala-031.
// Implement orderFrom, deskLookup, and deskRoutes yourself. deskApp is already
// orNotFound.
//
// orderFrom(code, qty, minQty, cents):
//   Trim `code`. Empty after trim → InvalidCode. qty < minQty → InvalidQty.
//   cents is None → UnknownCode. Otherwise OrderOut(trimmed, qty, rate * qty).
//   Pure: no HTTP, JDBC, env, logs, or rateOf.
//
// deskLookup(xa)(code):
//   Day 23 RateRepository(xa).find(code) mapped to Option[Int] cents.
//   Do not CREATE TABLE. Do not use jdbc:tc:.
//
// deskRoutes(config, lookup):
//   GET /health → 200 HealthOut(config.name). Do not call lookup.
//   GET /orders/:code → lookup.attempt:
//     Left → 503 unavailable
//     Right(None) → 404 not_found
//     Right(Some(cents)) → 200 RateOut
//   POST /orders OrderIn JSON → same 400/422 JSON mapping as Day 30, then
//     lookup(orderIn.code).attempt:
//       Left → 503 unavailable
//       Right(cents) → orderFrom(...) → 200 OrderOut or
//         InvalidQty → 422 invalid_qty
//         InvalidCode → 422 invalid_code
//         UnknownCode → 422 unknown_code
//   Other methods on those URIs → 405 with Allow (GET on /health and
//     /orders/:code; POST on /orders).
//
// Work one red test at a time. Do not alias quotesRoutes / quotesApp.
// Do not put JDBC inside orderFrom. Do not change Day 30 files, OpenAPI YAML,
// or ADR 1–2. ADR 3 is the quotes worked example.

/** Trim `code`, then InvalidCode / InvalidQty / UnknownCode, using `cents`. Pure. */
def orderFrom(
    code: String,
    qty: Int,
    minQty: Int,
    cents: Option[Int]
): Either[OrderError, OrderOut] =
  val trimmedCode = code.trim
  if (trimmedCode.isEmpty) then Left(OrderError.InvalidCode)
  else if (qty < minQty) then Left(OrderError.InvalidQty)
  else
    cents match {
      case Some(rate) => Right(OrderOut(trimmedCode, qty, rate * qty))
      case None       => Left(OrderError.UnknownCode)
    }

/** Persistence lookup for desk routes. Building this does not query. */
def deskLookup(xa: Transactor[IO])(code: String): IO[Option[Int]] =
  day23.RateRepository(xa).find(code).map(_.map(_.cents))

/** Desk HTTP surface. Cents come from `lookup`, not rateOf. Does not bind a port. */
def deskRoutes(config: DeskConfig, lookup: RateLookup): HttpRoutes[IO] =
  HttpRoutes.of[IO] {
    case GET -> Root / "health" =>
      Ok(HealthOut(config.name))
    case GET -> Root / "orders" / code =>
      lookup(code).attempt.flatMap {
        case Left(_)            => ServiceUnavailable(ApiError("unavailable"))
        case Right(None)        => NotFound(ApiError("not_found"))
        case Right(Some(cents)) => Ok(RateOut(code, cents))
      }
    case req @ POST -> Root / "orders" =>
      req.attemptAs[OrderIn].value.flatMap {
        case Left(_: MalformedMessageBodyFailure) => BadRequest(ApiError("malformed_json"))
        case Left(_: InvalidMessageBodyFailure)   => UnprocessableContent(ApiError("invalid_json"))
        case Left(_)                              => BadRequest(ApiError("malformed_json"))
        case Right(orderIn)                       =>
          lookup(orderIn.code).attempt.flatMap {
            case Left(_) =>
              ServiceUnavailable(ApiError("unavailable"))
            case Right(cents) =>
              orderFrom(orderIn.code, orderIn.qty, config.minQty, cents) match {
                case Left(OrderError.InvalidCode) =>
                  UnprocessableContent(ApiError("invalid_code"))
                case Left(OrderError.InvalidQty) =>
                  UnprocessableContent(ApiError("invalid_qty"))
                case Left(OrderError.UnknownCode) =>
                  UnprocessableContent(ApiError("unknown_code"))
                case Right(orderOut) =>
                  Ok(orderOut)
              }
          }
      }
    case _ -> Root / "health" =>
      MethodNotAllowed(Allow(GET))
    case _ -> Root / "orders" / _ =>
      MethodNotAllowed(Allow(GET))
    case _ -> Root / "orders" =>
      MethodNotAllowed(Allow(POST))
  }

def deskApp(config: DeskConfig, lookup: RateLookup): HttpApp[IO] =
  deskRoutes(config, lookup).orNotFound
