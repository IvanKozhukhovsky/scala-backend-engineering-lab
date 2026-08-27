//> using scala "3.3.8"
//> using jvm "21"
//> using options "-deprecation" "-feature" "-unchecked"
// Cats Effect: pin matches Getting Started at teach time (scala-014). Bump only with a docs rationale.
//> using dep "org.typelevel::cats-effect::3.6.4"
// FS2: pin to 3.12.x line compiled against cats-effect 3.6.x (scala-016 teach time).
//> using dep "co.fs2::fs2-core::3.12.2"
// http4s: 0.23.x is the Cats Effect 3 line; 0.23.36 is Quick Start / Service at scala-017 teach time.
// This unit uses http4s-dsl only (Request/Response/HttpRoutes). Ember and JSON modules wait for later units.
//> using dep "org.http4s::http4s-dsl::0.23.36"
//> using test.dep "org.scalameta::munit::1.0.4"
// munit-scalacheck has no 1.0.4; 1.0.0 is the 1.0 line. ScalaCheck 1.18.0 is transitive.
//> using test.dep "org.scalameta::munit-scalacheck::1.0.0"
