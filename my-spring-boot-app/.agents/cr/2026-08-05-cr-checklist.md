# Code Review Checklist

> **Change** `services-test` · **分支/Commit** `AI/task-DEV-f4ad1a6e` / `<worktree>` · **日期** `2026-08-05`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **执行顺序（强制）**：写入本清单并进入逐文件审查前，先在目标仓库对变更路径运行 `references/script/scan-all-rules.sh`，将输出贴入 Step 3 和 Step 4 备注；再用 LLM 完成 Step 2–5 中脚本未覆盖项及复核。

---

## 预扫结果（scan-all-rules.sh）

```
=== Step 4 Rule Scan (B/M/I + A/S/G) ===
Targets: src/test/java/com/example/myapp/services/ItemServiceTest.java src/test/java/com/example/myapp/services/UserServiceTest.java
Engine:  ripgrep

[P2] A2.2 — WildcardImport: src/test/java/com/example/myapp/services/ItemServiceTest.java:24
[P2] A2.2 — WildcardImport: src/test/java/com/example/myapp/services/UserServiceTest.java:22

=== Summary: 2 findings (P0=0, P1=0, P2=2) | 52/222 rules scanned ===
```

---

## Step 1 — 执行队列（产物 A）

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1–G17 | S1–S10 | B/M/I | 总状态 |
|---|----------------------|----------|-------|-------|--------|--------|-------|--------|
| 1 | `my-spring-boot-app/src/test/java/com/example/myapp/services/ItemServiceTest.java` | 被测类 `ItemService` 全方法单测 | ✅ | ⚠️ | N/A | N/A | N/A | ⚠️ 已审有问题 |
| 2 | `my-spring-boot-app/src/test/java/com/example/myapp/services/UserServiceTest.java` | 被测类 `UserService` 全方法单测 | ✅ | ⚠️ | N/A | N/A | N/A | ⚠️ 已审有问题 |

- **来源**：任务 `inputs_content` 显式指定 2 个 `*Test.java`（git diff 为空，按「上下文已验证事实」优先）。
- **Java 守卫**：含 2 个 `.java` 文件，通过。
- **G/S 列批量说明**：两文件均为 **Mockito 隔离的单元测试**，不含 SQL、MQ、线程池、外部 IO、认证授权、加密等逻辑；G1–G17（并发/资源/事务/限流/灰度/监控）、S1–S10（SQL 注入/XSS/CSRF/越权/密钥泄露/反序列化等）整节与测试代码无关，一次性标 `N/A(测试代码：无 SQL/MQ/并发/IO/认证)`。
- **B/M/I 列批量说明**：120 条 Bug 模式针对生产代码模式（日期/集合泛型/JDBC/线程/I/O/序列化等），测试文件仅含 Mock 桩桩 + AssertJ 断言，逐一核销见 §4.1。

---

## Step 2 — 功能（产物 B）

> REQ 来源：无独立 spec/design 文档，以被测类 `ItemService.java`/`UserService.java` 的公有方法契约为 spec 等价物（契约 = 方法签名 + 异常约定 + 分支逻辑）。

### ItemServiceTest → ItemService.java

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | findAll 委托仓库返回全部 | `ItemService:24-26` `return itemRepository.findAll()` | ItemServiceTest:54-81 | ✅ | 测试 `shouldReturnAllItems`/`shouldReturnEmptyListWhenNoData` 覆盖多记录+空列表 |
| REQ-2 | findById 委托仓库按 ID 查询 | `ItemService:28-30` | ItemServiceTest:87-111 | ✅ | 存在→Optional 含值；不存在→空 Optional |
| REQ-3 | findByName 委托仓库按名称查询 | `ItemService:32-34` | ItemServiceTest:117-126 | ✅ | |
| REQ-4 | save 新增时名称重复检查 | `ItemService:36-45` `if (item.getId()==null && existsByName(...))` | ItemServiceTest:132-187 | ✅ | 覆盖：不重复保存成功、重复抛 IAE、DataIntegrityViolation 转换、id 非空跳过检查 |
| REQ-5 | update 名称变更时唯一性校验 | `ItemService:47-69` `if (!item.getName().equals(...))` + `findByNameForUpdate` | ItemServiceTest:193-253 | ✅ | 覆盖：名称未变直接更新、不存在抛 IAE、新名称占用抛 IAE、DataIntegrityViolation 转换 |
| REQ-6 | deleteById 存在性校验 | `ItemService:71-76` `if (!existsById(id))` | ItemServiceTest:259-284 | ✅ | 存在→删除；不存在→抛 IAE 且不执行删除 |
| REQ-7 | searchByKeyword null/空白返回全部 | `ItemService:78-83` `keyword==null || trim().isEmpty()` | ItemServiceTest:290-326 | ✅ | 覆盖 null、空白、有效关键词自动 trim |
| REQ-8 | searchByKeywordAndUserId 空白时按用户查询 | `ItemService:85-90` | ItemServiceTest:332-358 | ✅ | 空白→findByUserId；有效→trim 后搜索 |
| REQ-9 | 委托查询方法 | `ItemService:92-106` findByCategory/findByUserId/findLowStockItems/getAllCategories | ItemServiceTest:364-402 | ✅ | 四个委托方法均有测试 |

