# claude_java_selenium_pom

Enterprise-grade, reusable Java Selenium test automation framework built on
the **Page Object Model** pattern. Designed so QA engineers can start writing
automated tests immediately by importing existing page objects, utilities,
configuration, and reporting â no framework plumbing required.

## Overview

This framework provides a production-ready foundation for UI test automation:
thread-safe cross-browser driver management, environment-aware configuration,
data-driven testing, structured logging, HTML reporting with failure
screenshots, retry logic for flaky tests, and CI/CD + Docker support out of
the box.

It is not a tutorial project â it follows SOLID, DRY, and Clean Architecture
principles, and every component is meant to be extended, not replaced.

## Architecture

```
                    âââââââââââââââââââ
                    â   Test Classes   â  (src/test)
                    ââââââââââ¬âââââââââ
                             â uses
                    ââââââââââ¼âââââââââ
                    â   Page Objects   â  (BasePage, LoginPage, ...)
                    ââââââââââ¬âââââââââ
                             â uses
        âââââââââââââââââââââ¼ââââââââââââââââââââââ
        â                    â                     â
âââââââââ¼ââââââââ   ââââââââââ¼âââââââââ   ââââââââââ¼âââââââââ
â  WaitUtils     â   â  DriverManager   â   â  ConfigManager   â
â  (explicit     â   â  (ThreadLocal    â   â  (properties +   â
â   waits)       â   â   WebDriver)     â   â   env overlays)  â
ââââââââââââââââââ   ââââââââââ¬âââââââââ   âââââââââââââââââââ
                               â built by
                      ââââââââââ¼âââââââââ
                      â  DriverFactory   â  (local + remote Grid)
                      ââââââââââââââââââ

TestListener (TestNG) âââ¶ ExtentManager (HTML report) + ScreenshotUtils + Log4j2
```

- **Page Object Model**: page objects encapsulate locators and page-specific actions; tests never touch `WebDriver` directly.
- **Factory pattern**: `DriverFactory` builds the correct driver for any browser/execution mode; callers never reference vendor driver classes.
- **Singleton**: `ConfigManager` and `ExtentManager` are process-wide, lazily-initialized singletons.
- **ThreadLocal isolation**: `DriverManager` and `ExtentManager` keep per-thread state so parallel execution never leaks a session or a report entry across threads.
- **Facade**: `TestListener` fronts logging, reporting, and screenshot subsystems behind a single TestNG hook.

## Folder Structure

```
claude_java_selenium_pom/
âââ .github/workflows/ci.yml        # GitHub Actions CI pipeline
âââ src/
â   âââ main/java/com/claude/framework/
â   â   âââ config/                 # ConfigManager
â   â   âââ driver/                 # DriverFactory, DriverManager
â   â   âââ enums/                  # BrowserType
â   â   âââ exceptions/            # FrameworkException
â   â   âââ listeners/              # TestListener, RetryAnalyzer
â   â   âââ pages/                  # BasePage + sample page objects
â   â   âââ reports/                # ExtentManager
â   â   âââ utils/                  # WaitUtils, ScreenshotUtils, JsonDataReader
â   âââ main/resources/            # config.properties + per-env overlays, log4j2.xml
â   âââ test/
â       âââ java/com/claude/tests/
â       â   âââ base/               # BaseTest
â       â   âââ tests/               # Sample test classes
â¾       âââ resources/testdata/     # JSON test-data fixtures
âââ testng.xml                      # Suite definition, parallel execution config
âââ Dockerfile
âââ docker-compose.yml              # Selenium Grid (hub + chrome/firefox nodes)
âââ pom.xml
âââ CONTRIBUTING.md
âââ CODE_OF_CONDUCT.md
âââ CHANGELOG.md
âââ LICENSE
```

## Technology Stack

| Concern            | Technology                          |
|---------------------|--------------------------------------|
| Language            | Java 21                              |
| Build tool           | Maven                                |
| Browser automation   | Selenium 4.27                        |
| Driver management     | WebDriverManager                     |
| Test engine          | TestNG 7.10                          |
| Reporting            | ExtentReports 5.1                    |
| Logging              | Log4j2                               |
| Data-driven testing  | Jackson (JSON)                       |
| Static analysis      | SpotBugs                             |
| Code coverage        | JaCoCo                               |
| Containerization      | Docker, Docker Compose, Selenium Grid |
| CI/CD                | GitHub Actions                       |

