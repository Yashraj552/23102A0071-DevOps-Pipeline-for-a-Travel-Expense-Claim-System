# Live Demonstration Script

## Before the demonstration
- Docker Desktop, Jenkins and the Ubuntu node are running, and nothing else uses ports 8082, 8083 and 8085
- The release pull request (version 1.1.0) is open and ready to merge
- Open windows: GitHub (repository, pull request, Docker Hub tags), Jenkins job, a browser tab for http://localhost:8082 and http://localhost:8085, PowerShell and Ubuntu terminals

## Steps
1. Introduction: the problem, the application and the architecture diagram (docs/13-architecture.md)
2. Current state: the application on port 8082 shows version 1.0.0, the node on port 8085 shows version 1.0.0, Docker Hub shows the existing tags
3. Git commit: merge the release pull request into develop
4. Jenkins starts by itself (cause "Started by an SCM change"). Explain the stages while it runs: checkout, build, package with unit tests, the Selenium quality gate, Docker build, push to Docker Hub, deploy a fresh container, verify
5. Result of the pipeline: new image tag on Docker Hub, new container, the application on port 8082 shows version 1.1.0
6. Ansible: copy the WAR built by Jenkins, run site.yml, run health.yml. The node switches to release 1.1.0 and stays healthy; release 1.0.0 remains for rollback
7. Reliability: show the releases folder and explain the automatic rollback of deploy.yml (shown in the earlier test)
8. Conclusion: repository, tags, limitations and future work

## If something fails
- Jenkins does not start a build within 3 minutes: press Build Now and say so
- Docker stages fail: show the last successful run and explain the cause (see the troubleshooting guide)
- A UI test fails: this is the quality gate working. Show the failure screenshot and the stopped deployment
- Ubuntu is not running: start it with wsl -d Ubuntu-24.04