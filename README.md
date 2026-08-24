# Scala Backend Engineering Lab

[![CI](https://github.com/IvanKozhukhovsky/scala-backend-engineering-lab/actions/workflows/ci.yml/badge.svg)](https://github.com/IvanKozhukhovsky/scala-backend-engineering-lab/actions/workflows/ci.yml)

A public, evidence-driven learning repository for developing professional **Scala 3 software-engineering and backend skills**. It starts with language fundamentals and grows toward a production-oriented backend capstone with typed domain modeling, effects/concurrency, HTTP, PostgreSQL, testing, Docker, CI, security, and observability.

The project deliberately combines two goals: learning Scala deeply and practicing the engineering workflow around real software. AI is used as a tutor/reviewer, while progress is only promoted when there is verifiable evidence.

## Current state

The original workspace contained four lesson/exercise days. They have been imported into a single Scala CLI project and preserved as learner implementations under `src/main/scala/day01` through `day04`. The lesson and exercise stages are recorded as completed, while **verification remains pending** until the new test/review workflow confirms them. This prevents the tracker from equating “a file exists” with “the concept is mastered.”

Run the local progress report:

```bash
python3 scripts/progress.py show
```

## Toolchain

- Scala **3.3.8 LTS**
- JVM **21**
- Scala CLI
- MUnit
- Scalafmt
- Metals in Cursor
- GitHub Actions

The repository intentionally uses the Scala LTS line for a stable learning baseline. Version-sensitive ecosystem choices are revisited at the phase where they become relevant instead of being added prematurely.

## Quick start

Install JDK 21, Scala CLI, and the Metals extension, then from the repository root run:

```bash
scala-cli test .
```

Format sources:

```bash
scala-cli fmt .
```

Run all local quality gates:

```bash
python3 scripts/verify.py
```

## Learning workflow

Each curriculum unit advances through three independently tracked gates:

```text
lesson completed → independent exercise completed → verification evidence → verified
```

`curriculum.json` is the machine-readable source of truth. `MISSION.md`, `RESOURCES.md`, generated `lessons/`, `reference/`, and `learning-records/` form the stateful teaching workspace. See `docs/learning-protocol.md` for the exact semantics.

## Cursor / AI engineering

The repository is also a small practical agent-engineering environment:

- **Rules** protect independent practice and Scala/repository conventions.
- **Skills** implement progress tracking and exercise review procedures.
- **Commands** provide explicit `/next-lesson`, `/review-exercise`, `/progress`, `/verify`, and `/portfolio-check` workflows.
- **Subagent** `scala-reviewer` provides an isolated review context for non-trivial changes.
- **Deterministic tools** (`progress.py`, compiler, Scalafmt, MUnit, CI) verify claims made by probabilistic agents.

The third-party `teach` skill by Matt Pocock is intentionally installed rather than vendored so it can be updated independently. Setup and the recommended skill set are documented in `docs/cursor-setup.md`.

## Roadmap

The curriculum contains 32 ordered units across eight phases: Scala fundamentals, type-safe domain modeling, testing/functional design, concurrency/effects, backend engineering, persistence, production engineering, and a capstone. The detailed state is in `curriculum.json`; GitHub Projects is used only as the public planning view.

## GitHub Project

After publishing the repository, create a user-level project named **Scala Backend Engineering Roadmap**. `/next-lesson` creates `learning` issues for the current phase and puts the first unit In Progress; `/verify` closes a verified unit (Done); `/progress` reconciles leftover cards. Exact fields and GitHub workflows are in `docs/github-project-setup.md`.

## Portfolio status

This repository is already suitable to publish as a transparent **learning lab**. It should be described in a resume as **in progress** until the backend/persistence/production gates are implemented. `docs/portfolio-readiness.md` defines the evidence required before calling the capstone a production-oriented Scala backend project.

## Repository map

See `docs/repository-architecture.md` for the full structure and the separation between learning state, source code, tests, Cursor configuration, and deterministic verification. Honest resume wording is in `docs/resume-snippets.md`.

## AI transparency

The repository does not hide AI assistance. The policy in `docs/ai-assisted-development.md` defines where AI may teach/review and where independent learner evidence is required.

## License

MIT License. See `LICENSE`.
