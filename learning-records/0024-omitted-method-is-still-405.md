# Omitted OpenAPI method is still 405

The learner first treated `POST /health` as an unknown path (or as “return nothing” because OpenAPI does not list it). They then named **405 Method Not Allowed**: `/health` exists because GET is in the spec; POST is only an omitted operation. OpenAPI stays silent; the service still answers HTTP. RFC 9110 also wants `Allow: GET`.

**Evidence**: retrieval after `ExercisesDay29Suite`; desk OpenAPI has GET-only `/health`.

**Implications**: scala-030 implements 405 + `Allow` for methods absent from a documented path, not 404. Do not map “not in the OpenAPI document” onto “no such URI.”
