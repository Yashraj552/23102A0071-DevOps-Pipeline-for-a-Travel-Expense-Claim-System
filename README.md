# Travel Expense Claim System

A web application for submitting, reviewing and tracking travel expense claims, with a complete DevOps pipeline: Git and GitHub, Jenkins, Selenium, Docker and Ansible.

Releases: v1.0.0 (MVP) and v1.1.0 (final release).

## What it does
- Login with two roles: Employee and Reviewer
- Employees submit claims with destination, purpose, amount, travel date and a receipt (PDF, JPG or PNG up to 2 MB)
- The server validates the data: amount above zero, travel date not in the future
- Reviewers see the pending claims, open the receipt and approve or reject with a comment
- Employees track the status of their own claims: SUBMITTED, APPROVED or REJECTED

Demo accounts (for demonstration only): employee / password123, employee2 / password123, reviewer / password123

## Architecture
See [docs/13-architecture.md](docs/13-architecture.md) for the application and the delivery pipeline.

Commit, then Jenkins (checkout, build, package with unit tests, Selenium quality gate, Docker build, push to Docker Hub, deploy a fresh container, verify), then Ansible provisions the node.

## Technology
Java 17, Spring Boot 3.2.5, Thymeleaf, Spring Security, Spring Data JPA, H2, Maven, JUnit 5, Selenium WebDriver, Jenkins, Docker, Docker Hub, Ansible, Ubuntu with systemd

## Ports
- 8080 Jenkins
- 8081 Tomcat (optional deployment)
- 8082 application (local run and Docker container)
- 8083 application started by the UI test stage
- 8085 application on the Ansible node

## Run locally

    mvn clean package
    java -jar target/travel-expense-claim-system-1.1.0.war

Open http://localhost:8082 and the health check at http://localhost:8082/actuator/health

## Run with Docker

    docker run -d --name expense -p 8082:8082 yashraj23102a0071/expense-app:latest

## Run the UI tests
Start the application, then:

    mvn test -Pselenium

## Provision a node with Ansible
In Ubuntu (WSL2) with systemd:

    cd ansible
    ansible-playbook -i inventory.ini site.yml -K
    ansible-playbook -i inventory.ini health.yml -K

More playbooks: reset.yml (clean state), deploy.yml (release with automatic rollback).

## Repository structure
- src: application and tests
- Jenkinsfile, Dockerfile, scripts: pipeline, image and UI test script
- ansible: inventory, playbooks, templates, execution logs
- docs: documentation
- .github: issue and pull request templates

## Documentation
- docs/01 problem and scope, docs/02 backlog and plan, docs/03 requirements and architecture
- docs/04 branch policy
- docs/05 Jenkins job, docs/06 pipeline and Tomcat, docs/07 test plan, docs/08 continuous testing
- docs/09 Docker lifecycle, docs/10 Jenkins-Docker deployment
- docs/11 configuration specification, docs/12 provisioning and reliability
- docs/13 final architecture, docs/14 troubleshooting guide, docs/15 limitations and future work, docs/16 demo script

## Branch policy
main is stable and tagged, develop is the integration branch, feature and bugfix branches are short-lived and merged through pull requests. Details: [docs/04-branch-policy.md](docs/04-branch-policy.md)

## Author
Yashraj Patil (23102A0071)