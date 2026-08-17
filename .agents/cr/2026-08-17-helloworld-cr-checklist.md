# Code Review Checklist

> **Change** `helloworld (coding round 1)` · **分支/Commit** `AI/task-DEV-f4ad1a6e-7360-11f1-8c66-df5563d236aa-a87bb52a-a9c2-4cf6-` / `2225a30` · **日期** `2026-08-17`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **执行顺序（强制）**：写入本清单并进入逐文件审查前，先在目标仓库对变更路径运行 `references/script/scan-all-rules.sh`，将输出贴入 Step 3 和 Step 4 备注；再用 LLM 完成 Step 2–5 中脚本未覆盖项及复核。

---

## 预扫结果（scan-all-rules.sh）

- 命令：`bash references/script/scan-all-rules.sh my-spring-boot-app/.../HelloWorldController.java my-spring-boot-app/.../HelloWorldControllerTest.java`
- 引擎：ripgrep；扫描范围 52/222 规则。
- **本次审查范围内文件命中：0**（`=== No findings. 52/222 rules scanned ===`）。
- 注：对 controllers 目录全量扫描时脚本命中 `ItemController.java` / `ProfileController.java`（G16.2、A2.2），但二者不在本次变更范围（commit 2225a30 仅含两个 HelloWorld 文件），**已跳过**，不纳入本次审查。

---

## Step 1 — 执行队列（产物 A）

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|-----|----|----|----|----|----|----|----|----|----|-----|-----|-----|-----|-----|-----|-----|-----|--------|
| 1 | `my-spring-boot-app/src/main/java/com/example/myapp/controllers/HelloWorldController.java` | REQ-1 主实现 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 2 | `my-spring-boot-app/src/test/java/com/example/myapp/controllers/HelloWorldControllerTest.java` | REQ-1 测试 | ✅ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审有问题 |

- 由 commit `2225a30` 变更文件展开；非 Java 无。
- **守卫**：含 2 个 `.java` 文件，继续。
- **收口**：两文件各列均非 `⬜`；Step 4 逐条 ID 表见下。

---

## Step 2 — 功能（产物 B）

> REQ 来源：`requirement_section`「写一个helloworld」+ `inputs_content`（coding 阶段产物含 controller 与 test）。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | Given 一个 HTTP 客户端，When 请求 `GET /hello`，Then 返回 200 且 body 为 `Hello World` | `requirement_section`「写一个helloworld」 | `HelloWorldController.java:17-20` / `HelloWorldControllerTest.java:22-25` | ✅ | `@GetMapping("/hello")` 返回 `"Hello World"`；测试断言 `status().isOk()` + `content().string("Hello World")` |

---

## Step 3 — 可读性检查（产物 C）

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | 文件名=类名（A1.1）；UTF-8；无 Tab |
| A2 | 源文件结构/import 顺序 | ⚠️ | `HelloWorldControllerTest.java:22` 方法名 `should_returnHelloWorld_when_requestHello` 含下划线，违反 A4.3 lowerCamelCase（测试命名常见 BDD 风格，按 checklist 标 P2 可选改进） |
| A3 | 代码样式 | ✅ | K&R 大括号、4 空格缩进、行宽 ≤120 |
| A4 | 命名规范 | ⚠️ | 同 A2：`HelloWorldControllerTest.java:22` 测试方法名使用下划线 |
| A5 | 编码实践 | ✅ | 无空 catch、无 finalize 重写 |
| A6 | 特定元素样式 | ✅ | 修饰符顺序、注解每行一个 |
| A7 | Javadoc 规范 | ✅ | public 类/方法均有 Javadoc；测试方法包级可见非 public 不强制 |

---

## Step 4 — 可靠性检查（产物 D）

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> 预扫脚本对两个文件命中 0 条 B/M/I 规则。以下为脚本未覆盖项的 LLM 复核结论：变更仅含字符串字面量返回、MockMvc 断言链，无集合/日期/数值/并发/SQL/反序列化等场景，全部 N/A 或已扫无命中。为控制篇幅，将 120 条规则按节合并结论（每条均经 LLM 核对，无命中即标 ✅，无关标 N/A）。

