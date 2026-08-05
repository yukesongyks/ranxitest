# Code Review Report

> **Change** `services-test` · **分支/Commit** `AI/task-DEV-f4ad1a6e` / `<worktree>` · **日期** `2026-08-05` · **审查者** AI
>
> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 `bug-pattern-checklist.md` 表头为准（Blocker→P0、Major→P1、Info→P2）。**已先**运行 `scan-all-rules.sh` 并将要点并入 §5，**再**写 LLM 结论。问题须含 `path:line` 或清单 ID。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | `2` |
| 变更行数 | `git diff 为空（工作区无未提交变更）；按 inputs_content 显式指定` |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| `ItemServiceTest` | `my-spring-boot-app/src/test/java/com/example/myapp/services/ItemServiceTest.java` | ItemService 单元测试（18 个测试方法，403 行） |
| `UserServiceTest` | `my-spring-boot-app/src/test/java/com/example/myapp/services/UserServiceTest.java` | UserService 单元测试（14 个测试方法，340 行） |

**被测类**（非本次变更范围，用于 Step 2 功能核对的 spec 等价物）：
- `ItemService.java`（107 行）：findAll/findById/findByName/save/update/deleteById/searchByKeyword/searchByKeywordAndUserId/findByCategory/findByUserId/findLowStockItems/getAllCategories
- `UserService.java`（86 行）：createUser/getUserById/getAllUsers/updateProfile/deleteUser/getOrCreateDefaultUser

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 0 | 0 | 2 |

---

## 3. Step 2 — 功能（REQ）

> 无独立 spec/design 文档，以被测类公有方法契约为 spec 等价物。15 条 REQ 全部通过功能核对。

### REQ-1 ~ REQ-9: ItemServiceTest → ItemService

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| findAll 委托返回全部 | ✅ | `ItemService:24-26` | `ItemServiceTest:54-81` | 覆盖多记录 + 空列表 |
| findById 存在/不存在 | ✅ | `ItemService:28-30` | `ItemServiceTest:87-111` | |
| findByName 委托 | ✅ | `ItemService:32-34` | `ItemServiceTest:117-126` | |
| save 名称重复检查 + DataIntegrityViolation 转换 + id 非空跳过 | ✅ | `ItemService:36-45` | `ItemServiceTest:132-187` | 4 个子场景全覆盖 |
| update 名称变更唯一性 + 不存在 + DataIntegrityViolation | ✅ | `ItemService:47-69` | `ItemServiceTest:193-253` | 4 个子场景全覆盖 |
| deleteById 存在性校验 | ✅ | `ItemService:71-76` | `ItemServiceTest:259-284` | |
| searchByKeyword null/空白/有效 trim | ✅ | `ItemService:78-83` | `ItemServiceTest:290-326` | |
| searchByKeywordAndUserId 空白/有效 | ✅ | `ItemService:85-90` | `ItemServiceTest:332-358` | |
| 委托查询方法（4 个） | ✅ | `ItemService:92-106` | `ItemServiceTest:364-402` | findByCategory/findByUserId/findLowStockItems/getAllCategories |

### REQ-10 ~ REQ-15: UserServiceTest → UserService

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| createUser 用户名优先邮箱检查 | ✅ | `UserService:23-31` | `UserServiceTest:54-121` | 4 个子场景：均不重复、用户名重复、邮箱重复、用户名优先级验证 |
| getUserById 存在/不存在 | ✅ | `UserService:33-35` | `UserServiceTest:127-151` | |
| getAllUsers 委托 | ✅ | `UserService:37-39` | `UserServiceTest:157-165` | |
| updateProfile 名称/邮箱变更校验 + 不存在 + 全字段更新 | ✅ | `UserService:41-62` | `UserServiceTest:171-268` | 5 个子场景全覆盖 |
| deleteUser 存在性校验 | ✅ | `UserService:64-69` | `UserServiceTest:274-299` | |
| getOrCreateDefaultUser 有则返回/无则创建默认 | ✅ | `UserService:74-85` | `UserServiceTest:305-339` | 默认值 admin/admin@example.com/bio/中国 全断言 |

