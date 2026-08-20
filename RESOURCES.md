# Scala Backend Engineering Resources

## Knowledge

- [Scala documentation](https://docs.scala-lang.org/)
  Primary source for Scala 3 syntax, language concepts, and the Scala 3 Book.
- [Scala 3 Book: Functional Error Handling](https://docs.scala-lang.org/scala3/book/fp-functional-error-handling.html)
  Primary narrative for `Option` / `Some` / `None`, including why sentinels such as `0` hide absence. Stop before the `Try` section until `scala-006`.
- [Scala 3.3.8 API: Option](https://www.scala-lang.org/api/3.3.8/scala/Option.html)
  Authoritative method contracts for `map`, `flatMap`, `getOrElse`, `get`, and `Option.apply`.
- [Scala language website](https://www.scala-lang.org/)
  Primary source for current releases, LTS information, news, and ecosystem entry points.
- [Scala CLI documentation](https://scala-cli.virtuslab.org/docs/)
  Primary source for compiling, testing, formatting, packaging, and IDE integration in this workspace.
- [Scala Style Guide](https://docs.scala-lang.org/style/)
  Baseline style guidance; repository-specific conventions take precedence when explicitly documented.
- [MUnit documentation](https://scalameta.org/munit/)
  Primary source for the testing framework used in the fundamentals phase.
- [Cats Effect documentation](https://typelevel.org/cats-effect/)
  Reserved for the effects/concurrency phase; do not introduce it before the curriculum reaches that phase.
- [http4s documentation](https://http4s.org/)
  Candidate primary source for the HTTP backend phase; the exact backend stack is chosen when that phase begins.

## Wisdom

- [Scala Users Forum](https://users.scala-lang.org/)
  Use for ecosystem questions and practitioner feedback when documentation alone does not settle a trade-off.
- [Scala Discord](https://discord.com/invite/scala)
  Use for community discussion and practical feedback on current Scala usage.

## Gaps

- PostgreSQL persistence library and migration tooling will be selected at the persistence phase from current primary documentation rather than pinned prematurely.
- Deployment platform will be selected only when the capstone reaches production-readiness work.
