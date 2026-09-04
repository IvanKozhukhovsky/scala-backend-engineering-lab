package day25

import java.nio.file.Files
import java.nio.file.Path

import munit.FunSuite

class Day25Suite extends FunSuite:
  private def quotesFile: String =
    Files.readString(Path.of("packaging", "quotes", "Dockerfile"))

  test("rateOf stays a pure Map lookup"):
    assertEquals(rateOf("EUR"), Some(108))
    assertEquals(rateOf("USD"), None)

  test("centsFor uses a prepared code, not an env name"):
    assertEquals(centsFor(PackConfig("GBP")), Some(127))
    assertEquals(centsFor(PackConfig("JPY")), None)

  test("imageRef is repository colon tag, not latest"):
    assertEquals(imageRef(quotesRuntime), "quotes:0.1.0")

  test("quotes runtime is a JRE 21 Alpine pin, not Postgres and not a JDK"):
    assertEquals(quotesRuntime.base, "eclipse-temurin:21-jre-alpine-3.24")
    assertEquals(quotesRuntime.jar, "quotes.jar")
    assertEquals(quotesRuntime.cmd, List("java", "-jar", "quotes.jar"))
    assertEquals(quotesRuntime.env, List("QUOTES_CODE" -> "EUR"))
    assert(!quotesRuntime.base.contains("postgres"))
    assert(!quotesRuntime.base.contains("jdk"))

  test("dockerfileOf is FROM, WORKDIR, COPY jar, ENV, exec-form CMD"):
    val text = dockerfileOf(quotesRuntime)
    assertEquals(
      text,
      """FROM eclipse-temurin:21-jre-alpine-3.24
        |WORKDIR /app
        |COPY quotes.jar quotes.jar
        |ENV QUOTES_CODE=EUR
        |CMD ["java", "-jar", "quotes.jar"]
        |""".stripMargin
    )
    assert(!text.contains("scala-cli"))
    assert(!text.contains("postgres"))
    assert(!text.contains("COPY ."))
    assert(!text.toLowerCase.contains("token"))
    assert(!text.toLowerCase.contains("secret"))
    assert(!text.contains("latest"))

  test("packaging/quotes/Dockerfile matches dockerfileOf"):
    assertEquals(quotesFile, dockerfileOf(quotesRuntime))
