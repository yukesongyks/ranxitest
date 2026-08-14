# Hello World 跨仓设计文档

> 阶段：需求澄清（brainstorming）
> 需求："帮我写个hello world" → 两个仓库各写一个符合各自技术栈的 Hello World

---

## 1. 跨仓依赖与现状摘要

| 仓库 | 技术栈 | 现有 HelloWorld | 包/模块结构 |
|---|---|---|---|
| **ranxitest** | Java 17 / Spring Boot 2.6.6 / Maven | 无 | `com.example.myapp` — controllers/ services/ models/ repositories/ utils/ |
| **dtazzi-cline** | TypeScript / Node.js / ESM / Kanban CLI | 根目录有 `HelloWorld.java`（非 TS 项目代码） | `src/` — core/ cli.ts index.ts 等 |

---

## 2. 方案设计

### 2.1 ranxitest：Spring Boot REST Hello World

**位置**：`my-spring-boot-app/src/main/java/com/example/myapp/controllers/HelloController.java`

**方案**：新增 `@RestController`，暴露 `GET /api/hello`，返回 JSON `{"message": "Hello World"}`。

**理由**：
- 与现有 `ItemController` 风格一致（`@RestController` + `@GetMapping`）
- 已有 `spring-boot-starter-web` 依赖，无需额外配置
- REST 端点比 Thymeleaf 模板更轻量，适合演示

**新增文件**：
- `controllers/HelloController.java` — REST 控制器
- `controllers/HelloControllerTest.java`（test） — 集成测试

**接口契约**：
```
GET /api/hello
→ 200 {"message": "Hello World"}
```

---

### 2.2 dtazzi-cline：TypeScript Hello World 模块

**位置**：`src/hello-world.ts`

**方案**：新增 TypeScript 模块，导出 `hello()` 函数返回 `"Hello World"`。

**理由**：
- 项目是 ESM 模块（`"type": "module"`），适合导出函数式模块
- 与 `src/core/` 下模块风格一致
- 轻量，不引入额外依赖

**新增文件**：
- `src/hello-world.ts` — 导出 `hello()` 函数
- `test/hello-world.test.ts` — 单元测试

**接口契约**：
```ts
export function hello(): string;  // returns "Hello World"
```

---

## 3. 仓间对齐点

无跨仓接口依赖。两个仓库的 Hello World 完全独立，互不影响。

---

## 4. 测试策略

| 仓库 | 测试类型 | 验证方式 |
|---|---|---|
| ranxitest | `@WebMvcTest` + MockMvc | `mvn test` — 验证 `GET /api/hello` 返回 200 + JSON |
| dtazzi-cline | vitest 单元测试 | `npx vitest run` — 验证 `hello()` 返回 `"Hello World"` |

---

## 5. 风险与决策记录

- **无歧义**：两个仓库独立，无跨仓耦合
- **无破坏性变更**：均为新增文件，不修改现有代码
- **dtazzi-cline 根目录已有 HelloWorld.java**：该文件是 Java 源码，与本次 TS 模块不冲突，不处理