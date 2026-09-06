package day28

import java.nio.file.Files
import java.nio.file.Path

import day27.secretInRecipe
import munit.FunSuite

class Day28Suite extends FunSuite:
  private def observedFile: String =
    Files.readString(Path.of("packaging", "quotes", "observed.Dockerfile"))

  test("rateOf stays a pure Map lookup"):
    assertEquals(rateOf("EUR"), Some(108))
    assertEquals(rateOf("USD"), None)

  test("isTraceId accepts 32 lowercase hex and rejects tags, uppercase, and all zeroes"):
    val id = "4bf92f3577b34da6a3ce929d0e0e4736"
    assert(isTraceId(id))
    assert(!isTraceId("v7"))
    assert(!isTraceId("4bf92f35"))
    assert(!isTraceId(id.toUpperCase))
    assert(!isTraceId("0" * 32))

  test("traceIdFromParent reads the W3C trace-id field"):
    val id = "4bf92f3577b34da6a3ce929d0e0e4736"
    assertEquals(
      traceIdFromParent(s"00-$id-00f067aa0ba902b7-01"),
      Some(id)
    )
    assertEquals(traceIdFromParent(s"00-${"0" * 32}-00f067aa0ba902b7-01"), None)
    assertEquals(traceIdFromParent("not-a-header"), None)

  test("logCtx puts trace_id beside code and op, not a token"):
    val id = "4bf92f3577b34da6a3ce929d0e0e4736"
    val ctx = logCtx(id, "EUR", "get_rate")
    assertEquals(ctx, Map("trace_id" -> id, "code" -> "EUR", "op" -> "get_rate"))
    assert(!ctx.values.exists(_.toLowerCase.contains("token")), ctx.toString)

  test("quotesRequests is a _total counter and scrapeOf is Prometheus text"):
    assert(isCounterName(quotesRequests.name), quotesRequests.name)
    assertEquals(quotesRequests.kind, MetricKind.Counter)
    assertEquals(quotesRequests.value, 0L)
    assertEquals(
      scrapeOf(quotesRequests),
      """# HELP quotes_http_requests_total HTTP requests
        |# TYPE quotes_http_requests_total counter
        |quotes_http_requests_total{code="200"} 0
        |""".stripMargin
    )
    assertEquals(scrapeOf(bump(quotesRequests)).contains(" 1\n"), true)

  test("quotesObserved keeps Day 27 uid 1000 and probes loopback /health"):
    assertEquals(quotesObserved.hardened.runtime.jar, "quotes.jar")
    assertEquals(quotesObserved.hardened.uid, 1000)
    assertEquals(quotesObserved.probe.path, "/health")
    assertEquals(quotesObserved.probe.port, 8080)
    assertEquals(quotesObserved.probe.interval, "30s")
    assertEquals(quotesObserved.probe.timeout, "3s")
    assertEquals(quotesObserved.probe.retries, 3)
    assertEquals(quotesObserved.probe.cmd.head, "wget")

  test("observedDockerfileOf is Day 27 plus HEALTHCHECK before CMD"):
    val text = observedDockerfileOf(quotesObserved)
    assertEquals(
      text,
      """FROM eclipse-temurin:21-jre-alpine-3.24
        |WORKDIR /app
        |COPY quotes.jar quotes.jar
        |ENV QUOTES_CODE=EUR
        |RUN adduser -D -H -u 1000 app
        |USER 1000
        |HEALTHCHECK --interval=30s --timeout=3s --retries=3 CMD ["wget", "-qO-", "http://127.0.0.1:8080/health"]
        |CMD ["java", "-jar", "quotes.jar"]
        |""".stripMargin
    )
    assert(text.indexOf("HEALTHCHECK") > text.indexOf("USER 1000"), text)
    assert(text.indexOf("CMD ") > text.indexOf("HEALTHCHECK"), text)
    assert(!text.contains("localhost"), text)
    assert(!text.contains("/metrics"), text)
    assert(!text.contains("USER 0"), text)
    assert(!secretInRecipe(text), text)

  test("packaging/quotes/observed.Dockerfile matches observedDockerfileOf"):
    assertEquals(observedFile, observedDockerfileOf(quotesObserved))
    assertEquals(
      Files.readString(Path.of("packaging", "quotes", "hardened.Dockerfile")),
      day27.hardenedDockerfileOf(day27.quotesHardened)
    )
