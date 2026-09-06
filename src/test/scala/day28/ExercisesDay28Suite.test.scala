package day28

import java.nio.file.Files
import java.nio.file.Path

import day27.deskHardened
import day27.secretInRecipe
import munit.FunSuite

class ExercisesDay28Suite extends FunSuite:
  private def observedFile: String =
    Files.readString(Path.of("packaging", "desk", "observed.Dockerfile"))

  test("rateOf stays a pure Map lookup"):
    assertEquals(rateOf("GBP"), Some(127))
    assertEquals(rateOf("JPY"), None)

  test("deskObserved is desk at uid 1000 with a loopback /health probe"):
    assertEquals(deskObserved.hardened.runtime.repository, "desk")
    assertEquals(deskObserved.hardened.runtime.jar, "desk.jar")
    assertEquals(deskObserved.hardened.uid, 1000)
    assertEquals(deskObserved.hardened, deskHardened)
    assertNotEquals(deskObserved.hardened.runtime.jar, quotesObserved.hardened.runtime.jar)
    assertEquals(deskObserved.probe.path, "/health")
    assertEquals(deskObserved.probe.port, 8080)
    assertEquals(deskObserved.probe.interval, "30s")
    assertEquals(deskObserved.probe.timeout, "3s")
    assertEquals(deskObserved.probe.retries, 3)
    assertEquals(deskObserved.probe.cmd.head, "wget")
    assert(!deskObserved.probe.cmd.exists(_.contains("localhost")), deskObserved.probe.cmd.toString)
    assert(!deskObserved.probe.cmd.exists(_.contains("/metrics")), deskObserved.probe.cmd.toString)

  test("observedDockerfileOf(deskObserved) has HEALTHCHECK after USER and no secrets"):
    val text = observedDockerfileOf(deskObserved)
    assert(text.startsWith("FROM eclipse-temurin:21-jre-alpine-3.24\n"), text)
    assert(text.contains("COPY desk.jar desk.jar"), text)
    assert(text.contains("ENV DESK_CODE=GBP"), text)
    assert(text.contains("RUN adduser -D -H -u 1000 app"), text)
    assert(text.contains("USER 1000"), text)
    assert(
      text.contains(
        "HEALTHCHECK --interval=30s --timeout=3s --retries=3 CMD [\"wget\", \"-qO-\", \"http://127.0.0.1:8080/health\"]"
      ),
      text
    )
    assert(text.contains("CMD [\"java\", \"-jar\", \"desk.jar\"]"), text)
    assert(text.indexOf("HEALTHCHECK") > text.indexOf("USER 1000"), text)
    assert(text.indexOf("CMD ") > text.indexOf("HEALTHCHECK"), text)
    assert(!text.contains("USER 0"), text)
    assert(!text.contains("localhost"), text)
    assert(!text.contains("/metrics"), text)
    assert(!secretInRecipe(text), text)
    assert(!text.contains("scala-cli"), text)
    assert(!text.contains("COPY ."), text)

  test("packaging/desk/observed.Dockerfile matches observedDockerfileOf(deskObserved)"):
    assert(Files.exists(Path.of("packaging", "desk", "observed.Dockerfile")))
    assertEquals(observedFile, observedDockerfileOf(deskObserved))
    assertEquals(
      Files.readString(Path.of("packaging", "desk", "hardened.Dockerfile")),
      day27.hardenedDockerfileOf(deskHardened)
    )
    assertEquals(
      Files.readString(Path.of("packaging", "desk", "Dockerfile")),
      day25.dockerfileOf(day25.deskRuntime)
    )

  test("deskRequests is a desk _total counter, not quotes"):
    assertEquals(deskRequests.name, "desk_http_requests_total")
    assert(isCounterName(deskRequests.name), deskRequests.name)
    assertEquals(deskRequests.help, "HTTP requests")
    assertEquals(deskRequests.kind, MetricKind.Counter)
    assertEquals(deskRequests.labels, List("code" -> "200"))
    assertEquals(deskRequests.value, 0L)
    assertNotEquals(deskRequests.name, quotesRequests.name)
    assertEquals(
      scrapeOf(deskRequests),
      """# HELP desk_http_requests_total HTTP requests
        |# TYPE desk_http_requests_total counter
        |desk_http_requests_total{code="200"} 0
        |""".stripMargin
    )
    assertEquals(scrapeOf(bump(deskRequests)).contains(" 1\n"), true)
