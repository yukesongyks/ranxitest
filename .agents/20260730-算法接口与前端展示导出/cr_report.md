# Code Review Report

> **Change** 算法接口与前端展示导出 · **分支** `AI/task-DEV-f4ad1a6e-...-8037206b` · **日期** 2026-07-30 · **审查者** AI
>
> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 `bug-pattern-checklist.md` 表头为准（Blocker→P0、Major→P1、Info→P2）。已运行 `scan-all-rules.sh`（52/222 条可程序化规则），预扫结果已并入 §5，LLM 复核完成。问题含 `path:line` 或清单 ID。每个 ❌/⚠️ 问题在 §7.1 附 `.java` 代码片段。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | 12 |
| 模板文件 | 1（`show.html`，非 Java，可读性/安全侧面审查） |
| 预扫脚本 | `scan-all-rules.sh`（52/222 条规则，命中变更文件 7 条 + 既有文件 11 条） |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| `AlgorithmController` | `my-spring-boot-app/.../controllers/AlgorithmController.java` | I01-I03 REST 接口 |
| `AlgorithmPageController` | `my-spring-boot-app/.../controllers/AlgorithmPageController.java` | I05 Thymeleaf 视图路由 |
| `ExportController` | `my-spring-boot-app/.../controllers/ExportController.java` | I04 文件流导出 |
| `AlgorithmService` | `my-spring-boot-app/.../services/AlgorithmService.java` | 算法逻辑（hash/bubbleSort/hello） |
| `ExportService` | `my-spring-boot-app/.../services/ExportService.java` | 结果序列化（CSV/JSON） |
| `BizException` | `my-spring-boot-app/.../exception/BizException.java` | 业务异常（code+message） |
| `ApiResult` | `my-spring-boot-app/.../models/dto/ApiResult.java` | 通用出参 `{code,msg,data}` |
| `HashRequest` | `my-spring-boot-app/.../models/dto/HashRequest.java` | I02 入参 DTO |
| `HashResult` | `my-spring-boot-app/.../models/dto/HashResult.java` | I02 出参 DTO |
| `BubbleSortRequest` | `my-spring-boot-app/.../models/dto/BubbleSortRequest.java` | I03 入参 DTO |
| `BubbleSortResult` | `my-spring-boot-app/.../models/dto/BubbleSortResult.java` | I03 出参 DTO |
| `HelloWorldResult` | `my-spring-boot-app/.../models/dto/HelloWorldResult.java` | I01 出参 DTO |
| `show.html` | `my-spring-boot-app/.../templates/algorithm/show.html` | 前端三 Tab 页面 |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 2 | 2 | 4 |

---

## 3. Step 2 — 功能（REQ）

> REQ 来源：`design.md` §1.2 核心功能 F01–F05 + §4 接口设计 I01–I05 + §5 功能模块设计。

### REQ-F01: HelloWorld 接口

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| GET `/api/algorithm/helloworld` 返回固定问候串 | ✅ | design I01: `出参 data.message = "Hello, World!"` | `AlgorithmController.java:33` → `AlgorithmService.hello():24` → `HelloWorldResult("Hello, World!")` | 路径、出参结构、固定文本均与设计一致 |

### REQ-F02: 哈希算法接口

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| POST `/api/algorithm/hash` 接收 text+algorithm 输出摘要 | ✅ | design I02: `出参 {algorithm, digest, length}` | `AlgorithmController.java:44` → `AlgorithmService.hash():34` | 入参 `@Valid HashRequest` 含 `@NotBlank`+`@Size(max=10000)`，出参结构一致 |
| algorithm 非法时回退 SHA-256（R01） | ✅ | design R01 + 异常场景表「非法算法→回退默认」 | `AlgorithmService.resolveAlgorithm():120` 非法回退 `DEFAULT_HASH_ALGORITHM` | 设计 R01 与错误表 ALGO_003 存在二义性（回退 vs 拒绝），代码选择回退，与 R01 一致 |
| 超长输入拒绝 ALGO_002 | ✅ | design I02 错误码表 | `AlgorithmService.java:39` `text.length() > 10000` → BizException(2) | 双层校验：DTO `@Size` + Service 显式校验 |

