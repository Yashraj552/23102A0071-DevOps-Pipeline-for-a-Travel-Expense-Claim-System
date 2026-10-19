# Pipeline as Code and Tomcat Deployment

## Overview
The Jenkinsfile in the repository root defines the pipeline. It builds the project, packages the WAR file and deploys it to a local Apache Tomcat server. The target environment is chosen with the ENV build parameter.

## Tomcat
- Apache Tomcat 10.1.61 (Jakarta EE 10, required by Spring Boot 3), installed in C:\DevTools
- HTTP connector port changed from 8080 to 8081 in conf/server.xml (Jenkins uses 8080)
- User deployer with the role manager-script in conf/tomcat-users.xml (password not stored in the repository)
- Started from a PowerShell window with catalina.bat run

## Jenkins
- Job: travel-expense-claim-pipeline, type Pipeline, definition Pipeline script from SCM, script path Jenkinsfile
- Credential: tomcat-creds (Username with password) holds the Tomcat deployer login
- Tools: JDK 21 and Maven 3.9.16, called by full path

## Stages
1. Checkout: checkout scm
2. Build: mvn clean compile
3. Package: mvn package (runs the tests, builds the WAR) and archives target/*.war
4. Deploy: uploads the WAR to the Tomcat Manager text interface (/manager/text/deploy) with curl, and fails the stage if the reply does not start with OK
5. Verify: calls /actuator/health of the deployed application

## Parameterized setting
- ENV: dev or staging
- Context path: /expense-dev or /expense-staging
- Build setting: -Dapp.environment=<ENV> (shown on the home page, and used for the database file and receipt folder of that environment)

## Deployed URLs
- http://localhost:8081/expense-dev/
- http://localhost:8081/expense-staging/
- Health check: http://localhost:8081/expense-dev/actuator/health

## Notes
- Tomcat must be running before the pipeline is started.
- Database files and receipts are stored under the Tomcat folder (data and uploads).
- Tests as a quality gate before deployment are added in Task 10.