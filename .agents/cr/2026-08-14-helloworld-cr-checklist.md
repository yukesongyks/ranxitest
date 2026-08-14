# Code Review Checklist

> **Change** `helloworld` · **分支/Commit** `AI/task-DEV-f4ad1a6e-...` / `8bdf2f9` · **日期** `2026-08-14`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **执行顺序（强制）**：已运行 `scan-all-rules.sh`（52/222 规则，无命中），输出已并入 Step 4 备注。

---

## Step 1 — 执行队列（产物 A）

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|-----|----|----|----|----|----|----|----|----|----|-----|-----|-----|-----|-----|-----|-----|-----|--------|
| 1 | `my-spring-boot-app/src/main/java/com/example/myapp/controllers/HelloController.java` | REQ-1/REQ-2 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | ✅ |
| 2 | `my-spring-boot-app/src/main/java/com/example/myapp/services/HelloService.java` | REQ-3 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 3 | `my-spring-boot-app/src/main/java/com/example/myapp/services/impl/HelloServiceImpl.java` | REQ-4 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 4 | `my-spring-boot-app/src/test/java/com/example/myapp/controllers/HelloControllerTest.java` | REQ-1/REQ-4 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ |

---

## Step 2 — 功能（产物 B）

> 仅从 spec（`docs/modules/hello/README.md`）提取 REQ，勿臆造。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | GET /api/hello 返回 200 OK + "Hello, World!" | README.md L24: `GET \| /api/hello \| 返回问候语 \| 200 OK - Hello, World!` | HelloController.java, HelloControllerTest.java | ✅ | HelloController.java:28-31 `@GetMapping("/hello")`; HelloControllerTest.java:35-37 `status().isOk()` + `content().string("Hello, World!")` |
| REQ-2 | HelloController 暴露 GET /api/hello 端点 | README.md L11: `HelloController \| REST 控制器 \| 暴露 GET /api/hello 端点` | HelloController.java | ✅ | HelloController.java:12-14 `@RestController` + `@RequestMapping("/api")`; L28-29 `@GetMapping("/hello")` |
| REQ-3 | HelloService 定义问候语业务契约 | README.md L12: `HelloService \| 服务接口 \| 定义问候语业务契约` | HelloService.java | ✅ | HelloService.java:13 `String greet();` |
| REQ-4 | HelloServiceImpl 返回 "Hello, World!" | README.md L13: `HelloServiceImpl \| 服务实现 \| 返回 "Hello, World!"` | HelloServiceImpl.java | ✅ | HelloServiceImpl.java:13-14 `return "Hello, World!";` |

---

## Step 3 — 可读性检查（产物 C）

对照 `references/readability-checklist.md` A1–A7 逐节核销：

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | 所有 Java 文件名=类名.java，UTF-8，无 Tab |
| A2 | 源文件结构/import 顺序 | ✅ | 无 `import *`；静态 import 分组正确（HelloControllerTest.java:11-14）；非静态 import 分组正确 |
| A3 | 代码样式 | ✅ | K&R 大括号，4空格缩进，行宽≤120，类成员间有空行 |
| A4 | 命名规范 | ✅ | 包名全小写，类名 UpperCamelCase，方法 lowerCamelCase，测试类名 `HelloControllerTest` 符合 A4.7 |
| A5 | 编码实践 | ✅ | HelloServiceImpl.java:12 `@Override` 正确使用；无空 catch；无 finalize() |
| A6 | 特定元素样式 | ✅ | 无数组、switch、long 字面量；注解每行一个 |
| A7 | Javadoc 规范 | ✅ | 所有 public 类/方法均有 Javadoc（HelloController.java:9-11,23-27; HelloService.java:3-5,8-12; HelloServiceImpl.java:6-8; HelloControllerTest.java:16-18） |

---

## Step 4 — 可靠性检查（产物 D）

