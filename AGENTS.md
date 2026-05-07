# AGENTS.md

## Project Overview

This repository is a Java/Spring Boot backend for a URL shortener. The root package is `com.app.url_shortener`.

The codebase is organized around four main areas:

- `iam`: authentication, authorization, users, roles, permissions, email verification, login, refresh tokens, and logout.
- `url`: URL shortening, short-code resolution, Redis caching/counters, DynamoDB persistence, and Hashids encoding.
- `security`: Spring Security, JWT configuration, token services, authentication conversion, handlers, and principals.
- `shared`: common configuration, exceptions, ProblemDetail responses, filters, idempotency, and persistence helpers.

## Tech Stack

- Java 21.
- Spring Boot 4.0.5.
- Maven with Maven Wrapper.
- Spring Web MVC, Validation, Actuator, Security, OAuth2 Resource Server, JPA, Redis Cache/Data Redis, and Flyway.
- PostgreSQL for IAM relational persistence.
- Redis for cache, counters, and idempotency support.
- DynamoDB for URL records.
- MapStruct and Lombok.
- Hashids for short-code encoding.
- OpenAPI via `springdoc-openapi-starter-webmvc-ui`.
- Tests use JUnit 5, Mockito, AssertJ, Spring Boot test dependencies, Testcontainers, and RestAssured where appropriate.

## Architecture

The `iam` module follows Clean Architecture and DDD. The `url` module follows Clean Architecture. Other modules such as `security`, `shared`, configuration, filters, exception handling, and infrastructure support may follow MVC/layered architecture.

For Clean Architecture modules, preserve this dependency direction:

- `domain` must not depend on Spring, JPA, HTTP, Redis, DynamoDB, or other infrastructure.
- `application` orchestrates use cases and depends on domain abstractions and ports.
- `infrastructure` implements output ports and handles persistence, external services, cache, Redis, DynamoDB, token generation, email strategies, and framework integration.
- `presentation` handles HTTP controllers, request/response DTOs, web mappers, and validation.

- Use cases orchestrate application flows; keep domain invariants in domain models whenever practical.
- Use cases must not contain HTTP, persistence, Redis, DynamoDB, or framework-specific details.

Do not move layered modules to Clean Architecture unless explicitly requested. Do not mix presentation, application, domain, and infrastructure responsibilities.

## Module Guidelines

For `iam`:

- Commands belong in `iam.application.command`.
- Use case interfaces belong in `iam.application.usecase`.
- Use case implementations belong in `iam.application.usecase.impl`.
- Output ports belong in `iam.application.port.output`.
- Results belong in `iam.application.result`.
- Domain models, value objects, enums, events, and domain exceptions belong in `iam.domain`.
- Adapters belong in `iam.infrastructure.adapter`.
- JPA entities, repositories, and persistence mappers belong in `iam.infrastructure.persistence`.
- Controllers, request/response DTOs, and web mappers belong in `iam.presentation`.
- Domain models must not depend on JPA entities.
- Controllers must delegate to use cases and must not contain business rules.

For `url`:

- Use cases belong in `url.application.usecase`.
- Output ports belong in `url.application.port.output`.
- Domain model and domain exceptions belong in `url.domain`.
- Redis, DynamoDB, Hashids, repositories, entities, and mappers belong in `url.infrastructure`.
- Controllers, docs, request/response DTOs, and validators belong in `url.presentation`.

For `security`:

- Keep security configuration, JWT configuration, authentication converters, handlers, token services, and principals in `security`.
- Preserve stateless authentication unless explicitly requested otherwise.
- Do not weaken endpoint protection or expose protected endpoints unless explicitly requested.
- Never log secrets, passwords, access tokens, refresh tokens, OTPs, or credentials.

For `shared`:

- Shared configuration, common exceptions, idempotency, persistence helpers, filters, and ProblemDetail support belong in `shared`.
- Keep reusable cross-cutting concerns in `shared`.
- Do not move domain-specific business rules into `shared`.

## Coding Standards

- Follow the style already present in the repository.
- Prefer constructor injection. Do not use field injection.
- Keep changes small, focused, and consistent with the target module.
- Do not introduce unnecessary abstractions.
- Do not add dependencies unless explicitly requested.
- Use clear names that match existing concepts.
- Use records for DTOs, commands, and results when consistent with nearby code.
- Use MapStruct mappers consistently where the project already uses mappers, with `componentModel = "spring"` and strict unmapped target handling when matching existing mapper style.
- Do not expose JPA or DynamoDB entities in API responses.
- Use `var` only when the inferred type is obvious.
- Keep package names and architecture terms unchanged.
- User-facing explanations may be written in Brazilian Portuguese when the user communicates in Portuguese.
- Code identifiers, class names, method names, packages, and documentation files should remain in English unless the existing file already uses another convention.
- Do not change public APIs, endpoint contracts, DTO fields, exception contracts, JWT claims, cookie names, or database schemas unless explicitly required.

