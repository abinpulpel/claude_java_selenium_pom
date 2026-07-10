package com.claude.framework.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.claude.framework.config.ConfigManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Thread-safe wrapper around ExtentReports (Singleton for the report
 * instance, {@link ThreadLocal} for the per-test node) so parallel TestNG
 * threads each log to their own test entry without interleaving output in
 * the shared HTML report.
 */
public final class ExtentManager {

    private static volatile ExtentReports extentReports;
    private static final ThreadLocal<ExtentTest> CURRENT_TEST = new ThreadLocal<>();

    private ExtentManager() {
    }

    public static synchronized ExtentReports getInstance() {
        if (extentReports == null) {
            String reportDir = ConfigManager.getInstance().get("report.dir", "target/reports");
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportDir + "/report-" + timestamp + ".html");
            sparkReporter.config().setDocumentTitle("Claude Java Selenium POM - Test Report");
            sparkReporter.config().setReportName("Automation Execution Report");

            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            extentReports.setSystemInfo("Environment", System.getProperty("env", "qa"));
            extentReports.setSystemInfo("Browser", System.getProperty("browser", "chrome"));
        }
        return extentReports;
    }

    public static void startTest(String testName, String description) {
        CURRENT_TEST.set(getInstance().createTest(testName, description));
    }

    public static ExtentTest getTest() {
        return CURRENT_TEST.get();
    }

    public static void flush() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }
}
