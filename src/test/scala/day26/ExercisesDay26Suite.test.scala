package day26

import java.nio.file.Files
import java.nio.file.Path

import munit.FunSuite

class ExercisesDay26Suite extends FunSuite:
  private def deskFile: String =
    Files.readString(Path.of("ci", "desk.yml"))

  private def dependabotFile: String =
    Files.readString(Path.of(".github", "dependabot.yml"))

  test("deskPipeline is a Desk gate, not quotes and not ubuntu-latest"):
    assertEquals(deskPipeline.name, "Desk")
    assertEquals(deskPipeline.pushBranches, Nil)
    assertEquals(deskPipeline.pullRequest, true)
    assertEquals(deskPipeline.workflowDispatch, false)
    assertEquals(deskPipeline.contents, "read")
    assertEquals(deskPipeline.job.id, "desk")
    assertEquals(deskPipeline.job.runner, "ubuntu-24.04")
    assertNotEquals(deskPipeline.name, quotesPipeline.name)
    assertNotEquals(deskPipeline.job.id, quotesPipeline.job.id)
    val actions = deskPipeline.job.steps.collect { case WorkflowStep.Uses(_, action, _) =>
      action
    }
    assertEquals(actions, List("actions/checkout@v7", "VirtusLab/scala-cli-setup@v1.16.0"))
    assert(!actions.exists(_.endsWith("@main")), actions.toString)

  test("workflowOf(deskPipeline) has no latest, write-all, sbt, or continue-on-error"):
    val text = workflowOf(deskPipeline)
    assert(text.startsWith("name: Desk\n"), text)
    assert(text.contains("runs-on: ubuntu-24.04"), text)
    assert(text.contains("permissions:\n  contents: read\n"), text)
    assert(text.contains("scala-cli fmt --check ."), text)
    assert(text.contains("scala-cli test . --test-only 'day26.ExercisesDay26Suite'"), text)
    assert(!text.contains("ubuntu-latest"), text)
    assert(!text.contains("write-all"), text)
    assert(!text.contains("continue-on-error"), text)
    assert(!text.contains("progress.py"), text)
    assert(!text.contains("@main"), text)

  test("ci/desk.yml matches workflowOf(deskPipeline)"):
    assert(Files.exists(Path.of("ci", "desk.yml")))
    assertEquals(deskFile, workflowOf(deskPipeline))
    assert(!Files.exists(Path.of(".github", "workflows", "desk.yml")))

  test("deskUpdates is docker at /packaging/desk weekly"):
    assertEquals(deskUpdates.ecosystem, Ecosystem.Docker)
    assertEquals(deskUpdates.directory, "/packaging/desk")
    assertEquals(deskUpdates.interval, "weekly")
    assertNotEquals(deskUpdates.directory, "/packaging/quotes")

  test("dependabot.yml is quotesUpdates plus deskUpdates"):
    assertEquals(dependabotFile, dependabotOf(quotesUpdates :+ deskUpdates))
    assert(dependabotFile.contains("directory: /packaging/desk"), dependabotFile)
    assert(!dependabotFile.contains("sbt"), dependabotFile)
