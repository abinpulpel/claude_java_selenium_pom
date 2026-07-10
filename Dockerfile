# syntax=docker/dockerfile:1
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
COPY testng.xml .

FROM maven:3.9-eclipse-temurin-21
LABEL org.opencontainers.image.source="https://github.com/abinpulpel/claude_java_selenium_pom"
LABEL org.opencontainers.image.description="Enterprise Java Selenium POM automation framework"

WORKDIR /workspace
COPY --from=build /workspace /workspace

ENV BROWSER=chrome
ENV ENV=qa
ENV HEADLESS=true

ENTRYPOINT ["sh", "-c", "mvn -B test -Dbrowser=$BROWSER -Denv=$ENV -Dheadless=$HEADLESS"]
