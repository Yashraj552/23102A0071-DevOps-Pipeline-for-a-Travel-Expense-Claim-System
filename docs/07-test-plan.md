# Test Plan: Selenium UI Tests

## Purpose and scope
The UI tests check the critical user journeys of the Travel Expense Claim System in a real browser: login, claim submission, validation, reviewer approval and status tracking. They complement the unit and integration tests that run during the Maven build.

## Tools
- Selenium WebDriver (version managed by Spring Boot dependency management) with Google Chrome
- JUnit 5, executed through Maven Surefire
- Selenium Manager downloads the matching ChromeDriver automatically

## Environment
- The application must be running. Default address: http://localhost:8082 (set with BASE_URL)
- Demo accounts: employee, reviewer (password: password123)
- Chrome opens a visible window by default. Use HEADLESS=true for no window

## How to run
Build and run the unit tests (UI tests are not included):

    mvn clean package

Start the application in one window:

    java -jar target\travel-expense-claim-system-1.0.0.war

Run the UI tests in a second window:

    mvn test -Pselenium
    mvn test -Pselenium "-DHEADLESS=true"
    mvn test -Pselenium "-DBASE_URL=http://localhost:8081/expense-dev"

UI tests carry the JUnit tag ui. They are excluded from the normal build and run only with the selenium profile.

## Test cases
| ID | Journey | Steps | Expected result | Test method |
|---|---|---|---|---|
| TC-01a | Login with valid account | Open login page, enter employee and password, click Log in | Home page opens, shows the user employee and the application title | LoginUiTest.tc01a_employeeCanLogIn |
| TC-01b | Login with wrong password | Enter employee and a wrong password | Error "Invalid username or password", stays on login page | LoginUiTest.tc01b_wrongPasswordShowsError |
| TC-02 | Submit a valid claim | Log in as employee, fill the claim form, attach a receipt, submit | Message "Claim submitted successfully", claim listed with status SUBMITTED | ClaimSubmissionUiTest.tc02_validClaimIsSubmittedAndListed |
| TC-03 | Invalid claim | Submit amount -5, a future date and no receipt | Errors for amount, travel date and receipt, claim is not saved | ClaimSubmissionUiTest.tc03_invalidClaimShowsValidationErrors |
| TC-04 | Reviewer approves | Employee submits a claim, reviewer opens it from the pending list, adds a comment, approves | Message that the claim was approved, claim leaves the pending list | ReviewWorkflowUiTest.tc04_reviewerApprovesPendingClaim |
| TC-05 | Employee sees APPROVED | After TC-04 steps, employee opens My Claims | Status APPROVED, reviewer comment shown, approved count at least 1 | ReviewWorkflowUiTest.tc05_employeeSeesApprovedStatusAndComment |

## Test data
| Data | Value |
|---|---|
| Employee account | employee / password123 |
| Reviewer account | reviewer / password123 |
| Wrong password | wrong-password |
| Valid claim | destination UiTest-(time stamp), purpose UI test claim, amount 2500.00, travel date yesterday, receipt a small temporary .pdf file |
| Invalid claim | amount -5, travel date tomorrow, no receipt |
| Review comment | Approved by UI test, Receipt verified by UI test |

Each test creates its own claim with a unique destination, so tests do not depend on each other or on older data.

## Failure screenshots
When a UI test fails, the JUnit extension ScreenshotOnFailureExtension saves a PNG to target/screenshots, named after the test class, the method and the time. It runs before the browser is closed.

To demonstrate it, run the opt-in test that fails on purpose:

    mvn test -Pselenium "-DUI_DEMO_FAILURE=true" "-Dtest=FailureScreenshotDemoUiTest"

## Test report
Maven Surefire writes the results to target/surefire-reports (text and XML files per test class) and prints a summary in the console.

## Limitations
- The application must be started separately. Starting it inside the pipeline is added in Task 10.
- Only Chrome is covered.
- The tests use the demo accounts and the application database.