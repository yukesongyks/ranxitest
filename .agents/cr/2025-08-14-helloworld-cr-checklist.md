# Code Review Checklist

> **Change** helloworld · **分支/Commit** `AI/task-DEV-f4ad1a6e-...` / `374d9d8` · **日期** `2025-08-14`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。

---

## Step 1 — 执行队列（产物 A）

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|-----|----|----|----|----|----|----|----|----|----|-----|-----|-----|-----|-----|-----|-----|-----|--------|
| 1 | `my-spring-boot-app/src/main/java/com/example/myapp/controllers/HelloWorldController.java` | REQ-1: HelloWorld端点 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | ✅ 已审 |
| 2 | `my-spring-boot-app/src/test/java/com/example/myapp/controllers/HelloWorldControllerTest.java` | REQ-2: HelloWorld测试 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 3 | `library-frontend/src/router/index.ts` | 非Java | 跳过 | 跳过 | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | 跳过 |
| 4 | `library-frontend/src/views/HelloWorldView.vue` | 非Java | 跳过 | 跳过 | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | 跳过 |

---

## Step 2 — 功能（产物 B）

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | 提供返回 "Hello, World!" 的 REST 端点 | 需求：帮我在每个仓库写个helloworld | `HelloWorldController.java` | ✅ | `HelloWorldController.java:18-19` — `@GetMapping("/api/hello")` + `return "Hello, World!";` |
| REQ-2 | 端点测试覆盖 200 响应与内容校验 | 需求：帮我在每个仓库写个helloworld | `HelloWorldControllerTest.java` | ✅ | `HelloWorldControllerTest.java:24-27` — `mockMvc.perform(get("/api/hello")).andExpect(status().isOk()).andExpect(content().string("Hello, World!"));` |

---

## Step 3 — 可读性检查（产物 C）

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | 文件名与类名一致，UTF-8编码 |
| A2 | 源文件结构/import 顺序 | ✅ | package → import → class，无 `import *` |
| A3 | 代码样式 | ✅ | K&R大括号，4空格缩进，行宽≤120 |
| A4 | 命名规范 | ✅ | 包名小写，类名UpperCamelCase，方法名lowerCamelCase，测试类名 `HelloWorldControllerTest` |
| A5 | 编码实践 | ✅ | 无重写方法，无catch块，无静态方法实例调用 |
| A6 | 特定元素样式 | ✅ | 无数组/switch/long字面量 |
| A7 | Javadoc 规范 | ✅ | public类和方法均有Javadoc，含 `@return` |

> 脚本预扫 Step3 部分：无命中。

---

## Step 4 — 可靠性检查（产物 D）

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> 预扫结果：`scan-all-rules.sh` 对 2 个 Java 文件扫描 52/222 条规则，**No findings**。

| ID | 状态 | 备注 |
|----|------|------|
| B001–B081 | N/A | 代码极简（无分支/无循环/无资源操作），规则不适用 |
| M001–M027 | N/A | 代码极简，规则不适用 |
| I001–I010 | N/A | 代码极简，规则不适用 |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1–G1.4 | N/A | 无并发/锁操作 |
| G2.1–G2.3 | N/A | GET只读端点，无写操作 |
| G3.1–G3.2 | N/A | 无事务 |
| G4.1–G4.3 | N/A | 无SQL |
| G5.1 | N/A | 无MQ |
| G6.1–G6.2 | N/A | 无缓存 |
| G7.1–G7.2 | N/A | 无调度任务 |
| G8.1–G8.6 | N/A | 无I/O流/线程池/ThreadLocal，无异常处理路径 |
| G9.1–G9.3 | N/A | 无外部RPC/HTTP调用 |
| G10.1–G10.2 | N/A | 简单字符串返回，无复杂契约 |
| G11.1 | ✅ | 测试类存在，含断言 `HelloWorldControllerTest.java:26-27` |
| G11.2 | N/A | 无参端点，无边界的空值/最大值场景 |
| G11.3 | N/A | 无入参 |
| G11.4 | N/A | 无数值运算 |
| G12.1–G12.2 | N/A | 非资金场景 |
| G13.1 | N/A | 无日志输出 |
| G14.1–G14.4 | N/A | 无金额/多租户/时区处理 |
| G15.1–G15.3 | N/A | 无DB变更/接口共存 |
| G16.1 | ⚠️ P2 | `HelloWorldController.java:17-20` — 核心链路 `/api/hello` 无指标埋点（成功率/耗时），但 HelloWorld 场景可接受 |
| G16.2 | ⚠️ P2 | `HelloWorldController.java:18-19` — 异常路径无日志；当前无异常处理逻辑，但若未来扩展需注意 |
| G16.3 | N/A | 无日志输出 |
| G16.4 | N/A | 无catch块 |
| G17.1–G17.3 | N/A | 无开关/降级/回滚需求 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1–S1.3 | N/A | 无SQL |
| S2.1–S2.3 | N/A | 无HTML/JS输出 |
| S3.1–S3.3 | N/A | 无外部URL请求 |
| S4.1–S4.2 | N/A | 无命令执行 |
| S5.1–S5.2 | N/A | 无XML解析 |
| S6.1–S6.3 | N/A | 无反序列化 |
| S7.1–S7.3 | N/A | 无文件上传/下载 |
| S8.1 | ⚠️ P2 | `HelloWorldController.java:17` — `/api/hello` 未接入鉴权，HelloWorld 演示场景可接受 |
| S8.2 | ✅ | 使用 GET 方法，符合 REST 语义（只读查询） |
| S8.3–S8.4 | N/A | 无数据ID/Cookie操作 |
| S9.1–S9.4 | N/A | 无密钥/敏感数据/加密 |
| S10.1–S10.3 | N/A | 无CSRF/CORS/跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

### 5.1 自定义扩展（`customized-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | N/A | 示例项：Controller 无入参，无需 `@Valid` |
| U1.2–U1.3 | N/A | 未启用自定义规则 |
| U2.1–U2.3 | N/A | 未启用自定义规则 |

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、**S1–S10 / G1–G17** 各列均非 `⬜`
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`
- [x] Step 5 全部 U* ID 均非 `⬜`
- [x] 所有 `⚠️` 已标注，含 `ID + path:line`