### UserServiceTest → UserService.java

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-10 | createUser 用户名优先于邮箱检查 | `UserService:23-31` 先 `existsByUsername` 后 `existsByEmail` | UserServiceTest:54-121 | ✅ | 覆盖：均不重复创建成功、用户名重复抛 IAE、邮箱重复抛 IAE、用户名重复时不检查邮箱 |
| REQ-11 | getUserById 委托仓库按 ID 查询 | `UserService:33-35` | UserServiceTest:127-151 | ✅ | 存在/不存在两个分支 |
| REQ-12 | getAllUsers 委托仓库返回全部 | `UserService:37-39` | UserServiceTest:157-165 | ✅ | |
| REQ-13 | updateProfile 名称/邮箱变更时唯一性校验 | `UserService:41-62` `!user.getUsername().equals(...) && existsByUsername(...)` | UserServiceTest:171-268 | ✅ | 覆盖：不存在抛 IAE、均未变直接更新、新用户名占用、新邮箱占用、均变更且不重复更新全部字段 |
| REQ-14 | deleteUser 存在性校验 | `UserService:64-69` | UserServiceTest:274-299 | ✅ | 存在→删除；不存在→抛 IAE 且不执行 |
| REQ-15 | getOrCreateDefaultUser 有则返回首个/无则创建默认 | `UserService:74-85` `findFirst().orElseGet(...)` | UserServiceTest:305-339 | ✅ | 有用户→返回第一个且不 save；空→创建 admin/admin@example.com/bio/中国 |

---

## Step 3 — 可读性检查（产物 C）

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | 两文件 UTF-8、LF、4 空格缩进、类体大括号规范 |
| A2 | 源文件结构/import 顺序 | ⚠️ | 预扫命中 `A2.2 WildcardImport`：ItemServiceTest:24 `import static org.mockito.Mockito.*`；UserServiceTest:22 同。应改为显式导入 `when`/`verify`/`times`/`never` 等 |
| A3 | 代码样式 | ✅ | AAA 模式（given/when/then 注释或结构清晰），方法长度合理 |
| A4 | 命名规范 | ✅ | 测试方法 `shouldXxxWhenYyy` 语义清晰；@DisplayName 中文描述准确 |
| A5 | 编码实践 | ✅ | 使用 @Nested 分组、@BeforeEach 公共夹具、AssertJ 流式断言 |
| A6 | 特定元素样式 | ✅ | 注释分隔线风格统一 |
| A7 | Javadoc 规范 | ✅ | 类级 Javadoc 描述测试意图；测试方法用 @DisplayName 替代 Javadoc，符合测试代码惯例 |

---

## Step 4 — 可靠性检查（产物 D）

> **逐条核销**：G/S/B/M/I 每类逐条核销。测试代码不含生产逻辑，多数类别 N/A。

### 4.1 Bug 模式（`bug-pattern-checklist.md`，120 条）

> **批量 N/A 理由**：两文件为 Mockito + AssertJ 单元测试，不含以下生产代码模式：日期/时间处理（B001/B012/B013/B022/B028）、集合泛型查询（B014/B034/B036）、数组操作（B002-B005）、BigDecimal 构造（B010）、线程池（B008/B024）、日历（B012/B013）、Jedis/连接池（B021）、序列化/Unsafe（B033）、equals/hashCode 实现（B026/B027）、Calendar/Hashtable（B012-B013/B034）、Money API（B019）、双括号初始化（B025）、递归（B038）、浮点比较（B030）、三目提升（B018）、常量溢出（B020）、移位（B009）等。测试中无 `assertEquals`（使用 assertThat，B006 N/A）、无 `catch(Throwable)`（B007 N/A）。

| ID | 状态 | 备注 |
|----|------|------|
| B001–B081（Blocker 81 条） | N/A | 测试代码不含上述生产模式；预扫无命中 |
| M001–M027（Major 27 条） | N/A | 同上；无资源泄漏/空指针/空方法/冗余比较等模式 |
| I001–I010（Info 10 条） | N/A | 无信息级模式适用 |

### 4.2 可靠性（`reliability-checklist.md`，G1–G18）

| ID 范围 | 状态 | 备注 |
|---------|------|------|
| G1（并发控制 G1.1–G1.4） | N/A | 测试代码无多线程/共享状态 |
| G2（超时/重试 G2.1–G2.3） | N/A | 测试无外部调用，无需超时/重试 |
| G3（限流 G3.1–G3.2） | N/A | 测试无限流场景 |
| G4（资源释放 G4.1–G4.4） | N/A | Mockito Extension 自动管理 Mock 生命周期，无手动资源 |
| G5（事务边界 G5.1） | N/A | 测试无 @Transactional 逻辑执行 |
| G6（并发与幂等 G6.1–G6.2） | N/A | 无并发/幂等场景 |
| G7（边界条件 G7.1–G7.2） | ✅ | 测试已覆盖边界：null 关键词、空白关键词、空列表、不存在 ID、名称重复 |
| G8–G17 | N/A | 灰度/监控/应急/日志/配置等均不适用于单元测试 |
| G18（安全补强 G18.1–G18.3） | N/A | 测试无安全敏感逻辑 |

### 4.3 安全（`security-checklist.md`，S1–S10）

| ID 范围 | 状态 | 备注 |
|---------|------|------|
| S1–S10 | N/A | 测试代码无 SQL 拼接、无 XSS 输出、无认证授权、无密钥/凭证、无反序列化、无文件上传、无 CSRF/CORS/跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | `customized-checklist.md` U* | N/A | — | N/A(未启用自定义规则)：该清单为空/示例项 |

---

## 收口核销

- [x] Step 1 执行队列 `⬜ 待审` = 0（2 文件均已审）
- [x] Step 2 所有 REQ 状态非 `⬜`（15 条 REQ 全部 ✅）
- [x] Step 3 A1–A7 全部非 `⬜`（6 ✅ + 1 ⚠️）
- [x] Step 4 G/S/B/M/I 全部非 `⬜`（批量 N/A + G7 ✅）
- [x] Step 5 非 `⬜`（N/A）
- [x] 预扫结果已贴入 Step 3/Step 4 备注
- [x] report 审查范围文件数 = 2（与队列一致）