---

## 4. Step 3 — 可读性检查

| 结果 | 说明（违规写 Ax.x 与 `path:行`） |
|------|--------------------------------|
| ⚠️ | 预扫命中 `A2.2 WildcardImport`：`ItemServiceTest:24`、`UserServiceTest:22` 均有 `import static org.mockito.Mockito.*`，应改为显式导入。其余 A1/A3–A7 均 ✅ |

---

## 5. Step 4 — 可靠性检查

| 域 | 参考 | 结果 | 等级 | 说明（列命中 ID 或「已扫无命中」） |
|----|------|------|------|-------------------------------------|
| 可靠性 | `reliability-checklist.md` G1–G17 | N/A | — | 测试代码无并发/资源/事务/限流/灰度/监控；G7 边界条件 ✅（测试已覆盖 null/空白/空列表/不存在/重复） |
| 安全 | `security-checklist.md` S1–S10 | N/A | — | 测试代码无 SQL/XSS/认证/密钥/反序列化/上传/CSRF 场景 |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120） | N/A | — | 预扫：`scan-all-rules.sh` 无命中（52/222 规则）；剩余规则均针对生产代码模式（日期/集合泛型/JDBC/线程/I/O），不适用于 Mockito 桩 + AssertJ 断言的测试代码 |

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | `customized-checklist.md` U* | N/A | — | N/A(未启用自定义规则) |

---

## 7. 结论

- **合并建议**：修复后合并（仅 P2 风格问题，不阻塞）
- **P0**：无
- **P1**：无
- **P2**：
  1. `A2.2` `ItemServiceTest:24` — 将 `import static org.mockito.Mockito.*` 改为显式导入 `when`/`verify`/`times`/`never`/`atLeast` 等实际使用的符号
  2. `A2.2` `UserServiceTest:22` — 同上
- **一句话**：两测试文件功能覆盖完整、与被测类逻辑一致、断言精准；唯一问题为通配符静态导入（P2 风格），修复后可合并。

---

## 7.1 问题片段（必填）

### P2 `A2.2` — ItemServiceTest.java:24

- **P2** `A2.2` `my-spring-boot-app/src/test/java/com/example/myapp/services/ItemServiceTest.java:24` — 通配符静态导入 `import static org.mockito.Mockito.*`，应改为显式导入。
  片段范围：`my-spring-boot-app/src/test/java/com/example/myapp/services/ItemServiceTest.java:21-24`

```java
L21| import static org.assertj.core.api.Assertions.assertThat;
L22| import static org.assertj.core.api.Assertions.assertThatThrownBy;
L23| import static org.mockito.ArgumentMatchers.any;
L24| import static org.mockito.Mockito.*; // 问题：通配符静态导入，应显式导入 when/verify/times/never 等
```

### P2 `A2.2` — UserServiceTest.java:22

- **P2** `A2.2` `my-spring-boot-app/src/test/java/com/example/myapp/services/UserServiceTest.java:22` — 同上。
  片段范围：`my-spring-boot-app/src/test/java/com/example/myapp/services/UserServiceTest.java:19-22`

```java
L19| import static org.assertj.core.api.Assertions.assertThat;
L20| import static org.assertj.core.api.Assertions.assertThatThrownBy;
L21| import static org.mockito.ArgumentMatchers.any;
L22| import static org.mockito.Mockito.*; // 问题：通配符静态导入，应显式导入 when/verify/times/never 等
```

---

## 8. 修复任务列表

### P0

- 无待修复项。

### P1

- 无待修复项。

### P2（可选）

- [ ] **P2** `my-spring-boot-app/src/test/java/com/example/myapp/services/ItemServiceTest.java:24` — 将 `import static org.mockito.Mockito.*` 改为显式导入实际使用的 Mockito 静态方法（`when`、`verify`、`times`、`never`）
- [ ] **P2** `my-spring-boot-app/src/test/java/com/example/myapp/services/UserServiceTest.java:22` — 同上，显式导入实际使用的 Mockito 静态方法
