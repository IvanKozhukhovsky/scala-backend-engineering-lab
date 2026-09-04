package day25

import java.nio.file.Files
import java.nio.file.Path

import munit.FunSuite

class ExercisesDay25Suite extends FunSuite:
  private def deskFile: String =
    Files.readString(Path.of("packaging", "desk", "Dockerfile"))

  test("rateOf stays a pure Map lookup"):
    assertEquals(rateOf("EUR"), Some(108))
    assertEquals(rateOf("USD"), None)

  test("deskRuntime is a desk JRE image, not quotes and not Postgres"):
    assertEquals(deskRuntime.repository, "desk")
    assertEquals(deskRuntime.tag, "0.1.0")
    assertEquals(deskRuntime.base, "eclipse-temurin:21-jre-alpine-3.24")
    assertEquals(deskRuntime.workdir, "/app")
    assertEquals(deskRuntime.jar, "desk.jar")
    assertEquals(deskRuntime.env, List("DESK_CODE" -> "GBP"))
    assertEquals(deskRuntime.cmd, List("java", "-jar", "desk.jar"))
    assertNotEquals(deskRuntime.repository, quotesRuntime.repository)
    assertNotEquals(deskRuntime.jar, quotesRuntime.jar)
    assert(!deskRuntime.base.contains("postgres"))

  test("dockerfileOf(deskRuntime) has no scala-cli, source tree, or secrets"):
    val text = dockerfileOf(deskRuntime)
    assert(text.startsWith("FROM eclipse-temurin:21-jre-alpine-3.24\n"), text)
    assert(text.contains("COPY desk.jar desk.jar"), text)
    assert(text.contains("ENV DESK_CODE=GBP"), text)
    assert(text.contains("CMD [\"java\", \"-jar\", \"desk.jar\"]"), text)
    assert(!text.contains("scala-cli"), text)
    assert(!text.contains("postgres"), text)
    assert(!text.contains("COPY ."), text)
    assert(!text.toLowerCase.contains("token"), text)
    assert(!text.toLowerCase.contains("password"), text)

  test("packaging/desk/Dockerfile matches dockerfileOf(deskRuntime)"):
    assert(Files.exists(Path.of("packaging", "desk", "Dockerfile")))
    assertEquals(deskFile, dockerfileOf(deskRuntime))

  test("imageRef for desk is desk:0.1.0"):
    assertEquals(imageRef(deskRuntime), "desk:0.1.0")
