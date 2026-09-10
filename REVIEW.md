# ranxitest Review Profile

## Project Context

- Java 17 / Spring Boot 2.6.6 / Maven project
- `my-spring-boot-app` module under `com.example.myapp`
- Utility classes under `com.example.myapp.util`

## Review Gates

### Gate: Utility Class Conventions
- Private constructor for utility classes
- Javadoc on public API methods
- Edge-case handling: null, empty, single-element inputs

### Gate: Generic Algorithm Correctness
- Type bounds (`<T extends Comparable<T>>`) must be used correctly
- In-place mutation must not lose elements or introduce duplicates
- Lomuto partition scheme pivot is the last element

### Gate: JUnit 5 Test Quality
- `@Test` annotation present (no vintage/TestNG)
- Test method names follow `snake_case` descriptive style (no given-when-then prefix)
- Each edge case covered by a distinct test method
- No flaky time-sensitive assertions unless bounded with reasonable margin
- `assertArrayEquals` for array content verification

## Dependency Scope

This profile covers only the `my-spring-boot-app` module.
New packages/paths are added as the project evolves.