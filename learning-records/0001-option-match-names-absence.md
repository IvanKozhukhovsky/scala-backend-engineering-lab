# Match names both Option cases

The learner consumed `Option` with explicit `Some`/`None` matches and used `List.find` to produce `None` for a missing id, without `.get` or `null`. On retrieval they distinguished `String` from `Option[String]`: returning the inner `str` requires `Some(str)` because the method’s type is still an option.

**Evidence**: independent exercise plus the explanation that `str` is a `String`, so the no-`@` branch must wrap it (or reuse the existing `Some`).

**Implications**: `scala-006` can move to failure-with-a-reason (`Either` / `Try`) without re-teaching how to wrap and unwrap a 0-or-1 container.
