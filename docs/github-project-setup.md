# GitHub Project Setup

The repository remains the source of truth for curriculum state. GitHub Issues and the linked Project board are the public execution view.

Cursor commands drive the board:

| Command | Board effect |
|---|---|
| `/next-lesson` | If all previous `learning` issues are closed, create one issue per unit in the new phase (Todo). Assign the first unverified unit (In Progress). Write each URL to `githubIssue`. |
| `/progress` | Rewrite lifecycle checkboxes from `curriculum.json`. Ask only about leftover open/closed mismatches. |
| `/verify` | After quality gates pass, tick acceptance boxes from curriculum evidence, then close the issue (Done). |

The agent uses GitHub MCP. Local planning/recording is `python3 scripts/github_board.py`.

## One-time Project setup

Create a user-level GitHub Project named **Scala Backend Engineering Roadmap** (the title in `curriculum.json` → `github.projectTitle`) and link this repository.

Start with a single **Board** view grouped by `Status`:

* `Todo` — queued units in the current phase
* `In Progress` — the unit currently being learned
* `Done` — verified units whose issues are closed

Keep work in progress small. Normally only one learning unit is `In Progress`.

Do not create issues for the entire curriculum in advance. `/next-lesson` creates **all units of the newly entered phase** once every older `learning` issue is closed.

The GitHub MCP plugin in this workspace can create, assign, update issue bodies, and close issues. It cannot set Project Status directly. Add these Project workflows once so columns stay in sync:

1. **Auto-add** issues from `IvanKozhukhovsky/scala-backend-engineering-lab` with the `learning` label.
2. Item added to the project → `Status = Todo`.
3. Issue assigned → `Status = In Progress`.
4. Issue closed → `Status = Done`.

Path in the GitHub UI: Project → `...` → Workflows.

The PAT used by GitHub MCP needs **Issues: Read and write**. Project column movement is performed by those workflows, not by extra Projects API scopes.

## Fields

The only required project field is:

* `Status`: `Todo`, `In Progress`, `Done`

Do not duplicate `lessonStatus`, `exerciseStatus`, or `verificationStatus` as GitHub Project fields. Those states belong to `curriculum.json` and the learning-unit issue checklist.

## Issues

Create one issue per curriculum unit, not separate issues for lesson, exercise, and verification.

The template in `.github/ISSUE_TEMPLATE/lesson.yml` is the manual fallback. `/next-lesson` creates the same shape of issue via MCP:

* title: `[LEARN] scala-0xx — <title>`
* label: `learning`
* body: curriculum id, phase, lifecycle checklist (boxes filled by `/progress` and `/verify` from `curriculum.json`)

Do not tick those boxes by hand. `python3 scripts/github_board.py sync-body` derives them from lesson/exercise/verification status and evidence.

A unit moves to Done only after `verificationStatus` is `verified` **and** `/verify` has closed the issue (`state_reason=completed`).

## Source of truth

`curriculum.json` is authoritative. Store the issue URL in `githubIssue`.

If GitHub and `curriculum.json` disagree, update GitHub to match the curriculum, after asking the learner when leftover cards are still open.

Local `python3 scripts/progress.py check` does not require GitHub. Board commands do.
