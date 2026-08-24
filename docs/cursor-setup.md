# Cursor Setup

This repository intentionally uses a small set of Cursor primitives rather than enabling every possible integration.

## Required local editor setup

1. Open the repository root in Cursor.
2. Install the Metals extension (`scalameta.metals`) for Scala language-server support.
3. Install JDK 21 and Scala CLI. The repository pins Scala itself in `project.scala`.
4. Verify the local toolchain with `python3 scripts/verify.py`.

## Third-party skills

Install Matt Pocock's skill collection interactively:

```bash
npx skills@latest add mattpocock/skills
```

Select these skills for this workspace:

- `teach` — required now. It is explicitly user-invoked and maintains a stateful teaching workspace (`MISSION.md`, `RESOURCES.md`, `lessons/`, `reference/`, `learning-records/`, `NOTES.md`).
- `tdd` — useful from the testing phase onward and for new behavior with an agreed public seam.
- `diagnosing-bugs` — useful when a failure is non-trivial and needs a systematic reproduction/feedback loop.

Do not add the full `code-review` workflow yet. This repository starts with the lighter `scala-reviewer` subagent; the multi-axis review workflow becomes useful once the capstone produces non-trivial diffs and specifications.

## Repository-native Cursor components

- `AGENTS.md` — concise project-wide contract.
- `.cursor/rules/` — persistent/scoped constraints for learning, Scala code, and public-repository quality.
- `.agents/skills/` — portable custom workflows for progress tracking, exercise review, and the GitHub learning board.
- `.cursor/commands/` — explicit convenience commands such as `/next-lesson`, `/progress`, and `/verify`.
- `.cursor/agents/scala-reviewer.md` — isolated review context for substantial exercise or backend changes.

## Integrations

GitHub MCP is enabled for the learning board. Everything below stays out until it matches a real curriculum need.

### MCP

The official GitHub MCP plugin is required for `/next-lesson`, `/progress`, and `/verify` to create, assign, and close `learning` issues. Local progress checks (`python3 scripts/progress.py check`) still work without GitHub.

Do not add other MCP servers until they match a real curriculum need, for example PostgreSQL inspection during the persistence phase.

### Plugins

No Cursor plugin is required at this stage. Metals is an editor extension, not a Cursor plugin. Add plugins only when the project actually adopts the external system they integrate with.

### Hooks

No custom hook is included yet because there is no deployment or secret-bearing workflow to enforce. Introduce hooks when deterministic policy enforcement becomes valuable, such as blocking unsafe deployment commands or running a secret scan before an external action.

This deliberate minimalism keeps the agent architecture understandable: rules constrain, skills teach procedures, tools execute, the reviewer subagent isolates judgment, and future MCP/hooks are introduced only at the problem boundary that requires them.
