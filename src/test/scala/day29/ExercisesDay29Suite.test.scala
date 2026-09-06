package day29

import java.nio.file.Files
import java.nio.file.Path

import munit.FunSuite

class ExercisesDay29Suite extends FunSuite:
  private def deskOpenApiFile: String =
    Files.readString(Path.of("docs", "capstone", "desk.openapi.yaml"))

  private def deskArchitectureFile: String =
    Files.readString(Path.of("docs", "capstone", "desk-architecture.md"))

  private def deskAdrFile: String =
    Files.readString(Path.of("docs", "adr", "0002-unknown-post-code-is-422.md"))

  test("rateOf stays a pure Map lookup"):
    assertEquals(rateOf("GBP"), Some(127))
    assertEquals(rateOf("JPY"), None)

  test("deskOpenApi is desk, not quotes, and has orders not convert"):
    assertEquals(deskOpenApi.title, "desk")
    assertEquals(deskOpenApi.version, "0.1.0")
    assertNotEquals(deskOpenApi.title, quotesOpenApi.title)
    val health = deskOpenApi.paths.find(_.path == "/health").get
    val item = deskOpenApi.paths.find(_.path == "/orders/{code}").get
    val collection = deskOpenApi.paths.find(_.path == "/orders").get
    assertEquals(allowedMethods(health), List(HttpOp.Get))
    assertEquals(allowedMethods(item), List(HttpOp.Get))
    assertEquals(allowedMethods(collection), List(HttpOp.Post))
    assertEquals(pathParamNames("/orders/{code}"), List("code"))
    assert(deskOpenApi.paths.forall(_.path != "/convert"), deskOpenApi.paths.toString)
    assert(deskOpenApi.paths.forall(_.path != "/rates/{code}"), deskOpenApi.paths.toString)

  test("openapiOf(deskOpenApi) is the desk YAML twin"):
    assertEquals(
      openapiOf(deskOpenApi),
      """openapi: 3.1.2
        |info:
        |  title: desk
        |  version: 0.1.0
        |paths:
        |  /health:
        |    get:
        |      summary: Liveness
        |      responses:
        |        '200':
        |          description: Service is up
        |          content:
        |            application/json:
        |              schema:
        |                $ref: '#/components/schemas/HealthOut'
        |  /orders/{code}:
        |    parameters:
        |      - name: code
        |        in: path
        |        required: true
        |        schema:
        |          type: string
        |    get:
        |      summary: Rate for a currency
        |      responses:
        |        '200':
        |          description: Known code
        |          content:
        |            application/json:
        |              schema:
        |                $ref: '#/components/schemas/RateOut'
        |        '404':
        |          description: Unknown code
        |          content:
        |            application/json:
        |              schema:
        |                $ref: '#/components/schemas/ApiError'
        |  /orders:
        |    post:
        |      summary: Place an order
        |      requestBody:
        |        required: true
        |        content:
        |          application/json:
        |            schema:
        |              $ref: '#/components/schemas/OrderIn'
        |      responses:
        |        '200':
        |          description: Placed
        |          content:
        |            application/json:
        |              schema:
        |                $ref: '#/components/schemas/OrderOut'
        |        '400':
        |          description: Malformed JSON
        |          content:
        |            application/json:
        |              schema:
        |                $ref: '#/components/schemas/ApiError'
        |        '422':
        |          description: Well-formed JSON the resource cannot process
        |          content:
        |            application/json:
        |              schema:
        |                $ref: '#/components/schemas/ApiError'
        |components:
        |  schemas:
        |    HealthOut:
        |      type: object
        |      required:
        |        - name
        |      properties:
        |        name:
        |          type: string
        |    RateOut:
        |      type: object
        |      required:
        |        - code
        |        - cents
        |      properties:
        |        code:
        |          type: string
        |        cents:
        |          type: integer
        |    OrderIn:
        |      type: object
        |      required:
        |        - code
        |        - qty
        |      properties:
        |        code:
        |          type: string
        |        qty:
        |          type: integer
        |    OrderOut:
        |      type: object
        |      required:
        |        - code
        |        - qty
        |        - cents
        |      properties:
        |        code:
        |          type: string
        |        qty:
        |          type: integer
        |        cents:
        |          type: integer
        |    ApiError:
        |      type: object
        |      required:
        |        - error
        |      properties:
        |        error:
        |          type: string
        |""".stripMargin
    )
    val text = openapiOf(deskOpenApi)
    assert(!text.contains("  /health:\n    post:"), text)
    assert(!text.contains("/convert"), text)
    assert(!text.contains("/rates/{code}"), text)

  test("docs/capstone/desk.openapi.yaml matches openapiOf(deskOpenApi)"):
    assert(Files.exists(Path.of("docs", "capstone", "desk.openapi.yaml")))
    assertEquals(deskOpenApiFile, openapiOf(deskOpenApi))
    assertEquals(
      Files.readString(Path.of("docs", "capstone", "quotes.openapi.yaml")),
      openapiOf(quotesOpenApi)
    )

  test("deskArchitecture is desk layers, not quotes"):
    assertEquals(deskArchitecture.service, "desk")
    assertNotEquals(deskArchitecture.service, quotesArchitecture.service)
    assertEquals(
      deskArchitecture.layers.map(_.kind),
      List(LayerKind.Core, LayerKind.Http, LayerKind.Persistence, LayerKind.Runtime)
    )
    val core = deskArchitecture.layers.head
    assertEquals(core.owns, "rateOf and orderOf")
    assertEquals(core.notOwns, "HTTP, JDBC, env, logs, metrics")
    assertEquals(deskArchitecture.layers(1).owns, "status mapping, JSON, logs, counters")
    assertEquals(deskArchitecture.layers(1).notOwns, "catalog lookup, SQL, Docker")
    assertEquals(deskArchitecture.layers(2).owns, "Flyway and ConnectionIO.transact")
    assertEquals(deskArchitecture.layers(2).notOwns, "HTTP status codes, rateOf")
    assertEquals(deskArchitecture.layers(3).owns, "Ciris env, HEALTHCHECK, process uid")
    assertEquals(deskArchitecture.layers(3).notOwns, "orderOf")

  test("docs/capstone/desk-architecture.md matches architectureOf(deskArchitecture)"):
    assert(Files.exists(Path.of("docs", "capstone", "desk-architecture.md")))
    assertEquals(deskArchitectureFile, architectureOf(deskArchitecture))
    assertEquals(
      Files.readString(Path.of("docs", "capstone", "quotes-architecture.md")),
      architectureOf(quotesArchitecture)
    )

  test("deskAdr0002 records 422 for unknown POST code"):
    assertEquals(deskAdr0002.number, 2)
    assertEquals(deskAdr0002.title, "Unknown POST code is 422")
    assertEquals(deskAdr0002.status, "Accepted")
    assert(deskAdr0002.decision.startsWith("We will"), deskAdr0002.decision)
    assert(deskAdr0002.decision.contains("422"), deskAdr0002.decision)
    assertEquals(
      deskAdr0002.context,
      "GET /orders/JPY is a missing item at a collection URI. POST /orders with code JPY is a body field on a known URI."
    )
    assertEquals(
      deskAdr0002.decision,
      "We will return 422 unknown_code for a POST body whose code is not in the catalog, and 404 not_found only for GET of a missing item."
    )
    assertEquals(
      deskAdr0002.consequences,
      "Clients can tell a path miss from a domain reject. The OpenAPI document lists both statuses on the matching operations."
    )

  test("docs/adr/0002-unknown-post-code-is-422.md matches adrOf(deskAdr0002)"):
    assert(Files.exists(Path.of("docs", "adr", "0002-unknown-post-code-is-422.md")))
    assertEquals(deskAdrFile, adrOf(deskAdr0002))
    assertEquals(
      Files.readString(Path.of("docs", "adr", "0001-openapi-is-the-http-contract.md")),
      adrOf(quotesAdr0001)
    )
