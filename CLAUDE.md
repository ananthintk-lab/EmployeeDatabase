# Ralph Agent Instructions

You are an autonomous coding agent working on a software project.

## Your Task

1. Read the PRD at `prd.json` (in the same directory as this file)
2. Read the progress log at `progress.txt` (check Codebase Patterns section first)
3. Check you're on the correct branch from PRD `branchName`. If not, check it out or create from main.
4. Pick the **highest priority** user story where `passes: false`
5. Implement that single user story
5. Write unit tests for new business logic
6. Write integration tests for API/database changes
6. Run:
       mvnw compile -q
       mvnw test -q
       mvnw verify -q
7. If failure occurs:
       analyze logs
       fix code
       rerun tests
8. Update CLAUDE.md files if you discover reusable patterns (see below)
9. If checks pass, commit ALL changes with message: `feat: [Story ID] - [Story Title]`
10. Update the PRD to set `passes: true` for the completed story
11. Append your progress to `progress.txt`

## Progress Report Format

APPEND to progress.txt (never replace, always append):
```
## [Date/Time] - [Story ID]
- What was implemented
- Files changed
- **Learnings for future iterations:**
  - Patterns discovered (e.g., "this codebase uses X for Y")
  - Gotchas encountered (e.g., "don't forget to update Z when changing W")
  - Useful context (e.g., "the evaluation panel is in component X")
---
```

## Testing & Post-Implementation Workflow
- **Validation:** Always run API integration tests via `mvn test verify` immediately after completing any feature code implementation.
- **GitHub Push Policy:** Do not ask the user for permission to commit. Once tests pass, automatically:
-  1. Create a descriptive feature branch.
-  2. Do NOT commit broken code
-  3. Commit the changes following conventional commit style.
-  4. Push the branch to origin (`git push origin <branch-name>`).
-  5. Provide the user with the GitHub PR link.
-  6. Keep changes focused and minimal
-  7. Follow existing code patterns

The learnings section is critical - it helps future iterations avoid repeating mistakes and understand the codebase better.

## API Documentation Rules (MANDATORY)

After implementing any REST API change:

1. Detect Swagger/OpenAPI endpoint
2. Print endpoint in final output
3. Print all newly added/modified REST endpoints
4. Ensure API is visible in Swagger UI

### Swagger Endpoint Detection

Check these common endpoints:

* http://localhost:8080/swagger-ui/index.html
* http://localhost:8080/swagger-ui.html
* http://localhost:8080/v3/api-docs

If server port is customized, replace 8080 with configured port.

### Required Final Output Format

Always print:

Swagger UI: <swagger-url>

OpenAPI JSON: <openapi-json-url>

New/Updated Endpoints:

* GET /api/...
* POST /api/...
* PUT /api/...
* DELETE /api/...

### Validation

If Swagger is unavailable:

* Check if springdoc-openapi dependency exists
* Check server.port
* Check context-path
* Suggest required dependency/config changes
   

## Consolidate Patterns

If you discover a **reusable pattern** that future iterations should know, add it to the `## Codebase Patterns` section at the TOP of progress.txt (create it if it doesn't exist). This section should consolidate the most important learnings:

```
## Codebase Patterns
- Example: Use `sql<number>` template for aggregations
- Example: Always use `IF NOT EXISTS` for migrations
- Example: Export types from actions.ts for UI components
```

Only add patterns that are **general and reusable**, not story-specific details.

## Update CLAUDE.md Files

Before committing, check if any edited files have learnings worth preserving in nearby CLAUDE.md files:

1. **Identify directories with edited files** - Look at which directories you modified
2. **Check for existing CLAUDE.md** - Look for CLAUDE.md in those directories or parent directories
3. **Add valuable learnings** - If you discovered something future developers/agents should know:
   - API patterns or conventions specific to that module
   - Gotchas or non-obvious requirements
   - Dependencies between files
   - Testing approaches for that area
   - Configuration or environment requirements

**Examples of good CLAUDE.md additions:**
- "When modifying X, also update Y to keep them in sync"
- "This module uses pattern Z for all API calls"
- "Tests require the dev server running on PORT 3000"
- "Field names must match the template exactly"

**Do NOT add:**
- Story-specific implementation details
- Temporary debugging notes
- Information already in progress.txt

Only update CLAUDE.md if you have **genuinely reusable knowledge** that would help future work in that directory.

## Quality Requirements

- ALL commits must pass your project's quality checks (typecheck, lint, test)
- Do NOT commit broken code
- Keep changes focused and minimal
- Follow existing code patterns

## Browser Testing (If Available)

For any story that changes UI, verify it works in the browser if you have browser testing tools configured (e.g., via MCP):

1. Navigate to the relevant page
2. Verify the UI changes work as expected
3. Take a screenshot if helpful for the progress log

If no browser tools are available, note in your progress report that manual browser verification is needed.

## Stop Condition

After completing a user story, check if ALL stories have `passes: true`.

If ALL stories are complete and passing, reply with:
<promise>COMPLETE</promise>

If there are still stories with `passes: false`, end your response normally (another iteration will pick up the next story).

## Important

- Work on ONE story per iteration
- Commit frequently
- Keep CI green
- Read the Codebase Patterns section in progress.txt before starting

## When context exceeds ~90%:
1. Summarize progress
2. Save state in tracker
3. Continue next iteration

Build a Spring Boot REST API for employee management. Create simple CRUD API for creating employee, edit employee details, delete employee details.

Requirements:
- Java 21
- Spring Boot
- InMemory
- Validation
- CRUD operations
- Unit tests
- Integration tests


## Architecture
- **Backend**: Spring Boot
- **Database**: InMemory


## API Design Standards
- Use RESTful principles
- Implement proper HTTP status codes
- Add request/response examples in OpenAPI spec
- Use consistent error response format
- Add API versioning strategy


## Code Standards
- Prefer functional components over class components
- Write descriptive variable names (no single letters except for loops)
- Add JAVADoC comments for public functions
- Follow clean architecture principles
- Keep components under 200 lines

## Testing Guidelines
- Write tests using Jest and React Testing Library
- Aim for 80% code coverage minimum
- Include edge cases and error scenarios
- Test API Calls
- Mock external dependencies

## Quality Gates
These commands must pass for every user story:
- `mvn clean install`
- `mvn test`


<!-- ## Verification -->
<!-- After implementing any UI-facing change, use Playwright MCP to open the -->
<!-- running app (localhost:3000) and manually exercise the affected flow before -->
<!-- considering the task done. Report what you clicked/typed and what you saw. -->
