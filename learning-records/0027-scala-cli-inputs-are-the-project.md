# Scala CLI inputs are the project

The learner compiled `./src/main/scala/day32/.` and lost `package day28` / `day29` plus `project.scala` (Scala 3.8.4 instead of 3.3.8). They then moved `generateChangelog` under `src/test/scala/day32/` so it belongs to the repo-root project. A path argument is the source set, not a package filter.

**Evidence**: compile error (`Not found: day28`) then generators in test sources.

**Implications**: `--main-class` picks which `@main` to run. It does not pull in sibling packages. Use `scala-cli --power run --test .` from the repository root for test-source generators.
