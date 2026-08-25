# Plan: Hello World Endpoint

## Global Constraints
- Java 17, Spring Boot 2.6.6
- Package: com.example.myapp.controllers
- Follow existing code conventions (same package, same import style)
- Must include a corresponding test class

## Tasks

### Task 1: Add Hello World Controller
- Create `HelloController.java` in `my-spring-boot-app/src/main/java/com/example/myapp/controllers/`
- Expose `GET /hello` returning `"Hello, World!"` (plain text, not JSON)
- Use `@RestController` and `@GetMapping("/hello")`
- Create `HelloControllerTest.java` in `my-spring-boot-app/src/test/java/com/example/myapp/controllers/`
- Test verifies GET /hello returns 200 with body "Hello, World!"