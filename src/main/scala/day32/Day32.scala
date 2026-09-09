package day32

import day28.ObservedImage
import day28.quotesObserved
import day29.quotesOpenApi

val rates: Map[String, Int] = Map("EUR" -> 108, "GBP" -> 127)

/** Cents per unit for a currency code, or None. Pure: no changelog, no GitHub, no Docker. */
def rateOf(code: String): Option[Int] =
  rates.get(code)

enum ChangeKind:
  case Added, Changed, Deprecated, Removed, Fixed, Security

final case class ChangeItem(kind: ChangeKind, text: String)

/** Curated notes for one SemVer. Building this does not create a git tag. */
final case class ReleaseNotes(
    service: String,
    version: String,
    date: String,
    items: List[ChangeItem],
    honest: String
)

final case class GitHubRelease(tag: String, title: String, body: String, prerelease: Boolean)

final case class ApiExample(
    method: String,
    path: String,
    body: Option[String],
    expectedStatus: Int
)

final case class StartupGuide(
    service: String,
    version: String,
    image: ObservedImage,
    examples: List[ApiExample],
    honest: String
)

/** SemVer core `X.Y.Z` — no leading `v`. A git tag may add a prefix. */
def isSemVer(version: String): Boolean =
  version.split("\\.", -1) match
    case Array(major, minor, patch) =>
      isNumericIdentifier(major) && isNumericIdentifier(minor) && isNumericIdentifier(patch)
    case _ => false

private def isNumericIdentifier(part: String): Boolean =
  part.nonEmpty && part.forall(_.isDigit) && (part == "0" || !part.startsWith("0"))

/** Major version zero is initial development. Not a hosted production claim. */
def isInitialDevelopment(version: String): Boolean =
  version.startsWith("0.")

/** Git tag for a service in this monorepo. SemVer stays `0.1.0`; this string is not SemVer. */
def releaseTag(service: String, version: String): String =
  s"$service-v$version"

/** True when the text claims a hosted production service. Negation is allowed. */
def overclaimsProduction(text: String): Boolean =
  val lower = text.toLowerCase
  List("is a production service", "production-ready", "deployed to", "hosted at")
    .exists(lower.contains)

def versionHeading(notes: ReleaseNotes): String =
  s"## [${notes.version}] - ${notes.date}"

private def kindTitle(kind: ChangeKind): String =
  kind match
    case ChangeKind.Added      => "Added"
    case ChangeKind.Changed    => "Changed"
    case ChangeKind.Deprecated => "Deprecated"
    case ChangeKind.Removed    => "Removed"
    case ChangeKind.Fixed      => "Fixed"
    case ChangeKind.Security   => "Security"

private val kindOrder: List[ChangeKind] =
  List(
    ChangeKind.Added,
    ChangeKind.Changed,
    ChangeKind.Deprecated,
    ChangeKind.Removed,
    ChangeKind.Fixed,
    ChangeKind.Security
  )

/** Keep a Changelog sections. Empty kinds are omitted. */
def itemsMarkdown(notes: ReleaseNotes): String =
  val blocks = kindOrder.flatMap { kind =>
    val lines = notes.items.collect { case ChangeItem(`kind`, text) => s"- $text" }
    if lines.isEmpty then Nil
    else s"### ${kindTitle(kind)}" :: "" :: lines ::: List("")
  }
  blocks.mkString("\n")

/** Version heading, sections, and the honesty paragraph. */
def notesBody(notes: ReleaseNotes): String =
  s"""${versionHeading(notes)}
     |
     |${itemsMarkdown(notes)}
     |${notes.honest}
     |""".stripMargin

/** Keep a Changelog file. Building this does not talk to GitHub. */
def changelogOf(notes: ReleaseNotes): String =
  val tag = releaseTag(notes.service, notes.version)
  s"""# Changelog
     |
     |All notable changes to this project will be documented in this file.
     |
     |The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
     |and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).
     |
     |## [Unreleased]
     |
     |${notesBody(notes).stripTrailing}
     |
     |[unreleased]: https://github.com/IvanKozhukhovsky/scala-backend-engineering-lab/compare/$tag...HEAD
     |[${notes.version}]: https://github.com/IvanKozhukhovsky/scala-backend-engineering-lab/releases/tag/$tag
     |""".stripMargin

/** GitHub Release fields. Building this does not publish a release. */
def githubReleaseOf(notes: ReleaseNotes): GitHubRelease =
  GitHubRelease(
    tag = releaseTag(notes.service, notes.version),
    title = s"${notes.service} ${notes.version}",
    body = notesBody(notes),
    prerelease = isInitialDevelopment(notes.version)
  )

private def exampleLine(example: ApiExample): String =
  example.body match
    case None       => s"- ${example.method} ${example.path} → ${example.expectedStatus}"
    case Some(body) => s"- ${example.method} ${example.path} $body → ${example.expectedStatus}"

/** Local API examples. Building this does not bind a port or `docker run`. */
def startupOf(guide: StartupGuide): String =
  val jar = guide.image.hardened.runtime.jar
  val uid = guide.image.hardened.uid
  val probe = guide.image.probe
  val examples = guide.examples.map(exampleLine).mkString("\n")
  s"""# ${guide.service} local startup
     |
     |Version: ${guide.version}
     |Image: $jar as USER $uid with HEALTHCHECK GET http://127.0.0.1:${probe.port}${probe.path}
     |
     |## API examples
     |
     |$examples
     |
     |${guide.honest}
     |""".stripMargin

val quotesHonest: String =
  "This repository remains a learning lab. quotes 0.1.0 is initial development, not a hosted production service."

val quotesNotes: ReleaseNotes =
  ReleaseNotes(
    service = "quotes",
    version = quotesOpenApi.version,
    date = "2026-09-09",
    items = List(
      ChangeItem(
        ChangeKind.Added,
        "HTTP surface from OpenAPI 3.1.2 (`GET /health`, `GET /rates/{code}`, `POST /convert`)."
      ),
      ChangeItem(
        ChangeKind.Added,
        "Persistence lookup with 503 on a failed session (ADR 3)."
      ),
      ChangeItem(
        ChangeKind.Added,
        "Observed runtime image: non-root `USER 1000` and `HEALTHCHECK` on loopback `/health`."
      ),
      ChangeItem(
        ChangeKind.Security,
        "Action SHA pins in the release workflow twin; secrets stay out of image layers."
      )
    ),
    honest = quotesHonest
  )

val quotesStartup: StartupGuide =
  StartupGuide(
    service = "quotes",
    version = quotesNotes.version,
    image = quotesObserved,
    examples = List(
      ApiExample("GET", "/health", None, 200),
      ApiExample("GET", "/rates/EUR", None, 200),
      ApiExample("GET", "/rates/JPY", None, 404),
      ApiExample("POST", "/convert", Some("""{"code":"EUR","qty":10}"""), 200)
    ),
    honest = quotesHonest
  )