### REQ-F03: 冒泡排序接口

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| POST `/api/algorithm/bubble-sort` 接收数组输出排序结果 | ✅ | design I03: `出参 {sorted, swaps, costMs, original}` | `AlgorithmController.java:57` → `AlgorithmService.bubbleSort():56` | 出参四个字段齐全 |
| order 默认 ASC（R02） | ✅ | design R02 | `AlgorithmService.resolveOrder():130` | 空值回退 ASC |
| 交换次数 + 提前终止 | ✅ | design 5.1.4「若某轮无交换则提前终止」 | `AlgorithmService.bubbleSortInPlace():115` `swapped` 标志 | 实现正确 |
| ⚠️ null 元素致 NPE | ❌ | design G11.3: 入参空值无防御性校验=P0 | `AlgorithmService.java:95` `work[i] = original.get(i)` | JSON `[1,null,3]` 通过 DTO 校验后此处自动拆箱 NPE（见 §7.1） |

### REQ-F04: 前端展示页面

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| GET `/algorithm` 返回三 Tab 页面 | ✅ | design I05 + 5.3.1 页面结构 | `AlgorithmPageController.java:15` → `algorithm/show`; `show.html` 三 Tab（helloworld/hash/bubble） | 路由、Tab 结构、导出按钮齐全 |
| Tab 切换 fetch 渲染 | ✅ | design 5.3.3 时序图 | `show.html` `switchTab()`/`runHelloWorld()`/`runHash()`/`runBubbleSort()` | 原生 JS fetch AJAX，与设计 A04 一致 |
| 前端 XSS 防护 | ✅ | design S2.1: 输出按场景编码 | `show.html` `escapeHtml()` 对 `data.data.*` 转义 | 已对 `& < > "` 转义 |

### REQ-F05: 导出接口

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| GET `/api/algorithm/export` 按 type/format 导出 | ✅ | design I04: `type/format/text/algorithm/array/order` | `ExportController.java:36` 六参数齐全 | CSV/JSON 双格式支持 |
| 导出与展示同源（R05） | ✅ | design R05 | `ExportService` 复用 `AlgorithmService` 计算 | 一致性保证 |
| 文件名时间戳防注入 | ✅ | design 6 安全性: 文件名不含用户输入 | `ExportController.java:67` `algorithm-{type}-{timestamp}` | type 经白名单校验，时间戳为系统生成 |
| ⚠️ 错误响应 JSON 未转义 | ❌ | design S2.1: JSON 输出按场景编码=P0 | `ExportController.java:101` `writeJsonError` 拼接 msg 未转义 | msg 含用户输入时可破坏 JSON 结构（见 §7.1） |

---

## 4. Step 3 — 可读性检查

> 对照 `references/readability-checklist.md` A1–A7 逐节核销。

| ID | 检查项 | 状态 | 备注 |
|----|--------|------|------|
| A1 | 源文件格式 | ✅ | 文件名=类名+.java，UTF-8，无 Tab |
| A2 | 源文件结构/import 顺序 | ✅ | 无通配符 import；package→import→类顺序正确；静态/非静态分组正确 |
| A3 | 代码样式 | ⚠️ | `AlgorithmController.java:43` 行宽略超 120 字符（`@PostMapping("/bubble-sort")` + 注解）；整体 K&R 大括号、4 空格缩进合规 |
| A4 | 命名规范 | ✅ | 包名全小写、类名 UpperCamelCase、方法 lowerCamelCase、常量 UPPER_SNAKE_CASE |
| A5 | 编码实践 | ✅ | 无空 catch（均有处理）；无 `finalize()` 重写 |
| A6 | 特定元素样式 | ✅ | 数组方括号属类型；switch 有 default；注解每行一个 |
| A7 | Javadoc 规范 | ✅ | public 类/方法均有 Javadoc 注释；`@param`/`@return`/`@throws` 顺序正确 |

