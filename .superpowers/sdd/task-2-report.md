# Task 2: LotteryController — 页面路由与 JSON 接口

## Status: COMPLETED

## TDD Evidence

### RED phase (Step 2): Compilation error

```
mvn test -Dtest=LotteryControllerTest
```

Result: `COMPILATION ERROR` — `cannot find symbol: class LotteryController`

The test file refers to `LotteryController` which does not yet exist, confirming the RED phase as specified in the brief.

### GREEN phase (Step 4): All tests pass

```
mvn test -Dtest=LotteryControllerTest
```

Result: `Tests run: 2, Failures: 0, Errors: 0, Skipped: 0` — BUILD SUCCESS

### Full suite regression

```
mvn test
```

Result: `Tests run: 8, Failures: 0, Errors: 0, Skipped: 0` — BUILD SUCCESS

## Files Created

### 1. `my-spring-boot-app/src/main/java/com/example/myapp/controllers/LotteryController.java`

- `@Controller` with constructor injection of `LotteryService`
- `GET /lottery` → returns Thymeleaf view name `"lottery/index"`
- `GET /api/lottery/draw` → `@ResponseBody` returns `List<Integer>` from `LotteryService.draw()`

### 2. `my-spring-boot-app/src/test/java/com/example/myapp/controllers/LotteryControllerTest.java`

- `@WebMvcTest(LotteryController.class)` with `@MockBean LotteryService`
- `drawEndpointReturnsFiveNumbersAsJson`: mocks `draw()` → `List.of(9, 45, 123, 500, 678)`, verifies `200 OK` with JSON array body
- `lotteryPageIsRendered`: verifies `GET /lottery` returns `200 OK` with view name `"lottery/index"`

### Deviation: `@TestPropertySource` annotation

Added `@TestPropertySource(properties = "spring.thymeleaf.check-template=false")` to the test class. This is necessary because Thymeleaf's `SpringResourceTemplateResolver` in Spring Boot 2.6.6 verifies template existence by default (`checkTemplate=true`), and when the template doesn't exist, it throws `TemplateInputException: Error resolving template` before MockMvc's `status()` and `view()` assertions can be evaluated. The property `spring.thymeleaf.check-template=false` (available on `ThymeleafProperties`) disables this pre-check, allowing the resolver to return a resolution — but the template still needs to exist for parsing to succeed.

### Deviation: Placeholder template

Created `my-spring-boot-app/src/main/resources/templates/lottery/index.html` as a minimal valid Thymeleaf template. Even with `check-template=false`, the Thymeleaf engine throws `FileNotFoundException` during template parsing if the file truly doesn't exist on disk. This placeholder will be replaced by Task 3's full implementation. The file contains only a minimal HTML skeleton with a comment indicating Task 3's future work.

## Self-Review

- Controller uses constructor injection (preferred over field injection)
- `@WebMvcTest` correctly excludes `@Service` beans; `@MockBean` provides the mock
- Both endpoints (`/lottery` and `/api/lottery/draw`) are tested for correct status codes, view names, and response bodies
- The JSON test uses `content().json()` which compares parsed JSON (order-independent), matching the exact values from the mock
- No modifications to Task 1's `LotteryService.java` or any other existing source files
- Git was not used for any write operations per constraints

## Deviations from brief (reviewer-acknowledged)

### `@TestPropertySource(properties = "spring.thymeleaf.check-template=false")`

Added to `LotteryControllerTest` because Spring Boot 2.6.6's Thymeleaf auto-configuration defaults `spring.thymeleaf.check-template` to `true`. When the Thymeleaf template does not exist yet, the `SpringResourceTemplateResolver` throws a `TemplateInputException` during template resolution — before MockMvc's `status()` and `view()` assertions can even execute. Setting `check-template=false` disables only the existence pre-check; the view-name assertion in `lotteryPageIsRendered()` still validates the correct view name is returned. This property is scoped to the test class only and does not affect production behavior.

### Placeholder `templates/lottery/index.html`

Even with `check-template=false`, the Thymeleaf engine throws a `FileNotFoundException` during template parsing if the file is absent from disk. A minimal valid HTML placeholder was created to satisfy the resolution requirement. Task 3 will replace this placeholder with the real lottery page implementation. The file contains only a skeleton with an HTML comment indicating the pending Task 3 work.

## Fix round 1

### Changes applied

1. **Deviations section** — Appended `## Deviations from brief (reviewer-acknowledged)` explaining the `@TestPropertySource` and placeholder template rationale (reviewer request 1).

2. **Strengthened JSON test** — In `drawEndpointReturnsFiveNumbersAsJson`:
   - Added `import static org.hamcrest.Matchers.hasSize;`
   - Added `.andExpect(jsonPath("$", hasSize(5)))` after the existing `content().json(...)` assertion to verify exactly 5 elements (reviewer request 2).

3. **Trailing newlines** — Added exactly one trailing newline to:
   - `LotteryController.java`
   - `LotteryControllerTest.java`
   - `templates/lottery/index.html`
   (reviewer request 3)

### Verification

```sh
cd my-spring-boot-app && \
export JAVA_HOME=/opt/tools/jdk-17.0.20.1+1 && \
export PATH=/opt/tools/apache-maven-3.9.16/bin:$JAVA_HOME/bin:$PATH && \
mvn test -Dtest=LotteryControllerTest
```

**Result:** `Tests run: 2, Failures: 0, Errors: 0, Skipped: 0` — BUILD SUCCESS

### Files touched

- `my-spring-boot-app/src/test/java/com/example/myapp/controllers/LotteryControllerTest.java` — added `hasSize` import + assertion, trailing newline
- `my-spring-boot-app/src/main/java/com/example/myapp/controllers/LotteryController.java` — trailing newline
- `my-spring-boot-app/src/main/resources/templates/lottery/index.html` — trailing newline
- `.superpowers/sdd/task-2-report.md` — appended deviations section + fix round 1 report