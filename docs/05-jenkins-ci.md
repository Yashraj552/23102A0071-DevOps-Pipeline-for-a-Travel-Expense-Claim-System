# Jenkins Continuous Integration Job

## Purpose
The job builds the Travel Expense Claim System automatically when a new commit appears on the develop branch, runs the unit and integration tests, and archives the packaged WAR file.

## Jenkins setup
- Jenkins LTS runs as a Windows service on http://localhost:8080
- Plugins: the suggested plugins (includes the Git plugin)
- Tools used by the job: JDK 21 and Maven 3.9.16, called by full path in the build step

## Job configuration
- Name: travel-expense-claim-ci
- Type: Freestyle project
- Source: https://github.com/Yashraj552/23102A0071-DevOps-Pipeline-for-a-Travel-Expense-Claim-System.git, branch */develop (public repository, no credentials)
- Trigger: Poll SCM, schedule H/2 * * * * (about every 2 minutes)
- Build step: Execute Windows batch command

      set "JAVA_HOME=C:\Program Files\Java\jdk-21"
      call "C:\DevTools\apache-maven-3.9.16\bin\mvn.cmd" -B clean package

- Post-build action: archive the artifacts, pattern target/*.war
- Old builds: the last 5 builds are kept

## How to check a build
1. Open the job travel-expense-claim-ci in Jenkins.
2. Open the latest build and its Console Output to see the Maven build and test result.
3. The packaged WAR file is listed under Build Artifacts.
4. Polling activity is listed under Git Polling Log on the job page.

## Notes
- Polling is used instead of a webhook because Jenkins runs locally without a public URL.
- Only the develop branch is built. The Jenkinsfile and deployment are added in Task 8.