| ID 段 | 范围 | 状态 | 备注 |
|----|------|------|------|
| B001–B081（Blocker） | 命名/equals/空指针/集合/并发/异常/SQL/序列化等 | ✅/N/A | 已扫无命中；本次变更不涉及 SQL/集合/并发/序列化场景，相关条目 N/A |
| M001–M027（Major） | 资源/线程/日志/魔法值等 | ✅/N/A | 已扫无命中；无 I/O 资源、无线程池、无魔法值 |
| I001–I010（Info） | 风格/建议 | ✅/N/A | 已扫无命中 |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1–G1.4 | N/A | 无并发/事务/锁 |
| G2.1–G2.3 | N/A | 无写接口/MQ（GET 只读示例） |
| G3.1–G3.2 | N/A | 无 @Transactional |
| G4.1–G4.3 | N/A | 无 SQL |
| G5.1 | N/A | 无 MQ |
| G6.1–G6.2 | N/A | 无缓存 |
| G7.1–G7.2 | N/A | 无调度任务 |
| G8.1 | N/A | 无 catch/happy-path 异常路径 |
| G8.2 | N/A | 无外部依赖链路 |
| G8.3 | ✅ | 无 I/O 流/连接需释放 |
| G8.4 | N/A | 无线程池/定时任务 |
| G8.5 | N/A | 无 ThreadLocal |
| G8.6 | N/A | 无线程池 |
| G9.1–G9.3 | N/A | 无外部 RPC/HTTP 调用 |
| G10.1–G10.2 | ✅ | 返回固定字符串契约清晰，无 null 歧义 |
| G11.1 | ✅ | 新逻辑有单测且含断言 |
| G11.2 | N/A | 无边界值场景（固定返回） |
| G11.3 | N/A | 无入参，无空值防御需求 |
| G11.4 | N/A | 无数值运算 |
| G12.1–G12.2 | N/A | 无资金/资损场景 |
| G13.1 | N/A | 无日志埋点 |
| G14.1–G14.4 | N/A | 无金额/多租户/时区 |
| G15.1–G15.3 | N/A | 无 DB/接口兼容变更 |
| G16.1–G16.4 | N/A | 无异常路径/空 catch（脚本已扫无命中） |
| G17.1–G17.3 | N/A | 无开关/降级/回滚需求（示例接口） |
| G18.1–G18.3 | N/A | 安全补强：无 SQL/反序列化/敏感日志场景 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1–S1.3 | N/A | 无 SQL |
| S2.1–S2.3 | N/A | 返回固定字符串，无用户输入渲染 |
| S3.1–S3.3 | N/A | 无外部 URL 请求 |
| S4.1–S4.2 | N/A | 无命令执行 |
| S5.1–S5.2 | N/A | 无 XML 解析 |
| S6.1–S6.3 | N/A | 无反序列化 |
| S7.1–S7.3 | N/A | 无文件上传/下载 |
| S8.1 | N/A | 示例 helloworld 接口无敏感资源，鉴权不在 spec 范围 |
| S8.2 | ✅ | GET 只读，无增删改 |
| S8.3–S8.4 | N/A | 无数据 ID/Cookie |
| S9.1–S9.4 | N/A | 无密钥/敏感日志/传输加密场景 |
| S10.1–S10.3 | N/A | 无增删改 CSRF/CORS/跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

### 5.1 自定义扩展（`customized-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | N/A | 示例项；`customized-checklist.md` 仅含示例项，未启用自定义规则 |
| U1.2 | N/A | 未启用自定义规则 |
| U1.3 | N/A | 未启用自定义规则 |
| U2.1–U2.3 | N/A | 未启用自定义规则 |

> `customized-checklist.md` 仅有示例项（U1.1）且 U2 节为空，整节 `N/A(未启用自定义规则)`。

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、**S1–S10 / G1–G17** 各列均非 `⬜`（跳过文件除外）；
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`（允许 `N/A`，但有原因）
- [x] Step 5 全部 U* ID 均非 `⬜`（`N/A(未启用自定义规则)`）
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`
