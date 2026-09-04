# github-actions `/` is not a docker path

The learner first treated `directory: /` as “look in `.github/workflows`” for every ecosystem, including `docker` (so they thought a root docker watch would open `ci.yml`). After correction they named the split: `github-actions` with `/` uses `.github/workflows` as that ecosystem’s default; `docker` directories are folders from the repository root (`/` would be a root `Dockerfile`, which this lab does not have).

**Evidence**: retrieval after `ExercisesDay26Suite`; `.github/dependabot.yml` has `github-actions` at `/` and `docker` at `/packaging/quotes` plus `/packaging/desk`.

**Implications**: If they say Dependabot “opens workflows” from a docker row, ask which filename that ecosystem reads. Do not re-teach `ci/desk.yml` vs `.github/workflows` unless they put the desk recipe under workflows.
