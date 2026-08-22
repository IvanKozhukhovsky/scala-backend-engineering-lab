# Error reasons are an enum, not a String on Left

The learner treated domain failures as a closed set of `SignupError` cases (`EmptyName`, `TooYoung(age)`, and in explanation `Banned`) rather than `Left("…")` on `Either[String, String]`. That is the same `Left` payload idea as Day 06, now with a type the compiler can count.

**Evidence**: independent `label` / `register` / `explain` (exhaustive matches, no `case _` on the enums), plus the retrieval answer that typical errors belong on an enum.

**Implications**: `scala-008` can introduce traits and type classes without re-teaching closed alternatives or putting a domain error on `Left`.
