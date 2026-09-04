package day25

import cats.effect.IO
import cats.effect.IOApp
import ciris.*

val rates: Map[String, Int] = Map("EUR" -> 108, "GBP" -> 127)

/** Cents per unit for a currency code, or None. Pure: no env, no Docker, no JAR. */
def rateOf(code: String): Option[Int] =
  rates.get(code)

/** Code loaded at the rim. The core never sees the env name. */
final case class PackConfig(code: String)

/** Description of config. Loading is an effect; this value is not loaded yet. */
def quotesPackConfig: ConfigValue[Effect, PackConfig] =
  env("QUOTES_CODE").or(prop("quotes.code")).as[String].default("EUR").map(PackConfig.apply)

def loadQuotesPackConfig: IO[PackConfig] =
  quotesPackConfig.load[IO]

/** Lookup after config is already a value. Still no env inside `rateOf`. */
def centsFor(config: PackConfig): Option[Int] =
  rateOf(config.code)

/** Immutable runtime recipe. Building this value does not talk to Docker. */
final case class RuntimeImage(
    repository: String,
    tag: String,
    base: String,
    workdir: String,
    jar: String,
    env: List[(String, String)],
    cmd: List[String]
)

/** `quotes:0.1.0` — a name for `docker build -t`, not a layer in the Dockerfile. */
def imageRef(image: RuntimeImage): String =
  s"${image.repository}:${image.tag}"

def jsonArray(parts: List[String]): String =
  parts.map(part => "\"" + part + "\"").mkString("[", ", ", "]")

/** Text recipe. `CMD` is exec form so the JVM is PID 1, not a shell wrapper. */
def dockerfileOf(image: RuntimeImage): String =
  val lines =
    List(
      s"FROM ${image.base}",
      s"WORKDIR ${image.workdir}",
      s"COPY ${image.jar} ${image.jar}"
    ) ++
      image.env.map { (key, value) => s"ENV $key=$value" } ++
      List(s"CMD ${jsonArray(image.cmd)}")
  lines.mkString("\n") + "\n"

/** Pin: Eclipse Temurin 21 JRE on Alpine 3.24 — JVM 21, not a JDK, not `latest`. */
val quotesRuntime: RuntimeImage = RuntimeImage(
  repository = "quotes",
  tag = "0.1.0",
  base = "eclipse-temurin:21-jre-alpine-3.24",
  workdir = "/app",
  jar = "quotes.jar",
  env = List("QUOTES_CODE" -> "EUR"),
  cmd = List("java", "-jar", "quotes.jar")
)

object Day25 extends IOApp.Simple:
  val run: IO[Unit] =
    loadQuotesPackConfig.flatMap { config =>
      IO.println(centsFor(config))
    }
