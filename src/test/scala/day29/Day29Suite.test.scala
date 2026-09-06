package day29

import java.nio.file.Files
import java.nio.file.Path

import munit.FunSuite

class Day29Suite extends FunSuite:
  private def quotesOpenApiFile: String =
    Files.readString(Path.of("docs", "capstone", "quotes.openapi.yaml"))

  private def quotesArchitectureFile: String =
    Files.readString(Path.of("docs", "capstone", "quotes-architecture.md"))

  private def quotesAdrFile: String =
    Files.readString(Path.of("docs", "adr", "0001-openapi-is-the-http-contract.md"))

  test("rateOf stays a pure Map lookup"):
    assertEquals(rateOf("EUR"), Some(108))
    assertEquals(rateOf("USD"), None)

  test("openapi is 3.1.2 and info.version is the document"):
    assertEquals(oasVersion, "3.1.2")
    assertEquals(quotesOpenApi.version, "0.1.0")
    val text = openapiOf(quotesOpenApi)
    assert(text.startsWith("openapi: 3.1.2\n"), text)
    assert(text.contains("  title: quotes\n"), text)
    assert(text.contains("  version: 0.1.0\n"), text)
    assert(!text.contains("openapi: 3.2"), text)

  test("quotesOpenApi lists GET /health, GET /rates/{code}, POST /convert"):
    assertEquals(quotesOpenApi.title, "quotes")
    val health = quotesOpenApi.paths.find(_.path == "/health").get
    val rates = quotesOpenApi.paths.find(_.path == "/rates/{code}").get
    val convert = quotesOpenApi.paths.find(_.path == "/convert").get
    assertEquals(allowedMethods(health), List(HttpOp.Get))
    assertEquals(allowedMethods(rates), List(HttpOp.Get))
    assertEquals(allowedMethods(convert), List(HttpOp.Post))
    assertEquals(pathParamNames("/rates/{code}"), List("code"))
    assertEquals(pathParamNames("/health"), Nil)

  test("openapiOf writes path parameters and does not document POST /health"):
    val text = openapiOf(quotesOpenApi)
    assert(
      text.contains(
        "  /rates/{code}:\n    parameters:\n      - name: code\n        in: path\n        required: true"
      ),
      text
    )
    assert(text.contains("  /convert:\n    post:"), text)
    assert(!text.contains("  /health:\n    post:"), text)
    assert(!text.contains("/metrics"), text)
    assert(!text.contains("/orders"), text)
    assert(!text.contains("HttpRoutes"), text)
    assert(!text.contains("Ember"), text)

  test("openapiOf is the quotes YAML twin"):
    assertEquals(
      openapiOf(quotesOpenApi),
      """openapi: 3.1.2
        |info:
        |  title: quotes
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
        |  /rates/{code}:
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
        |  /convert:
        |    post:
        |      summary: Convert an amount
        |      requestBody:
        |        required: true
        |        content:
        |          application/json:
        |            schema:
        |              $ref: '#/components/schemas/ConvertIn'
        |      responses:
        |        '200':
        |          description: Converted
        |          content:
        |            application/json:
        |              schema:
        |                $ref: '#/components/schemas/ConvertOut'
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
        |    ConvertIn:
        |      type: object
        |      required:
        |        - code
        |        - qty
        |      properties:
        |        code:
        |          type: string
        |        qty:
        |          type: integer
        |    ConvertOut:
        |      type: object
        |      required:
        |        - code
        |        - cents
        |      properties:
        |        code:
        |          type: string
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

  test("docs/capstone/quotes.openapi.yaml matches openapiOf"):
    assertEquals(quotesOpenApiFile, openapiOf(quotesOpenApi))

  test("quotesArchitecture names four layers and keeps rateOf off HTTP"):
    assertEquals(quotesArchitecture.service, "quotes")
    assertEquals(
      quotesArchitecture.layers.map(_.kind),
      List(LayerKind.Core, LayerKind.Http, LayerKind.Persistence, LayerKind.Runtime)
    )
    val core = quotesArchitecture.layers.head
    assert(core.owns.contains("rateOf"), core.owns)
    assert(core.notOwns.contains("HTTP"), core.notOwns)

  test("docs/capstone/quotes-architecture.md matches architectureOf"):
    assertEquals(quotesArchitectureFile, architectureOf(quotesArchitecture))

  test("quotesAdr0001 is accepted and names OpenAPI as the contract"):
    assertEquals(quotesAdr0001.number, 1)
    assertEquals(quotesAdr0001.status, "Accepted")
    assert(quotesAdr0001.decision.contains("We will"), quotesAdr0001.decision)
    assert(quotesAdr0001.decision.contains("OpenAPI"), quotesAdr0001.decision)

  test("docs/adr/0001-openapi-is-the-http-contract.md matches adrOf"):
    assertEquals(quotesAdrFile, adrOf(quotesAdr0001))
