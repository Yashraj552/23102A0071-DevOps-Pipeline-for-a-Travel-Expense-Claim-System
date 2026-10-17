# Travel Expense Claim System

A web application for submitting, reviewing and tracking travel expense claims, built to demonstrate a complete DevOps pipeline (Git, Jenkins, Selenium, Docker, Ansible).

## Tech stack
Java 17, Spring Boot 3.2.5, Maven, Thymeleaf, H2, Tomcat, Jenkins, Selenium, Docker, Ansible

## Features (MVP)
- Employee claim submission with receipt upload
- Data and receipt validation
- Reviewer pending list, approve or reject with comment
- Status tracking (SUBMITTED, APPROVED, REJECTED)

## Run locally
```bash
mvn clean package
mvn spring-boot:run
```
App: http://localhost:8082 | Health: http://localhost:8082/actuator/health

## Project structure
```
docs/       documentation and screenshots
ansible/    configuration management
src/        application source and tests
.github/    issue and pull request templates
```

## Branch policy
- main: stable and tagged, no direct commits
- develop: integration branch
- `feature/<name>`, `bugfix/<name>`: short-lived working branches

Details: [docs/04-branch-policy.md](docs/04-branch-policy.md)

## Commit convention
feat, fix, docs, test, chore (format: `type: description`)

## Documentation
See the docs folder for problem scope, backlog, SRS and architecture.

## Author
Yashraj Patil (23102A0071)
## Routes
- `GET /` home page
- `GET /claims` list of claims
- `GET /claims/new` claim form
- `POST /claims` submit a claim
- `GET /claims/{id}` claim details
