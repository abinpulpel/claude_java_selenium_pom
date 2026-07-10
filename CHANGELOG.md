# Changelog

All notable changes to this project are documented in this file.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-07-10

### Added
- Initial enterprise-grade framework scaffold: Maven build (`pom.xml`) targeting Java 21.
- Page Object Model base classes (`BasePage`, sample `LoginPage`/`HomePage`).
- Thread-safe `DriverFactory` / `DriverManager` supporting Chrome, Firefox, Edge, Safari, local and remote (Selenium Grid) execution.
- Centralized `ConfigManager` with environment overlays (`qa`, `staging`, `prod`) and system-property overrides.
- `WaitUtils`, `ScreenshotUtils`, and `JsonDataReader` reusable utilities.
- ExtentReports integration (`ExtentManager`) and a TestNG `TestListener` for lifecycle logging, reporting, and failure screenshots.
- `RetryAnalyzer` for automatic retry of flaky test failures.
- Sample data-driven `LoginTest` with JSON test-data fixture.
- TestNG suite (`testng.xml`) configured for parallel execution.
- GitHub Actions CI workflow: static analysis, cross-browser test matrix, coverage, and artifact upload.
- Docker and Docker Compose support for local Selenium Grid execution.
- Log4j2 logging configuration with console and rolling file appenders.
- Project governance docs: README, CONTRIBUTING, CODE_OF_CONDUCT, LICENSE (MIT).