**结论**：可读性整体良好，1 处 P2 行宽微超限。

---

## 5. Step 4 — 可靠性检查

> **预扫**：已运行 `scan-all-rules.sh`（52/222 条规则），命中变更文件 7 条（B022×1, G16.2×6）+ P2×2。LLM 逐文件复核完成误报排除与未覆盖项补全。

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 可靠性 | `reliability-checklist.md` G1–G17 | ❌ | P0–P2 | G11.3 P0 命中（null 拆箱）；G16.2 P1 命中×6（catch 无日志）；G10.2 P1 命中（错误码契约） |
| 安全 | `security-checklist.md` S1–S10 | ❌ | P0 | S2.1 P0 命中（JSON 输出未转义）；S1/S3–S10 N/A（无 SQL/SSRF/XXE/反序列化/文件上传） |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120） | ⚠️ | P2 | 预扫 B022 误报（局部变量非 static）；I004 P2 有效（建议 java.time） |

### 5.1 预扫结果（scan-all-rules.sh）

变更文件命中（排除既有文件 ItemController/ProfileController/GlobalExceptionHandler/ItemService）：

| 规则 ID | 等级(预扫) | 等级(LLM复核) | 文件:行 | LLM 判定 |
|---------|-----------|--------------|---------|----------|
| B022 | P0 | P2(误报) | `ExportController.java:66` | **误报**：B022 针对 `static SimpleDateFormat`，此处为方法局部变量，无线程安全风险；I004 建议用 `DateTimeFormatter` 有效(P2) |
| G16.2 | P0 | P1 | `AlgorithmController.java:51` | **等级修正**：G16.2 清单定义为 P1（非 P0）；catch 处理了异常但无日志 |
| G16.2 | P0 | P1 | `AlgorithmController.java:64` | 同上 |
| G16.2 | P0 | P1 | `ExportController.java:78` | 同上 |
| G16.2 | P0 | P1 | `ExportController.java:81` | 同上 |
| G16.2 | P0 | P1 | `ExportController.java:84` | 同上 |
| G16.2 | P0 | P1 | `AlgorithmService.java:62` | 同上 |

### 5.2 LLM 补充发现（脚本未覆盖）

| 规则 ID | 等级 | 文件:行 | 简述 |
|---------|------|---------|------|
| G11.3 | P0 | `AlgorithmService.java:95` | `List<Integer>` 元素为 null 时自动拆箱致 NPE；DTO `@NotEmpty` 不防 null 元素，Service bounds 校验跳过 null 但拆箱处未防御 |
| S2.1 | P0 | `ExportController.java:101` | `writeJsonError` 手拼 JSON `{"code":...,"msg":"..."}` 中 msg 未转义；BizException message 含用户输入 `type` 值时可注入 `"` 破坏 JSON |
| G10.2 | P1 | 全局 | 设计 §5.0 错误码格式 `{MODULE}_{SEQ}`（如 `ALGO_001`），实现用数字 code + msg 内嵌语义码；契约不一致 |
| G11.1 | P2 | 全局 | 新算法逻辑（hash/bubbleSort）无单元测试 |
| — | P2 | `AlgorithmService.java:165` | `toHex` 用 `String.format("%02x", b)` 逐字节，效率低；可用查表或 `HexFormat.of()` |

### 5.3 Bug 模式逐条核销（变更相关子集）

> 仅列出与变更代码相关的 B/M/I 规则，无关项标 N/A 并在备注写原因。

