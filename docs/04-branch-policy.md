# Branch and Commit Policy

## Branches
| Branch | Purpose | Rules |
|---|---|---|
| main | Stable, tagged releases | No direct commits. Merge from develop only. |
| develop | Integration of finished features | Merge feature branches through pull requests. |
| `feature/<name>` | New functionality | Branch from develop, e.g. feature/claim-submission |
| `bugfix/<name>` | Defect fixes | Branch from develop, e.g. bugfix/amount-validation |
| `hotfix/<name>` | Urgent fix on main | Merge to main and develop |

## Naming rules
Lowercase, words separated by hyphens, short and descriptive.

## Commit messages
Format: `type: short description` (imperative, under 72 characters)

| Type | Use |
|---|---|
| feat | New feature |
| fix | Bug fix |
| docs | Documentation |
| test | Tests |
| chore | Setup, config, build |
| refactor | Code change without behaviour change |

## Pull request rules
1. Open a PR from the feature branch into develop.
2. Address at least one review comment before merging.
3. Build must pass before merge.
4. Delete the branch after merging.

## Releases
Merge develop into main for a release and tag it, e.g. v1.0.0.