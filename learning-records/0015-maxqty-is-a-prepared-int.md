# maxQty is a prepared Int, not an env lookup

The learner named the split: if `orderOf` loaded `ORDER_MAX_QTY` itself it would own the configuration dependency; as written it only owns the domain rule and receives a prepared `Int`. The HTTP shell (or the test) decides where that number comes from.

**Evidence**: retrieval after `ExercisesDay19Suite`; `orderOf(code, qty, maxQty: Int)` with Ciris only in `orderConfig`; `logger.info(Map("code", "op"))` on successful POST.

**Implications**: HTTP integration tests can inject `OrderConfig` / `QuoteConfig` without a live environment. Do not re-teach passing values vs looking up env unless a later layer reads config inside a repository.
