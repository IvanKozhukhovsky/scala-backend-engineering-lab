# Show lives in the given, not on the case class

The learner treated `given Showable[User]` plus `extension def show` as the implementation of `Showable.show`, with the compiler filling that method in — not `User.toString` and not a method written on the case class. That is the type-class split: the capability sits beside the type.

**Evidence**: independent `label` / `asNonEmpty` / `Showable[Status]` / `Showable[User]` / `shown` (tests pass, including `shown` ≠ `toString`), plus the retrieval answer that `given` supplies `show` and the compiler finds it.

**Implications**: `scala-009` can teach MUnit without re-teaching `given` / `using`. Later JSON or equality instances are the same shape with different names — do not introduce those libraries yet.
