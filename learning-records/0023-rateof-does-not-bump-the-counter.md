# rateOf does not bump the counter

The learner named that `assertEquals(rateOf("JPY"), None)` must not touch the request total: `rateOf` is a pure `Map.get` by code string. They first said `Map.get` increments; the increment is `bump` at the HTTP rim, not inside the lookup. They also split `/health` (yes/no now, command inside the container) from `/metrics` (how many over time).

**Evidence**: retrieval after `ExercisesDay28Suite`; `deskRequests` is a separate `MetricSample`, not updated by `rateOf`.

**Implications**: If they put `bump` in `rateOf`, ask what the `None` test would share across calls. If they say HEALTHCHECK runs on the Docker host, ask where `wget` has to exist.
