package day27

import java.nio.file.Files
import java.nio.file.Path

import day26.WorkflowStep
import day26.workflowOf
import munit.FunSuite

class Day27Suite extends FunSuite:
  private def hardenedFile: String =
    Files.readString(Path.of("packaging", "quotes", "hardened.Dockerfile"))

  private def pinnedFile: String =
    Files.readString(Path.of("ci", "quotes-pinned.yml"))

  test("isFullSha accepts 40 hex chars and rejects tags and short SHAs"):
    assert(isFullSha(checkoutPin.sha))
    assert(!isFullSha("v7"))
    assert(!isFullSha("3d3c42e5"))
    assert(!isFullSha(checkoutPin.sha.toUpperCase))

  test("shaFromUses reads a pin and ignores a trailing tag comment"):
    assertEquals(shaFromUses(checkoutPin.usesRef), Some(checkoutPin.sha))
    assertEquals(shaFromUses("actions/checkout@v7"), None)

  test("cache-action v8 pin is the commit, not the annotated tag object"):
    assertEquals(cacheActionPin.sha, "95e5b1029b6b86e7bac033ee44a0697d8a527d2d")
    assert(isFullSha(cacheActionTagObjectSha))
    assertNotEquals(cacheActionPin.sha, cacheActionTagObjectSha)

  test("quotesPinnedPipeline replaces every uses tag with a SHA comment pin"):
    val uses = quotesPinnedPipeline.job.steps.collect { case WorkflowStep.Uses(_, action, _) =>
      action
    }
    assertEquals(
      uses,
      List(checkoutPin.usesRef, cacheActionPin.usesRef, scalaCliSetupPin.usesRef)
    )
    assert(uses.forall(ref => shaFromUses(ref).isDefined), uses.toString)
    assert(!uses.exists(_.contains("@v7")), uses.toString)
    assert(!uses.exists(_.contains("@main")), uses.toString)

  test("workflowOf(quotesPinnedPipeline) is the checked-in pinned twin"):
    val text = workflowOf(quotesPinnedPipeline)
    assertEquals(text, pinnedFile)
    assert(text.contains(s"uses: ${checkoutPin.usesRef}"), text)
    assert(text.contains("permissions:\n  contents: read\n"), text)
    assert(!text.contains("ubuntu-latest"), text)
    assert(!text.contains("write-all"), text)
    assert(!text.contains("continue-on-error"), text)
    assert(!Files.exists(Path.of(".github", "workflows", "quotes-pinned.yml")))

  test("quotesHardened keeps the Day 25 runtime and adds uid 1000"):
    assertEquals(quotesHardened.runtime.jar, "quotes.jar")
    assertEquals(quotesHardened.runtime.env, List("QUOTES_CODE" -> "EUR"))
    assertEquals(quotesHardened.userName, "app")
    assertEquals(quotesHardened.uid, 1000)
    assertNotEquals(quotesHardened.uid, 0)

  test("hardenedDockerfileOf is Day 25 plus adduser and USER before CMD"):
    val text = hardenedDockerfileOf(quotesHardened)
    assertEquals(
      text,
      """FROM eclipse-temurin:21-jre-alpine-3.24
        |WORKDIR /app
        |COPY quotes.jar quotes.jar
        |ENV QUOTES_CODE=EUR
        |RUN adduser -D -H -u 1000 app
        |USER 1000
        |CMD ["java", "-jar", "quotes.jar"]
        |""".stripMargin
    )
    assert(text.indexOf("USER 1000") > text.indexOf("adduser"), text)
    assert(text.indexOf("CMD ") > text.indexOf("USER 1000"), text)
    assert(!text.contains("USER 0"), text)
    assert(!secretInRecipe(text), text)

  test("packaging/quotes/hardened.Dockerfile matches hardenedDockerfileOf"):
    assertEquals(hardenedFile, hardenedDockerfileOf(quotesHardened))

  test("secretInRecipe flags tokens, passwords, Secret, and .env — not QUOTES_CODE"):
    assert(secretInRecipe("ENV API_TOKEN=abc"))
    assert(secretInRecipe("COPY .env .env"))
    assert(secretInRecipe("ENV DB_PASSWORD=secret"))
    assert(!secretInRecipe(hardenedDockerfileOf(quotesHardened)))
