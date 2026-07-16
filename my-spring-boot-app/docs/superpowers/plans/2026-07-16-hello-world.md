# Hello World 端点 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在现有 Spring Boot 应用中新增一个 `/hello` REST 端点，返回 `"Hello, World!"` 字符串。

**Architecture:** 在现有 `com.example.myapp.controllers` 包下新增 `HelloController.java`，使用 `@RestController` + `@GetMapping("/hello")` 提供纯文本响应。遵循项目已有的 Spring Boot MVC 分层约定，测试类放入对应 test 包。

**Tech Stack:** Spring Boot 2.6.6, Java 17, Maven, JUnit 5 + MockMvc (spring-boot-starter-test)

## Global Constraints

- 遵循项目现有包结构 `com.example.myapp`，不新建子包
- 使用项目已有的依赖，不引入新依赖
- 遵循 TDD 流程：先写测试 → 测试失败 → 实现 → 测试通过
- 测试类命名遵循 `*Test.java` 约定（与现有 `MyAppApplicationTests.java` 一致）

---

## File Structure

```
my-spring-boot-app/src/
├── main/java/com/example/myapp/controllers/
│   └── HelloController.java          # 新增: @RestController, GET /hello → "Hello, World!"
└── test/java/com/example/myapp/
    └── HelloControllerTest.java      # 新增: MockMvc 集成测试，验证 /hello 返回 200 + "Hello, World!"
```

---

### Task 1: HelloController — REST 端点 "Hello, World!"

**Files:**
- Create: `my-spring-boot-app/src/main/java/com/example/myapp/controllers/HelloController.java`
- Create: `my-spring-boot-app/src/test/java/com/example/myapp/HelloControllerTest.java`

**Interfaces:**
- Consumes: 无（独立任务，不依赖其他任务）
- Produces: `GET /hello` → HTTP 200, body `"Hello, World!"` (Content-Type: text/plain)

- [ ] **Step 1: 编写失败的测试**

```java
package com.example.myapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHelloEndpoint() throws Exception {
        mockMvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, World!"));
    }
}
```

- [ ] **Step 2: 运行测试验证失败**

Run: `cd my-spring-boot-app && mvn test -Dtest=HelloControllerTest -pl .`
Expected: FAIL with 404 (no mapping for /hello)

- [ ] **Step 3: 实现 HelloController 使测试通过**

```java
package com.example.myapp.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }
}
```

- [ ] **Step 4: 运行测试验证通过**

Run: `cd my-spring-boot-app && mvn test -Dtest=HelloControllerTest -pl .`
Expected: PASS — Tests run: 1, Failures: 0

- [ ] **Step 5: 确认应用启动并手动验证**

Run: `cd my-spring-boot-app && mvn spring-boot:run -pl . &`
Curl: `curl -s http://localhost:8080/hello`
Expected output: `Hello, World!`

---

## Verification

- `mvn test -Dtest=HelloControllerTest` 全部通过
- 应用启动后 `curl http://localhost:8080/hello` 返回 `Hello, World!`