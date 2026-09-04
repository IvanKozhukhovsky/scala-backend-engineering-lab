package day26

/** Compile classpath vs test-only classpath. `using test.dep` is the test scope. */
enum DepScope:
  case Compile, Test

/** Render a Scala CLI using-directive. Building this string does not download anything. */
def usingDirective(coord: String, scope: DepScope): String =
  val kind = scope match
    case DepScope.Compile => "dep"
    case DepScope.Test    => "test.dep"
  s"//> using $kind \"$coord\""

/** Package manager Dependabot knows how to bump. Scala CLI `using dep` is not on this list. */
enum Ecosystem:
  case GitHubActions, Docker
  def yaml: String =
    this match
      case GitHubActions => "github-actions"
      case Docker        => "docker"

/** One Dependabot `updates` entry. Building this does not open a pull request. */
final case class DepUpdate(ecosystem: Ecosystem, directory: String, interval: String)

/** Text Dependabot reads from `.github/dependabot.yml`. */
def dependabotOf(updates: List[DepUpdate]): String =
  val blocks = updates.map { update =>
    s"""  - package-ecosystem: ${update.ecosystem.yaml}
       |    directory: ${update.directory}
       |    schedule:
       |      interval: ${update.interval}""".stripMargin
  }
  (List("version: 2", "updates:") ++ blocks).mkString("\n") + "\n"

/** A step is an action (`uses`) or a shell script (`run`). Inputs are already YAML scalars. */
enum WorkflowStep:
  case Uses(name: String, action: String, inputs: List[(String, String)])
  case Run(name: String, script: String)

final case class QualityJob(id: String, runner: String, steps: List[WorkflowStep])

/** Immutable CI recipe. Building this value does not talk to GitHub. */
final case class QualityPipeline(
    name: String,
    pushBranches: List[String],
    pullRequest: Boolean,
    workflowDispatch: Boolean,
    contents: String,
    job: QualityJob
)

/** Text GitHub Actions reads under `.github/workflows`. */
def workflowOf(pipeline: QualityPipeline): String =
  val triggerLines =
    List("on:") ++
      (if pipeline.pushBranches.nonEmpty then
         List("  push:", s"    branches: [${pipeline.pushBranches.mkString(",")}]")
       else Nil) ++
      (if pipeline.pullRequest then List("  pull_request:") else Nil) ++
      (if pipeline.workflowDispatch then List("  workflow_dispatch:") else Nil)

  def stepLines(step: WorkflowStep): List[String] =
    step match
      case WorkflowStep.Uses(name, action, inputs) =>
        val header = List(s"      - name: $name", s"        uses: $action")
        val withBlock =
          if inputs.isEmpty then Nil
          else "        with:" :: inputs.map { (key, value) => s"          $key: $value" }
        header ++ withBlock
      case WorkflowStep.Run(name, script) =>
        List(s"      - name: $name", s"        run: $script")

  val stepBlock =
    pipeline.job.steps.map(stepLines) match
      case Nil           => Nil
      case first :: rest => rest.foldLeft(first)(_ ++ List("") ++ _)

  val lines =
    List(s"name: ${pipeline.name}", "") ++
      triggerLines ++
      List(
        "",
        "permissions:",
        s"  contents: ${pipeline.contents}",
        "",
        "jobs:",
        s"  ${pipeline.job.id}:",
        s"    runs-on: ${pipeline.job.runner}",
        "    steps:"
      ) ++ stepBlock
  lines.mkString("\n") + "\n"

/** Pins: Ubuntu 24.04 (not `latest`), Scala CLI 1.16.0, `contents: read`. */
val quotesPipeline: QualityPipeline = QualityPipeline(
  name = "CI",
  pushBranches = List("main"),
  pullRequest = true,
  workflowDispatch = true,
  contents = "read",
  job = QualityJob(
    id = "quality",
    runner = "ubuntu-24.04",
    steps = List(
      WorkflowStep.Uses("Checkout", "actions/checkout@v7", List("fetch-depth" -> "0")),
      WorkflowStep.Uses("Cache Scala dependencies", "coursier/cache-action@v8", Nil),
      WorkflowStep.Uses(
        "Set up Scala CLI",
        "VirtusLab/scala-cli-setup@v1.16.0",
        List("scala-cli-version" -> "\"1.16.0\"")
      ),
      WorkflowStep.Run("Validate curriculum state", "python3 scripts/progress.py check"),
      WorkflowStep.Run("Check formatting", "scala-cli fmt --check ."),
      WorkflowStep.Run("Run tests", "scala-cli test .")
    )
  )
)

/** Actions at repo root; Docker in `packaging/quotes`. Not `sbt` — this lab is Scala CLI. */
val quotesUpdates: List[DepUpdate] = List(
  DepUpdate(Ecosystem.GitHubActions, "/", "weekly"),
  DepUpdate(Ecosystem.Docker, "/packaging/quotes", "weekly")
)
