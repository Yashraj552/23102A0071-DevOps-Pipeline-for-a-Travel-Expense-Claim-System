# Troubleshooting Guide

## Problems that occurred during the project

### Linux commands typed in PowerShell, and Windows commands typed in Ubuntu
- Problem: sudo apt in PowerShell and wsl --shutdown inside Ubuntu both answered "command not found"
- Cause: PowerShell and the Ubuntu shell are two different environments
- Fix: run Git, Maven and wsl commands in PowerShell, and apt, Ansible and systemctl commands in Ubuntu

### Git author email set to a web address
- Problem: git config user.email contained the repository URL
- Cause: the wrong value was copied into the command
- Fix: set the real email address of the GitHub account with git config --global user.email

### Pull request merged into main instead of develop
- Problem: the reviewer-actions pull request was merged into main and develop did not receive the commits
- Cause: GitHub opens a pull request against the default base branch, and the base was not changed
- Fix: the branch was restored from its last commit and a new pull request into develop was merged. Always check the line "base: develop" before creating a pull request

### Commits made on develop instead of a feature branch
- Problem: the release baseline commits were made on develop and the push of the feature branch failed with "src refspec does not match any"
- Cause: the git checkout -b command was skipped
- Fix: create the branch at the current commit, push it, and move the local develop back to origin/develop with git branch -f

### Jenkinsfile with invalid quote characters
- Problem: the pipeline failed to start after the Jenkinsfile was generated from a script
- Cause: the Groovy triple quotes were written with escape characters
- Fix: the Jenkinsfile was corrected to use plain triple quotes (commit "fix: use plain triple quotes in Jenkinsfile bat steps")

### Docker build cannot pull the base images
- Problem: docker build failed with "lookup registry-1.docker.io: no such host" in Jenkins
- Cause: Docker Desktop could not resolve the Docker Hub address (DNS or network problem)
- Fix: test with docker pull hello-world, restart Docker Desktop and WSL (wsl --shutdown), set a DNS server in Docker Desktop, or change the network. Then run the pipeline again

### Selenium warning about the Chrome DevTools version
- Problem: the UI test log shows "Unable to find CDP implementation matching 155"
- Cause: the Selenium version is older than the installed Chrome version
- Fix: none needed. The tests do not use DevTools and pass. Upgrading Selenium would remove the warning

### Dry run of the playbook fails on a clean node
- Problem: ansible-playbook --check stopped at the task that creates the link to the release
- Cause: in check mode the release folder is not created, so the link target does not exist
- Fix: none needed. The real run succeeds. Dry runs are meaningful on a node that is already provisioned

### Permission denied when listing application folders
- Problem: ls on /var/lib/expense and /etc/expense answered "Permission denied"
- Cause: these folders are private to the service user, by design
- Fix: use sudo for the check

### Importing the clean WSL baseline failed
- Problem: wsl --import answered "The system cannot find the path specified"
- Cause: the baseline file from the earlier export did not exist
- Fix: the node was reset with the reset playbook instead

## Other common problems

### Port already in use
- Check with netstat -ano | findstr :PORT. The ports are 8080 Jenkins, 8081 Tomcat, 8082 application and container, 8083 UI tests and 8085 Ansible node

### Tomcat Manager answers 401 or 403
- 401: wrong user or password. 403: the user lacks the role manager-script. Correct tomcat-users.xml and restart Tomcat

### Chrome does not start in Jenkins
- Chrome must be installed for all users, and the Jenkins service must run under an account that can start it. Use HEADLESS=true and NO_SANDBOX=true for CI runs

### curl behaves differently in PowerShell
- In PowerShell curl is an alias of another command. Use curl.exe

### systemd is not running in WSL
- Add [boot] systemd=true to /etc/wsl.conf, run wsl --shutdown and reopen Ubuntu

### Docker login fails in Jenkins
- Check the credential dockerhub-creds (user name and access token with write permission). Create the repository expense-app first

### The container does not become healthy
- Run docker logs expense. Check that the application started and that port 8082 is not used by another process