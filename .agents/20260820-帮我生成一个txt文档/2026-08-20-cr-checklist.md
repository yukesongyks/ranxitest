# Code Review Checklist

> **Change** TXT 文档生成与导出（帮我生成一个txt文档） · **分支/Commit** `AI/task-DEV-f4ad1a6e-7360-11f1-8c66-df5563d236aa-ba3312a6-036e-4ce3-9132-91ad15ed26d8` / `8a96197` · **日期** `2026-08-20`

> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。

---

## Step 1 — 文件列表与执行队列（产物 A）

| # | 文件（仓库相对路径） | 角色 | 归属原因 | 状态 |
|---|---------------------|------|----------|------|
| 1 | `my-spring-boot-app/src/main/java/com/example/myapp/controllers/ItemController.java` | 修改 | 注入 DocgenExportProperties + `@ModelAttribute` 导出开关 | ✅ 已审 |
| 2 | `my-spring-boot-app/src/main/java/com/example/myapp/controllers/ItemExportController.java` | 新增 | W01/O01 控制器 | ⚠️ 已审有问题（G16.1 P0） |
| 3 | `my-spring-boot-app/src/main/java/com/example/myapp/docgen/DocgenErrorCode.java` | 新增 | 错误码枚举 | ✅ 已审 |
| 4 | `my-spring-boot-app/src/main/java/com/example/myapp/docgen/DocgenExportException.java` | 新增 | 业务异常 | ✅ 已审 |
| 5 | `my-spring-boot-app/src/main/java/com/example/myapp/docgen/DocgenExportProperties.java` | 新增 | 导出配置（开关/timeout） | ⚠️ 已审有问题（timeoutMs 未使用） |
| 6 | `my-spring-boot-app/src/main/java/com/example/myapp/docgen/TxtExportOptions.java` | 新增 | 导出选项 | ✅ 已审 |
| 7 | `my-spring-boot-app/src/main/java/com/example/myapp/docgen/TxtExportService.java` | 新增 | S01/S03 + R01/R02 | ⚠️ 已审有问题（M016 P1） |
| 8 | `my-spring-boot-app/src/main/java/com/example/myapp/docgen/TxtRow.java` | 新增 | 行模型 | ✅ 已审 |
| 9 | `my-spring-boot-app/src/main/java/com/example/myapp/services/ItemExportService.java` | 新增 | S02 适配层 | ⚠️ 已审有问题（catch 过宽） |
| 10 | `my-spring-boot-app/src/test/java/com/example/myapp/controllers/ItemExportControllerTest.java` | 新增 | 控制器单测 | ⚠️ 已审有问题（A3.4、边界缺口） |
| 11 | `my-spring-boot-app/src/test/java/com/example/myapp/docgen/TxtExportServiceTest.java` | 新增 | Service 单测 | ✅ 已审 |
| 12 | `my-spring-boot-app/src/test/java/com/example/myapp/services/ItemExportServiceTest.java` | 新增 | 适配层单测 | ✅ 已审 |
| 13 | `my-spring-boot-app/src/main/resources/application.properties` | 修改 | 新增 `docgen.export.*` 配置 | 跳过（非 Java） |
| 14 | `my-spring-boot-app/src/main/resources/templates/items/list.html` | 修改 | 导出按钮 `th:if="${docgenExportEnabled}"` | 跳过（非 Java） |
| 15 | `.agents/20260820-帮我生成一个txt文档/impl.md` | 修改 | 编码实现报告 | 跳过（非 Java） |

Java 守卫：存在 12 个 `.java` 文件 ✅。预扫：`scan-all-rules.sh` 已执行（10 findings，见报告 §5）。

---

## Step 2 — 功能性检查（产物 B）

| REQ | 来源（design.md 章节） | 结果 | 说明 |
|-----|------------------------|------|------|
| F01 页面导出入口 | §1 功能清单 / §5.2.2 W01 | ✅ | `ItemExportController:76-92` + `list.html:201` |
| F02 OpenAPI 导出 | §1 F02 / §5.2.2 O01 | ✅ | `ItemExportController:101-134` |
| F03 内容格式化（表头/数据/汇总、UTF-8、CRLF） | §1 F03 / §5.2.2 W01 出参 | ✅ | `TxtExportService:36-57`，默认 CRLF、UTF-8 |
| F04 通用 TXT 生成服务 | §1 F04 / §4.3 S01 | ✅ | `TxtExportService.exportTxt(List<TxtRow>, TxtExportOptions)` |
| S02 物品行组装（ID/名称/描述/价格，两位小数） | §4.3 / §5.3 | ✅ | `ItemExportService.buildRows()`，`formatPrice` HALF_UP |
| S03 文件名 `items-yyyyMMdd-HHmmss.txt` | §4.3 S03 / §5.2.3 R03 | ✅ | `TxtExportService.buildFileName` |
| R01 1 万行 / 10MB 超限 → DOCGEN_002 | §5.2.3 R01 | ✅ | `TxtExportService:40,50-55,70-76` |
| R02 转义 `\t\r\n` → 空格 | §5.2.3 R02 | ✅ | `TxtExportService:94-108`（含 null → 空串） |
| R03 文件名白名单防路径穿越 | §5.2.3 R03 | ⚠️ | 依赖调用点常量 `FILE_NAME_PREFIX` 约定满足，`buildFileName` 无前缀参数校验（P2） |
| 错误码 DOCGEN_001/002/003 | §5.1 错误码清单 | ✅ | `DocgenErrorCode:11-17`，O01 失败 JSON `{result,msg,data}` |
| O01 参数契约（limit 默认 10000/最大 100000；encoding 仅 utf-8/gbk） | §5.2.2 O01 | ✅ | `ItemExportController:107-116`；`limit 与默认上限取较小值`：`117` 截断 |
| 空数据 → 表头+「共 0 条记录」 | §5.2.3 异常场景 | ✅ | `TxtExportServiceTest:92-103` |
| 7.3 开关（false → W01 503/入口隐藏、O01 维护中） | §7.3 | ✅ | `ItemExportController:78-80,104-106,155-161`；`DocgenExportProperties`；`list.html th:if` |
| 6.5/7.1 监控埋点与请求摘要日志 | §6.5 / §7.1 | ❌ | G16.1：成功路径无请求量/成功数/耗时/行数/体积埋点与日志（P0，见报告 §5） |

