# Configuration Specification: Application Node

## Purpose
The specification lists what a server needs to run the Travel Expense Claim System. The Ansible playbook ansible/site.yml creates all of it.

## Target node
- Ubuntu (WSL2) with systemd, configured by Ansible with a local connection
- The inventory is ansible/inventory.ini, the variables are in ansible/group_vars/expense_nodes.yml

## Packages
| Package | Purpose |
|---|---|
| openjdk-17-jre-headless | Java runtime for the application (compiled for Java 17) |
| curl | Health checks from the command line |
| ca-certificates | Trusted certificates |
| ufw | Firewall, installed only when configure_firewall is true |

## User and group
| Name | Type | Purpose |
|---|---|---|
| expense (user and group) | System account, shell /usr/sbin/nologin, no login | Runs the application without privileges |

## Folders
| Path | Owner and mode | Purpose |
|---|---|---|
| /opt/expense | root, 0755 | Application home |
| /opt/expense/releases | root, 0755 | Release folders, one per version |
| /opt/expense/releases/<version> | root, 0755 | Files of one release |
| /etc/expense | root:expense, 0750 | Configuration |
| /var/lib/expense | expense, 0750 | Working directory of the application |
| /var/lib/expense/data | expense, 0750 | Database files |
| /var/lib/expense/uploads | expense, 0750 | Uploaded receipts |
| /var/log/expense | expense, 0750 | Application log |

## Files
| Path | Owner and mode | Purpose |
|---|---|---|
| /opt/expense/releases/<version>/app.war | root, 0644 | Executable application (copied from the build) |
| /opt/expense/current | symbolic link | Points to the active release folder. Used for updates and rollback |
| /etc/expense/expense.env | root:expense, 0640 | Settings: SERVER_PORT and APP_ENVIRONMENT |
| /etc/systemd/system/expense.service | root, 0644 | Service definition |
| /var/log/expense/expense.log | expense | Console output of the application |

## Ports
| Port | Protocol | Purpose |
|---|---|---|
| 8085 | TCP | Application (SERVER_PORT in the environment file) |

The firewall rule for the port is created only when configure_firewall is true.

## Services
| Service | State | Purpose |
|---|---|---|
| expense | enabled and started, restarted when the release, the unit or the environment file changes | Runs java -jar /opt/expense/current/app.war as the user expense |

## Checks done by the playbook
- systemd is the service manager
- The release file exists on the control node
- The application port is open and /actuator/health answers with status 200

## Variables
app_version, app_war_src, app_port, app_environment, configure_firewall and the folder names are set in ansible/group_vars/expense_nodes.yml.

## Run

    cd /mnt/c/Users/Yashraj/Desktop/travel-expense-claim-system/ansible
    ansible-playbook -i inventory.ini site.yml --check --diff -K
    ansible-playbook -i inventory.ini site.yml -K

## Limitations
- The control node and the target node are the same Ubuntu in WSL2
- The log is not rotated, the application uses a file-based H2 database