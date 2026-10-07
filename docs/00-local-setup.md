# Local Development Setup (Ubuntu / WSL2)

```bash
sudo apt update
sudo apt install -y openjdk-17-jdk maven git curl
java -version
mvn -version
git --version
```

## Run the skeleton
```bash
cd travel-expense-claim-system
mvn clean package
mvn spring-boot:run
```
Open http://localhost:8082 and confirm the home page shows "Travel Expense Claim System".
Check http://localhost:8082/actuator/health returns `{"status":"UP"}`.

Also test the packaged artifact:
```bash
java -jar target/travel-expense-claim-system-0.0.1-SNAPSHOT.war
```

## Screenshots to capture (save in docs/screenshots/)
1. Terminal: java, mvn, git versions
2. Browser: home page on port 8082
3. Browser: /actuator/health
4. GitHub Projects Kanban board (15 cards)
5. Diagrams rendered (or draw.io PNG exports)
