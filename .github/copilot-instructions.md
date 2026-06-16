Repository-specific Copilot instructions

Build, test, and lint commands

- Run the app locally (Unix):
  ./mvnw spring-boot:run
- Build and run tests:
  ./mvnw clean package
- Run the full test suite:
  ./mvnw test
- Run a single test class or method:
  ./mvnw -Dtest=AuthControllerIntegrationTest test
  ./mvnw -Dtest=AuthControllerIntegrationTest#testMethod test
- Start local Postgres (used by app):
  docker compose up -d db
  docker compose down
- Notes on env/config: docker-compose reads .env for POSTGRES_* defaults; application.properties reads SPRING and MAIL env vars. Override at runtime using environment variables.

High-level architecture (big picture)

- Single-module Spring Boot service (Java 17) with main entry com.nicholaswallace.lessons_plan.LessonsPlanApplication.
- Package layout (important):
  - controller: REST endpoints (AuthController, AppUserController, LessonDocController, HelloWorldController)
  - service: business logic (AuthenticationService, JwtService, LessonDocService, AppUserService, SecureTokenService, email senders)
  - repository: Spring Data JPA repositories (AppUserRepository, LessonDocRepository, MagicLinkTokenRepository)
  - model: JPA entities and enums (AppUser, LessonDoc, MagicLinkToken, Role, Plan)
  - dto: request/response DTOs (including auth payloads)
  - security: JwtAuthenticationFilter and AuthenticatedUser
  - config: beans and properties (AuthProperties via @ConfigurationProperties, TimeConfig, SecurityConfig)
  - exception: ApiException, handler and typed exceptions

- Auth flow overview: sign-in uses magic links (MagicLinkToken entity). Email sending is pluggable: if a JavaMailSender bean exists, SmtpMagicLinkEmailSender is used; otherwise LoggingMagicLinkEmailSender logs links. A verified magic-link call issues a JWT. JwtService implements HS256 signing and JwtAuthenticationFilter extracts Bearer tokens.

Key conventions and patterns (repo-specific)

- Constructor injection is used across beans; follow the same pattern.
- Lombok is used for model getters/setters; keep annotations minimal.
- AuthProperties is registered with @EnableConfigurationProperties in the main class — configuration values live in application.properties and are overridable via environment variables.
- Mail sender selection: SmtpMagicLinkEmailSender is annotated with @ConditionalOnBean(JavaMailSender.class); LoggingMagicLinkEmailSender uses @ConditionalOnMissingBean(JavaMailSender.class). Use this pattern for optional pluggable components.
- Time-sensitive logic uses a Clock bean provided by TimeConfig (use Clock injection to make code testable/reliable).
- Security: SecurityConfig declares permitted endpoints (POST /auth/sign-in, GET /auth/magic-link/verify, POST /user, GET /helloWorld); everything else requires Bearer JWT.
- JWT: JwtService manually builds and verifies HS256 tokens (shared secret from app.auth.jwt-secret). Tokens contain sub, email, role and plan claims and use app.auth.jwt-ttl.
- Tests: JUnit 5 + Spring Boot test starters. Tests mirror production packages under src/test/java. To run a single integration test, use the -Dtest flag shown above.

Files and AI assistant configs to consult

- AGENTS.md contains repository guidelines and build/test notes — consult it first for local conventions.
- There are no CLAUDE.md, .cursorrules, or similar assistant config files in the repo root.

What was added

- This file (.github/copilot-instructions.md) created at repository root to help future Copilot sessions with build/test commands, architecture overview, and repository-specific patterns.

If you want additional coverage (examples of typical curl requests, sample env values, or MCP server setup for web testing), say which area to expand and it will be added.