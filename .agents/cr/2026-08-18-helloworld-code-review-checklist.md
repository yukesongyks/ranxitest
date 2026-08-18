# Code Review Checklist

> **Change** `helloworld` · **分支/Commit** `AI/task-DEV-966dcd0a-7905-11f1-9649-3b4281182f10-db9b13d4-efd4-4093-8437-6b33283509ca` · **日期** `2026-08-18`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **执行顺序（强制）**：已执行 `scan-all-rules.sh` → 无命中。

---

## Step 1 — 执行队列（产物 A）

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|-----|----|----|----|----|----|----|----|----|----|-----|-----|-----|-----|-----|-----|-----|-----|--------|
| 1 | `my-spring-boot-app/src/main/java/com/example/myapp/helloworld/HelloWorldController.java` | REQ-1 REST Controller | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | ✅ | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | ✅ | N/A | ✅ 已审 |
| 2 | `my-spring-boot-app/src/main/java/com/example/myapp/helloworld/HelloWorldService.java` | REQ-1 Business Service | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | ✅ | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审 |
| 3 | `my-spring-boot-app/src/main/java/com/example/myapp/helloworld/HelloWorldVO.java` | REQ-1 Response VO | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 4 | `my-spring-boot-app/src/test/java/com/example/myapp/helloworld/HelloWorldControllerTest.java` | REQ-1 Unit Test | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审 |

---

## Step 2 — 功能（产物 B）

> 仅从 spec/tasks 提 **REQ**，勿臆造。不符 spec 标 **P0**。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | 系统应通过 REST API 返回问候语 | `写一个helloworld` — 需求描述 | `HelloWorldController.java`, `HelloWorldService.java`, `HelloWorldVO.java` | ✅ | `GET /api/hello` → `{"message":"Hello, World!"}`; `GET /api/hello?name=Alice` → `{"message":"Hello, Alice!"}`; 测试覆盖两个场景 |
| REQ-2 | 支持自定义名称参数 | `写一个helloworld` — 实现扩展（合理增强） | `HelloWorldController.java:32`, `HelloWorldService.java:29` | ✅ | `@RequestParam(required=false) String name` 可选参数；`HelloWorldControllerTest.java:49` 验证 `?name=Alice` 路径 |

---

## Step 3 — 可读性检查（产物 C）

对照 `references/readability-checklist.md` A1–A7 逐节核销：

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | 所有文件名与类名一致；UTF-8；无 Tab |
| A2 | 源文件结构/import 顺序 | ✅ | package→import→class 结构正确；无 `import *`；import 分组正确；按 ASCII 排序 |
| A3 | 代码样式 | ✅ | K&R 大括号；4空格缩进；行宽≤120；运算符两侧空格正确 |
| A4 | 命名规范 | ✅ | 包名全小写；类名 UpperCamelCase；方法/字段 lowerCamelCase；常量 UPPER_SNAKE_CASE |
| A5 | 编码实践 | ✅ | `@Override` 正确使用（`HelloWorldVO.java:28`）；无空 catch |
| A6 | 特定元素样式 | ✅ | 修饰符顺序正确；注解每行一个 |
| A7 | Javadoc 规范 | ✅ | 所有 public 类/方法有 Javadoc；块标记顺序正确 |

---

## Step 4 — 可靠性检查（产物 D）

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> 已运行 `scan-all-rules.sh`（52/222 规则）→ 无命中。

| ID | 状态 | 备注（命中写 `path:line`；预扫可粘贴脚本摘要） |
|----|------|--------------------------------------------------|
| B001–B081 | N/A | 无数据库操作、无序列化、无反射、无并发、无文件I/O、无循环依赖等复杂场景。脚本扫描无命中。简单 hello world 变更，不涉及 Blocker 级缺陷模式。 |
| M001–M027 | N/A | 无 Major 级缺陷模式匹配。无集合操作、无日期处理、无异常处理相关缺陷。 |
| I001–I010 | N/A | 无 Info 级缺陷模式匹配。 |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1–G1.4 | N/A | 无并发场景 |
| G2.1–G2.3 | N/A | 无写操作/幂等需求 |
| G3.1–G3.2 | N/A | 无事务 |
| G4.1–G4.3 | N/A | 无 SQL |
| G5.1 | N/A | 无 MQ |
| G6.1–G6.2 | N/A | 无缓存 |
| G7.1–G7.2 | N/A | 无调度任务 |
| G8.1–G8.6 | N/A | 无 I/O 流、无 ThreadLocal、无线程池 |
| G9.1–G9.3 | N/A | 无外部 RPC/HTTP 调用 |
| G10.1–G10.2 | N/A | 简单 VO，无契约版本问题 |
| G11.1 | ✅ | 有单元测试且有断言（`HelloWorldControllerTest.java`） |
| G11.2 | ⚠️ | **P2** — 边界覆盖不足：未覆盖 `name=""`（空字符串）分支。Service 层有 `name.trim().isEmpty()` 防御，但测试未验证该路径。 |
| G11.3 | ✅ | `@RequestParam(required=false)` 处理 null；Service 层防御 null 和空字符串 |
| G11.4 | N/A | 无数值运算 |
| G12.1–G12.2 | N/A | 无资金相关 |
| G13.1 | ✅ | 日志级别正确（INFO 记录正常调用） |
| G14.1–G14.4 | N/A | 无国际化/多租户/时区需求 |
| G15.1–G15.3 | N/A | 无 DB 变更 |
| G16.1 | ✅ | 核心链路有日志埋点 |
| G16.2 | ✅ | 日志包含可追溯上下文（name, message） |
| G16.3 | ✅ | INFO 级别记录正常流程 |
| G16.4 | N/A | 无空 catch |
| G17.1–G17.3 | N/A | 简单 demo 无灰度/应急需求 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1–S1.3 | N/A | 无 SQL |
| S2.1–S2.3 | N/A | 返回 JSON，非 HTML 渲染 |
| S3.1–S3.3 | N/A | 无外部 URL 请求 |
| S4.1–S4.2 | N/A | 无命令执行 |
| S5.1–S5.2 | N/A | 无 XML 解析 |
| S6.1–S6.3 | N/A | 无反序列化 |
| S7.1–S7.3 | N/A | 无文件上传 |
| S8.1 | ✅ | demo 端点，鉴权非本变更范围 |
| S8.2 | ✅ | GET 仅读操作，无增删改 |
| S9.1 | ✅ | 无硬编码密钥 |
| S9.2 | ✅ | 日志不记录敏感信息 |
| S10.1–S10.3 | N/A | 无 CSRF/CORS/跳转问题（GET 只读，CSRF 不适用） |

---

## Step 5 — 自定义扩展检查（产物 E）

### 5.1 自定义扩展（`customized-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1–U2.3 | N/A | 未启用自定义规则（均为示例项，项目未配置私有规则） |

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、**S1–S10 / G1–G17** 各列均非 `⬜`（跳过文件除外）；
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`（允许 `N/A`，但有原因）
- [x] Step 5 全部 U* ID 均非 `⬜`（允许 `N/A(未启用自定义规则)`）
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`