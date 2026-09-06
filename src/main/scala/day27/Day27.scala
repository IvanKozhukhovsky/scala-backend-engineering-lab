package day27

import day25.RuntimeImage
import day25.jsonArray
import day25.quotesRuntime
import day26.QualityPipeline
import day26.WorkflowStep
import day26.quotesPipeline

/** Full-length git object id. A tag can move; this 40-hex string cannot. */
final case class ActionPin(ownerRepo: String, sha: String, tag: String):
  def usesRef: String = s"$ownerRepo@$sha # $tag"

/** `true` only for a lowercase 40-character hex string — not `@v7`, not a short SHA. */
def isFullSha(sha: String): Boolean =
  sha.length == 40 && sha.forall(c => (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f'))

/** Owner/repo before `@`. Works for both `owner/repo@v7` and `owner/repo@<sha> # v7`. */
def ownerRepoOf(uses: String): String =
  uses.takeWhile(_ != '@')

/** Commit SHA after `@`, if it is a full-length pin. */
def shaFromUses(uses: String): Option[String] =
  val afterAt = uses.dropWhile(_ != '@').drop(1)
  val sha = afterAt.takeWhile(_ != ' ')
  if isFullSha(sha) then Some(sha) else None

def pinStep(step: WorkflowStep, pins: Map[String, ActionPin]): WorkflowStep =
  step match
    case WorkflowStep.Uses(name, action, inputs) =>
      pins.get(ownerRepoOf(action)) match
        case Some(pin) => WorkflowStep.Uses(name, pin.usesRef, inputs)
        case None      => step
    case run: WorkflowStep.Run => run

/** Replace matching `uses:` tags with SHA pins. Building this does not talk to GitHub. */
def pinPipeline(pipeline: QualityPipeline, pins: List[ActionPin]): QualityPipeline =
  val byRepo = pins.map(pin => pin.ownerRepo -> pin).toMap
  pipeline.copy(job =
    pipeline.job.copy(steps = pipeline.job.steps.map(step => pinStep(step, byRepo)))
  )

/** Pins resolved 2026-09-04 from the tags Day 26 already used. `coursier/cache-action` `v8` is an
  * annotated tag — pin the commit, not the tag object.
  */
val checkoutPin: ActionPin =
  ActionPin("actions/checkout", "3d3c42e5aac5ba805825da76410c181273ba90b1", "v7")

val cacheActionPin: ActionPin =
  ActionPin("coursier/cache-action", "95e5b1029b6b86e7bac033ee44a0697d8a527d2d", "v8")

/** Tag-object SHA for `v8` — looks like a pin, is not the commit GitHub Actions runs. */
val cacheActionTagObjectSha: String = "7e3b209d76a1754b6e13c2f30b835a6a91cdaea0"

val scalaCliSetupPin: ActionPin =
  ActionPin("VirtusLab/scala-cli-setup", "8db5313925605c20e292348c1749e80dac7eed0f", "v1.16.0")

val quotesActionPins: List[ActionPin] =
  List(checkoutPin, cacheActionPin, scalaCliSetupPin)

val quotesPinnedPipeline: QualityPipeline =
  pinPipeline(quotesPipeline, quotesActionPins)

/** Alpine BusyBox `adduser`, then numeric `USER`. `uid` is what the process runs as; `userName` is
  * only the account `adduser` creates.
  */
final case class HardenedImage(runtime: RuntimeImage, userName: String, uid: Int)

/** Day 25 recipe plus `adduser` / `USER`. `CMD` stays exec form so `java` is still PID 1. */
def hardenedDockerfileOf(image: HardenedImage): String =
  val runtime = image.runtime
  val lines =
    List(
      s"FROM ${runtime.base}",
      s"WORKDIR ${runtime.workdir}",
      s"COPY ${runtime.jar} ${runtime.jar}"
    ) ++
      runtime.env.map { (key, value) => s"ENV $key=$value" } ++
      List(
        s"RUN adduser -D -H -u ${image.uid} ${image.userName}",
        s"USER ${image.uid}",
        s"CMD ${jsonArray(runtime.cmd)}"
      )
  lines.mkString("\n") + "\n"

val quotesHardened: HardenedImage =
  HardenedImage(runtime = quotesRuntime, userName = "app", uid = 1000)

/** Tokens, passwords, Secret values, and `.env` files do not belong in image layers. */
def secretInRecipe(text: String): Boolean =
  val lower = text.toLowerCase
  List("token", "password", "secret", ".env").exists(lower.contains)