| ID | 状态 | 备注 |
|----|------|------|
| B001 | ✅ | 无 `LocalDateTime.parse`/`UUID.fromString` 字面量调用 |
| B002–B005 | N/A | 无数组 `equals`/`fill`/`toString`/`asList` 使用 |
| B008 | ✅ | 无 `Executors` 线程池创建 |
| B010 | ✅ | 无 `new BigDecimal(double)` |
| B011 | ✅ | 无包装类型 `==` 比较 |
| B022 | ⚠️ | `ExportController.java:66` `new SimpleDateFormat` 局部变量；B022 针对静态实例，判定为**误报**；I004 有效(P2) |
| B023 | ✅ | 无未抛出的异常实例 |
| B024 | N/A | 无 `new Thread()` 未 start |
| B025–B081 | N/A | 与本变更代码无关（无 Calendar/Collection 泛型错误/Money/Jedis/ThreadLocal 等场景） |
| M001–M027 | N/A | 与本变更代码无关 |
| I001–I010 | ⚠️ | I004 `ExportController.java:66` 使用 `java.util.Date`+`SimpleDateFormat`，建议改用 `java.time.LocalDateTime`+`DateTimeFormatter`(P2) |
| 其余 B/M/I | N/A | 与变更无关 |

### 5.4 可靠性/安全逐条核销

| ID | 状态 | 备注 |
|----|------|------|
| G1 并发 | N/A(无共享可变态) | 无状态纯函数，design 已声明线程安全 |
| G2 幂等 | N/A(无写操作) | 算法为只读计算，无落库 |
| G3 事务 | N/A(无 @Transactional) | 无事务使用 |
| G4 SQL | N/A(无 SQL) | 无持久化 |
| G5 MQ | N/A(无消息) | 无 MQ |
| G6 缓存 | N/A(无缓存) | 无缓存 |
| G7 调度 | N/A(无定时任务) | 无调度 |
| G8.3 资源释放 | ✅ | `response.getWriter()` 由容器管理，无需手动 close |
| G8.6 线程池 | N/A(无线程池) | 无线程池 |
| G9 网络调用 | N/A(无外部调用) | 全内存计算 |
| G10.1 契约 null 语义 | ✅ | 出参无 null 兼表示「无数据」与「异常」的歧义 |
| G10.2 契约变更 | ⚠️ | 错误码实现与设计 `ALGO_xxx` 格式不一致(P1) |
| G11.1 单测 | ⚠️ | 新逻辑无单元测试(P2) |
| G11.2 边界覆盖 | ⚠️ | 未覆盖 null 元素边界(P0 关联) |
| G11.3 入参防御 | ❌ | `AlgorithmService.java:95` null 元素无防御(P0) |
| G11.4 数值运算 | ✅ | `Math.abs((long) num)` 正确处理 int 范围；无金额运算 |
| G12 资损 | N/A(无资金场景) | 演示功能 |
| G13 监控 | N/A(无监控埋点) | design §7 声明可监控但未实现埋点 |
| G16.2 异常日志 | ⚠️ | 6 处 catch 无日志(P1) |
| G16.4 空 catch | ✅ | 无空 catch（均有 writeJsonError 或 rethrow） |
| S2.1 输出编码 | ❌ | `ExportController.java:101` writeJsonError msg 未 JSON 转义(P0) |
| S8.2 GET 执行增删改 | ✅ | 导出为 GET 但仅读计算（下载），非增删改 |
| S9.2 敏感信息日志 | ✅ | 哈希输入不落库不记日志（design §6 已声明） |
| S10 CSRF | N/A(演示无鉴权) | design §1.4 排除鉴权 |

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | `customized-checklist.md` U* | N/A(未启用自定义规则) | — | 项目未维护自定义 checklist，标 N/A |

---

## 7. 结论

- **合并建议**：修复后合并
- **P0**：
  1. `AlgorithmService.java:95` — `List<Integer>` null 元素自动拆箱致 NPE（G11.3）
  2. `ExportController.java:101` — `writeJsonError` 中 msg 未 JSON 转义可注入（S2.1）
- **P1/P2**：
  1. P1 — G16.2 × 6 处 catch 块无日志记录（AlgorithmController/ExportController/AlgorithmService）
  2. P1 — G10.2 错误码数字 vs 设计 `ALGO_xxx`/`EXPORT_xxx` 格式契约不一致
  3. P2 — I004 `ExportController.java:66` 建议用 `DateTimeFormatter` 替代 `SimpleDateFormat`+`Date`
  4. P2 — G11.1 新算法逻辑无单元测试
  5. P2 — A3.4 `AlgorithmController.java:43` 行宽微超 120 字符
  6. P2 — `AlgorithmService.toHex` 逐字节 `String.format` 效率低
