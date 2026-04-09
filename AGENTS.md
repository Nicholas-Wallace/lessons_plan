# Repository Guidelines

## Project Structure & Module Organization
This repository is a single-module Spring Boot service. Application code lives in `src/main/java/com/nicholaswallace/lessons_plan`, split by responsibility:
- `controller/` for REST endpoints
- `service/` for business rules
- `repository/` for Spring Data JPA access
- `model/` for entities and enums

Configuration is in `src/main/resources/application.properties`. Tests belong in `src/test/java` and should mirror the production package layout. Local database infrastructure is defined in `docker-compose.yml`, with default credentials stored in `.env`.

## Build, Test, and Development Commands
- `.\mvnw.cmd spring-boot:run` starts the API locally.
- `.\mvnw.cmd test` runs the JUnit/Spring Boot test suite.
- `.\mvnw.cmd clean package` builds the application and runs tests.
- `docker compose up -d db` starts PostgreSQL on `localhost:5332`.
- `docker compose down` stops the local database container.

Use `./mvnw` instead of `.\mvnw.cmd` on Unix-like systems.

## Coding Style & Naming Conventions
Target Java 17 and use 4-space indentation. Keep package names lowercase, class and enum names in `PascalCase`, and methods/fields in `camelCase`. Follow the existing Spring style: constructor injection, thin controllers, and service classes that own validation and persistence rules. Lombok is already used for accessors in model classes; keep annotations limited to simple boilerplate reduction.

## Testing Guidelines
Tests use JUnit 5 with Spring Boot test starters. Name test classes `*Test` or `*Tests` and place them in matching packages under `src/test/java`. Add service tests for business rules and Spring integration tests when controller, repository, or JPA behavior changes. No coverage gate is configured, so every feature or bug fix should add tests that protect the changed behavior.

## Commit & Pull Request Guidelines
Recent commits use short, task-focused subjects such as `initial commit`, `create User and LessonDocs`, and `finalizando o crud`. Keep that style, but prefer a single language and an imperative subject like `add lesson document validation`. Pull requests should include a short summary, linked issue if available, test notes, and any database or configuration changes. For API changes, include example request/response payloads.

## Configuration & Security Tips
Do not commit real secrets. Keep local credentials in `.env`, and make sure `application.properties` continues to rely on environment-variable overrides for database access.
