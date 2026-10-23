# Docker Image and Container Lifecycle

## Purpose
The application is packaged as a Docker image so that it runs the same way on every machine. This document describes the Dockerfile and the container lifecycle.

## Files
- Dockerfile: multi-stage build
- .dockerignore: keeps build output, local data, documentation and Git files out of the build context

## Dockerfile design
- Stage 1: maven:3.9-eclipse-temurin-17 builds the WAR with mvn package. The tests are skipped because the Jenkins pipeline runs them. The Maven cache is kept in a BuildKit cache mount, so it is not part of the image.
- Stage 2: eclipse-temurin:17-jre contains only the JRE and the WAR file.
- The application runs as the non-root user expense.
- EXPOSE 8082 documents the application port.
- HEALTHCHECK calls /actuator/health, which is public.
- The database files and receipts are written to /app/data and /app/uploads, which can be mounted as volumes.
- The build argument APP_ENV (default docker) sets the app.environment build setting.

## Build and tag

    docker build -t expense-app:1.0.0 -t expense-app:latest .
    docker images expense-app
    docker history expense-app:1.0.0
    docker image inspect expense-app:1.0.0

## Run and map ports

    docker run -d --name expense -p 8082:8082 expense-app:1.0.0
    docker ps
    docker port expense
    curl.exe http://localhost:8082/actuator/health

The option -p host:container maps a host port to a container port.

## Logs and inspection

    docker logs --tail 40 expense
    docker inspect expense
    docker exec expense whoami
    docker stats --no-stream

## Stop, start, restart and remove

    docker stop expense
    docker start expense
    docker restart expense
    docker stop expense
    docker rm expense
    docker rmi expense-app:latest

## Container lifecycle
1. Image built with docker build
2. Container created and started with docker run (state: running, health: starting then healthy)
3. Stopped with docker stop (state: exited)
4. Started again with docker start or docker restart (state: running)
5. Removed with docker rm (the container is gone, the image remains)
6. Image tag removed with docker rmi

## Data
Without a volume, the data is lost when the container is removed. With the options -v expense-data:/app/data and -v expense-uploads:/app/uploads, it survives.

## Command log
The recorded session is in docker-command-log.txt (image ID, size and timings are in that file).

## Notes
- Docker Desktop (WSL2 backend) must be running.
- Pushing the image to Docker Hub and starting it from Jenkins are part of Task 12.