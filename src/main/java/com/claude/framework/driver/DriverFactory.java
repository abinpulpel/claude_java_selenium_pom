package com.claude.framework.driver;

import com.claude.framework.config.ConfigManager;
import com.claude.framework.enums.BrowserType;
import com.claude.framework.exceptions.FrameworkException;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;

/**
 * Builds {@link WebDriver} instances for every supported browser and both
 * local and remote (Grid / BrowserStack / LambdaTest) execution modes.
 *
 * <p>This is a Factory (creational pattern): callers ask for "a chrome
 * driver" or "whatever {@code -Dbrowser} says" and never touch vendor
 * classes directly, which keeps {@link DriverManager} and every page object
 * decoupled from Selenium's browser-specific driver types.
 */
public final class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        ConfigManager config = ConfigManager.getInstance();
        BrowserType browserType = BrowserType.fromString(config.get("browser", "chrome"));
        boolean headless = config.getBoolean("headless", false);
        boolean remote = config.getBoolean("remote.execution", false);

        WebDriver driver = remote
                ? createRemoteDriver(browserType, headless, config.get("remote.grid.url"))
                : createLocalDriver(browserType, headless);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getInt("implicit.wait.seconds", 10)));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.getInt("page.load.timeout.seconds", 30)));
        driver.manage().window().maximize();
        return driver;
    }

    private static WebDriver createLocalDriver(BrowserType browserType, boolean headless) {
        return switch (browserType) {
            case CHROME -> {
                WebDriverManager.chromedriver().setup();
                yield new ChromeDriver(chromeOptions(headless));
            }
            case FIREFOX -> {
                WebDriverManager.firefoxdriver().setup();
                yield new FirefoxDriver(firefoxOptions(headless));
            }
            case EDGE -> {
                WebDriverManager.edgedriver().setup();
                yield new EdgeDriver(edgeOptions(headless));
            }
            case SAFARI -> new SafariDriver();
        };
    }

    private static WebDriver createRemoteDriver(BrowserType browserType, boolean headless, String gridUrl) {
        try {
            var capabilities = switch (browserType) {
                case CHROME -> chromeOptions(headless);
                case FIREFOX -> firefoxOptions(headless);
                case EDGE -> edgeOptions(headless);
                case SAFARI -> throw new FrameworkException("Safari is not supported for remote/Grid execution");
            };
            return new RemoteWebDriver(URI.create(gridUrl).toURL(), capabilities);
        } catch (MalformedURLException e) {
            throw new FrameworkException("Invalid remote Grid URL: " + gridUrl, e);
        }
    }

    private static ChromeOptions chromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--remote-allow-origins=*", "--disable-notifications", "--no-sandbox", "--disable-dev-shm-usage");
        return options;
    }

    private static FirefoxOptions firefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
        }
        return options;
    }

    private static EdgeOptions edgeOptions(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        return options;
    }
}
