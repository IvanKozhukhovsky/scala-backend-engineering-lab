package day26

import java.nio.file.Files
import java.nio.file.Path

import munit.FunSuite

class Day26Suite extends FunSuite:
  private def ciFile: String =
    Files.readString(Path.of(".github", "workflows", "ci.yml"))

  private def dependabotFile: String =
    Files.readString(Path.of(".github", "dependabot.yml"))

  private def projectFile: String =
    Files.readString(Path.of("project.scala"))

  test("usingDirective distinguishes compile dep from test.dep"):
    assertEquals(
      usingDirective("org.typelevel::cats-effect::3.6.4", DepScope.Compile),
      "//> using dep \"org.typelevel::cats-effect::3.6.4\""
    )
    assertEquals(
      usingDirective("org.scalameta::munit::1.0.4", DepScope.Test),
      "//> using test.dep \"org.scalameta::munit::1.0.4\""
    )

  test("project.scala keeps MUnit on test.dep"):
    val munitTest = usingDirective("org.scalameta::munit::1.0.4", DepScope.Test)
    val munitCompile = usingDirective("org.scalameta::munit::1.0.4", DepScope.Compile)
    assert(projectFile.contains(munitTest), projectFile)
    assert(!projectFile.contains(munitCompile), projectFile)

  test("Testcontainers stays using dep because Day 24's main starts a container"):
    val tc = "org.testcontainers:testcontainers-postgresql:2.0.5"
    assert(projectFile.contains(usingDirective(tc, DepScope.Compile)), projectFile)
    assert(!projectFile.contains(usingDirective(tc, DepScope.Test)), projectFile)

  test("quotesPipeline pins Ubuntu 24.04, Scala CLI 1.16.0, contents read"):
    assertEquals(quotesPipeline.name, "CI")
    assertEquals(quotesPipeline.pushBranches, List("main"))
    assertEquals(quotesPipeline.pullRequest, true)
    assertEquals(quotesPipeline.workflowDispatch, true)
    assertEquals(quotesPipeline.contents, "read")
    assertEquals(quotesPipeline.job.id, "quality")
    assertEquals(quotesPipeline.job.runner, "ubuntu-24.04")
    assertEquals(
      quotesPipeline.job.steps.collect { case WorkflowStep.Uses(_, action, _) => action },
      List(
        "actions/checkout@v7",
        "coursier/cache-action@v8",
        "VirtusLab/scala-cli-setup@v1.16.0"
      )
    )

  test("workflowOf(quotesPipeline) is the checked-in CI workflow"):
    val text = workflowOf(quotesPipeline)
    assertEquals(text, ciFile)
    assert(!text.contains("ubuntu-latest"), text)
    assert(!text.contains("@main"), text)
    assert(!text.contains("write-all"), text)
    assert(!text.contains("continue-on-error"), text)
    assert(text.contains("scala-cli fmt --check ."), text)
    assert(text.contains("scala-cli test ."), text)
    assert(text.contains("python3 scripts/progress.py check"), text)

  test("quotesUpdates is github-actions at / and docker at /packaging/quotes"):
    assertEquals(
      quotesUpdates,
      List(
        DepUpdate(Ecosystem.GitHubActions, "/", "weekly"),
        DepUpdate(Ecosystem.Docker, "/packaging/quotes", "weekly")
      )
    )

  test("dependabot.yml starts with quotesUpdates and is not sbt"):
    val expected = dependabotOf(quotesUpdates)
    assert(dependabotFile.startsWith(expected.stripSuffix("\n")), dependabotFile)
    assert(!dependabotFile.contains("sbt"), dependabotFile)
    assert(!dependabotFile.contains("latest"), dependabotFile)
    assert(dependabotFile.contains("directory: /packaging/quotes"), dependabotFile)
