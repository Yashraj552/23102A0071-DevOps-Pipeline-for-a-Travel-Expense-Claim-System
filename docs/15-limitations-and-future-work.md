# Limitations and Future Enhancement Plan

## Limitations
- Everything runs on one local Windows machine: Jenkins, Docker Desktop, Tomcat and the Ansible node (Ubuntu in WSL2). The Ansible control node and target node are the same system
- The application uses a file-based H2 database and three demo accounts that are built in. There is no user registration and no password change
- Receipt checks use the file extension and the size, not the file content, and receipts are stored on the local disk
- The Docker image rebuilds the application from the same commit instead of reusing the WAR file that the tests ran against
- The image on Docker Hub is public and contains the demo accounts
- Jenkins polls the repository every two minutes, so a build starts with a delay. A webhook would need a public address
- The Selenium suite covers five journeys in Chrome only
- Rollback restores the previous release and the service, but does not roll back data
- There is no monitoring or alerting, and no vulnerability scanning

## Future enhancement plan
| Area | Enhancement |
|---|---|
| Application | Real user accounts with registration and roles in a database, email notifications, multi-level approval, multi-currency, reports |
| Database | PostgreSQL or MySQL with schema migrations (Flyway) instead of H2 |
| Testing | More Selenium journeys, more browsers, API tests, code coverage |
| Code quality and security | SonarQube, dependency and image scanning (for example Trivy), secrets in a vault |
| CI/CD | Webhook trigger, Jenkins agents, a private registry, build once and promote the same artifact to staging and production |
| Deployment | Kubernetes with rolling updates, blue-green or canary releases |
| Configuration management | Ansible roles and Molecule tests, a separate remote server with SSH, Terraform for the infrastructure |
| Operations | Prometheus and Grafana for metrics, centralized logs, alerts |