- **一句话**：功能与系分设计 REQ 基本对齐，但存在 2 个 P0 阻断项（null 拆箱 NPE + JSON 注入），须修复后方可合并。

---

## 7.1 问题片段（必填）

### P0-1: G11.3 — null 元素自动拆箱 NPE

- **P0** `G11.3` `my-spring-boot-app/src/main/java/com/example/myapp/services/AlgorithmService.java:95` — `List<Integer>` 元素为 null 时 `original.get(i)` 自动拆箱为 int 致 NPE；DTO `@NotEmpty` 不防 null 元素，bounds 校验循环用 `num != null` 跳过 null 但拆箱处未防御。
  片段范围：`my-spring-boot-app/src/main/java/com/example/myapp/services/AlgorithmService.java:84-96`

```java
L84|        // 校验元素越界（ALGO_006）
L85|        for (Integer num : array) {
L86|            if (num != null && Math.abs((long) num) > MAX_ARRAY_ELEMENT) {
L87|                throw new BizException(6, "ALGO_006: 数组元素绝对值不能超过 " + MAX_ARRAY_ELEMENT);
L88|            }
L89|        }
L90|
L91|        List<Integer> original = new ArrayList<>(array);
L92|        int[] work = new int[original.size()];
L93|        for (int i = 0; i < original.size(); i++) {
L94|            work[i] = original.get(i);  // ← P0: null Integer 自动拆箱为 int 抛 NPE
L95|        }
```

### P0-2: S2.1 — writeJsonError JSON 注入

- **P0** `S2.1` `my-spring-boot-app/src/main/java/com/example/myapp/controllers/ExportController.java:101` — `writeJsonError` 手拼 JSON `{"code":...,"msg":"..."}` 中 `msg` 未 JSON 转义；`resolveType` 将用户输入 `type` 拼入 BizException message，`type=bad"` 可注入 `"` 破坏 JSON 结构。
  片段范围：`my-spring-boot-app/src/main/java/com/example/myapp/controllers/ExportController.java:99-102`

```java
L99|    private void writeJsonError(HttpServletResponse response, int status, int code, String msg) throws IOException {
L100|       response.setContentType("application/json;charset=UTF-8");
L101|       response.setStatus(status);
L102|       response.getWriter().write("{\"code\":" + code + ",\"msg\":\"" + msg + "\"}");  // ← P0: msg 未转义
L103|   }
```

### P1-1: G16.2 — catch 块无日志（AlgorithmController 示例）

- **P1** `G16.2` `my-spring-boot-app/src/main/java/com/example/myapp/controllers/AlgorithmController.java:46-52` — catch BizException 仅返回 error 响应，无日志记录，异常路径无可追溯上下文（traceId/bizId）。
  片段范围：`my-spring-boot-app/src/main/java/com/example/myapp/controllers/AlgorithmController.java:44-52`

```java
L44|    @PostMapping("/hash")
L45|    public ApiResult<HashResult> hash(@Valid @RequestBody HashRequest request) {
L46|        try {
L47|            HashResult result = algorithmService.hash(request.getText(), request.getAlgorithm());
L48|            return ApiResult.success(result);
L49|        } catch (BizException e) {
L50|            return ApiResult.error(e.getCode(), e.getMessage());  // ← P1: 无 log.warn/error
L51|        }
L52|    }
```

### P1-2: G16.2 — catch 块无日志（ExportController 示例）

- **P1** `G16.2` `my-spring-boot-app/src/main/java/com/example/myapp/controllers/ExportController.java:78-87` — 三处 catch（BizException/NumberFormatException/Exception）均直接 writeJsonError，无任何日志输出。
  片段范围：`my-spring-boot-app/src/main/java/com/example/myapp/controllers/ExportController.java:78-87`

