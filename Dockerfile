# Stage 1: build the WAR file with Maven (tests already run in the Jenkins pipeline)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
ARG APP_ENV=docker
RUN --mount=type=cache,target=/root/.m2 mvn -B -DskipTests -Dapp.environment=${APP_ENV} clean package

# Stage 2: small runtime image with only the JRE and the WAR file
FROM eclipse-temurin:17-jre

LABEL org.opencontainers.image.title="Travel Expense Claim System" \
      org.opencontainers.image.description="Spring Boot application for submitting, reviewing and tracking travel expense claims" \
      org.opencontainers.image.source="https://github.com/Yashraj552/23102A0071-DevOps-Pipeline-for-a-Travel-Expense-Claim-System"

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && useradd --system --create-home --shell /usr/sbin/nologin expense

WORKDIR /app
COPY --from=build --chown=expense:expense /build/target/*.war /app/app.war
RUN mkdir -p /app/data /app/uploads && chown expense:expense /app/data /app/uploads

USER expense
EXPOSE 8082

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD curl --fail --silent http://localhost:8082/actuator/health || exit 1

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.war"]