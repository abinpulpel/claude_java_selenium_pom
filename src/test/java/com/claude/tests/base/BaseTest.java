package com.claude.tests.base;

import com.claude.framework.driver.DriverFactory;
import com.claude.framework.driver.DriverManager;
import com.claude.framework.pages.LoginPage;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Every test class extends this instead of touching {@link DriverFactory}
 * or {@link DriverManager} directly, so driver setup/teardown is defined
 * exactly once and stays consistent across the whole suite (DRY).
 */
public abstract class BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverManager.setDriver(DriverFactory.createDriver());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverManager.quitDriver();
    }

    protected LoginPage navigateToLoginPage() {
        String baseUrl = com.claude.framework.config.ConfigManager.getInstance().get("base.url");
        DriverManager.getDriver().get(baseUrl);
        return new LoginPage(DriverManager.getDriver());
    }
}
