# Laboratory Development Agent

## Repository conventions

- The main branch contains shared project configuration.
- Every laboratory has its own Git branch.
- Completed laboratory branches are reference implementations.
- Never modify completed laboratories or the main branch
  unless explicitly requested.
- Work only in the current laboratory branch.

## Assignment priorities

1. Correctness and full compliance with the assignment.
2. Compliance with required algorithms and restrictions.
3. Consistency with previous laboratories.
4. Good software architecture and readability.
5. Simple and maintainable implementation.

The assignment specification is the primary source of truth.

## Learning from previous laboratories

For each new laboratory:

- Inspect relevant previous laboratory branches using Git.
- Read their source code, project structure and build files.
- Identify recurring architecture and coding conventions.
- Reuse good ideas, not existing bugs or poor design.
- Improve architecture when justified by the new assignment.
- Avoid unnecessary frameworks and dependencies.

Use read-only Git commands when inspecting other branches.
Do not switch branches during implementation.

## Development workflow

1. Read the assignment completely.
2. Extract all requirements and restrictions.
3. Inspect relevant previous laboratory implementations.
4. Create an implementation plan.
5. Implement the solution incrementally.
6. Build and run the project when possible.
7. Test requirements and important edge cases.
8. Fix discovered defects.
9. Document how to build and run the solution.
10. Verify the final implementation against the assignment.

Do not claim that unexecuted tests passed.

## Git workflow

- Check the current branch before modifying files.
- Preserve existing user changes.
- Make meaningful commits after verified milestones.
- Use descriptive commit messages.
- Push commits to the current remote laboratory branch
  when permissions allow.
- Never force push or rewrite published history.
- Never merge branches without explicit authorization.
- Never commit secrets or unnecessary build artifacts.
- Never change unrelated files.

## Final report

Report in Russian:

- Requirements implemented
- Architecture and improvements
- Tests actually executed
- Remaining problems or limitations
- Commit hashes
- GitHub push status
- Instructions for building and running the project

## Batch laboratory workflow

When the user explicitly requests multiple laboratories:

- You may create and switch between new laboratory branches.
- Always create new branches from an updated main.
- Never modify existing completed laboratory branches.
- Process laboratories sequentially.
- Finish and commit one laboratory before switching.
- Ensure the working tree is clean before switching.
- Never force push or overwrite existing branches.
- Keep all assignment source files unchanged.
- Stop and report any unresolved Git conflicts.
