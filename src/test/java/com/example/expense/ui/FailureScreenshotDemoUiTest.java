package com.example.expense.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

/**
 * Fails on purpose to demonstrate the failure screenshot mechanism.
 * It only runs with -DUI_DEMO_FAILURE=true and is skipped otherwise.
 */
@EnabledIfSystemProperty(named = "UI_DEMO_FAILURE", matches = "true")
class FailureScreenshotDemoUiTest extends BaseUiTest {

    @Test
    void demoFailureCreatesScreenshot() {
        open("/login");
        assertEquals("This title is wrong on purpose", driver.getTitle());
    }
}