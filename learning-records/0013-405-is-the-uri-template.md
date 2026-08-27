# 405 names the URI template; 404 is a GET miss

The learner treated `POST /quotes/JPY` as 405: the path `/quotes/:code` is a real resource that only allows GET, so the catch-all method case fires without consulting `rateOf`. 404 is reserved for GET when that code is absent. Correction retained: 404 answers “no resource at this URI,” not “the method is allowed.” POST never asks whether JPY exists.

**Evidence**: retrieval after `ExercisesDay17Suite`; `case _ -> Root / "quotes" / code => MethodNotAllowed(Allow(GET))`.

**Implications**: JSON and API-error units can assume method-vs-missing is already split; do not re-teach 404 vs 405 unless a codec maps both to the same body.
