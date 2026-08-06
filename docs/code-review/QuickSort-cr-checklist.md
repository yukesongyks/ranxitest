# Code Review Checklist

> **Change** `QuickSort 快速排序实现` · **分支/Commit** `AI/task-DEV-966dcd0a` / `HEAD` · **日期** `2026-08-06`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。**完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **执行顺序（强制）**：写入本清单并进入逐文件审查前，先在目标仓库对变更路径运行 `references/script/scan-all-rules.sh`，将输出贴入 Step 3 和 Step 4 备注；再用 LLM 完成 Step 2–5 中脚本未覆盖项及复核。
>
> **预扫结果**：`scan-all-rules.sh` 对两文件扫描 52/222 条规则，**No findings（已扫无命中）**，退出码 0（无 P0）。

---

## Step 1 — 执行队列（产物 A）

> **Step4 列语义**：每个 **Sn / Gn** 表示「**本文件**在 Step4 审查中，对 `reliability-checklist.md` 第 **G*n*** 节、`security-checklist.md` 第 **S*n*** 节的扫描结论」。**Bug 模式（B/M/I）** 不在本表分列，在下方 **§4.1** 按清单 ID 核销。与变更无关填 `N/A`；已扫无命中填 `✅`；命中风险填 `⚠️` 或 `❌`。

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|-----|----|----|----|----|----|----|----|----|----|-----|-----|-----|-----|-----|-----|-----|-----|--------|
| 1 | `my-spring-boot-app/src/main/java/com/example/myapp/util/QuickSort.java` | REQ-1 快排核心实现 | ✅ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | ⚠️ |
| 2 | `my-spring-boot-app/src/test/java/com/example/myapp/util/QuickSortTest.java` | REQ-1 测试覆盖 | ✅ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | ⚠️ |

- 由 `git diff --name-only HEAD~1` 展开得 2 个 `.java` 文件；非 Java 标 `跳过`（本次均为 Java）。
- **守卫**：存在 `.java`，继续审查。
- **收口说明**：G1–G15 为并发/IO/资源/日志/配置/超时/重试/MQ/缓存/线程池/序列化/限流/熔断/降级等域，本变更均为纯内存排序算法工具类与单元测试，无上述场景，统一标 `N/A(无并发/IO/MQ/缓存/外部资源)`；S1–S10 为 SQL 注入/XSS/反序列化/CSRF 等 Web 安全域，本次无 Web 入口/无 SQL/无反序列化，统一标 `N/A`；S9 命令注入 N/A。G16（异常处理）✅：`sort()` 对 null 直接返回、无 try-catch 吞异常、无外部调用需捕获。G17（可应急）N/A（无运维开关需求）。

---

## Step 2 — 功能（产物 B）

> 需求原文：「实现一个快速排序算法」。提取 REQ-1。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | Given 任意 `Comparable` 数组，When 调用 `QuickSort.sort()`，Then 原地升序排序并返回同引用 | `实现一个快速排序算法` | `my-spring-boot-app/.../QuickSort.java` | ✅ | `QuickSort.java:32-37` sort 入口；`QuickSort.java:67-79` Lomuto 分区；`QuickSortTest.java:24-33` 乱序→升序；`QuickSortTest.java:136-144` 同引用验证 |
| REQ-1 | Given null/空/单元素数组，When 调用 `sort()`，Then 原样返回不抛异常 | 隐含健壮性约束（代码 Javadoc 第 26 行声明） | `my-spring-boot-app/.../QuickSort.java` | ✅ | `QuickSort.java:33-35` null/len≤1 守卫；`QuickSortTest.java:40-48` null；`QuickSortTest.java:56-64` 空；`QuickSortTest.java:72-80` 单元素 |
| REQ-1 | Given 泛型类型（如 String），When 排序，Then 按字典序升序 | 泛型声明 `Comparable<? super T>` | `my-spring-boot-app/.../QuickSort.java` | ✅ | `QuickSort.java:32` 泛型签名；`QuickSortTest.java:152-160` String 排序 |

---

## Step 3 — 可读性检查（产物 C）

