# Continuous Testing in Jenkins

## Purpose
The Selenium UI tests from Task 9 run automatically in the Jenkins pipeline. If a test fails, the pipeline stops and the application is not deployed.

## Stage order
1. Checkout
2. Build (mvn clean compile)
3. Package (mvn package: unit and integration tests, builds the WAR, archives it)
4. UI Tests (Selenium suite against the packaged WAR)
5. Deploy (Tomcat Manager)
6. Verify (health check)

A failed stage stops the pipeline, so Deploy and Verify are skipped when the UI Tests stage fails.

## How the UI Tests stage works
The stage runs scripts/run-ui-tests.ps1:
- It starts the WAR from the Package stage on port 8083 with an in-memory database, so test data is not kept.
- It waits for /actuator/health to answer.
- It runs mvn test -Pselenium with BASE_URL=http://localhost:8083, HEADLESS=true and NO_SANDBOX=true.
- It stops the application in a finally block, also when the tests fail, and returns the Maven exit code to Jenkins.

The same WAR file that passes the tests is the one that is deployed.

## Reports
- The post section publishes target/surefire-reports/*.xml with the JUnit step, also for failed builds. The results appear as Test Result on the build page, with a trend on the job page.
- Failure screenshots (target/screenshots) and the application log of the test run are archived as build artifacts.

## Defect demonstration
A deliberate defect (the amount error message is no longer shown on the claim form) was introduced on the branch bugfix/amount-error-message. Unit tests do not render the page, so they passed. The Selenium test tc03 failed, the pipeline stopped before Deploy, and the failure screenshot was archived. The defect was then corrected and the pipeline passed again.

## Requirements
- Jenkins and Tomcat running on the same machine, Chrome installed for all users
- Internet access on the first run (Selenium downloads the ChromeDriver)
- Ports: Jenkins 8080, Tomcat 8081, UI test application 8083