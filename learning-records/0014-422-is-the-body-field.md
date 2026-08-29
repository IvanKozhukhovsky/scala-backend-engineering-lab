# 422 names a body the URI cannot process

The learner split GET `/orders/JPY` (404: that item URI has no resource) from POST `/orders` with `{"code":"JPY","qty":2}` (422: `/orders` exists; JPY is a field). They kept 422 off 400 because the JSON syntax is well-formed. Correction retained: 404 answers “no resource at this URI,” not “we already know this path.” 400 is broken syntax; 422 is usable JSON whose instructions cannot run.

**Evidence**: retrieval after `ExercisesDay18Suite`; `Left(OrderError.UnknownCode) => UnprocessableContent(ApiError("unknown_code"))`.

**Implications**: service-architecture and integration-test units can assume this error contract; do not re-teach 400 vs 422 vs 404 unless a later layer maps them to one body.