---

## Step 3 — 可读性检查（产物 C）

| A-ID | 结果 | 说明 |
|------|------|------|
| A1 源文件格式 | ✅ | 新文件 UTF-8、无 Tab（扫描 A1.3 未命中） |
| A2 源文件结构 | ⚠️ | `ItemController.java:14` `import org.springframework.web.bind.annotation.*`（A2.2 扫描命中）——**既有代码，非本次变更引入**；`TxtExportServiceTest` 使用全限定名 `java.util.List`/`java.util.Arrays`（风格 P2） |
| A3 代码样式 | ⚠️ | A3.4 扫描命中：`ItemExportControllerTest.java:62` 行宽超 120（P2） |
| A4 命名规范 | ✅ | 新代码命名合规（`SUPPORTED_ENCODINGS` 为可变 Map 但仅初始化写入，无实际风险） |
| A5 编码实践 | ✅ | 无空 catch、无 finalize、无实例调静态 |
| A6 特定元素样式 | ✅ | 未用 C 风格数组、无 switch、long 字面量规范（`10L * 1024L`） |
| A7 Javadoc | ✅ | 新增 public 类/方法均有 Javadoc，块标记顺序正确 |

---

## Step 4 — 可靠性 / 安全 / Bug 模式（产物 D）

### 4.1 自动化预扫结果（scan-all-rules.sh，52/222 条）

| 等级 | ID | 定位 | 复核结论 |
|------|----|------|----------|
| P0 | G16.2 | `ItemExportController.java:88,130` | ✅ 误报（catch 下方 `log.error` 在 89/131 行，满足日志要求） |
| P0 | G16.2 | `ItemExportService.java:62` | ✅ 误报（`log.error` 在 63 行） |
| P0 | G16.2 | `ItemController.java:66,91,102,138` | ⚠️ 既有代码非本次变更（catch 后仅 flash 消息无日志）→ 超范围，建议 P2 后续优化 |
| P1 | M016 | `TxtExportService.java:66` | ❌ 真实命中：`LocalDateTime.now()` 依赖默认时区（P1，见报告 §7.1） |
| P2 | A2.2 | `ItemController.java:14` | 既有代码，超范围 |
| P2 | A3.4 | `ItemExportControllerTest.java:62` | ❌ 真实命中（P2，见报告 §7.1） |

### 4.2 LLM 补充核对

| 域 | 结果 | 说明 |
|----|------|------|
| G1 并发 | ✅ N/A | 无共享可变状态、无锁需求（设计 §5.2.3 并发结论一致） |
| G2/G5/G6/G7 | ✅ N/A | 无写接口幂等需求、无 MQ/缓存/调度 |
| G3 事务 | ✅ N/A | 纯只读 + 即时计算 |
| G4 SQL | ✅ | JPA 方法查询；`findAll()` 无分页但 ≤1 万行硬上限（R01）兜底 |
| G8 防御编程 | ⚠️ | `ItemExportService.java:62` catch `RuntimeException` 范围过宽，编程错误被包装为 DOCGEN_001（有日志栈可查，P2） |
| G9 网络 | ✅ N/A | 无外部 RPC/HTTP |
| G11 自测 | ⚠️ | 边界缺口：O01 limit 截断路径、GBK 成功路径、W01 超限/数据源失败路径未覆盖（P2） |
| G13 日志级别 | ✅ | 业务异常 error 级别、超限 warn，无 info-exception 混用 |
| G14 时区 | ❌ | M016（TxtExportService:66）；文件名时间戳，实际影响低 |
| G16.1 监控埋点 | ❌ | **P0**：W01/O01 成功路径无指标埋点与请求摘要日志（设计 §6.5/§7.1 明确要求） |
| G16.2 异常日志上下文 | ✅ | 新代码异常日志含 errorCode（`code={}` + 栈）；无 traceId 基建属应用现状 |
| G17 可应急 | ✅ | 7.3 开关生效（配置置 false 即拒绝服务，无需发版） |
| S1-S10 安全 | ✅ | 无 SQL 拼接/命令执行/反序列化/密钥硬编码；R02 转义防文本注入；文件名白名单防穿越；A03 假设公共数据不鉴权（设计声明） |
| B 族（25 条已扫 + 人工补扫） | ✅ | 无 Blocker 命中；`yyyyMMdd-HHmmss` 小写 y 正确（B052 不命中） |
| M 族（6 条已扫 + 人工补扫） | ❌ | M016 一处（P1） |
| I 族（2 条已扫） | ✅ | 未用 `@Test(expected=...)`（I001 不命中）、无 `new Date()` |

---

## Step 5 — 自定义扩展检查（产物 E）

`N/A(未启用自定义规则)`：customized-checklist 均为示例项（U1.1 示例、U2 空），未配置团队私有规则。

---

## 收口核对

- 执行队列待审数：0（12 个 Java 文件全审；3 个非 Java 跳过）✅
- 报告审查文件数 = 队列 Java 文件数（12）✅
- Step 2 章节级勾选与逐文件结论一致 ✅