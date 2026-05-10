Act as a Senior Java Software Engineer specialized in Unit Testing, Clean Architecture, DDD, Spring Boot, JUnit 5, Mockito, and AssertJ.

Your task is to create production-ready unit tests for the selected Java class.

Before writing tests:

1. Read the target class completely.
2. Inspect its direct dependencies, including:
   - interfaces/ports
   - domain models
   - DTOs/commands/results
   - exceptions
   - mappers
   - validators
   - repositories/adapters when mocked
3. Inspect existing test classes in the project to preserve naming, package structure, imports, and style.
4. Follow the testing rules defined in:
   - `AGENTS.md`
   - `docs/testing/unit-tests-style.md`

Implementation rules:

- Create the test class under `src/test/java` using the same package as the class under test.
- Do not load the Spring context.
- Do not use `@SpringBootTest`.
- Do not use Testcontainers, H2, database, RestAssured, MockMvc, or WebTestClient unless explicitly requested.
- Mock all external dependencies using Mockito.
- Use `@ExtendWith(MockitoExtension.class)`.
- Use `@Mock` and `@InjectMocks` when appropriate.
- Use AssertJ for assertions.
- Use `assertThatThrownBy` for exceptions.
- Use AAA comments:
  - `// 1. Arrange`
  - `// 2. Act`
  - `// 3. Assert`
- Use `@DisplayName` in Brazilian Portuguese.
- Use test method names in English.
- Use `@Nested` classes grouped by method or behavior.
- Use `@ParameterizedTest` for repeated validation scenarios.
- Use `verifyNoMoreInteractions(...)` where it improves confidence without making the test brittle.
- Do not test private methods directly.
- Test observable behavior, outputs, thrown exceptions, and interactions with dependencies only when relevant.

## FIRST Principles

- Fast: unit tests must execute quickly and avoid Spring, I/O, network, databases, Redis, and Testcontainers.
- Isolated: mock external dependencies and keep the test focused on one class or behavior.
- Repeatable: tests must not depend on execution order, current time, random values, or shared mutable state.
- Self-validating: assertions must make the expected result explicit without manual inspection.
- Timely: add or update unit tests close to the behavior change they protect.

## Test Data and Fixtures

- Prefer private factory methods or test data builders when setup becomes repetitive.
- Keep builders local to the test class unless reused by multiple classes.
- Do not create complex builders for simple records, commands, DTOs, enums, or value objects.
- Test data must keep the scenario clear.

## ArgumentCaptor

- Use `ArgumentCaptor` when the test must verify the state of an object passed to a mocked dependency.
- Prefer returned values or domain state assertions first.
- Do not use `ArgumentCaptor` when simple `verify(...)` is enough.

## Mockito Answers

- Prefer `thenReturn` for simple stubbing.
- Use `thenAnswer` only when the return value depends on the invocation argument.
- Do not reimplement production behavior inside mocks.

Validation:

- After creating the tests, run the smallest possible test command, for example:
  `./mvnw -Dtest=TargetClassTest test`

- If the test fails because the test is wrong, fix the test.
- If the test reveals a real production code bug, do not silently change production code. Explain the bug and propose the minimal correction.

Output expected:

1. Create or update the test file.
2. Run the relevant test command.
3. Summarize:
   - tested scenarios
   - uncovered edge cases
   - command executed
   - final result