## Prerequisites

- Java 21 (Temurin recommended)
- Maven 3.9+
- Docker (optional, for Grid execution)
- Chrome/Firefox/Edge installed locally for non-headless runs

## Installation

```bash
git clone https://github.com/abinpulpel/claude_java_selenium_pom.git
cd claude_java_selenium_pom
mvn clean install -DskipTests
```

## Configuration

Configuration lives in `src/main/resources/config.properties` with
per-environment overlays (`config-qa.properties`, `config-staging.properties`,
`config-prod.properties`). Any key can be overridden at runtime via `-D`:

```bash
mvn clean test -Dbrowser=firefox -Denv=staging -Dheadless=true
```

Key configuration options:

| Property                  | Description                              | Default |
|----------------------------|--------------------------------------------|---------|
| `browser`                  | chrome / firefox / edge / safari            | chrome  |
| `headless`                 | Run without a visible browser window        | false   |
| `env`                      | Selects the config overlay file             | qa      |
| `remote.execution`         | Run against a Selenium Grid                 | false   |
| `remote.grid.url`          | Grid hub URL                                | -       |
| `implicit.wait.seconds`    | Implicit wait                               | 10      |
| `explicit.wait.seconds`    | Default explicit wait in `WaitUtils`        | 20      |
| `retry.max.count`          | Retries for `RetryAnalyzer`                 | 2       |

## Execution

```bash
# Default suite
mvn clean test

# Specific TestNG suite file
mvn clean test -DsuiteXmlFile=testng.xml
```

### Parallel Execution

Parallelism is configured in `testng.xml` (`parallel="methods" thread-count="3"`)
and mirrored in the Surefire plugin (`pom.xml`). Adjust `thread-count` and
`-DthreadCount` together to change concurrency.

### Headless Execution

```bash
mvn clean test -Dheadless=true
```

### Cross-Browser Execution

```bash
mvn clean test -Dbrowser=chrome
mvn clean test -Dbrowser=firefox
mvn clean test -Dbrowser=edge
```

### Docker / Selenium Grid

```bash
docker compose up --build
```

Spins up a Selenium Hub, Chrome and Firefox nodes, and a `test-runner`
container that executes the suite against the Grid (`remote.execution=true`).

## CI/CD

`.github/workflows/ci.yml` runs on every push/PR to `main`: static analysis
(SpotBugs), a cross-browser headless test matrix (Chrome + Firefox), JaCoCo
coverage, and uploads Surefire results, the Extent HTML report, screenshots,
and the coverage report as build artifacts. It can also be triggered manually
with a chosen browser/environment via `workflow_dispatch`.

## Reporting

ExtentReports generates a timestamped HTML report under `target/reports/`
after every run, including environment/browser metadata and embedded
screenshots for failed tests.

## Logging

Log4j2 (`src/main/resources/log4j2.xml`) writes to the console and to
rolling daily log files under `target/logs/automation.log`.

## Troubleshooting

| Symptom                                  | Likely cause / fix                                                       |
|--------------------------------------------|----------------------------------------------------------------------------|
| `IllegalStateException: No WebDriver...`   | A test bypassed `BaseTest.setUp()` â extend `BaseTest`.                    |
| Driver binary download fails                | Check network access; WebDriverManager needs outbound HTTPS.              |
| Tests pass locally, fail in CI              | Confirm `-Dheadless=true` and that the app under test is reachable from CI.|
| Flaky tests                                | Attach `RetryAnalyzer`, or check for a missing explicit wait in `WaitUtils`.|
| Grid session won't start                    | Confirm `docker compose ps` shows the hub and node containers healthy.    |

## Contribution Guide

See [CONTRIBUTING.md](CONTRIBUTING.md) for branching, commit conventions, and
the pull request checklist. See [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) for
community standards.

## License

Distributed under the [MIT License](LICENSE).

## Roadmap

- [ ] API-layer test support (Rest Assured) alongside UI tests
- [ ] BrowserStack / LambdaTest cloud execution profiles
- [ ] Visual regression testing integration
- [ ] Accessibility (axe-core) checks in the base test flow
- [ ] Allure reporting as an alternative to ExtentReports
