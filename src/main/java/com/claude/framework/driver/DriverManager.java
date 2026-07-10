package com.claude.framework.driver;

import org.openqa.selenium.WebDriver;

/**
 * Holds one {@link WebDriver} per thread so parallel TestNG execution never
 * shares a browser session across tests. Deliberately package-visible
 * mutation (set/unset) but a public accessor, so only the base test class
 * controls the driver lifecycle while page objects and tests just consume it.
 */
public final class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER_THREAD_LOCAL = new ThreadLocal<>();

    private DriverManager() {
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER_THREAD_LOCAL.get();
        if (driver == null) {
            throw new IllegalStateException(
                    "No WebDriver has been initialized for this thread. Call DriverManager.setDriver(...) first.");
        }
        return driver;
    }

    public static void setDriver(WebDriver driver) {
        DRIVER_THREAD_LOCAL.set(driver);
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER_THREAD_LOCAL.get();
        if (driver != null) {
            driver.quit();
            DRIVER_THREAD_LOCAL.remove();
        }
    }
}
