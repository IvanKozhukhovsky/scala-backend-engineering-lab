package day26

// Independent exercise for scala-026.
// Fill deskPipeline and deskUpdates yourself. Then write ci/desk.yml so the file
// equals workflowOf(deskPipeline) exactly (trailing newline included), and extend
// .github/dependabot.yml so it equals dependabotOf(quotesUpdates :+ deskUpdates).
//
// deskPipeline:
//   name: Desk
//   pushBranches: Nil (pull_request only — no push, no workflow_dispatch)
//   pullRequest: true
//   workflowDispatch: false
//   contents: read
//   job id: desk
//   runner: ubuntu-24.04
//   steps:
//     Checkout — actions/checkout@v7 (no inputs)
//     Set up Scala CLI — VirtusLab/scala-cli-setup@v1.16.0 with scala-cli-version "1.16.0"
//     Check formatting — scala-cli fmt --check .
//     Run tests — scala-cli test . --test-only 'day26.ExercisesDay26Suite'
//
// deskUpdates:
//   ecosystem: Docker
//   directory: /packaging/desk
//   interval: weekly
//
// Do not alias quotesPipeline. Do not use ubuntu-latest. Do not uses @main.
// Do not permissions write-all. Do not continue-on-error. Do not package-ecosystem sbt.
// Do not put ci/desk.yml under .github/workflows (GitHub would run a second workflow).
// Do not move Testcontainers to test.dep (Day 24's main still starts a container).

/** Desk quality recipe. Building this does not talk to GitHub. */
def deskPipeline: QualityPipeline =
  QualityPipeline(
    name = "Desk",
    pushBranches = Nil,
    pullRequest = true,
    workflowDispatch = false,
    contents = "read",
    job = QualityJob(
      id = "desk",
      runner = "ubuntu-24.04",
      steps = List(
        WorkflowStep.Uses("Checkout", "actions/checkout@v7", Nil),
        WorkflowStep.Uses(
          "Set up Scala CLI",
          "VirtusLab/scala-cli-setup@v1.16.0",
          List("scala-cli-version" -> "\"1.16.0\"")
        ),
        WorkflowStep.Run("Check formatting", "scala-cli fmt --check ."),
        WorkflowStep.Run("Run tests", "scala-cli test . --test-only 'day26.ExercisesDay26Suite'")
      )
    )
  )

/** Dependabot watch for the desk image. Building this does not open a pull request. */
def deskUpdates: DepUpdate =
  DepUpdate(ecosystem = Ecosystem.Docker, directory = "/packaging/desk", interval = "weekly")
