# Contributing to claude_java_selenium_pom

Thanks for investing time in improving this framework. This guide covers how
to get set up and the standards a pull request needs to meet.

## Prerequisites

- Java 21 (Temurin recommended)
- Maven 3.9+
- Docker (optional, for Selenium Grid execution)
- Google Chrome / Firefox / Edge installed locally for non-headless runs

## Getting Started

```bash
git clone https://github.com/abinpulpel/claude_java_selenium_pom.git
cd claude_java_selenium_pom
mvn clean install -DskipTests
```

## Running Tests

```bash
# Default (Chrome, qa environment)
mvn clean test

# Specific browser / environment
mvn clean test -Dbrowser=firefox -Denv=staging -Dheadless=true

# Against a Dockerized Selenium Grid
docker compose up --build
```

## Branching & Commits

- Branch from `main` using `feature/<short-description>` or `fix/<short-description>`.
- Follow [Conventional Commits](https://www.conventionalcommits.org/) (`feat:`, `fix:`, `docs:`, `refactor:`, `test:`, `chore:`).
- Keep commits focused; squash noisy WIP history before opening a PR.

## Code Standards

- Follow existing package structure: `config`, `driver`, `pages`, `utils`, `listeners`, `reports`, `exceptions`.
- Every public class and method needs a Javadoc comment explaining *why*, not just *what*.
- No hard-coded waits (`Thread.sleep`) — use `WaitUtils`.
- No duplicated locators or logic across page objects — extract to `BasePage` or a shared utility.
- New reusable behavior belongs in `src/main`; test-specific logic belongs in `src/test`.
- Run `mvn com.github.spotbugs:spotbugs-maven-plugin:check` and resolve findings before submitting.

## Pull Request Checklist

- [ ] Tests pass locally: `mvn clean test`
- [ ] New/changed code has Javadoc
- [ ] No new SpotBugs findings
- [ ] README/CHANGELOG updated if behavior or setup steps changed
- [ ] PR description explains the change and links any related issue

## Reporting Issues

Open a GitHub issue with reproduction steps, expected vs. actual behavior,
browser/environment used, and relevant log or screenshot output from
`target/logs` or `target/screenshots`.
