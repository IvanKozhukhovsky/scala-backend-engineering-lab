package day29

val rates: Map[String, Int] = Map("EUR" -> 108, "GBP" -> 127)

/** Cents per unit for a currency code, or None. Pure: no HTTP, no YAML, no files. */
def rateOf(code: String): Option[Int] =
  rates.get(code)

/** OAS 3.1 line at teach time. 3.2.0 exists; this unit does not need QUERY or `$self`. */
val oasVersion: String = "3.1.2"

enum HttpOp:
  case Get, Post
  def yaml: String =
    this match
      case HttpOp.Get  => "get"
      case HttpOp.Post => "post"

enum JsonType:
  case Str, Int
  def yaml: String =
    this match
      case JsonType.Str => "string"
      case JsonType.Int => "integer"

final case class JsonField(name: String, tpe: JsonType)

final case class JsonSchema(name: String, fields: List[JsonField]):
  def required: List[String] = fields.map(_.name)

final case class ApiResponse(status: String, description: String, schema: String)

final case class ApiOperation(
    method: HttpOp,
    summary: String,
    requestBody: Option[String],
    responses: List[ApiResponse]
)

final case class ApiPath(path: String, operations: List[ApiOperation])

/** Immutable OpenAPI document. Building this does not bind a port or write a file. */
final case class OpenApiSpec(
    title: String,
    version: String,
    paths: List[ApiPath],
    schemas: List[JsonSchema]
)

enum LayerKind:
  case Core, Http, Persistence, Runtime

final case class Layer(kind: LayerKind, owns: String, notOwns: String)

/** Named ownership. Building this does not start Postgres or Ember. */
final case class ServiceArchitecture(service: String, layers: List[Layer])

/** One architecturally significant decision. Nygard: title, context, decision, status,
  * consequences.
  */
final case class Adr(
    number: Int,
    title: String,
    status: String,
    context: String,
    decision: String,
    consequences: String
)

private val pathParam = raw"\{([^}]+)\}".r

def pathParamNames(path: String): List[String] =
  pathParam.findAllMatchIn(path).map(_.group(1)).toList

def allowedMethods(path: ApiPath): List[HttpOp] =
  path.operations.map(_.method)

def yamlDoc(lines: List[String]): String =
  lines.mkString("\n") + "\n"

def refLine(indent: String, schema: String): String =
  s"$indent$$ref: '#/components/schemas/$schema'"

def pathParamLines(path: String): List[String] =
  pathParamNames(path) match
    case Nil   => Nil
    case names =>
      "    parameters:" :: names.flatMap { name =>
        List(
          s"      - name: $name",
          "        in: path",
          "        required: true",
          "        schema:",
          "          type: string"
        )
      }

def responseLines(response: ApiResponse): List[String] =
  List(
    s"        '${response.status}':",
    s"          description: ${response.description}",
    "          content:",
    "            application/json:",
    "              schema:",
    refLine("                ", response.schema)
  )

def operationLines(op: ApiOperation): List[String] =
  val header = List(s"    ${op.method.yaml}:", s"      summary: ${op.summary}")
  val body = op.requestBody match
    case None         => Nil
    case Some(schema) =>
      List(
        "      requestBody:",
        "        required: true",
        "        content:",
        "          application/json:",
        "            schema:",
        refLine("              ", schema)
      )
  header ++ body ++ ("      responses:" :: op.responses.flatMap(responseLines))

def schemaLines(schema: JsonSchema): List[String] =
  val required = "      required:" :: schema.required.map(name => s"        - $name")
  val properties = "      properties:" :: schema.fields.flatMap { field =>
    List(s"        ${field.name}:", s"          type: ${field.tpe.yaml}")
  }
  List(s"    ${schema.name}:", "      type: object") ++ required ++ properties

