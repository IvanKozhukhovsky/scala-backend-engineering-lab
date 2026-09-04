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
  Install pin (`http4s-dsl` / `http4s-ember-server` / `http4s-ember-client` `0.23.36` in this repo) and Scala 3.3+ support. Ember modules are in scope from `scala-020`.
- [http4s: Service](https://http4s.org/v0.23/docs/service.html)
  `HttpRoutes[F]` as `Request => F[Response]`. For `scala-017` stop before EmberServerBuilder / `Router` as skills — that unit runs requests in-process. For `scala-020` read *Running Your Service* (`EmberServerBuilder`, `withHttpApp`, `build`). Stop before `Router`, `<+>`, and `IO.never` as skills.
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
- [Ciris](https://cir.is/)
  Entry point for typed configuration loading used from `scala-019` onward. This repo pins `ciris` `3.15.0` (Scala 3.3 line on the homepage at teach time).
- [Ciris: Configurations](https://cir.is/docs/configurations)
  Primary narrative for `scala-019`: `ConfigValue`, `env` / `prop` / `or`, `as`, `default`, `secret`, `parMapN`, `load`. Stop before Alternatives, custom sources, and AWS/YAML modules.
- [Ciris API: Secret](https://cir.is/api/ciris/Secret.html)
  Authoritative wording: `toString` is a short SHA-1 prefix; `value` unwraps the secret.
- [log4cats](https://github.com/typelevel/log4cats)
  Primary narrative for referentially transparent logging. This repo pins `log4cats-core` `2.7.1` (cats-effect 3.6 line; `2.8.0` depends on CE 3.7). Stop before slf4j, LoggerFactory, and interpolated syntax as skills.
- [log4cats-testing](https://github.com/typelevel/log4cats/blob/main/testing/README.md)
  `StructuredTestingLogger` for asserting on message plus `ctx` map. This unit does not require `munit-cats-effect`.
- [http4s: Server Middleware](https://http4s.org/v0.23/docs/server-middleware.html)
  Lists `Logger` / `RequestLogger` as string dumps of headers and bodies. Out of scope for `scala-019` — full bodies can be huge; structured `ctx` maps are the skill.
- [http4s: HTTP Client](https://http4s.org/v0.23/docs/client.html)
  Primary narrative for `scala-020`: `EmberClientBuilder.build` is a `Resource`, `expect` decodes 2xx, `Client.run` returns `Resource[F, Response[F]]`. Stop before JavaNetClientBuilder as a skill, client middleware, and metrics.
- [http4s API: Client.fromHttpApp](https://http4s.org/v0.23/api/org/http4s/client/Client$.html)
  In-process client from an `HttpApp`. Useful as a fake; it is not a socket integration test.
- [http4s API: Server.baseUri](https://http4s.org/v0.23/api/org/http4s/server/Server.html)
  Live base URI (scheme, host, bound port) after Ember acquires the socket. Tests bind `127.0.0.1` and `port"0"` so this URI is a destination and does not collide on 8080.
- [doobie](https://typelevel.org/doobie/)
  Entry point for the functional JDBC layer used from `scala-021`. Homepage pin at teach time: `org.typelevel` `doobie-core` `1.0.0-RC13` (Scala 3, Cats Effect 3). Package is `org.typelevel.doobie` — not `org.tpolecat` / `doobie.`. Not an ORM: you write SQL. From `scala-022` also pin `doobie-postgres`, `doobie-hikari`, and `doobie-h2` at the same version (Postgres driver `42.7.10`, H2 `2.4.240`).
- [doobie: Introduction](https://typelevel.org/doobie/docs/01-Introduction.html)
  Book framing and the same version pin. Skip the local `world` database setup until a later unit opens PostgreSQL.
- [doobie: Connecting](https://typelevel.org/doobie/docs/03-Connecting.html)
  `ConnectionIO[A]` as a program that needs a `Connection` later; `sql` interpolator; `transact` as the edge. For `scala-021` stop before `Transactor.fromDriverManager` as a skill. For `scala-022` read through a `for` comprehension plus `transact` (one transaction, commit/rollback, close). For `scala-023` the same chapter is the transaction boundary: compose first, `transact` once. Stop before Kleisli interpreters.
- [doobie: FAQ — several things in the same transaction](https://typelevel.org/doobie/docs/17-FAQ.html)
  Primary wording for `scala-023`: compose `ConnectionIO`, then `.transact(xa)`; expose `ConnectionIO` so callers place the boundary. Stop before `withoutTransaction`, `IN` clauses, and YOLO.
- [doobie: Selecting Data](https://typelevel.org/doobie/docs/04-Selecting.html)
  `Query0`, `.query[A]`, `.option` / `.unique` / `.to[List]`, mapping rows to a case class by column position. Stop before HList, shapeless records, YOLO, and `Stream` as skills.
- [doobie: Parameterized Queries](https://typelevel.org/doobie/docs/05-Parameterized.html)
  Primary narrative for `scala-021`: `$minPop` looks like interpolation but becomes a `PreparedStatement` placeholder. Stop before `IN` clauses and `Fragments`.
- [doobie: DDL, Inserting, and Updating](https://typelevel.org/doobie/docs/07-Updating.html)
  `.update` / `Update0` for INSERT. For `scala-021` read through *Inserting*; stop before generated keys, `lastval`, and batch `Update`.
- [doobie API: Query0](https://github.com/typelevel/doobie/blob/v1.0.0-RC13/modules/core/src/main/scala/doobie/util/query.scala)
  Authoritative `sql: String` diagnostic on the query value — enough to test statements without a live database.
- [doobie: Managing Connections](https://typelevel.org/doobie/docs/14-Managing-Connections.html)
  Primary narrative for `scala-022`: `HikariTransactor.fromHikariConfig` is a `Resource`; DriverManager is fine for tests. Stop before `DataSourceTransactor`, `fromConnection`, and custom `Strategy`.
- [doobie: Extensions for PostgreSQL](https://typelevel.org/doobie/docs/15-Extensions-PostgreSQL.html)
  `doobie-postgres` `1.0.0-RC13` pulls in PostgreSQL JDBC Driver `42.7.10`. Driver class `org.postgresql.Driver`. Stop before arrays, enums, PostGIS, `LISTEN`, and `COPY`.
- [doobie: Extensions for H2](https://typelevel.org/doobie/docs/16-Extensions-H2.html)
  `doobie-h2` `1.0.0-RC13` pulls in H2 `2.4.240` so tests can `transact` without Docker. Stop before `H2Transactor.newH2Transactor` as a skill — this unit uses DriverManager plus `DB_CLOSE_DELAY=-1`.
- [Flyway: API (Java)](https://documentation.red-gate.com/flyway/reference/usage/api-java)
  Primary install pin at `scala-023` teach time: OSS `org.flywaydb:flyway-core` `13.4.0` (Java 17+; this repo is JVM 21). `Flyway.configure().dataSource(url, user, password).load()` then `migrate()`. Stop before Spring XML and Redgate/Teams artifacts (`com.redgate.flyway`).
- [Flyway: Quickstart - API](https://documentation.red-gate.com/flyway/getting-started-with-flyway/quickstart-guides/quickstart-api)
  Primary narrative: `V1__Create_person_table.sql` under `src/main/resources/db/migration`, then a second versioned file; re-running applies only what is pending. Skip the Maven archetype.
- [Flyway: Migrations](https://documentation.red-gate.com/flyway/flyway-concepts/migrations)
  `migrate` on startup, idempotent, schema history table. Default wraps each migration script in a transaction. Stop before Undo, Callbacks, repeatable migrations, placeholders, and `clean`.
- [Flyway: Locations](https://documentation.red-gate.com/flyway/reference/configuration/flyway-namespace/flyway-locations-setting)
  API default `classpath:db/migration`. This repo adds `//> using resourceDir "./src/main/resources"` so that path exists.
- [Flyway: SQL migration prefix](https://documentation.red-gate.com/flyway/reference/configuration/flyway-namespace/flyway-sql-migration-prefix-setting)
  File shape `prefixVERSIONseparatorDESCRIPTIONsuffix`; defaults yield `V1.1__My_description.sql`.
- [Flyway: PostgreSQL Database](https://documentation.red-gate.com/flyway/reference/database-driver-reference/postgresql-database)
  Production module `org.flywaydb:flyway-database-postgresql` `13.4.0`. H2 support stays in `flyway-core`. `scala-024` migrates a Testcontainers Postgres; do not `migrate` an ad-hoc developer `localhost:5432`.
- [Scala CLI: Directives — resourceDir](https://scala-cli.virtuslab.org/docs/reference/directives/)
  `//> using resourceDir` puts a directory on the classpath so Flyway can see `db/migration`.
- [Testcontainers: Postgres module](https://java.testcontainers.org/modules/databases/postgres/)
  Primary install pin at `scala-024` teach time: Java `org.testcontainers:testcontainers-postgresql` `2.0.5`. Package `org.testcontainers.postgresql.PostgreSQLContainer` (2.x; not `org.testcontainers.containers`). The library does not pull a JDBC driver — this repo already has `doobie-postgres`. Stop before PostGIS / pgvector / Timescale as skills.
- [Testcontainers: PostgreSQL module examples](https://testcontainers.com/modules/postgresql/)
  `new PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine")); postgres.start()`. Image pin used in this unit.
- [Testcontainers: JDBC support](https://java.testcontainers.org/modules/databases/jdbc/)
  `getJdbcUrl` / `getUsername` / `getPassword` after you instantiate the container. Recognise `jdbc:tc:postgresql:…` as the hidden driver (host/port ignored; default stop when the last connection closes). Stop before init scripts, `TC_DAEMON`, and `TC_TMPFS` as skills.
- [Testcontainers: JUnit 4 Quickstart](https://java.testcontainers.org/quickstart/junit_4_quickstart/)
  Why a local install is unreliable; randomised host/port; `getHost()` rather than hard-coded `localhost`. Skip `@Rule` as a skill — this repo is MUnit and wraps `start` / `stop` in `Resource.make`.
- [Testcontainers: container runtime requirements](https://java.testcontainers.org/supported_docker_environment/)
  Docker Engine / Docker Desktop (or Testcontainers Cloud). Needed before `start()`.
- [Docker: Replace H2 with a real database](https://docs.docker.com/guides/testcontainers-java-replace-h2/)
  Why an H2 suite does not prove Postgres dialect behaviour. Optional companion; keep H2 as the fast suite.
- [Docker: What is an image?](https://docs.docker.com/get-started/docker-concepts/the-basics/what-is-an-image/)
  Primary wording for `scala-025`: an image is an immutable packaged filesystem (layers). Stop before registry publishing as a skill.
- [Docker: What is a container?](https://docs.docker.com/get-started/docker-concepts/the-basics/what-is-a-container/)
  Isolated process started from an image; portable because the host need not have the toolchain. Postgres and the app are different containers.
- [Docker: Writing a Dockerfile](https://docs.docker.com/get-started/docker-concepts/building-images/writing-a-dockerfile/)
  Primary narrative: `FROM`, `WORKDIR`, `COPY`, `ENV`, `CMD`. Stop before multi-stage cache tricks, Gordon, and `USER` as skills (`scala-027`).
- [Dockerfile reference: ENV](https://docs.docker.com/reference/dockerfile/#env)
  `ENV` persists into the running container; `docker run --env` overrides. Do not bake secrets.
- [Dockerfile reference: CMD](https://docs.docker.com/reference/dockerfile/#cmd)
  Exec form is a JSON array. `CMD` does not run at build time. Prefer it over a shell wrapper so `java` is PID 1.
- [eclipse-temurin Docker Official Image](https://hub.docker.com/_/eclipse-temurin)
  `COPY` a JAR and `CMD ["java", "-jar", …]`. This unit pins `eclipse-temurin:21-jre-alpine-3.24` (JVM 21, JRE, Alpine 3.24). Stop before `jlink`.
- [Scala CLI: Package](https://scala-cli.virtuslab.org/docs/commands/package/)
  `--power` is required. Assembly (`--assembly`, `--preamble=false` for `java -jar`); Docker (`--docker`, `--docker-from`, `--docker-image-repository`, `--docker-image-tag`). Stop before GraalVM native-image and OS packages.

## Wisdom

- [Scala Users Forum](https://users.scala-lang.org/)
  Use for ecosystem questions and practitioner feedback when documentation alone does not settle a trade-off.
- [Scala Discord](https://discord.com/invite/scala)
  Use for community discussion and practical feedback on current Scala usage.

## Gaps

- Docker Compose, multi-container local stacks, and publishing images from CI wait until later production-engineering / capstone work (`scala-026` for CI; Compose is not a separate unit yet).
- Non-root `USER`, baked-secret review, and image hardening wait for `scala-027`.
- `HEALTHCHECK`, metrics, and tracing wait for `scala-028`.
- Deployment platform will be selected only when the capstone reaches production-readiness work.
