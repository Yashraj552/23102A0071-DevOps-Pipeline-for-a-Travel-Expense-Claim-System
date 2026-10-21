package com.example.expense.ui;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

/**
 * Saves a screenshot to target/screenshots when a UI test fails.
 * It runs right after the test method and before the browser is closed in @AfterEach.
 */
public class ScreenshotOnFailureExtension implements AfterTestExecutionCallback {

    @Override
    public void afterTestExecution(ExtensionContext context) {
        if (context.getExecutionException().isEmpty()) {
            return;
        }
        Object instance = context.getRequiredTestInstance();
        if (!(instance instanceof BaseUiTest base) || base.getDriver() == null) {
            return;
        }
        try {
            File shot = ((TakesScreenshot) base.getDriver()).getScreenshotAs(OutputType.FILE);
            Path dir = Paths.get("target", "screenshots");
            Files.createDirectories(dir);
            String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            String name = context.getRequiredTestClass().getSimpleName() + "_"
                    + context.getRequiredTestMethod().getName() + "_" + stamp + ".png";
            Path target = dir.resolve(name);
            Files.copy(shot.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Failure screenshot saved: " + target.toAbsolutePath());
        } catch (Exception e) {
            System.err.println("Could not save failure screenshot: " + e.getMessage());
        }
    }
}