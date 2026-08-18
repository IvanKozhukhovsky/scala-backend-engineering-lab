# `find` returns `Option` because a first match may be absent

The learner explained that `List.find` yields `Some(value)` when a match exists and `None` when it does not, using the Day 03 adult-over-30 example. Future sessions can treat `Option` as already met in this 0-or-1 form, and teach why it is a better absence model than `null` or a possibly-empty `List`.

**Evidence:** retrieval answer after `Day03Suite` locked `Some(User(Charlie, 35))` and `None` for `firstAdultOver30`.

**Implications:** scala-005 (Option and safe absence) should build on this, not restart from zero.
