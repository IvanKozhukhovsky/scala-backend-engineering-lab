# A GitHubRelease is not a git tag

The learner explained that `githubReleaseOf(deskNotes)` only describes a release: SemVer is `0.1.0` (MAJOR.MINOR.PATCH); `desk-v0.1.0` is the tag *string*. Evaluating the function does not create a git tag.

**Evidence**: retrieval after `ExercisesDay32Suite`.

**Implications**: Do not treat `v0.1.0` as SemVer. A monorepo tag names the service (`desk-v…`) so it does not collide with quotes.
