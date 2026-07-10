package com.claude.framework.utils;

import com.claude.framework.exceptions.FrameworkException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Captures PNG screenshots on demand (typically wired into a TestNG
 * listener's {@code onTestFailure} hook) and returns the saved path so it
 * can be embedded in the ExtentReports HTML report.
 */
public final class ScreenshotUtils {

    private static final Logger LOGGER = LogManager.getLogger(ScreenshotUtils.class);
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    private ScreenshotUtils() {
    }

    public static String capture(WebDriver driver, String testName, String screenshotDir) {
        if (!(driver instanceof TakesScreenshot takesScreenshot)) {
            LOGGER.warn("Driver does not support screenshots: {}", driver.getClass().getName());
            return null;
        }
        try {
            File source = takesScreenshot.getScreenshotAs(OutputType.FILE);
            String fileName = "%s-%s.png".formatted(sanitize(testName), LocalDateTime.now().format(TIMESTAMP));
            File destination = Paths.get(screenshotDir, fileName).toFile();
            FileUtils.copyFile(source, destination);
            LOGGER.info("Screenshot saved to {}", destination.getAbsolutePath());
            return destination.getAbsolutePath();
        } catch (IOException e) {
            throw new FrameworkException("Failed to capture screenshot for test: " + testName, e);
        }
    }

    private static String sanitize(String testName) {
        return testName.replaceAll("[^a-zA-Z0-9-_]", "_");
    }
}
