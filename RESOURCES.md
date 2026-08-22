# Scala Backend Engineering Resources

## Knowledge

- [Scala documentation](https://docs.scala-lang.org/)
  Primary source for Scala 3 syntax, language concepts, and the Scala 3 Book.
- [Scala 3 Book: Algebraic Data Types](https://docs.scala-lang.org/scala3/book/types-adts-gadts.html)
  Primary narrative for Scala 3 `enum`: named values, parameterized cases, and ADTs. Skip the GADT section until a later unit needs it.
- [Scala 3 Book: FP Modeling](https://docs.scala-lang.org/scala3/book/domain-modeling-fp.html)
  When to use `enum` for alternatives (sum types) versus `case class` for compound data.
- [Scala 3 Reference: Enumerations](https://docs.scala-lang.org/scala3/reference/enums/enums.html)
  Authoritative desugaring: an `enum` is a sealed class; cases live on the companion object.
- [Scala 3 Reference: E029 Pattern Match Exhaustivity](https://docs.scala-lang.org/scala3/reference/error-codes/E029.html)
  The compiler warning for a match that misses an enum case, and why `case _` silences it.
- [Tour of Scala: Traits](https://docs.scala-lang.org/tour/traits.html)
  Shared interfaces: a trait cannot be instantiated; classes extend it and implement abstract members.
- [Scala 3 Book: OOP Modeling](https://docs.scala-lang.org/scala3/book/domain-modeling-oop.html)
  Traits as the primary decomposition tool. Use the Traits section only; skip mixin composition, self types, and the advanced component example.
- [Scala 3 Book: Extension Methods](https://docs.scala-lang.org/scala3/book/ca-extension-methods.html)
  Adding methods to a type after it is defined, including types whose source you do not own.
- [Scala 3 Book: Type Classes](https://docs.scala-lang.org/scala3/book/ca-type-classes.html)
  Primary narrative for this unit: parameterized trait plus `given` instance, contrasted with a classic `extends` trait.
- [Scala 3 Book: Context Parameters](https://docs.scala-lang.org/scala3/book/ca-context-parameters.html)
  `using` clauses and `given` instances: how the compiler fills the type-class argument.
- [Scala 3 Reference: Implementing Type classes](https://docs.scala-lang.org/scala3/reference/contextual/type-classes.html)
  Authoritative wording: new behaviour on a closed type without subtyping; implementations are givens, not `extends`.
- [Tour of Scala: Pattern Matching](https://docs.scala-lang.org/tour/pattern-matching.html)
  Exhaustivity on sealed types, including the `MatchError` risk if a warning is ignored.
- [Scala 3 Book: Functional Error Handling](https://docs.scala-lang.org/scala3/book/fp-functional-error-handling.html)
  Primary narrative for `Option`, then `Try` as the exception-holding alternative. The `Try` walkthrough is short here; pair it with the `Try` / `Either` APIs.
- [Scala 3.3.8 API: Try](https://www.scala-lang.org/api/3.3.8/scala/util/Try.html)
  Authoritative contract for `Success` / `Failure`, `Try.apply`, `map`, and `get`.
- [Scala 3.3.8 API: Either](https://www.scala-lang.org/api/3.3.8/scala/util/Either.html)
  Authoritative contract for the Left-failure / Right-success convention and right-biased `map`.
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
