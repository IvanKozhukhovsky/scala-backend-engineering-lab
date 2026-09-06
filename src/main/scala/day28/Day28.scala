package day28

import day25.jsonArray
import day27.HardenedImage
import day27.quotesHardened

val rates: Map[String, Int] = Map("EUR" -> 108, "GBP" -> 127)

/** Cents per unit for a currency code, or None. Pure: no logs, no metrics, no Docker. */
def rateOf(code: String): Option[Int] =
  rates.get(code)

/** W3C trace-id: 16 bytes as 32 lowercase hex. All zeroes is invalid. */
def isTraceId(id: String): Boolean =
  id.length == 32 &&
    id != "0" * 32 &&
    id.forall(c => (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f'))

/** `00-<trace-id>-<parent-id>-<flags>`. Building this does not talk to a collector. */
def traceIdFromParent(header: String): Option[String] =
  header.split("-") match
    case Array(_, id, _, _) if isTraceId(id) => Some(id)
    case _                                   => None

/** Day 19 ctx plus `trace_id` so a log line can join a request. */
def logCtx(traceId: String, code: String, op: String): Map[String, String] =
  Map("trace_id" -> traceId, "code" -> code, "op" -> op)

/** Prometheus counter. A gauge can go down; this unit does not bump those. */
enum MetricKind:
  case Counter

final case class MetricSample(
    name: String,
    help: String,
    kind: MetricKind,
    labels: List[(String, String)],
    value: Long
)

/** Prometheus: an accumulating count SHOULD end with `_total`. */
def isCounterName(name: String): Boolean =
  name.endsWith("_total")

def bump(sample: MetricSample): MetricSample =
  sample.copy(value = sample.value + 1)

/** Prometheus text exposition. Building this string does not scrape a process. */
def scrapeOf(sample: MetricSample): String =
  val kind = sample.kind match
    case MetricKind.Counter => "counter"
  val labelBlock =
    if sample.labels.isEmpty then ""
    else sample.labels.map((key, value) => s"""$key="$value"""").mkString("{", ",", "}")
  s"""# HELP ${sample.name} ${sample.help}
     |# TYPE ${sample.name} $kind
     |${sample.name}$labelBlock ${sample.value}
     |""".stripMargin

val quotesRequests: MetricSample =
  MetricSample(
    name = "quotes_http_requests_total",
    help = "HTTP requests",
    kind = MetricKind.Counter,
    labels = List("code" -> "200"),
    value = 0L
  )

/** Docker HEALTHCHECK recipe. The command runs *inside* the container. */
final case class HealthProbe(
    interval: String,
    timeout: String,
    retries: Int,
    port: Int,
    path: String
):
  def cmd: List[String] =
    List("wget", "-qO-", s"http://127.0.0.1:$port$path")

def healthcheckLine(probe: HealthProbe): String =
  s"HEALTHCHECK --interval=${probe.interval} --timeout=${probe.timeout} --retries=${probe.retries} CMD ${jsonArray(probe.cmd)}"

final case class ObservedImage(hardened: HardenedImage, probe: HealthProbe)

/** Day 27 recipe plus HEALTHCHECK before CMD. Building this does not talk to Docker. */
def observedDockerfileOf(image: ObservedImage): String =
  val hardened = day27.hardenedDockerfileOf(image.hardened)
  val cmdIdx = hardened.indexOf("CMD ")
  hardened.take(cmdIdx) + healthcheckLine(image.probe) + "\n" + hardened.drop(cmdIdx)

val quotesProbe: HealthProbe =
  HealthProbe(interval = "30s", timeout = "3s", retries = 3, port = 8080, path = "/health")

val quotesObserved: ObservedImage =
  ObservedImage(hardened = quotesHardened, probe = quotesProbe)
