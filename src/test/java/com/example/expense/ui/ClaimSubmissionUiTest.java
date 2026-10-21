package com.example.expense.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Journey 2: submit a valid claim (TC-02). Journey 3: invalid claim shows validation errors (TC-03). */
class ClaimSubmissionUiTest extends BaseUiTest {

    @Test
    void tc02_validClaimIsSubmittedAndListed() throws IOException {
        loginAs(EMPLOYEE);
        String destination = uniqueDestination();

        submitValidClaim(destination);

        assertTrue(text(By.id("flash-message")).contains("Claim submitted successfully"));
        By row = claimRow("claims-table", destination);
        wait.until(ExpectedConditions.presenceOfElementLocated(row));
        String status = driver.findElement(row).findElement(By.cssSelector("span.badge"))
                .getAttribute("textContent").trim();
        assertEquals("SUBMITTED", status);
    }

    @Test
    void tc03_invalidClaimShowsValidationErrors() {
        loginAs(EMPLOYEE);
        String destination = uniqueDestination();

        open("/claims/new");
        fillClaimForm(destination, "UI test invalid claim", "-5", LocalDate.now().plusDays(1));
        driver.findElement(By.id("submit")).click();

        assertTrue(text(By.id("amount-error")).contains("Amount must be greater than zero"));
        assertTrue(text(By.id("travelDate-error")).contains("Travel date cannot be in the future"));
        assertTrue(text(By.id("receipt-error")).contains("Receipt is required"));

        open("/claims");
        assertFalse(driver.getPageSource().contains(destination), "The invalid claim must not be saved");
    }
}