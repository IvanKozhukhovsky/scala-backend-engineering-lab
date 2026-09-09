package day32

import day29.deskOpenApi

// Independent exercise for scala-032.
// Fill deskNotes and deskStartup yourself. Then write
// docs/capstone/desk.CHANGELOG.md so the file equals changelogOf(deskNotes)
// exactly (trailing newline included),
// and docs/capstone/desk-startup.md so it equals startupOf(deskStartup).
//
// deskNotes:
//   service: desk
//   version: deskOpenApi.version (0.1.0 — do not write 1.0.0 or v0.1.0)
//   date: 2026-09-09
//   Added:
//     HTTP surface from OpenAPI 3.1.2 (`GET /health`, `GET /orders/{code}`, `POST /orders`).
//     Persistence lookup with 503 on a failed session (ADR 3).
//     Observed runtime image: non-root `USER 1000` and `HEALTHCHECK` on loopback `/health`.
//   Security:
//     Action SHA pins in the release workflow twin; secrets stay out of image layers.
//   honest: This repository remains a learning lab. desk 0.1.0 is initial development, not a hosted production service.
//   Do not alias quotesNotes. Do not dump a git log. Do not claim a production service.
//
// deskStartup:
//   service: desk
//   version: same as deskNotes
//   image: deskObserved (Day 28) — do not alias quotesObserved
//   examples:
//     GET /health → 200
//     GET /orders/GBP → 200
//     GET /orders/JPY → 404
//     POST /orders {"code":"GBP","qty":10} → 200
//   honest: same sentence as deskNotes
//
// Do not put POST /convert or GET /rates/{code} on the desk notes.
// Do not change docs/capstone/quotes.CHANGELOG.md or quotes-startup.md.
// Do not rewrite packaging/*/Dockerfile, ci.yml, or the root CHANGELOG.md
// (those are Day 25 / Day 26 / lab-diary contracts).
// Do not create a git tag or a GitHub Release in this unit.

/** Desk Keep a Changelog notes. Building this does not create a git tag. */
def deskNotes: ReleaseNotes =
  ReleaseNotes(
    service = "desk",
    version = deskOpenApi.version,
    date = "2026-09-09",
    items = List(
      ChangeItem(
        ChangeKind.Added,
        "HTTP surface from OpenAPI 3.1.2 (`GET /health`, `GET /orders/{code}`, `POST /orders`)."
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
    honest =
      "This repository remains a learning lab. desk 0.1.0 is initial development, not a hosted production service."
  )

/** Desk local startup examples. Building this does not bind a port. */
def deskStartup: StartupGuide =
  StartupGuide(
    service = "desk",
    version = deskNotes.version,
    image = day28.deskObserved,
    examples = List(
      ApiExample("GET", "/health", None, 200),
      ApiExample("GET", "/orders/GBP", None, 200),
      ApiExample("GET", "/orders/JPY", None, 404),
      ApiExample("POST", "/orders", Some("""{"code":"GBP","qty":10}"""), 200)
    ),
    honest = deskNotes.honest
  )
