# Travel Expense Claim System

A web application for submitting, reviewing and tracking travel expense claims, built to demonstrate a complete DevOps pipeline (Git, Jenkins, Selenium, Docker, Ansible).

Current version: 1.0.0 (MVP)

## Tech stack
Java 17, Spring Boot 3.2.5, Spring Security, Maven, Thymeleaf, Bootstrap, H2, Tomcat, Jenkins, Selenium, Docker, Ansible

## Features (MVP)
- Login with two roles: Employee and Reviewer
- Employees submit claims with destination, purpose, amount, travel date and a receipt
- Data validation (amount above zero, travel date not in the future) and receipt validation (PDF, JPG or PNG, up to 2 MB)
- Reviewers see pending claims, open the receipt, and approve or reject with a comment (comment required on reject)
- Employees track their own claims: status (SUBMITTED, APPROVED, REJECTED), counts and reviewer comment

## Demo accounts (for demonstration only)
- employee / password123 (Employee)
- employee2 / password123 (Employee)
- reviewer / password123 (Reviewer)

## Run locally
    mvn clean package
    mvn spring-boot:run

App: http://localhost:8082 | Health: http://localhost:8082/actuator/health

## Routes
- `GET /login` login page
- `GET /` home page (login required)
- `GET /claims` My Claims (Employee)
- `GET /claims/new` and `POST /claims` submit a claim (Employee)
- `GET /claims/{id}` claim details (owner only)
- `GET /review` pending claims (Reviewer)
- `GET /review/{id}` review a claim (Reviewer)
- `POST /review/{id}/approve` and `POST /review/{id}/reject` (Reviewer)
- `GET /receipts/{claimId}` receipt file (owner or Reviewer)
- `GET /actuator/health` health check (public)

## Project structure
- docs: documentation and screenshots
- ansible: configuration management
- src: application source and tests
- .github: issue and pull request templates

## Branch policy
- main: stable and tagged, no direct commits
- develop: integration branch
- `feature/<name>`, `bugfix/<name>`: short-lived working branches

Details: [docs/04-branch-policy.md](docs/04-branch-policy.md)

## Commit convention
feat, fix, docs, test, chore (format: `type: description`)

## Author
Yashraj Patil (23102A0071)