package com.claude.tests.tests;

import com.claude.framework.pages.HomePage;
import com.claude.framework.pages.LoginPage;
import com.claude.framework.utils.JsonDataReader;
import com.claude.tests.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * Sample test class demonstrating the intended usage pattern: extend
 * {@link BaseTest}, drive page objects only (never raw {@code WebDriver}
 * calls), and pull test data from the JSON fixtures under
 * {@code src/test/resources/testdata}. Replace with real test cases for
 * your application under test.
 */
public class LoginTest extends BaseTest {

    @Test(description = "Valid credentials should land the user on the home page")
    public void validLoginNavigatesToHomePage() {
        LoginPage loginPage = navigateToLoginPage();
        HomePage homePage = loginPage.login("standard_user", "correct_password");

        Assert.assertTrue(homePage.isWelcomeBannerDisplayed(), "Welcome banner should be visible after login");
    }

    @Test(description = "Invalid credentials should surface an inline error and stay on the login page")
    public void invalidLoginShowsErrorMessage() {
        LoginPage loginPage = navigateToLoginPage();
        loginPage.enterUsername("standard_user");
        loginPage.enterPassword("wrong_password");
        loginPage.clickLogin();

        Assert.assertTrue(loginPage.isErrorDisplayed(), "An error message should be displayed for invalid credentials");
    }

    @Test(description = "Data-driven login using fixtures from loginData.json", dataProvider = "loginCredentials")
    public void dataDrivenLogin(String username, String password, boolean expectedSuccess) {
        LoginPage loginPage = navigateToLoginPage();
        loginPage.enterUsername(username);
        loginPage.enterPassword(password);
        loginPage.clickLogin();

        if (expectedSuccess) {
            Assert.assertTrue(loginPage.getCurrentUrl().contains("home"), "Expected redirect to home page");
        } else {
            Assert.assertTrue(loginPage.isErrorDisplayed(), "Expected an inline error message");
        }
    }

    @org.testng.annotations.DataProvider(name = "loginCredentials")
    public Object[][] loginCredentials() {
        Map<String, Object> data = JsonDataReader.read("testdata/loginData.json", Map.class);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> cases = (List<Map<String, Object>>) data.get("cases");
        Object[][] rows = new Object[cases.size()][3];
        for (int i = 0; i < cases.size(); i++) {
            Map<String, Object> testCase = cases.get(i);
            rows[i][0] = testCase.get("username");
            rows[i][1] = testCase.get("password");
            rows[i][2] = testCase.get("expectedSuccess");
        }
        return rows;
    }
}
