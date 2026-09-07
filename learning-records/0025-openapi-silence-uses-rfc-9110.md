# OpenAPI silence uses RFC 9110; listed operations still use OpenAPI

The learner named RFC 9110 as the expected-value source for `POST /health` (405 + `Allow: GET`) because the OpenAPI path item does not list POST. They overshot by calling OpenAPI “only the description” with lower priority. OpenAPI remains the contract for operations it lists (GET 200, POST 422). RFC 9110 fills the gap the document does not assert.

**Evidence**: retrieval after `ExercisesDay30Suite`.

**Implications**: TDD expected values come from OpenAPI for documented operations and from RFC 9110 for omitted methods. Do not rank the two as a single priority list.
