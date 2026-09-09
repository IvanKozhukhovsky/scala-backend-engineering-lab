package day32

import java.nio.file.Files
import java.nio.file.Path

import day28.deskObserved
import day28.quotesObserved
import day29.deskOpenApi
import day29.quotesOpenApi
import munit.FunSuite

class ExercisesDay32Suite extends FunSuite:
  private def changelogFile: String =
    Files.readString(Path.of("docs", "capstone", "desk.CHANGELOG.md"))

  private def startupFile: String =
    Files.readString(Path.of("docs", "capstone", "desk-startup.md"))

  test("rateOf stays a pure Map lookup"):
    assertEquals(rateOf("GBP"), Some(127))
    assertEquals(rateOf("JPY"), None)

  test("deskNotes is desk 0.1.0, not quotes and not 1.0.0"):
    assertEquals(deskNotes.service, "desk")
    assertEquals(deskNotes.version, "0.1.0")
    assertEquals(deskNotes.version, deskOpenApi.version)
    assertEquals(deskNotes.date, "2026-09-09")
    assertNotEquals(deskNotes.service, quotesNotes.service)
    assertNotEquals(deskNotes.honest, quotesHonest)
    assert(isSemVer(deskNotes.version))
    assert(!isSemVer("v0.1.0"))
    assertEquals(releaseTag("desk", deskNotes.version), "desk-v0.1.0")
    assert(isInitialDevelopment(deskNotes.version))
    assert(!overclaimsProduction(deskNotes.honest))
    assert(!deskNotes.items.exists(_.text.contains("/convert")), deskNotes.items.toString)
    assert(!deskNotes.items.exists(_.text.contains("/rates/")), deskNotes.items.toString)
    assert(deskNotes.items.exists(_.text.contains("/orders")), deskNotes.items.toString)

  test("githubReleaseOf(deskNotes) is a pre-release"):
    val release = githubReleaseOf(deskNotes)
    assertEquals(release.tag, "desk-v0.1.0")
    assertEquals(release.title, "desk 0.1.0")
    assertEquals(release.prerelease, true)
    assert(release.body.contains("/orders"), release.body)
    assert(!release.body.contains("/convert"), release.body)
    assert(!overclaimsProduction(release.body))

  test("changelogOf(deskNotes) is the desk Keep a Changelog twin"):
    assertEquals(
      changelogOf(deskNotes),
      """# Changelog
        |
        |All notable changes to this project will be documented in this file.
        |
        |The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
        |and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).
        |
        |## [Unreleased]
        |
        |## [0.1.0] - 2026-09-09
        |
        |### Added
        |
        |- HTTP surface from OpenAPI 3.1.2 (`GET /health`, `GET /orders/{code}`, `POST /orders`).
        |- Persistence lookup with 503 on a failed session (ADR 3).
        |- Observed runtime image: non-root `USER 1000` and `HEALTHCHECK` on loopback `/health`.
        |
        |### Security
        |
        |- Action SHA pins in the release workflow twin; secrets stay out of image layers.
        |
        |This repository remains a learning lab. desk 0.1.0 is initial development, not a hosted production service.
        |
        |[unreleased]: https://github.com/IvanKozhukhovsky/scala-backend-engineering-lab/compare/desk-v0.1.0...HEAD
        |[0.1.0]: https://github.com/IvanKozhukhovsky/scala-backend-engineering-lab/releases/tag/desk-v0.1.0
        |""".stripMargin
    )

  test("docs/capstone/desk.CHANGELOG.md matches changelogOf"):
    assertEquals(changelogFile, changelogOf(deskNotes))

  test("deskStartup uses deskObserved and orders paths"):
    assertEquals(deskStartup.service, "desk")
    assertEquals(deskStartup.version, deskNotes.version)
    assertEquals(deskStartup.image, deskObserved)
    assertNotEquals(deskStartup.image, quotesObserved)
    assertEquals(deskStartup.honest, deskNotes.honest)
    assertEquals(deskOpenApi.version, quotesOpenApi.version)

  test("startupOf(deskStartup) is the desk startup twin"):
    assertEquals(
      startupOf(deskStartup),
      """# desk local startup
        |
        |Version: 0.1.0
        |Image: desk.jar as USER 1000 with HEALTHCHECK GET http://127.0.0.1:8080/health
        |
        |## API examples
        |
        |- GET /health → 200
        |- GET /orders/GBP → 200
        |- GET /orders/JPY → 404
        |- POST /orders {"code":"GBP","qty":10} → 200
        |
        |This repository remains a learning lab. desk 0.1.0 is initial development, not a hosted production service.
        |""".stripMargin
    )
    assert(!startupOf(deskStartup).contains("/rates"), startupOf(deskStartup))
    assert(!startupOf(deskStartup).contains("/convert"), startupOf(deskStartup))

  test("docs/capstone/desk-startup.md matches startupOf"):
    assertEquals(startupFile, startupOf(deskStartup))
