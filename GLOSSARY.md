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
