package day27

import java.nio.file.Files
import java.nio.file.Path

import day26.WorkflowStep
import day26.deskPipeline
import day26.workflowOf
import munit.FunSuite

class ExercisesDay27Suite extends FunSuite:
  private def hardenedFile: String =
    Files.readString(Path.of("packaging", "desk", "hardened.Dockerfile"))

  private def pinnedFile: String =
    Files.readString(Path.of("ci", "desk-pinned.yml"))

  test("deskHardened is desk at uid 1000, not quotes and not root"):
    assertEquals(deskHardened.runtime.repository, "desk")
    assertEquals(deskHardened.runtime.jar, "desk.jar")
    assertEquals(deskHardened.runtime.env, List("DESK_CODE" -> "GBP"))
    assertEquals(deskHardened.userName, "app")
    assertEquals(deskHardened.uid, 1000)
    assertNotEquals(deskHardened.runtime.jar, quotesHardened.runtime.jar)
    assertNotEquals(deskHardened.uid, 0)

  test("hardenedDockerfileOf(deskHardened) has USER after adduser and no secrets"):
    val text = hardenedDockerfileOf(deskHardened)
    assert(text.startsWith("FROM eclipse-temurin:21-jre-alpine-3.24\n"), text)
    assert(text.contains("COPY desk.jar desk.jar"), text)
    assert(text.contains("ENV DESK_CODE=GBP"), text)
    assert(text.contains("RUN adduser -D -H -u 1000 app"), text)
    assert(text.contains("USER 1000"), text)
    assert(text.contains("CMD [\"java\", \"-jar\", \"desk.jar\"]"), text)
    assert(text.indexOf("USER 1000") > text.indexOf("adduser"), text)
    assert(!text.contains("USER 0"), text)
    assert(!secretInRecipe(text), text)
    assert(!text.contains("scala-cli"), text)
    assert(!text.contains("COPY ."), text)

  test("packaging/desk/hardened.Dockerfile matches hardenedDockerfileOf(deskHardened)"):
    assert(Files.exists(Path.of("packaging", "desk", "hardened.Dockerfile")))
    assertEquals(hardenedFile, hardenedDockerfileOf(deskHardened))
    assertEquals(
      Files.readString(Path.of("packaging", "desk", "Dockerfile")),
      day25.dockerfileOf(day25.deskRuntime)
    )

  test("deskActionPins are checkout and scala-cli-setup, reused not invented"):
    assertEquals(
      deskActionPins.map(_.ownerRepo).toSet,
      Set("actions/checkout", "VirtusLab/scala-cli-setup")
    )
    assertEquals(
      deskActionPins.map(pin => pin.ownerRepo -> (pin.sha, pin.tag)).toMap,
      Map(
        checkoutPin.ownerRepo -> (checkoutPin.sha, checkoutPin.tag),
        scalaCliSetupPin.ownerRepo -> (scalaCliSetupPin.sha, scalaCliSetupPin.tag)
      )
    )
    assert(deskActionPins.forall(pin => isFullSha(pin.sha)), deskActionPins.toString)
    assert(!deskActionPins.exists(_.ownerRepo == cacheActionPin.ownerRepo), deskActionPins.toString)

  test("deskPinnedPipeline is Desk with SHA uses, not quotes and not tag pins"):
    assertEquals(deskPinnedPipeline.name, "Desk")
    assertEquals(deskPinnedPipeline.job.id, "desk")
    assertNotEquals(deskPinnedPipeline.job.id, quotesPinnedPipeline.job.id)
    val uses = deskPinnedPipeline.job.steps.collect { case WorkflowStep.Uses(_, action, _) =>
      action
    }
    assertEquals(uses, List(checkoutPin.usesRef, scalaCliSetupPin.usesRef))
    assert(uses.forall(ref => shaFromUses(ref).isDefined), uses.toString)
    assert(!uses.exists(_.endsWith("@v7")), uses.toString)
    assert(!uses.exists(_.contains("@main")), uses.toString)

  test("ci/desk-pinned.yml matches workflowOf(deskPinnedPipeline)"):
    assert(Files.exists(Path.of("ci", "desk-pinned.yml")))
    val text = workflowOf(deskPinnedPipeline)
    assertEquals(pinnedFile, text)
    assert(!text.contains("ubuntu-latest"), text)
    assert(!text.contains("write-all"), text)
    assert(!text.contains("continue-on-error"), text)
    assert(!text.contains("cache-action"), text)
    assert(!Files.exists(Path.of(".github", "workflows", "desk-pinned.yml")))
    assertEquals(Files.readString(Path.of("ci", "desk.yml")), workflowOf(deskPipeline))
