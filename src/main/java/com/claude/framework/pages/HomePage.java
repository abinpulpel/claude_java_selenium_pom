package com.claude.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Sample landing page reached after a successful login. Template only —
 * swap in your application's real post-login page object.
 */
public class HomePage extends BasePage {

    private final By welcomeBanner = By.cssSelector(".welcome-banner");
    private final By logoutButton = By.id("logout");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public boolean isWelcomeBannerDisplayed() {
        return isDisplayed(welcomeBanner);
    }

    public String getWelcomeText() {
        return getText(welcomeBanner);
    }

    public LoginPage logout() {
        click(logoutButton);
        return new LoginPage(driver);
    }
}
