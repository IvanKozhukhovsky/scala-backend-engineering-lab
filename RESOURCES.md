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
- [Scala CLI: Test](https://scala-cli.virtuslab.org/docs/commands/test)
  Test sources (`src/test/scala`, `*.test.scala`), `scala-cli test .`, and `--test-only`.
- [Scala Style Guide](https://docs.scala-lang.org/style/)
  Baseline style guidance; repository-specific conventions take precedence when explicitly documented.
- [MUnit documentation](https://scalameta.org/munit/)
  Primary source for the testing framework used in this workspace (pinned at 1.0.4 in `project.scala`).
- [MUnit: Getting started](https://scalameta.org/munit/docs/getting-started.html)
  `FunSuite`, `test`, and `assertEquals(obtained, expected)`.
- [MUnit: Declaring tests](https://scalameta.org/munit/docs/tests.html)
  An empty test body still passes. Stop before async tests, tags, fixtures, and retries until a later unit.
- [MUnit: Writing assertions](https://scalameta.org/munit/docs/assertions.html)
  Diffs from `assertEquals`; `assert` has a weaker failure message; unrelated types do not compile.
- [MUnit: ScalaCheck](https://scalameta.org/munit/docs/integrations/scalacheck.html)
  `ScalaCheckSuite`, `property`, `forAll`, and `assertEquals` inside a property. The page’s version number tracks current MUnit; this repo pins `munit-scalacheck` `1.0.0` next to MUnit `1.0.4`. Stop before ScalaTest migration.
- [ScalaCheck User Guide](https://github.com/typelevel/scalacheck/blob/main/doc/UserGuide.md)
  Primary narrative for `forAll`, `==>` (including “gave up”), `Gen.choose`, and shrinking. Stop before combining properties, custom `Arbitrary`, and custom `Shrink`.
- [ScalaCheck](https://github.com/typelevel/scalacheck)
  Upstream library pulled transitively (this repo: ScalaCheck `1.18.0` via `munit-scalacheck` `1.0.0`).
- [Scala 3 Book: Immutable Values](https://docs.scala-lang.org/scala3/book/fp-immutable-values.html)
  Primary narrative for `val`, immutable collections, and case-class `copy`. Stop after the `copy` example; skip the Data Modeling cross-link until a later unit needs it.
- [Scala 3 Book: Pure Functions](https://docs.scala-lang.org/scala3/book/fp-pure-functions.html)
  Definition of purity, impure examples (`println`, `currentTimeMillis`), and the pure-core / impure-wrapper split. Treat the `IO` Monad sentence as out of scope until the effects phase.
- [Scala 3 Book: What is Functional Programming?](https://docs.scala-lang.org/scala3/book/fp-what-is-fp.html)
  Short framing only: expressions that return values rather than statements that change program state.
- [Gary Bernhardt: Boundaries](https://www.destroyallsoftware.com/talks/boundaries)
  Public talk that names the functional-core / imperative-shell split. Optional; the Scala 3 Book is the assigned primary source. The DAS screencast of that name is paywalled — do not assign it.
- [Scala 3 Book: Packaging and Imports](https://docs.scala-lang.org/scala3/book/packaging-imports.html)
  Primary narrative for `package`, `import pkg.Name`, `import pkg.{A, B}`, and `import pkg.*`. Stop before renaming, hiding, default imports, `_root_`, and importing givens.
- [Tour of Scala: Packages and Imports](https://docs.scala-lang.org/tour/packages-and-imports.html)
  Directory convention (package name = folder) and that same-package members need no import. Skip nested curly-brace packages and `_root_` unless a later unit needs them.
- [Scala docs: Futures and Promises](https://docs.scala-lang.org/overviews/core/futures.html)
  Primary narrative for this unit: `Future` as a placeholder, `ExecutionContext` as a thread pool, `Future.apply` starts the body, `map` as a combinator. Stop before Promises, Blocking, custom executors, and treating `onComplete` as the main style.
- [Scala 3.3.8 API: Future companion](https://www.scala-lang.org/api/3.3.8/scala/concurrent/Future$.html)
  Authoritative wording: `apply` *starts* an asynchronous computation. `successful` is already completed.
- [Scala 3.3.8 API: Future](https://www.scala-lang.org/api/3.3.8/scala/concurrent/Future.html)
  `map` / `flatMap` contracts and that callbacks need an `ExecutionContext`.
- [Scala 3.3.8 API: ExecutionContext](https://www.scala-lang.org/api/3.3.8/scala/concurrent/ExecutionContext.html)
  Pass the context into methods (`using` / implicit) rather than hardcoding `Implicits.global` inside library code.
- [Scala 3.3.8 API: Await](https://www.scala-lang.org/api/3.3.8/scala/concurrent/Await$.html)
  `result` blocks the calling thread; useful for tests, discouraged as the normal way to compose work.
- [Scala Book: Futures](https://docs.scala-lang.org/overviews/scala-book/futures.html)
  Gentler walkthrough, including “starts as soon as you construct it.” Optional; the page is the Scala 2 Book and points to a newer edition. Stop before Akka and GUI demos.
- [MUnit: Declaring tests](https://scalameta.org/munit/docs/tests.html)
  A test body may return a `Future`; MUnit waits. This unit’s suites use `Await.result` so the blocking edge stays visible.
- [Cats Effect documentation](https://typelevel.org/cats-effect/)
  Entry point for the effects runtime used from `scala-014` onward.
- [Cats Effect: Getting Started](https://typelevel.org/cats-effect/docs/getting-started)
  Primary install pin (`cats-effect` `3.6.4` in this repo), `IOApp.Simple`, and the REPL `unsafeRunSync` note. Stop before the FizzBuzz fiber demo.
- [Cats Effect: IO](https://typelevel.org/cats-effect/docs/datatypes/io)
  Primary narrative: IO as a description, referential transparency vs `Future`, `IO.pure` vs `IO.apply`. Stop before async constructors, error handling as a skill, concurrency, and cancelation.
- [Cats Effect 3 API: IO](https://typelevel.org/cats-effect/api/3.x/cats/effect/IO.html)
  Authoritative wording: pure immutable description; not evaluated until unsafe run / end of the world; not memoized.
- [Cats Effect: Concepts](https://typelevel.org/cats-effect/docs/concepts)
  Primary narrative for fibers, cooperative cancelation, and why `background` is preferred over bare `start`. Stop before tracing / scheduler deep dives.
- [Cats Effect: Resource](https://typelevel.org/cats-effect/docs/std/resource)
  Primary narrative for `Resource.make` / `use`, reverse release order, and finalization when `use` ends. Stop before `Semaphore` / `Ref` cross-links as skills.
- [Cats Effect: Tutorial](https://typelevel.org/cats-effect/docs/tutorial)
  File-copy walkthrough for Resource + cancelation. Stop before the full producer–consumer / `Ref` build (`scala-015` does not require that section).
- [Cats Effect 3 API: GenSpawn](https://typelevel.org/cats-effect/api/3.x/cats/effect/kernel/GenSpawn.html)
  Authoritative contracts for `start`, `join`, `cancel`, and `background`.
- [Cats Effect 3 API: Resource](https://typelevel.org/cats-effect/api/3.x/cats/effect/kernel/Resource.html)
  Authoritative wording: release on success, failure, and interrupt of `use`.
- [FS2 documentation](https://fs2.io/)
  Entry point for the streaming library used from `scala-016` onward.
- [FS2 Guide](https://github.com/typelevel/fs2/blob/main/docs/guide.md)
  Primary narrative for this unit: constructing streams, `map` / `filter`, `evalMap`, and `compile`. Stop before concurrency and I/O chapters.
- [FS2 API: Stream](https://typelevel.org/fs2/api/fs2/Stream.html)
  Authoritative contracts for `emit`, `emits`, `evalMap`, `evalTap`, and `compile`.
- [http4s documentation](https://http4s.org/v0.23/)
  Entry point for the HTTP library used from `scala-017` onward (0.23.x Cats Effect 3 line).
- [http4s Quick Start](https://http4s.org/v0.23/docs/quickstart.html)
  Install pin (`http4s-dsl` `0.23.36` in this repo) and Scala 3.3+ support. Ember client/server modules wait until a later unit needs a bound port.
- [http4s: Service](https://http4s.org/v0.23/docs/service.html)
  `HttpRoutes[F]` as `Request => F[Response]`. Stop before EmberServerBuilder / `Router` as skills — this unit runs requests in-process.
- [http4s: The DSL](https://http4s.org/v0.23/docs/dsl.html)
  Primary narrative for this unit: `HttpRoutes.of`, `GET -> Root / …`, `Ok` / `NotFound` / `MethodNotAllowed`, testing with a constructed `Request` and `.orNotFound`. Stop before cookies, `Future` bodies, and streaming drip examples.
- [http4s: JSON Handling](https://http4s.org/v0.23/docs/json.html)
  Primary narrative for `scala-018`: `http4s-circe` `0.23.36`, `jsonOf` / `jsonEncoderOf`, circe `Encoder` / `Decoder`. Stop before the Ember hello-world server and client. The page’s paradise plugin is Scala 2; this repo uses Scala 3 `derives Codec.AsObject`.
- [http4s: Entity Handling](https://http4s.org/v0.23/docs/entity.html)
  `EntityEncoder` / `EntityDecoder` as the body + media-type bridge. `jsonOf` / `jsonEncoderOf` are listed under JSON.
- [http4s: Error Handling](https://http4s.org/v0.23/docs/error-handling.html)
  `MalformedMessageBodyFailure` (syntax) vs `InvalidMessageBodyFailure` (semantics). Unhandled `MessageFailure` reaches the backend as a failed task — map it yourself when running `HttpApp` in-process. Stop before Ember `ErrorAction` middleware.
- [RFC 9110: HTTP Semantics — Methods](https://www.rfc-editor.org/rfc/rfc9110.html#name-methods)
  Authoritative wording: safe methods (GET/HEAD/OPTIONS/TRACE), idempotent methods (safe + PUT + DELETE), POST is neither; 405 must include `Allow`.
- [RFC 9110: 400 Bad Request](https://www.rfc-editor.org/rfc/rfc9110.html#status.400)
  Malformed syntax (and similar client errors). Used for broken JSON in `scala-018`.
- [RFC 9110: 422 Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#status.422)
  Content type and syntax are correct; the instructions cannot be processed. http4s 0.23.31+ DSL: `UnprocessableContent`.
- [MDN: HTTP request methods](https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Methods)
  Gentler table of safe / idempotent / cacheable methods. Optional; RFC 9110 is the assigned spec.
- [MDN: HTTP response status codes](https://developer.mozilla.org/en-US/docs/Web/HTTP/Reference/Status)
  Status classes and the 200 / 404 / 405 wording used in this unit. Optional companion to the RFC.

## Wisdom

- [Scala Users Forum](https://users.scala-lang.org/)
  Use for ecosystem questions and practitioner feedback when documentation alone does not settle a trade-off.
- [Scala Discord](https://discord.com/invite/scala)
  Use for community discussion and practical feedback on current Scala usage.

## Gaps

- PostgreSQL persistence library and migration tooling will be selected at the persistence phase from current primary documentation rather than pinned prematurely.
- Deployment platform will be selected only when the capstone reaches production-readiness work.
