package com.claude.framework.listeners;

import com.aventstack.extentreports.Status;
import com.claude.framework.config.ConfigManager;
import com.claude.framework.driver.DriverManager;
import com.claude.framework.reports.ExtentManager;
import com.claude.framework.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG {@link ITestListener} wiring test lifecycle events to logging,
 * ExtentReports, and failure screenshots. Registered via {@code testng.xml}
 * so no individual test class needs to know reporting exists (Facade
 * pattern: a simple lifecycle hook fronting several reporting subsystems).
 */
public class TestListener implements ITestListener {

    private static final Logger LOGGER = LogManager.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        LOGGER.info("Starting test: {}", result.getMethod().getMethodName());
        ExtentManager.startTest(result.getMethod().getMethodName(), result.getMethod().getDescription());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOGGER.info("Test passed: {}", result.getMethod().getMethodName());
        ExtentManager.getTest().log(Status.PASS, "Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LOGGER.error("Test failed: {}", result.getMethod().getMethodName(), result.getThrowable());
        ExtentManager.getTest().log(Status.FAIL, "Test failed: " + result.getThrowable());

        if (ConfigManager.getInstance().getBoolean("screenshot.on.failure", true)) {
            try {
                String screenshotDir = ConfigManager.getInstance().get("screenshot.dir", "target/screenshots");
                String path = ScreenshotUtils.capture(
                        DriverManager.getDriver(), result.getMethod().getMethodName(), screenshotDir);
                if (path != null) {
                    ExtentManager.getTest().addScreenCaptureFromPath(path);
                }
            } catch (IllegalStateException e) {
                LOGGER.warn("No active driver to capture a failure screenshot from: {}", e.getMessage());
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOGGER.warn("Test skipped: {}", result.getMethod().getMethodName());
        ExtentManager.getTest().log(Status.SKIP, "Test skipped: " + result.getThrowable());
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.flush();
    }
}
