# Review Profile — my-spring-boot-app

- **Stack**: Java 17, Spring Boot 2.6.6, Maven, JUnit 5 (Jupiter)
- **Test framework**: JUnit 5 (`spring-boot-starter-test`)
- **Package**: `com.example.myapp`
- **Layers**: `controllers`, `models`, `repositories`, `services`, `utils`

## Project-specific gates
- Utility classes in `utils` must be `final` with private constructor; no instance state.
- Public sort/algorithm utilities must handle `null` and empty inputs gracefully (no NPE).
- Tests must use JUnit 5 assertions (`assertArrayEquals`, `assertEquals`); avoid JUnit 4 API.
- Algorithm implementations must include tests for: null, empty, singleton, sorted, reverse-sorted, duplicates, negative values, and large inputs.