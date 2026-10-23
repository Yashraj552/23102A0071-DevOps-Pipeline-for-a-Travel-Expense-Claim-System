# Jenkins-Docker Continuous Deployment

## Purpose
After the tests pass, the Jenkins pipeline builds a versioned Docker image, publishes it to Docker Hub and replaces the running container with a fresh one. A commit on the configured branch is enough to start this.

## Stages
1. Checkout, Build, Package: compile, unit and integration tests, WAR file
2. UI Tests: Selenium suite against the packaged application. A failure stops the pipeline here
3. Docker Build: reads the project version from pom.xml and builds the image expense-app:<version>-<build number> (and latest). The image gets the labels org.opencontainers.image.revision (Git commit) and ci.build.number
4. Docker Push: logs in to Docker Hub with the Jenkins credential dockerhub-creds, pushes <user>/expense-app:<version>-<build number> and latest, then logs out
5. Deploy Container: pulls the pushed image, stops and removes the old container expense and starts a new one
6. Verify Container: calls /actuator/health and shows the running container
7. Deploy to Tomcat and Verify Tomcat: optional (parameter DEPLOY_TOMCAT), the deployment from Task 8

## Versioning
- Tag: <project version>-<Jenkins build number>, for example 1.0.0 followed by a dash and the build number. Each build has its own tag and can be redeployed
- latest always points to the newest build
- The image labels connect an image to its Git commit and build number

## Container
- Name: expense
- Port: host 8082 to container 8082
- Volumes: expense-data (/app/data) and expense-uploads (/app/uploads), so data survives the replacement
- Restart policy: unless-stopped

## Trigger
The Jenkinsfile polls the repository every two minutes (pollSCM). A new commit on the branch configured in the job starts the pipeline automatically.

## Setup needed
- Docker Desktop running, and the Jenkins service allowed to use it
- Docker Hub repository expense-app and the Jenkins credential dockerhub-creds (username and access token)
- Credentials are never stored in the repository

## Checking a deployment

    docker ps --filter name=expense
    docker inspect expense --format "{{.Config.Image}}"
    curl.exe http://localhost:8082/actuator/health

## Limitations
- The Docker build compiles the application again inside the image, from the same commit that was tested
- The image is public on Docker Hub and contains the demo accounts of the application
- There is no automatic rollback yet. Rollback is part of Task 14