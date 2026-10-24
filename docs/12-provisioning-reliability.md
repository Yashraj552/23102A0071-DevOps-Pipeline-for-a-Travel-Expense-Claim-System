# Automated Provisioning and Reliability Validation

## Purpose
The node is provisioned from a clean state with Ansible, the playbook is rerun to show that it changes nothing, the health is checked, and a failed release is rolled back to the previous stable release.

## Playbooks
| File | Purpose |
|---|---|
| ansible/site.yml | Provisions the node and deploys the release defined by app_version (from Task 13) |
| ansible/reset.yml | Returns the node to a clean state. Runs only with -e confirm_reset=yes |
| ansible/deploy.yml | Deploys a release and rolls back automatically if the health check fails |
| ansible/health.yml | Shows the active release, the service state and the health, and fails if the application is not healthy |

## Clean target environment
reset.yml stops and removes the service, deletes the application, configuration, data and log folders, removes the user and the group and purges the Java package. After the reset there is no user, no folder, no service, no Java and no listening port 8085.

## Provisioning
site.yml installs the packages, creates the user and the folders, installs the environment file and the service definition, copies release 1.0.0, points /opt/expense/current to it, starts the service and checks the port and the health endpoint.

## Idempotency
Running site.yml again on the provisioned node reports changed=0. The service is restarted only through handlers when the release, the service definition or the environment file changes.

## Health check
- site.yml waits for the port and calls /actuator/health
- health.yml reports the active release, the service state and the health status

## Releases and rollback
- Each release lives in /opt/expense/releases/<version>/app.war and /opt/expense/current points to the active one
- The previous stable release is 1.0.0, the release that provisioning deployed and checked
- deploy.yml copies the new release, switches the link, restarts the service and checks the health inside a block. If a task fails, the rescue section switches the link back to the previous release, restarts the service and checks the health again
- The failed release stays in the releases folder for analysis
- Manual rollback: run deploy.yml with the previous version and its file

## Rollback demonstration
A release 1.0.1-bad was simulated with an invalid file. The service could not start, the health check failed, and deploy.yml returned to release 1.0.0 (recap: rescued=1).

## Run

    cd /mnt/c/Users/Yashraj/Desktop/travel-expense-claim-system/ansible
    ansible-playbook -i inventory.ini reset.yml -K -e confirm_reset=yes
    ansible-playbook -i inventory.ini site.yml -K
    ansible-playbook -i inventory.ini site.yml -K
    ansible-playbook -i inventory.ini health.yml -K
    ansible-playbook -i inventory.ini deploy.yml -K -e release_version=<version> -e release_src=<file>

## Logs
ansible/logs contains reset-run.txt, provision-run.txt, idempotency-run.txt and rollback-run.txt.

## Limitations
- The control node and the node are the same Ubuntu in WSL2
- The bad release is simulated, and a run that was rolled back exits without an error. The sign is rescued=1 and the ROLLED BACK message
- Only the symbolic link and the service are rolled back. Data in /var/lib/expense is not changed by a rollback