/** OpenAPI YAML. Building this string does not talk to Swagger UI or Ember. */
def openapiOf(spec: OpenApiSpec): String =
  val pathBlock = spec.paths.flatMap { item =>
    s"  ${item.path}:" :: pathParamLines(item.path) ++ item.operations.flatMap(operationLines)
  }
  val schemaBlock =
    spec.schemas match
      case Nil     => Nil
      case schemas =>
        List("components:", "  schemas:") ++ schemas.flatMap(schemaLines)
  yamlDoc(
    List(
      s"openapi: $oasVersion",
      "info:",
      s"  title: ${spec.title}",
      s"  version: ${spec.version}",
      "paths:"
    ) ++ pathBlock ++ schemaBlock
  )

/** Short architecture note. Building this does not implement the service. */
def architectureOf(arch: ServiceArchitecture): String =
  val blocks = arch.layers.map { layer =>
    s"""## ${layer.kind}
       |
       |Owns: ${layer.owns}
       |
       |Does not own: ${layer.notOwns}
       |""".stripMargin
  }
  s"""# ${arch.service} architecture
     |
     |""".stripMargin + blocks.mkString("\n")

/** Nygard ADR markdown. Building this does not change the running system. */
def adrOf(adr: Adr): String =
  s"""# ADR ${adr.number}: ${adr.title}
     |
     |## Context
     |
     |${adr.context}
     |
     |## Decision
     |
     |${adr.decision}
     |
     |## Status
     |
     |${adr.status}
     |
     |## Consequences
     |
     |${adr.consequences}
     |""".stripMargin

def jsonObject(name: String, fields: JsonField*): JsonSchema =
  JsonSchema(name, fields.toList)

val healthOut: JsonSchema =
  jsonObject("HealthOut", JsonField("name", JsonType.Str))

val rateOut: JsonSchema =
  jsonObject("RateOut", JsonField("code", JsonType.Str), JsonField("cents", JsonType.Int))

val convertIn: JsonSchema =
  jsonObject("ConvertIn", JsonField("code", JsonType.Str), JsonField("qty", JsonType.Int))

val convertOut: JsonSchema =
  jsonObject("ConvertOut", JsonField("code", JsonType.Str), JsonField("cents", JsonType.Int))

val apiError: JsonSchema =
  jsonObject("ApiError", JsonField("error", JsonType.Str))

val quotesOpenApi: OpenApiSpec =
  OpenApiSpec(
    title = "quotes",
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
        "/rates/{code}",
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
        "/convert",
        List(
          ApiOperation(
            HttpOp.Post,
            "Convert an amount",
            Some("ConvertIn"),
            List(
              ApiResponse("200", "Converted", "ConvertOut"),
              ApiResponse("400", "Malformed JSON", "ApiError"),
              ApiResponse("422", "Well-formed JSON the resource cannot process", "ApiError")
            )
          )
        )
      )
    ),
    schemas = List(healthOut, rateOut, convertIn, convertOut, apiError)
  )

val quotesArchitecture: ServiceArchitecture =
  ServiceArchitecture(
    service = "quotes",
    layers = List(
      Layer(LayerKind.Core, "rateOf and convertAmount", "HTTP, JDBC, env, logs, metrics"),
      Layer(LayerKind.Http, "status mapping, JSON, logs, counters", "catalog lookup, SQL, Docker"),
      Layer(LayerKind.Persistence, "Flyway and ConnectionIO.transact", "HTTP status codes, rateOf"),
      Layer(LayerKind.Runtime, "Ciris env, HEALTHCHECK, process uid", "convertAmount")
    )
  )

val quotesAdr0001: Adr =
  Adr(
    number = 1,
    title = "OpenAPI is the HTTP contract",
    status = "Accepted",
    context =
      "Days 17 through 19 encoded the HTTP surface as HttpRoutes. scala-030 will implement the capstone with TDD. A reviewer who only reads Scala cannot see the intended operations without running tests.",
    decision =
      "We will keep a checked-in OpenAPI 3.1.2 document as the HTTP contract. Tests compare the file to openapiOf. Implementation must match the spec.",
    consequences =
      "TDD has an oracle without Ember. Methods omitted from a path are not documented operations; the service still answers 405. Spec drift fails a test. This unit does not generate http4s from OpenAPI."
  )
