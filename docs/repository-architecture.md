# Repository Architecture

```text
scala-backend-engineering-lab/
├── src/main/scala/          learner implementations and examples
├── src/test/scala/          executable behavior checks
├── project.scala            pinned Scala/JVM/test configuration
├── curriculum.json          machine-readable learning state
├── MISSION.md               long-term learning outcome
├── RESOURCES.md             trusted knowledge sources
├── lessons/                 generated teaching units
├── reference/               compact reference material
├── learning-records/        demonstrated durable learning
├── .agents/skills/          portable learning workflows
├── .cursor/rules/           persistent/scoped agent constraints
├── .cursor/commands/        explicit convenience workflows
├── .cursor/agents/          isolated specialist contexts
├── .github/                 CI and public workflow templates
├── scripts/                 deterministic progress/verification tools (`progress.py`, `verify.py`, `github_board.py`)
└── docs/                    engineering and portfolio documentation
```

The design intentionally separates probabilistic AI behavior from deterministic checks. Cursor may decide how to explain or review a concept, but `scripts/progress.py`, Scala compilation, Scalafmt, MUnit, and CI provide machine-checkable invariants.
