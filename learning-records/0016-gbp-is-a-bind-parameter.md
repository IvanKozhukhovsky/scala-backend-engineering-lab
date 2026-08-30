# GBP is a bind parameter, not SQL text

The learner named the split: `findFill("GBP").sql` has `?` and no `GBP`; the code is sent to the database as a parameter beside the statement, so a hostile string cannot become extra SQL.

**Evidence**: retrieval after `ExercisesDay21Suite`; `sql"… where code = $code"` rather than `s"…'$code'"`.

**Implications**: The repository unit can transact these `Query0` values without re-teaching why concatenation is injection. If a later query quotes `$code` (`'?'`), ask whether that is a bind or a one-character literal.
