# Scala Backend Engineering Glossary

This glossary is intentionally sparse. Add a term only after it has been demonstrated in an exercise or explanation.

## Terms

**Option**:
A value that is either present or absent.
_Avoid_: null, nullable

**Some**:
The `Option` case that holds one value.

**None**:
The `Option` case that holds nothing.
_Avoid_: dummy records or magic zeros used as stand-ins for absence

**Try**:
A computation that either succeeded or failed with a `Throwable`.
_Avoid_: throwing that exception to the caller of a `Try`-returning method

**Success**:
The `Try` case that holds the computed value.

**Failure**:
The `Try` case that holds the exception.

**Either**:
A value that is exactly one of two types. In this workspace, `Left` is failure and `Right` is success.
_Avoid_: using `None` when the caller needs a reason

**Left**:
The `Either` case that holds the failure payload.

**Right**:
The `Either` case that holds the success payload.
