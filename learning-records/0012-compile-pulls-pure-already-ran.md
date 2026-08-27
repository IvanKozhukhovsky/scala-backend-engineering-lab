# Compile pulls; pure already ran

The learner contrasted `IO.pure(listSum)` with `knownRates(...).compile.fold`: the `List` work inside `pure`'s argument finishes eagerly when the expression is evaluated, so `pure` only lifts an already-computed `Int`. The stream pipeline is a description until `compile` produces an `IO`, and that `IO` pulls elements (running `evalMap`) when executed. Correction retained: `IO.pure` is not a runner for the bracket contents — those ran before `pure` received the value.

**Evidence**: `ExercisesDay16Suite` (four tests pass); stream-based `totalConverted` via `knownRates` + `compile.fold`; retrieval distinguishing eager `List` + `pure` from lazy stream + compile.

**Implications**: Later HTTP/body streaming can assume pull-based `compile` at the edge without re-teaching `IO.pure` eagerness.
