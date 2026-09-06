package day27

import day26.QualityPipeline

// Independent exercise for scala-027.
// Fill deskHardened and deskActionPins yourself. Then write
// packaging/desk/hardened.Dockerfile so the file equals
// hardenedDockerfileOf(deskHardened) exactly (trailing newline included),
// and write ci/desk-pinned.yml so the file equals
// workflowOf(pinPipeline(deskPipeline, deskActionPins)) exactly.
//
// deskHardened:
//   runtime: deskRuntime (Day 25)
//   userName: app
//   uid: 1000
//
// deskActionPins: checkoutPin and scalaCliSetupPin only (desk has no coursier step).
// Reuse the ActionPin values from Day27.scala — do not invent SHAs.
//
// Do not alias quotesHardened or quotesPinnedPipeline.
// Do not uses @v7 / @v1.16.0 / @main as the pin (the SHA is the pin; the tag is a comment).
// Do not USER 0 or omit USER.
// Do not ENV a token, password, or Secret. Do not COPY .env.
// Do not put ci/desk-pinned.yml under .github/workflows.
// Do not change packaging/desk/Dockerfile or ci/desk.yml (those are Day 25 / Day 26 contracts).

/** Desk image with a non-root USER. Building this does not talk to Docker. */
def deskHardened: HardenedImage =
  HardenedImage(
    runtime = day25.deskRuntime,
    userName = "app",
    uid = 1000
  )

/** SHA pins for the desk workflow's uses: lines. Building this does not talk to GitHub. */
def deskActionPins: List[ActionPin] =
  List(
    checkoutPin,
    scalaCliSetupPin
  )

def deskPinnedPipeline: QualityPipeline =
  pinPipeline(day26.deskPipeline, deskActionPins)
