package com.example.expense.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

/** Journey 4: reviewer approves a claim (TC-04). Journey 5: employee sees APPROVED (TC-05). */
class ReviewWorkflowUiTest extends BaseUiTest {

    @Test
    void tc04_reviewerApprovesPendingClaim() throws IOException {
        String destination = uniqueDestination();
        loginAs(EMPLOYEE);
        submitValidClaim(destination);
        logout();

        approveClaim(destination, "Approved by UI test");

        assertTrue(text(By.id("flash-message")).contains("approved"));
        open("/review");
        assertFalse(driver.getPageSource().contains(destination), "An approved claim must leave the pending list");
    }

    @Test
    void tc05_employeeSeesApprovedStatusAndComment() throws IOException {
        String destination = uniqueDestination();
        String comment = "Receipt verified by UI test";
        loginAs(EMPLOYEE);
        submitValidClaim(destination);
        logout();
        approveClaim(destination, comment);
        logout();

        loginAs(EMPLOYEE);
        open("/claims");
        By row = claimRow("claims-table", destination);
        wait.until(ExpectedConditions.presenceOfElementLocated(row));

        String status = driver.findElement(row).findElement(By.cssSelector("span.badge"))
                .getAttribute("textContent").trim();
        assertEquals("APPROVED", status);
        assertTrue(driver.findElement(row).getText().contains(comment));
        assertTrue(Integer.parseInt(text(By.id("count-approved"))) >= 1);
    }
}