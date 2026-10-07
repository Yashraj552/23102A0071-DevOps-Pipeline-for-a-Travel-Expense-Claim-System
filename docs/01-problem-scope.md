# Task 1: Problem Definition and Scope

## 1. Problem Statement
Employees who travel for work currently claim expenses through paper forms or email attachments. Receipts get lost, managers cannot see which claims are pending, employees cannot see where their claim stands, and finance re-checks every claim by hand. This causes delayed reimbursements, errors and no audit trail.

The Travel Expense Claim System is a small web application where employees submit claims with a receipt, the system validates them automatically, reviewers approve or reject them, and everyone can track status. The project's main focus is the **DevOps pipeline** around it: Git, Jenkins, Selenium, Docker and Ansible.

## 2. Target Users
| User | Description |
|---|---|
| Employee | Travels for work and submits expense claims |
| Reviewer | Manager who approves or rejects claims |

## 3. Existing Pain Points
1. Paper or email claims are lost or incomplete.
2. No visibility of claim status for employees.
3. Manual validation (amount, date, receipt) is slow and inconsistent.
4. Reviewers have no single list of pending claims.
5. No history or audit trail of decisions.

## 4. Stakeholders
| Stakeholder | Interest |
|---|---|
| Employee | Fast submission, clear status |
| Reviewer | Simple pending list, quick decision with comment |
| Finance/Admin | Reliable approved-claim records |
| Evaluator/Faculty | Working MVP and complete DevOps evidence |
| Developer (me) | Deliver within the semester using free tools |

## 5. Objectives
1. Build a minimal working claim workflow: submit, validate, review, approve/reject, track.
2. Automate build, test and deployment with a Jenkins pipeline.
3. Package the app in Docker and provision servers with Ansible.
4. Produce documentation and evidence for all 15 tasks.

## 6. Constraints
- Single developer, one semester.
- Free and open-source tools only.
- Local machine, VM or WSL2 for all environments.
- Fixed stack: Java 17, Spring Boot, Maven, H2, Tomcat, Jenkins, Selenium, Docker, Ansible.
- Small MVP: no external integrations.

## 7. Measurable Success Criteria
| # | Criterion | Target |
|---|---|---|
| 1 | Time for an employee to submit a valid claim | Under 2 minutes |
| 2 | Invalid claims (amount <= 0, future date, bad receipt) rejected by the system | 100% |
| 3 | Status visible to employee after reviewer action | Immediately on page refresh |
| 4 | Commit-to-deployed-container pipeline time | Under 10 minutes |
| 5 | Selenium critical journeys passing | 5 of 5 |
| 6 | Re-running Ansible on a provisioned node | 0 changes (idempotent) |

## 8. Frozen MVP Scope (15 Tasks)
| # | Task | Deliverable |
|---|---|---|
| 1 | Problem definition and scope | This document |
| 2 | Agile planning and DevOps workflow | Backlog, board, DoD, diagram |
| 3 | Requirements, architecture, setup | SRS, diagrams, local setup |
| 4 | GitHub repository | Repo, README, templates |
| 5 | Feature 1 with branching | Claim submission + PR |
| 6 | MVP completion | Validation, review, tracking, conflict, tag |
| 7 | Jenkins CI job | Freestyle job + artefact |
| 8 | Pipeline as code + Tomcat | Jenkinsfile, deployment |
| 9 | Selenium tests | Test plan, scripts, report |
| 10 | Continuous testing | Quality gate in Jenkins |
| 11 | Docker | Dockerfile, lifecycle log |
| 12 | Jenkins-Docker CD | Versioned image, registry |
| 13 | Configuration management | Ansible playbook |
| 14 | Provisioning and reliability | Idempotency, health, rollback |
| 15 | Final release and viva | Report, demo, presentation |

### Application features in scope
Claim submission, data and receipt validation, reviewer pending list, approve/reject with comment, My Claims status tracking, simple login with two roles.

### Out of scope
Email or SMS notifications, payment integration, multi-currency, multi-level approval, reporting dashboards, mobile app, SSO, production-grade database.

## 9. Approval
MVP scope frozen on: ____/____/2026. Approved by: ______________ (guide/faculty).
