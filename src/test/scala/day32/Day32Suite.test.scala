package day32

import java.nio.file.Files
import java.nio.file.Path

import day25.imageRef
import day28.quotesObserved
import day29.quotesOpenApi
import munit.FunSuite

class Day32Suite extends FunSuite:
  private def changelogFile: String =
    Files.readString(Path.of("docs", "capstone", "quotes.CHANGELOG.md"))

  private def startupFile: String =
    Files.readString(Path.of("docs", "capstone", "quotes-startup.md"))

  test("rateOf stays a pure Map lookup"):
    assertEquals(rateOf("EUR"), Some(108))
    assertEquals(rateOf("USD"), None)

  test("isSemVer accepts 0.1.0 and rejects a v prefix"):
    assert(isSemVer("0.1.0"))
    assert(isSemVer("1.0.0"))
    assert(!isSemVer("v0.1.0"))
    assert(!isSemVer("0.1"))
    assert(!isSemVer("01.0.0"))

  test("quotes version is OpenAPI info.version and the Day 25 image tag"):
    assertEquals(quotesNotes.version, "0.1.0")
    assertEquals(quotesNotes.version, quotesOpenApi.version)
    assertEquals(quotesNotes.version, quotesObserved.hardened.runtime.tag)
    assertEquals(imageRef(quotesObserved.hardened.runtime), "quotes:0.1.0")
    assertEquals(releaseTag("quotes", quotesNotes.version), "quotes-v0.1.0")
    assertNotEquals(releaseTag("quotes", quotesNotes.version), quotesNotes.version)
    assert(isInitialDevelopment(quotesNotes.version))

  test("overclaimsProduction rejects a hosted claim and allows the honest sentence"):
    assert(overclaimsProduction("quotes is a production service"))
    assert(overclaimsProduction("production-ready SaaS"))
    assert(overclaimsProduction("deployed to Fly.io"))
    assert(overclaimsProduction("hosted at https://example.com"))
    assert(!overclaimsProduction(quotesHonest))
    assert(!overclaimsProduction(changelogOf(quotesNotes)))

  test("githubReleaseOf is a pre-release named from the service and SemVer"):
    val release = githubReleaseOf(quotesNotes)
    assertEquals(release.tag, "quotes-v0.1.0")
    assertEquals(release.title, "quotes 0.1.0")
    assertEquals(release.prerelease, true)
    assert(release.body.contains("## [0.1.0] - 2026-09-09"), release.body)
    assert(release.body.contains(quotesHonest), release.body)
    assert(!release.body.contains("Merge pull request"), release.body)
    assert(!overclaimsProduction(release.body))

  test("changelogOf is Keep a Changelog, not a git log"):
    val text = changelogOf(quotesNotes)
    assertEquals(
      text,
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
        |- HTTP surface from OpenAPI 3.1.2 (`GET /health`, `GET /rates/{code}`, `POST /convert`).
        |- Persistence lookup with 503 on a failed session (ADR 3).
        |- Observed runtime image: non-root `USER 1000` and `HEALTHCHECK` on loopback `/health`.
        |
        |### Security
        |
        |- Action SHA pins in the release workflow twin; secrets stay out of image layers.
        |
        |This repository remains a learning lab. quotes 0.1.0 is initial development, not a hosted production service.
        |
        |[unreleased]: https://github.com/IvanKozhukhovsky/scala-backend-engineering-lab/compare/quotes-v0.1.0...HEAD
        |[0.1.0]: https://github.com/IvanKozhukhovsky/scala-backend-engineering-lab/releases/tag/quotes-v0.1.0
        |""".stripMargin
    )
    assert(text.contains("## [Unreleased]"), text)
    assert(!text.contains("/orders"), text)
    assert(!text.contains("git log"), text)
    assert(!text.contains("2026-08-24"), text)

  test("docs/capstone/quotes.CHANGELOG.md matches changelogOf"):
    assertEquals(changelogFile, changelogOf(quotesNotes))
    assert(!changelogFile.contains("is a production service"), changelogFile)

  test("root CHANGELOG.md stays the lab diary"):
    val root = Files.readString(Path.of("CHANGELOG.md"))
    assertNotEquals(root, changelogOf(quotesNotes))
    assert(root.contains("2026-08-24"), root)
    assert(!root.contains("quotes-v0.1.0"), root)

  test("startupOf lists OpenAPI examples and the observed image"):
    assertEquals(quotesStartup.image, quotesObserved)
    assertEquals(quotesStartup.version, quotesNotes.version)
    assertEquals(quotesStartup.honest, quotesHonest)
    val text = startupOf(quotesStartup)
    assertEquals(
      text,
      """# quotes local startup
        |
        |Version: 0.1.0
        |Image: quotes.jar as USER 1000 with HEALTHCHECK GET http://127.0.0.1:8080/health
        |
        |## API examples
        |
        |- GET /health → 200
        |- GET /rates/EUR → 200
        |- GET /rates/JPY → 404
        |- POST /convert {"code":"EUR","qty":10} → 200
        |
        |This repository remains a learning lab. quotes 0.1.0 is initial development, not a hosted production service.
        |""".stripMargin
    )
    assert(!text.contains("localhost"), text)
    assert(!text.contains("/orders"), text)
    assert(!overclaimsProduction(text))

  test("docs/capstone/quotes-startup.md matches startupOf"):
    assertEquals(startupFile, startupOf(quotesStartup))

  test("empty Change kinds are omitted"):
    val notes = quotesNotes.copy(items = List(ChangeItem(ChangeKind.Added, "One feature.")))
    val text = itemsMarkdown(notes)
    assert(text.contains("### Added"), text)
    assert(!text.contains("### Changed"), text)
    assert(!text.contains("### Fixed"), text)
