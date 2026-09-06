package day29

// Independent exercise for scala-029.
// Fill deskOpenApi, deskArchitecture, and deskAdr0002 yourself. Then write
// docs/capstone/desk.openapi.yaml so the file equals openapiOf(deskOpenApi)
// exactly (trailing newline included),
// docs/capstone/desk-architecture.md so it equals architectureOf(deskArchitecture),
// and docs/adr/0002-unknown-post-code-is-422.md so it equals adrOf(deskAdr0002).
//
// deskOpenApi:
//   title: desk
//   version: 0.1.0
//   GET /health — summary Liveness — 200 Service is up / HealthOut
//   GET /orders/{code} — summary Rate for a currency — 200 Known code / RateOut,
//     404 Unknown code / ApiError
//   POST /orders — summary Place an order — requestBody OrderIn —
//     200 Placed / OrderOut, 400 Malformed JSON / ApiError,
//     422 Well-formed JSON the resource cannot process / ApiError
//   schemas: HealthOut, RateOut, OrderIn (code string, qty integer),
//     OrderOut (code string, qty integer, cents integer), ApiError
//   Reuse healthOut, rateOut, and apiError. Do not alias quotesOpenApi.
//
// deskArchitecture:
//   service: desk
//   Core owns: rateOf and orderOf — does not own: HTTP, JDBC, env, logs, metrics
//   Http owns: status mapping, JSON, logs, counters — does not own: catalog lookup, SQL, Docker
//   Persistence owns: Flyway and ConnectionIO.transact — does not own: HTTP status codes, rateOf
//   Runtime owns: Ciris env, HEALTHCHECK, process uid — does not own: orderOf
//   Do not alias quotesArchitecture.
//
// deskAdr0002:
//   number: 2
//   title: Unknown POST code is 422
//   status: Accepted
//   context: GET /orders/JPY is a missing item at a collection URI. POST /orders with code JPY is a body field on a known URI.
//   decision: We will return 422 unknown_code for a POST body whose code is not in the catalog, and 404 not_found only for GET of a missing item.
//   consequences: Clients can tell a path miss from a domain reject. The OpenAPI document lists both statuses on the matching operations.
//
// Do not put POST on /health.
// Do not list /convert or /rates/{code} on the desk spec.
// Do not implement HttpRoutes, Ember, or Flyway in this unit.
// Do not change docs/capstone/quotes.openapi.yaml, quotes-architecture.md, or
// docs/adr/0001-openapi-is-the-http-contract.md (those are the worked example).

val orderIn: JsonSchema =
  jsonObject("OrderIn", JsonField("code", JsonType.Str), JsonField("qty", JsonType.Int))

val orderOut: JsonSchema =
  jsonObject(
    "OrderOut",
    JsonField("code", JsonType.Str),
    JsonField("qty", JsonType.Int),
    JsonField("cents", JsonType.Int)
  )

/** Desk HTTP contract. Building this does not bind a port. */
def deskOpenApi: OpenApiSpec =
  OpenApiSpec(
    title = "desk",
    version = "0.1.0",
    paths = List(
      ApiPath(
        "/health",
        List(
          ApiOperation(
            HttpOp.Get,
            "Liveness",
            None,
            List(ApiResponse("200", "Service is up", "HealthOut"))
          )
        )
      ),
      ApiPath(
        "/orders/{code}",
        List(
          ApiOperation(
            HttpOp.Get,
            "Rate for a currency",
            None,
            List(
              ApiResponse("200", "Known code", "RateOut"),
              ApiResponse("404", "Unknown code", "ApiError")
            )
          )
        )
      ),
      ApiPath(
        "/orders",
        List(
          ApiOperation(
            HttpOp.Post,
            "Place an order",
            Some("OrderIn"),
            List(
              ApiResponse("200", "Placed", "OrderOut"),
              ApiResponse("400", "Malformed JSON", "ApiError"),
              ApiResponse("422", "Well-formed JSON the resource cannot process", "ApiError")
            )
          )
        )
      )
    ),
    schemas = List(healthOut, rateOut, orderIn, orderOut, apiError)
  )

/** Desk layer map. Building this does not start Postgres. */
def deskArchitecture: ServiceArchitecture =
  ServiceArchitecture(
    service = "desk",
    layers = List(
      Layer(LayerKind.Core, "rateOf and orderOf", "HTTP, JDBC, env, logs, metrics"),
      Layer(LayerKind.Http, "status mapping, JSON, logs, counters", "catalog lookup, SQL, Docker"),
      Layer(LayerKind.Persistence, "Flyway and ConnectionIO.transact", "HTTP status codes, rateOf"),
      Layer(LayerKind.Runtime, "Ciris env, HEALTHCHECK, process uid", "orderOf")
    )
  )

/** Second ADR. Building this does not change HttpRoutes. */
def deskAdr0002: Adr =
  Adr(
    number = 2,
    title = "Unknown POST code is 422",
    status = "Accepted",
    context =
      "GET /orders/JPY is a missing item at a collection URI. POST /orders with code JPY is a body field on a known URI.",
    decision =
      "We will return 422 unknown_code for a POST body whose code is not in the catalog, and 404 not_found only for GET of a missing item.",
    consequences =
      "Clients can tell a path miss from a domain reject. The OpenAPI document lists both statuses on the matching operations."
  )
