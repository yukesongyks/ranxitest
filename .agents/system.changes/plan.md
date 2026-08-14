# Hello World 跨仓实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在两个仓库中各自新增符合其技术栈的 Hello World 功能：ranxitest 新增 Spring Boot REST 端点，dtazzi-cline 新增 TypeScript 模块。

**Architecture:** 两个仓库完全独立，无跨仓接口依赖。ranxitest 采用 `@RestController` + `@GetMapping` 暴露 REST JSON 端点；dtazzi-cline 采用 ESM 模块导出纯函数。均为纯新增文件，零破坏性变更。

**Tech Stack:** ranxitest: Java 17 / Spring Boot 2.6.6 / Maven / JUnit 5 + MockMvc；dtazzi-cline: TypeScript / Node.js / ESM / vitest

**Design Doc:** `.agents/system.changes/dima.md`

---

## Global Constraints

- 两个仓库完全独立，无跨仓耦合
- 均为新增文件，不修改任何现有代码
- ranxitest 遵循现有 `@RestController` 风格（参考 `ItemController`）
- dtazzi-cline 遵循 ESM 模块导出风格
- 每个任务完成后必须通过对应测试

---

## 仓库 A：ranxitest

### Task A-1: HelloController — REST 端点

**Files:**
- Create: `my-spring-boot-app/src/main/java/com/example/myapp/controllers/HelloController.java`
- Create: `my-spring-boot-app/src/test/java/com/example/myapp/controllers/HelloControllerTest.java`

**Interfaces:**
- Consumes: 无（独立任务，无上游依赖）
- Produces: `GET /api/hello` → `200 {"message": "Hello World"}`

**契约：**
```
GET /api/hello
Response 200: {"message": "Hello World"}
```

- [ ] **Step 1: 编写 HelloController**

```java
package com.example.myapp.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("message", "Hello World");
    }
}
```

- [ ] **Step 2: 编写 HelloControllerTest（集成测试）**

```java
package com.example.myapp.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HelloController.class)
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void hello_shouldReturnHelloWorld() throws Exception {
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello World"));
    }
}
```

- [ ] **Step 3: 运行测试验证通过**

Run: `cd my-spring-boot-app && mvn test -pl . -Dtest=HelloControllerTest -DfailIfNoTests=false`
Expected: BUILD SUCCESS, test `hello_shouldReturnHelloWorld` PASS

- [ ] **Step 4: 运行完整测试套件确认无回归**

Run: `cd my-spring-boot-app && mvn test`
Expected: BUILD SUCCESS, all tests pass

---

## 仓库 B：dtazzi-cline

### Task B-1: hello-world TypeScript 模块

**Files:**
- Create: `src/hello-world.ts`
- Create: `test/hello-world.test.ts`

**Interfaces:**
- Consumes: 无（独立任务，无上游依赖）
- Produces: `export function hello(): string` — 返回 `"Hello World"`

**契约：**
```ts
export function hello(): string;  // returns "Hello World"
```

- [ ] **Step 1: 编写 hello-world.ts**

```typescript
/**
 * Returns the canonical "Hello World" greeting.
 */
export function hello(): string {
  return "Hello World";
}
```

- [ ] **Step 2: 编写单元测试 hello-world.test.ts**

```typescript
import { describe, it, expect } from "vitest";
import { hello } from "../src/hello-world.js";

describe("hello", () => {
  it('should return "Hello World"', () => {
    expect(hello()).toBe("Hello World");
  });
});
```

- [ ] **Step 3: 运行测试验证通过**

Run: `npx vitest run test/hello-world.test.ts`
Expected: 1 test passed

- [ ] **Step 4: 运行完整测试套件确认无回归**

Run: `npx vitest run`
Expected: all tests pass

---

## 仓间对齐点检查

| 检查项 | 状态 |
|---|---|
| 跨仓接口依赖 | 无 — 两个仓库完全独立 |
| 破坏性变更 | 无 — 均为新增文件 |
| 契约冲突 | 无 — 各自独立命名空间 |
| 测试覆盖 | ranxitest: `@WebMvcTest` 集成测试；dtazzi-cline: vitest 单元测试 |

---

## Self-Review

1. **Spec coverage:** 需求 "帮我写个hello world" → dima.md 澄清为两个仓库各写一个 Hello World。Task A-1 覆盖 ranxitest，Task B-1 覆盖 dtazzi-cline。全覆盖。
2. **Placeholder scan:** 无 TBD/TODO/占位符，所有步骤均有完整代码和命令。
3. **Type consistency:** 跨任务无共享类型/签名，无需一致性检查。