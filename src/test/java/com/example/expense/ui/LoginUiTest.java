package com.example.expense.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Journey 1: login (TC-01). */
class LoginUiTest extends BaseUiTest {

    @Test
    void tc01a_employeeCanLogIn() {
        login(EMPLOYEE, PASSWORD);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("current-user")));

        assertEquals("employee", text(By.id("current-user")));
        assertEquals("Travel Expense Claim System", text(By.id("app-title")));
        assertTrue(driver.getCurrentUrl().startsWith(baseUrl));
    }

    @Test
    void tc01b_wrongPasswordShowsError() {
        login(EMPLOYEE, "wrong-password");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-error")));

        assertEquals("Invalid username or password", text(By.id("login-error")));
        assertTrue(driver.getCurrentUrl().contains("/login"));
    }
}