> 两文件均为 Java，整节适用。对照 `references/readability-checklist.md` A1–A7 逐节核销。

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | 两文件均为 LF 结尾、UTF-8、单一 public/包级类、无 BOM |
| A2 | 源文件结构/import 顺序 | ⚠️ | `QuickSort.java:3-4` 及 `QuickSortTest.java:6-7` 导入了 `java.util.Arrays` 与 `java.util.Collections` 但全文未使用（未引用 `Arrays.` 或 `Collections.` 任何方法），属冗余 import，违反 import 最小化原则 |
| A3 | 代码样式 | ✅ | 4 空格缩进、大括号 K&R、行宽均 <120，符合 Google Style |
| A4 | 命名规范 | ✅ | 类名 PascalCase（`QuickSort`/`QuickSortTest`）、方法名 camelCase（`sort`/`quickSort`/`partition`/`swap`）、局部变量 `array`/`pivot`/`low`/`high`/`i`/`j` 语义清晰 |
| A5 | 编码实践 | ✅ | 工具类 `final` + 私有构造器（`QuickSort.java:15,20-21`）；泛型上界 `Comparable<? super T>` 使用 PECS；魔法值无（`low-1`/`i+1` 为算法固有） |
| A6 | 特定元素样式 | ✅ | 无空 catch/无空方法；测试遵循 given-when-then 三段式注释 |
| A7 | Javadoc 规范 | ✅ | `QuickSort.java:6-14,17-21,23-31,40-47,58-66,82-89` 每个方法均有 Javadoc 含 `@param`/`@return`；类级含 `@author`/`@date`。`QuickSortTest` 各测试方法均有 Javadoc + `@DisplayName` |

---

## Step 4 — 可靠性检查（产物 D）

> 预扫：`scan-all-rules.sh` 对两文件扫描 52/222 条，**No findings**。以下为 LLM 复核脚本未覆盖项。

### 4.2 逐条核销（G/S）

| ID | 域 | 结果 | 等级 | 说明 |
|----|----|------|------|------|
| G1–G15 | 并发/IO/资源/日志/配置/超时/重试/MQ/缓存/线程池/序列化/限流/熔断/降级 | N/A | — | N/A(纯内存排序工具类，无并发/IO/MQ/缓存/外部资源/运维开关场景) |
| G16 | 异常处理 | ✅ | — | `QuickSort.java:33` 对 null 提前返回不抛 NPE；无 try-catch 吞异常；分区/swap 无外部调用需捕获。已扫无命中 |
| G17 | 可应急 | N/A | — | N/A(算法工具类无运行时开关/降级需求) |
| S1–S10 | Web 安全（SQL 注入/XSS/反序列化/路径穿越/SSRF/任意文件/日志注入/越权/命令注入/CSRF） | N/A | — | N/A(无 Web 入口/无 SQL/无反序列化/无文件操作/无 HTTP 调用) |

### 4.1 Bug 模式（B/M/I，120 条）

> `scan-all-rules.sh` 覆盖 B(25)/M(6)/I(2) 正则可扫部分，已扫无命中。以下为脚本未覆盖、需类型/语义分析的 LLM 复核要点。

| 清单域 | 结果 | 等级 | 说明 |
|--------|------|------|------|
| B 空指针/索引越界 | ✅ | — | `partition` 的 `array[high]`/`array[j]` 由 `quickSort` 的 `low<high` 守卫保证不越界；`swap(array, i+1, high)` 中 `i∈[low-1, high-1]` 故 `i+1∈[low, high]` 安全；`sort()` 对 null 守卫。已扫无命中 |
| B 整数溢出 | ✅ | — | `low-1`/`i+1` 为索引运算，数组长度受 `int` 范围内，无溢出风险 |
| M 多线程 | N/A | — | N/A(无共享可变状态，工具类无实例字段) |
| I 信息泄露 | N/A | — | N/A(无日志/无异常栈输出) |

---

## Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明（列命中 ID 或「未启用自定义规则」） |
|----|------|------|------|------------------------------------------|
| 自定义扩展 | `customized-checklist.md` U* | N/A | — | 未启用自定义规则（仓库无 `customized-checklist.md`） |

---

## 收口核验

- ✅ Step 1 两文件所有 Sn/Gn 列均非 `⬜`
- ✅ Step 2 所有 REQ 核销完成
- ✅ Step 3 A1–A7 全部核销（2 项 ⚠️）
- ✅ Step 4 G/S/Bug 模式逐条核销
- ✅ Step 5 自定义扩展已说明
