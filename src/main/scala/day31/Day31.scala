package day31

import java.sql.SQLException

import cats.effect.IO
import cats.effect.IOApp
import org.http4s.HttpApp
import org.http4s.HttpRoutes
import org.http4s.InvalidMessageBodyFailure
import org.http4s.MalformedMessageBodyFailure
import org.http4s.Method
import org.http4s.Request
import org.http4s.dsl.io.*
import org.http4s.headers.Allow
import org.http4s.implicits.*
import org.typelevel.doobie.Transactor
import org.typelevel.doobie.implicits.*

import day29.Adr
import day30.ApiError
import day30.ConvertError
import day30.ConvertIn
import day30.ConvertOut
import day30.HealthOut
import day30.QuotesConfig
import day30.RateOut
import day30.given

/** Lookup cents for a code. Http calls this; Persistence implements it. */
type RateLookup = String => IO[Option[Int]]

/** Amount times the looked-up rate, or a domain reason. Does not call rateOf. */
def convertAmount(
    code: String,
    qty: Int,
    minQty: Int,
    cents: Option[Int]
): Either[ConvertError, ConvertOut] =
  if qty < minQty then Left(ConvertError.InvalidQty)
  else
    cents match
      case None       => Left(ConvertError.UnknownCode)
      case Some(rate) => Right(ConvertOut(code, rate * qty))

/** In-process catalog. Tests inject this; it is not JDBC. */
def memoryLookup(catalog: Map[String, Int])(code: String): IO[Option[Int]] =
  IO.pure(catalog.get(code))

/** Failed session. Tests inject this; it is not a missing row. */
def unavailableLookup(_code: String): IO[Option[Int]] =
  IO.raiseError(new SQLException("connection refused"))

/** Persistence: Day 23 repository mapped to cents. transact stays in the repository. */
def quotesLookup(xa: Transactor[IO])(code: String): IO[Option[Int]] =
  day23.RateRepository(xa).find(code).map(_.map(_.cents))

/** HTTP surface for docs/capstone/quotes.openapi.yaml plus RFC 9110 503. Does not bind a port. */
def quotesRoutes(config: QuotesConfig, lookup: RateLookup): HttpRoutes[IO] =
  HttpRoutes.of[IO] {
    case GET -> Root / "health" =>
      Ok(HealthOut(config.name))
    case GET -> Root / "rates" / code =>
      lookup(code).attempt.flatMap {
        case Left(_)            => ServiceUnavailable(ApiError("unavailable"))
        case Right(None)        => NotFound(ApiError("not_found"))
        case Right(Some(cents)) => Ok(RateOut(code, cents))
      }
    case req @ POST -> Root / "convert" =>
      req.attemptAs[ConvertIn].value.flatMap {
        case Left(_: MalformedMessageBodyFailure) =>
          BadRequest(ApiError("malformed_json"))
        case Left(_: InvalidMessageBodyFailure) =>
          UnprocessableContent(ApiError("invalid_json"))
        case Left(_) =>
          BadRequest(ApiError("malformed_json"))
        case Right(in) =>
          lookup(in.code).attempt.flatMap {
            case Left(_) =>
              ServiceUnavailable(ApiError("unavailable"))
            case Right(cents) =>
              convertAmount(in.code, in.qty, config.minQty, cents) match
                case Left(ConvertError.InvalidQty) =>
                  UnprocessableContent(ApiError("invalid_qty"))
                case Left(ConvertError.UnknownCode) =>
                  UnprocessableContent(ApiError("unknown_code"))
                case Right(out) =>
                  Ok(out)
          }
      }
    case _ -> Root / "health" =>
      MethodNotAllowed(Allow(GET))
    case _ -> Root / "rates" / _ =>
      MethodNotAllowed(Allow(GET))
    case _ -> Root / "convert" =>
      MethodNotAllowed(Allow(POST))
  }

def quotesApp(config: QuotesConfig, lookup: RateLookup): HttpApp[IO] =
  quotesRoutes(config, lookup).orNotFound

val quotesAdr0003: Adr =
  Adr(
    number = 3,
    title = "Persistence failure is 503",
    status = "Accepted",
    context =
      "GET /rates/JPY is a successful query with no row. A failed JDBC session is not a missing item. Day 29's OpenAPI lists 404 for an unknown code and does not list 503.",
    decision =
      "We will map a failed Persistence IO to 503 unavailable. A successful empty Option stays 404 on GET and 422 on POST. GET /health does not transact.",
    consequences =
      "Clients can retry 503 and must not treat a downed catalog as unknown_code. OpenAPI stays silent on 503; RFC 9110 names the status. Retry-After is optional and not required in this unit."
  )

object Day31 extends IOApp.Simple:
  val run: IO[Unit] =
    val name = "day31-main"
    val xa = day23.h2Xa(name)
    val app = quotesApp(QuotesConfig("quotes", 1), quotesLookup(xa))
    val req = Request[IO](Method.GET, uri"/rates/EUR")
    for
      _ <- day23.migrateQuotes(day23.quotesUrl(name))
      _ <- day23.seedRates.transact(xa)
      out <- app.run(req).flatMap(_.as[RateOut])
      _ <- IO.println(out)
    yield ()
