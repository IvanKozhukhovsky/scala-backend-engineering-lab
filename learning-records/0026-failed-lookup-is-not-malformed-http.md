# A failed lookup is not a malformed request

The learner named RFC 9110 503 Service Unavailable for `SQLException` on POST `/orders` with a well-formed EUR body. They overshot by saying we cannot verify that the HTTP request is well-formed. JSON syntax is already decided (400 vs 422) before `lookup`. JDBC failed to answer whether EUR is in the catalog and what cents to use. `Right(None)` is 422 `unknown_code`; `Left(_)` is 503 because there is no row list at all.

**Evidence**: retrieval after `ExercisesDay31Suite`.

**Implications**: Do not collapse 400 (syntax) with 503 (unavailable catalog). 422 still means the catalog answered “no such code.”