## Spring Standards

- Keep controllers thin: validation, mapping, HTTP status, cookies/headers, and delegation only.
- Put business orchestration in use case implementations.
- Use `@Service`, `@Component`, `@Configuration`, `@RestController`, and `@RestControllerAdvice` consistently with existing code.
- Prefer constructor injection directly or via Lombok `@RequiredArgsConstructor`.
- Keep request validation in DTOs, custom validators, or presentation-level validation components.
- Keep transaction boundaries in application services/use cases when persistence changes require them.

## Security Standards

- Keep Spring Security and JWT behavior in `security`.
- Keep public endpoints explicit in `SecurityConfig`; all other endpoints should remain authenticated by default.
- Preserve `SessionCreationPolicy.STATELESS`.
- Preserve JWT authorities from the `authorities` claim with no authority prefix unless explicitly changed.
- Use secure, HTTP-only refresh-token cookies consistently with existing auth controller behavior.
- Do not log or return sensitive authentication material except intended token responses.

## Persistence and Database Standards

- PostgreSQL schema changes are managed with Flyway migrations under `src/main/resources/db/migration`.
- Do not modify existing migrations that may already have been applied; add a new versioned migration instead.
- IAM persistence uses JPA entities, Spring Data repositories, and persistence mappers under `iam.infrastructure.persistence`.
- URL persistence uses DynamoDB entities/adapters and Redis infrastructure under `url.infrastructure`.
- Keep domain models separate from persistence entities.
- Keep local infrastructure assumptions in config files and Docker Compose; do not hard-code environment-specific values in code.

## Error Handling Standards

- Domain and business exceptions should extend the shared exception hierarchy and carry an `ErrorCode`.
- Shared exception categories include validation, conflict, not found, unauthorized, forbidden, rate limit, and infrastructure/internal errors.
- HTTP error responses are handled centrally by `GlobalExceptionHandler` and `ProblemDetailFactory`.
- Controllers should not build ad hoc error response bodies for expected business failures.
- Do not leak technical details, credentials, tokens, or private configuration values in errors or logs.

## Testing Standards

For detailed unit testing rules, always read:

- `docs/testing/unit-tests-style.md`

before creating or updating unit tests.

- Use JUnit 5, Mockito, and AssertJ.
- For unit tests, do not load the Spring context and do not use `@SpringBootTest`.
- Do not use Testcontainers, RestAssured, MockMvc, or WebTestClient for isolated unit tests unless explicitly requested.
- Use `@ExtendWith(MockitoExtension.class)`, `@Mock`, and `@InjectMocks` where appropriate.
- Use AssertJ assertions and `assertThatThrownBy` for exception assertions.
- Follow the AAA pattern with comments: `// 1. Arrange`, `// 2. Act`, `// 3. Assert`.
- Test classes and methods should be package-private.
- Use `@DisplayName` in Brazilian Portuguese and test method names in English.
- Use `@Nested` to group scenarios and `@Tag("unit")` for unit test classes.
- Prefer parameterized tests for repeated validation scenarios.
- Test observable behavior, not private implementation details; do not test private methods directly.
- When testing use cases, mock output ports and use real domain models and value objects whenever practical.
- Do not mock simple domain objects, enums, records, commands, or results.
- Verify port interactions only when they are part of the observable behavior.
- Integration tests may use Spring Boot, Testcontainers, RestAssured, Redis, LocalStack, and DynamoDB only when testing integrated behavior.

## Commands

Use the Maven Wrapper from the repository root:

- `./mvnw clean compile`
- `./mvnw test`
- `./mvnw -Dtest=ClassNameTest test` when changing a single class, prefer the smallest related test first.
- `./mvnw clean verify`

Local infrastructure for integration scenarios is defined in `docker-compose.yml` with PostgreSQL, Redis, and LocalStack/DynamoDB.

## Task Workflow

1. Inspect relevant files before changing code.
2. Identify the current pattern used by the target module.
3. Make the smallest safe change.
4. Add or update tests when changing behavior.
5. Run the smallest relevant validation command.
6. Avoid broad refactors unless explicitly requested.
7. Summarize files changed, behavior implemented or documentation created, commands executed, validation result, and known limitations.

## Definition of Done

- Code compiles.
- Relevant tests pass.
- Architecture boundaries are respected.
- No unrelated files are changed.
- No secrets or sensitive data are introduced.
- Final response summarizes files changed, behavior implemented or documentation created, commands executed, validation result, assumptions, and limitations.

## Do Not

- Do not modify production code while creating `AGENTS.md`.
- Do not create broad refactors.
- Do not add new dependencies.
- Do not weaken security.
- Do not expose entities directly in API responses.
- Do not place business rules in controllers.
- Do not put infrastructure concerns inside domain models.
- Do not change existing migrations that may already have been applied.
- Do not ignore failing tests.
- Do not hide production bugs by changing tests incorrectly.
- Do not include secrets or real environment values.
