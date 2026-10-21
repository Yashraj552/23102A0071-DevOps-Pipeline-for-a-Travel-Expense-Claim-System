package com.example.expense.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Base class for all UI tests. The tests need a running application.
 * System properties: BASE_URL (default http://localhost:8082) and HEADLESS (default false).
 */
@Tag("ui")
@ExtendWith(ScreenshotOnFailureExtension.class)
public abstract class BaseUiTest {

    protected static final String EMPLOYEE = "employee";
    protected static final String REVIEWER = "reviewer";
    protected static final String PASSWORD = "password123";

    protected final String baseUrl = stripTrailingSlash(System.getProperty("BASE_URL", "http://localhost:8082"));
    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeEach
    void startBrowser() {
        ChromeOptions options = new ChromeOptions();
        if (Boolean.parseBoolean(System.getProperty("HEADLESS", "false"))) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1366,900");
        // Switch off Chrome's password manager and the "password found in a data breach" popup,
        // because the demo password is a well-known one and the popup would block clicks.
        java.util.Map<String, Object> prefs = new java.util.HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        options.setExperimentalOption("prefs", prefs);
        options.addArguments("--disable-features=PasswordLeakDetection,PasswordManagerOnboarding");
        options.addArguments("--disable-notifications");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void stopBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    public WebDriver getDriver() {
        return driver;
    }

    private static String stripTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    protected void open(String path) {
        driver.get(baseUrl + path);
    }

    protected void login(String username, String password) {
        open("/login");
        driver.findElement(By.id("username")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("login-button")).click();
    }

    protected void loginAs(String username) {
        login(username, PASSWORD);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("current-user")));
    }

    protected void logout() {
        driver.findElement(By.id("nav-logout")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logout-message")));
    }

    /** Text of an element read from the DOM, so it does not depend on CSS visibility. */
    protected String text(By by) {
        wait.until(ExpectedConditions.presenceOfElementLocated(by));
        return driver.findElement(by).getAttribute("textContent").trim();
    }

    protected String uniqueDestination() {
        return "UiTest-" + System.currentTimeMillis();
    }

    protected Path createReceipt(String extension) throws IOException {
        Path file = Files.createTempFile("ui-test-receipt-", "." + extension);
        Files.writeString(file, "sample receipt for UI test");
        file.toFile().deleteOnExit();
        return file;
    }

    protected void fillClaimForm(String destination, String purpose, String amount, LocalDate travelDate) {
        driver.findElement(By.id("destination")).sendKeys(destination);
        driver.findElement(By.id("purpose")).sendKeys(purpose);
        driver.findElement(By.id("amount")).sendKeys(amount);
        // The date is set with JavaScript in ISO format, because typing into a date field depends on the browser locale.
        WebElement date = driver.findElement(By.id("travelDate"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", date, travelDate.toString());
    }

    /** The employee must be logged in. Submits a valid claim and waits for the success message. */
    protected void submitValidClaim(String destination) throws IOException {
        open("/claims/new");
        fillClaimForm(destination, "UI test claim", "2500.00", LocalDate.now().minusDays(1));
        driver.findElement(By.id("receipt")).sendKeys(createReceipt("pdf").toAbsolutePath().toString());
        clickWithScroll(By.id("submit"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("flash-message")));
    }

    /** Logs in as the reviewer, opens the claim from the pending list and approves it with a comment. */
    protected void approveClaim(String destination, String comment) {
        loginAs(REVIEWER);
        open("/review");
        WebElement row = wait.until(ExpectedConditions.presenceOfElementLocated(claimRow("pending-table", destination)));
        row.findElement(By.tagName("a")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("claim-status")));
        driver.findElement(By.id("comment")).sendKeys(comment);
        clickWithScroll(By.id("approve-button"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("flash-message")));
    }

    /** Scrolls the element to the centre of the window, then clicks it, so nothing at the page edge can block the click. */
    protected void clickWithScroll(By by) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        try {
            try {
            wait.until(ExpectedConditions.elementToBeClickable(by)).click();
        } catch (org.openqa.selenium.ElementClickInterceptedException e) {
            Object cover = ((JavascriptExecutor) driver).executeScript(
                "var r = arguments[0].getBoundingClientRect();"
                + "var c = document.elementFromPoint(r.left + r.width / 2, r.top + r.height / 2);"
                + "return c ? c.outerHTML.substring(0, 200) : 'none';", element);
            System.out.println("Click blocked by: " + cover);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
        } catch (org.openqa.selenium.ElementClickInterceptedException e) {
            Object cover = ((JavascriptExecutor) driver).executeScript(
                "var r = arguments[0].getBoundingClientRect();"
                + "var c = document.elementFromPoint(r.left + r.width / 2, r.top + r.height / 2);"
                + "return c ? c.outerHTML.substring(0, 200) : 'none';", element);
            System.out.println("Click blocked by: " + cover);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }
    protected By claimRow(String tableId, String destination) {
        return By.xpath("//table[@id='" + tableId + "']//tr[td[normalize-space()='" + destination + "']]");
    }
}