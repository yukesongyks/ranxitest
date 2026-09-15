# REVIEW.md — ranxitest

Spring Boot + Thymeleaf + H2 demo application.

## Project-specific gates

1. **Layer discipline**: Controller handles HTTP binding/redirect; Service owns business logic and transaction boundaries; Repository is Spring Data JPA interface only. No business logic in controllers or templates.
2. **Thymeleaf templates**: Form binding uses `th:object` + `th:field`. POST endpoints must use `@ModelAttribute` and validate input before persistence. Redirect-after-POST pattern required for mutation endpoints.
3. **JPA entities**: Use `@Entity`, `@Id`, `@GeneratedValue`. Avoid exposing entities directly in templates — prefer DTOs or selective field exposure. Password/sensitive fields must not be rendered in HTML by default.
4. **H2 in-memory DB**: No production persistence. Schema auto-update via `ddl-auto=update`. Test data setup via `CommandLineRunner` or `data.sql`.
5. **Testing**: `mvn test` for the `my-spring-boot-app` module. At minimum, an integration test (`@SpringBootTest`) should verify the application context loads and key endpoints respond.