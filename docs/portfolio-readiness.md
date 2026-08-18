# Portfolio Readiness

This repository is publishable immediately as a transparent **Scala Backend Engineering Lab**, but four fundamentals exercises alone should not be presented as a production backend system. The repository is designed to grow into that claim through verifiable gates.

## Level 1 — Public learning lab

Ready when repository hygiene, README, curriculum, automated tests, and CI are visible. This level is suitable for a GitHub profile and can appear in a resume as an in-progress engineering learning project.

## Level 2 — Backend engineering project

Required evidence:

- Typed HTTP API with explicit request/response/error contracts.
- Non-trivial domain model and validation.
- PostgreSQL persistence with migrations and transaction boundaries.
- Unit and integration tests, including database/HTTP boundaries.
- Structured configuration and logging.
- Dockerized reproducible runtime.
- CI gates for formatting, compilation/tests, and tracker consistency.
- Security notes covering secrets, input validation, dependency hygiene, and least privilege where applicable.
- Architecture documentation explaining boundaries and major trade-offs.

## Level 3 — Portfolio-grade capstone

In addition to Level 2:

- Metrics/health checks and an observability story.
- Failure handling and at least one meaningful concurrency/effect use case.
- Release notes or tagged release.
- API examples and reproducible local startup instructions.
- A short architecture decision record for at least two non-obvious design choices.
- The author can explain the system and its trade-offs without relying on AI.

## Resume framing

Before Level 2, describe it as **Scala Backend Engineering Lab — in progress**. After Level 2/3, describe the capstone outcome rather than claiming that the lesson repository itself is a production service.