```java
L78|        } catch (BizException e) {
L79|            writeJsonError(response, HttpServletResponse.SC_BAD_REQUEST,
L80|                    e.getCode(), e.getMessage());  // ← P1: 无 log
L81|        } catch (NumberFormatException e) {
L82|            writeJsonError(response, HttpServletResponse.SC_BAD_REQUEST,
L83|                    3, "EXPORT_003: array 包含非整数: " + e.getMessage());  // ← P1: 无 log
L84|        } catch (Exception e) {
L85|            writeJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
L86|                    999, "EXPORT_999: 导出异常: " + e.getMessage());  // ← P1: 无 log
L87|        }
```

### P2-1: I004 — SimpleDateFormat + java.util.Date

- **P2** `I004` `my-spring-boot-app/src/main/java/com/example/myapp/controllers/ExportController.java:66` — 使用 `new SimpleDateFormat` + `new Date()`；虽然为局部变量（B022 线程安全误报），建议改用 `java.time.LocalDateTime.now()` + `DateTimeFormatter`。
  片段范围：`my-spring-boot-app/src/main/java/com/example/myapp/controllers/ExportController.java:65-67`

```java
L65|            String fileExtension = resolvedFormat.toLowerCase();
L66|            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());  // ← P2: 建议用 DateTimeFormatter
L67|            String fileName = "algorithm-" + resolvedType + "-" + timestamp + "." + fileExtension;
```

---

## 8. 修复任务列表

> 供后续 CR修复阶段逐项执行与核销。

### P0

- [ ] **P0** `my-spring-boot-app/src/main/java/com/example/myapp/services/AlgorithmService.java:94` — 拆箱前增加 null 元素校验：`Integer val = original.get(i); if (val == null) throw new BizException(8, "ALGO_008: 数组元素不能为 null"); work[i] = val;`
- [ ] **P0** `S2.1` `my-spring-boot-app/src/main/java/com/example/myapp/controllers/ExportController.java:102` — writeJsonError 中 msg 调用 JSON 转义方法（或复用 ExportService.escapeJson）后再拼接

### P1

- [ ] **P1** `G16.2` `my-spring-boot-app/src/main/java/com/example/myapp/controllers/AlgorithmController.java:49-51,62-64` — catch BizException 块增加 `log.warn("算法接口业务异常 code={} msg={}", e.getCode(), e.getMessage())`
- [ ] **P1** `G16.2` `my-spring-boot-app/src/main/java/com/example/myapp/controllers/ExportController.java:78,81,84` — 三处 catch 块增加对应级别日志（BizException→WARN，Exception→ERROR）
- [ ] **P1** `G16.2` `my-spring-boot-app/src/main/java/com/example/myapp/services/AlgorithmService.java:62` — catch NoSuchAlgorithmException 增加 `log.error("哈希算法不可用: {}", normalizedAlg, e)` 后再 throw
- [ ] **P1** `G10.2` 全局 — 错误码实现与设计契约对齐：统一 `ALGO_xxx`/`EXPORT_xxx` 格式，或在 design.md 中明确 code 为数字序号、msg 内嵌语义码的设计取舍

### P2（可选）

- [ ] **P2** `I004` `my-spring-boot-app/src/main/java/com/example/myapp/controllers/ExportController.java:66` — 替换 `SimpleDateFormat`+`Date` 为 `DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now())`
- [ ] **P2** `G11.1` 全局 — 为 `AlgorithmService.hash`/`bubbleSort`/`hello` 补充单元测试，覆盖正常、空、超大、null 元素边界
- [ ] **P2** `A3.4` `my-spring-boot-app/src/main/java/com/example/myapp/controllers/AlgorithmController.java:43` — 拆分超长行或换行使行宽 ≤ 120
- [ ] **P2** `my-spring-boot-app/src/main/java/com/example/myapp/services/AlgorithmService.java:164-168` — `toHex` 用 `HexFormat.of().formatHex(bytes)` 或查表替代逐字节 `String.format`
