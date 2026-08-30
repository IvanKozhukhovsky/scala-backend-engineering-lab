//> using scala "3.3.8"
//> using jvm "21"
//> using options "-deprecation" "-feature" "-unchecked"
// Cats Effect: pin matches Getting Started at teach time (scala-014). Bump only with a docs rationale.
//> using dep "org.typelevel::cats-effect::3.6.4"
// FS2: pin to 3.12.x line compiled against cats-effect 3.6.x (scala-016 teach time).
//> using dep "co.fs2::fs2-core::3.12.2"
// http4s: 0.23.x is the Cats Effect 3 line; 0.23.36 is Quick Start / Service at scala-017 teach time.
// http4s-circe: JSON module from the JSON Handling page at scala-018 teach time.
// circe-generic / paradise in that page are Scala 2 auto-derivation; this repo uses Scala 3 `derives`.
//> using dep "org.http4s::http4s-dsl::0.23.36"
//> using dep "org.http4s::http4s-circe::0.23.36"
// Ember: native server/client from Quick Start / Service / Client at scala-020. Same 0.23.36 pin.
// Tests bind 127.0.0.1 and port 0 (OS picks a free port). slf4j / logback still wait.
//> using dep "org.http4s::http4s-ember-server::0.23.36"
//> using dep "org.http4s::http4s-ember-client::0.23.36"
// Ciris: typed env/prop loading from Configurations at scala-019 teach time. No YAML/AWS modules.
//> using dep "is.cir::ciris::3.15.0"
// log4cats: CE3 logging algebra. 2.7.1 tracks cats-effect 3.6.x (2.8.0 wants CE 3.7).
// NoOpLogger lives in core since 2.7.0. slf4j / logback wait until the observability unit.
//> using dep "org.typelevel::log4cats-core::2.7.1"
// doobie: functional JDBC. Homepage pin at scala-021 teach time: 1.0.0-RC13,
// org.typelevel (not org.tpolecat), package org.typelevel.doobie.
// Core only this unit — no Transactor, postgres driver, Hikari, or H2.
//> using dep "org.typelevel::doobie-core::1.0.0-RC13"
//> using test.dep "org.typelevel::log4cats-testing::2.7.1"
//> using test.dep "org.scalameta::munit::1.0.4"
// munit-scalacheck has no 1.0.4; 1.0.0 is the 1.0 line. ScalaCheck 1.18.0 is transitive.
//> using test.dep "org.scalameta::munit-scalacheck::1.0.0"