> **逐条核销（强制）**：已运行 `scan-all-rules.sh`（52/222 规则，**无命中**）。

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> 脚本预扫 52/222 规则无命中。LLM 逐条核对变更范围内无触发的 B/M/I 规则——本次为纯新增 HelloWorld 代码，无空指针、资源泄漏、集合操作、异常处理等模式。

| ID | 状态 | 备注 |
|----|------|------|
| B001–B081 | N/A | 本次变更无对应模式（无异常处理、集合操作、资源管理等） |
| M001–M027 | N/A | 本次变更无对应模式 |
| I001–I010 | N/A | 本次变更无对应模式 |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1–G1.4 | N/A | 无并发/锁场景 |
| G2.1–G2.3 | N/A | 无写接口/消息消费 |
| G3.1–G3.2 | N/A | 无事务 |
| G4.1–G4.3 | N/A | 无 SQL/数据库操作 |
| G5.1 | N/A | 无 MQ |
| G6.1–G6.2 | N/A | 无缓存 |
| G7.1–G7.2 | N/A | 无调度任务 |
| G8.1 | ✅ | 无 catch 吞异常；简单 controller 仅 happy path 但 Spring 框架兜底 |
| G8.2 | N/A | 无外部依赖调用 |
| G8.3 | N/A | 无 I/O 流/连接/锁 |
| G8.4 | N/A | 无线程池/定时任务 |
| G8.5 | N/A | 无 ThreadLocal |
| G8.6 | N/A | 无自定义线程池 |
| G9.1–G9.3 | N/A | 无外部 RPC/HTTP 调用 |
| G10.1–G10.2 | N/A | 无复杂接口契约 |
| G11.1 | ✅ | 有单测且含断言：HelloControllerTest.java:35-37 |
| G11.2 | ⚠️ | 测试覆盖正常+自定义消息，但**未覆盖空/异常边界**（如 service 返回 null/抛异常时 Controller 行为） |
| G11.3 | N/A | 无入参 |
| G11.4 | N/A | 无数值运算 |
| G12.1–G12.2 | N/A | 无资损场景 |
| G13.1 | N/A | 无日志输出 |
| G14.1–G14.4 | N/A | 无金额/多租户/时区操作 |
| G15.1–G15.3 | N/A | 无数据库变更/接口升级 |
| G16.1–G16.4 | N/A | 无监控埋点/日志；HelloController 无异常路径 |
| G17.1–G17.3 | N/A | 无功能开关/降级需求 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1–S1.3 | N/A | 无 SQL |
| S2.1–S2.3 | N/A | 无 HTML/JS 输出（返回纯文本 `text/plain`） |
| S3.1–S3.3 | N/A | 无外部 URL 请求 |
| S4.1–S4.2 | N/A | 无系统命令 |
| S5.1–S5.2 | N/A | 无 XML 解析 |
| S6.1–S6.3 | N/A | 无反序列化 |
| S7.1–S7.3 | N/A | 无文件上传/下载 |
| S8.1 | ✅ | 无鉴权需求（HelloWorld 演示端点）；`@GetMapping` 为只读操作，符合 S8.2 |
| S8.2 | ✅ | GET 方法仅读取，无增删改 |
| S8.3 | N/A | 无数据 ID |
| S8.4 | N/A | 无 Cookie 操作 |
| S9.1–S9.4 | N/A | 无密钥/敏感数据/加密 |
| S10.1–S10.3 | N/A | 无 CSRF/CORS/跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

### 5.1 自定义扩展（`customized-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | N/A | 仅示例项；HelloController 无入参，无需 `@Valid` |
| U1.2 | N/A | 未定义 |
| U1.3 | N/A | 未定义 |
| U2.1 | N/A | 未定义 |
| U2.2 | N/A | 未定义 |
| U2.3 | N/A | 未定义 |

> 整节标注：**N/A(未启用自定义规则)** — `customized-checklist.md` 仅含 U1.1 示例项，实际未启用团队自定义规则。

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、**S1–S10 / G1–G17** 各列均非 `⬜`
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`
- [x] Step 5 全部 U* ID 均非 `⬜`
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`