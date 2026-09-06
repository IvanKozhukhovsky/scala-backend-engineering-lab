# SHA is the pin; the tag is a comment

The learner named that a retargeted `v7` does not change the run: GitHub Actions still uses the 40-character commit written after `@`. `# v7` is documentation of which tag was resolved, not a second pin.

**Evidence**: retrieval after `ExercisesDay27Suite`; `ci/desk-pinned.yml` uses `actions/checkout@3d3c42e5… # v7`.

**Implications**: If they say the comment “tracks” the tag, ask which characters after `@` GitHub reads. Do not re-teach `USER 1000` unless they omit it.
