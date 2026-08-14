# Code Review Report

> **Change** 快速排序算法实现 · **分支/Commit** AI/task-DEV-f4ad1a6e · **日期** 2026-08-14 · **审查者** AI
>
> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 `bug-pattern-checklist.md` 表头为准（Blocker→P0、Major→P1、Info→P2）。**注意**：因沙箱限制无法运行 `scan-all-rules.sh` 及 `mvn test`，本次审查为纯 LLM 静态代码审查。问题须含 `path:line` 或清单 ID。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | 2 |
| 变更行数 | +309 (QuickSort.java) / +280 (QuickSortTest.java) |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| QuickSort | `my-spring-boot-app/src/main/java/com/example/myapp/util/QuickSort.java` | 快速排序算法工具类（5个公开方法） |
| QuickSortTest | `my-spring-boot-app/src/test/java/com/example/myapp/util/QuickSortTest.java` | 单元测试（24个测试用例） |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 1 | 0 | 1 |

---

## 3. Step 2 — 功能（REQ）

### REQ-F01: 整数数组升序排序

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| sort(int[] arr) 执行升序排序 | ✅ | design.md §5.1.2 S01 — "对整数数组进行原地升序快速排序" | `QuickSort.java:31-33` — `sort(arr)` 委托 `sort(arr, true)` | 符合 |

### REQ-F02: 整数数组降序排序

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| sort(int[] arr, boolean ascending) 支持降序 | ✅ | design.md §5.1.2 S02 — "支持升序/降序" | `QuickSort.java:43-48` + `QuickSort.java:133-138` — ascending 参数控制比较方向 | 符合 |

### REQ-F03: 泛型数组排序（Comparable）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| sort(T[] arr) 泛型升序 | ✅ | design.md §5.1.2 S03 — "对实现了 Comparable 接口的泛型数组进行原地升序快速排序" | `QuickSort.java:58-60` — `sort(arr)` 委托 `sort(arr, true)` | 符合 |

### REQ-F04: 自定义比较器排序

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| sort(T[] arr, Comparator) | ✅ | design.md §5.1.2 S05 — "使用自定义比较器对泛型数组进行原地快速排序" | `QuickSort.java:88-96` — 完整实现 | 符合 |
| comparator 为 null 抛 IllegalArgumentException | ✅ | design.md §5.1.2 S05 R05 — "comparator 为 null 时抛出 IllegalArgumentException" | `QuickSort.java:89-91` — `throw new IllegalArgumentException("Comparator must not be null")` | 符合 |

### REQ-F05: 三数取中优化

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 三数取中选 pivot | ✅ | design.md §5.1.3.1 R03 — "取 arr[lo]、arr[mid]、arr[hi] 的中位数作为 pivot" | `QuickSort.java:117-127` (int[]), `QuickSort.java:188-196` (Comparable), `QuickSort.java:261-269` (Comparator) | 三种实现均含三数取中 |

### REQ-F06: 边界条件处理

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| arr 为 null → 直接返回 | ✅ | design.md §5.1.3.1 R01 — "直接返回，不抛异常" | `QuickSort.java:44` (int[]), `QuickSort.java:72` (Comparable), `QuickSort.java:92` (Comparator) | 符合 |
| arr.length ≤ 1 → 直接返回 | ✅ | design.md §5.1.3.1 R02 — "直接返回，无需排序" | 同上行 | 符合 |
| 递归终止 lo ≥ hi | ✅ | design.md §5.1.3.1 R05 — "lo ≥ hi 时返回" | `QuickSort.java:104` / `QuickSort.java:177` / `QuickSort.java:250` | 符合 |
| 所有元素相同 → O(n log n) | ❌ | design.md §5.1.3.1 异常场景表 — "分区均匀，O(n log n) 正常完成" | `QuickSort.java:133-143` — 使用 `arr[j] < pivotValue` 严格小于比较 | **P0**：Lomuto 分区对全部相等元素产生 0:n-1 的不均匀拆分，实际复杂度退化为 O(n²)，与 spec 声称的 O(n log n) 不符 |

---

## 4. Step 3 — 可读性检查

> 对照 `readability-checklist.md` A1–A7。

| ID | 检查项 | 状态 | 备注 |
|----|--------|------|------|
| A1 | 源文件格式 | ✅ | 文件名 = 顶层类名 + `.java`，UTF-8 编码 |
| A2 | 源文件结构/import 顺序 | ✅ | package → import → class，无 `import *`，仅 `java.util.Comparator` |
| A3 | 代码样式 | ✅ | K&R 大括号，4 空格缩进，行宽 ≤ 120 |
| A4 | 命名规范 | ✅ | 类名 UpperCamelCase，方法名 lowerCamelCase，泛型单字母 `T` |
| A5 | 编码实践 | ✅ | 无重写方法，静态方法通过类名调用 |
| A6 | 特定元素样式 | ✅ | 数组方括号属类型 `int[] arr`，无 switch 语句 |
| A7 | Javadoc 规范 | ✅ | 所有 public 方法含 Javadoc（`@param` / `@throws` / `@return`），`@author` 和 `@date` 完备 |

---

## 5. Step 4 — 可靠性检查

> ⚠️ 因沙箱限制无法运行 `scan-all-rules.sh`，本节由 LLM 全量静态审查。

### 5.1 可靠性（`reliability-checklist.md` G1–G17）

| 域 | 结果 | 说明 |
|----|------|------|
| G1 并发控制 | N/A | 无状态静态方法，无共享可变状态，无并发场景 |
| G2 幂等拦截 | N/A | 无写接口/消息消费 |
| G3 事务控制 | N/A | 无数据库事务 |
| G4 SQL与索引 | N/A | 无 SQL 操作 |
| G5 消息（MQ） | N/A | 无消息队列 |
| G6 缓存 | N/A | 无缓存 |
| G7 调度任务 | N/A | 无调度任务 |
| G8 防御编程 | ✅ | 无 I/O 流、无连接、无线程池、无 ThreadLocal；null 检查完备 |
| G9 网络调用 | N/A | 无网络调用 |
| G10 接口契约 | N/A | 无外部接口契约 |
| G11 开发自测 | ✅ | G11.1 有单测（24 个用例）；G11.2 覆盖边界（null/空/单元素/重复/大数组）；G11.3 入参空值防御性校验到位；G11.4 无浮点运算 |
| G12 资损防控 | N/A | 无资金相关场景 |
| G13 监控核对 | N/A | 无日志输出 |
| G14 国际化/多租户/时区 | N/A | 无金额/多租户/时区处理 |
| G15 可灰度 | N/A | 新增工具类，无存量替换 |
| G16 可监控 | ✅ | G16.4 无空 catch 块 |
| G17 可应急 | N/A | 工具类，调用方替换 `Arrays.sort()` 即可回退 |

### 5.2 安全（`security-checklist.md` S1–S10）

| 域 | 结果 | 说明 |
|----|------|------|
| S1 SQL 注入 | N/A | 无 SQL |
| S2 XSS | N/A | 无 Web 输出 |
| S3 SSRF | N/A | 无外部 URL 请求 |
| S4 命令执行 | N/A | 无系统命令调用 |
| S5 XXE | N/A | 无 XML 解析 |
| S6 反序列化 | N/A | 无反序列化 |
| S7 文件上传/下载 | N/A | 无文件操作 |
| S8 访问控制 | N/A | 无 HTTP 接口 |
| S9 数据安全 | N/A | 无密钥/凭证/日志 |
| S10 CSRF/CORS | N/A | 无 Web 接口 |

### 5.3 Bug 模式（`bug-pattern-checklist.md` B/M/I）

> 对 120 条规则逐一核销，仅列出与变更相关的条目，其余均标 N/A（纯算法工具类，无 DB/IO/MQ/Cache/Spring 等）。

| ID | 状态 | 备注 |
|----|------|------|
| B038 InfiniteRecursion | ✅ | 递归有终止条件 `lo >= hi` → return（`QuickSort.java:104,177,250`） |
| B046 LoopConditionChecker | ✅ | 所有 for 循环变量均正常递增（`j++`） |
| B075 SuspiciousForLoop | ✅ | 循环条件与增量方向一致 |
| M005 ClassCanBeStatic | ✅ | `QuickSortTest.Person` 已是 `static class`（`QuickSortTest.java:263`） |
| M007 EmptyCatch | ✅ | 无 catch 块 |
| M020 MissingOverride | ✅ | 无重写方法 |
| 其余 B001–B081 / M001–M027 / I001–I010 | N/A | 变更不涉及对应的代码模式（无 DB/IO/日期/线程池/集合修改/序列化等） |

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 说明 |
|----|------|------|------|
| 自定义扩展 | `customized-checklist.md` U* | N/A(未启用自定义规则) | U1.1 为示例项（Controller 校验注解），与本次变更无关；U2 业务红线为空 |

---

## 7. 结论

- **合并建议**：修复后合并（1 个 P0 需修复）
- **P0**：
  1. **REQ-F06 全部相等元素性能退化**：Lomuto 分区对全部相等元素产生 O(n²) 复杂度，与 spec 声称的 O(n log n) 不符。建议改为三路分区（Dutch national flag）或将 `<` 改为 `<=` 并配合 Hoare 分区方案。
- **P1/P2**：
  1. **P2** — R06 小数组插入排序优化未实现（spec 标注为"可选优化"，非阻塞）

- **一句话**：代码结构清晰、命名规范、Javadoc 完备、测试覆盖充分；核心算法正确性良好，唯一阻塞项为全部相等元素场景的性能退化问题需修复。

---

## 7.1 问题片段（必填）

### P0 — REQ-F06 全部相等元素 O(n²) 退化

- **P0** `REQ-F06` `my-spring-boot-app/src/main/java/com/example/myapp/util/QuickSort.java:133-143` — Lomuto 分区使用严格小于 `<` 比较，当所有元素相等时 `arr[j] < pivotValue` 恒为 false，导致每次分区仅移动一个 pivot 元素，复杂度退化为 O(n²)，与 spec（design.md §5.1.3.1 异常场景表）声称的 "分区均匀，O(n log n) 正常完成" 不符。

  片段范围：`my-spring-boot-app/src/main/java/com/example/myapp/util/QuickSort.java:117-147`

```java
L117|    private static int partition(int[] arr, int lo, int hi, boolean ascending) {
L118|        // 三数取中：取 arr[lo]、arr[mid]、arr[hi] 的中位数作为 pivot
L119|        int mid = lo + (hi - lo) / 2;
L120|        int pivot = medianOfThree(arr[lo], arr[mid], arr[hi]);
L121|
L122|        // 将 pivot 值交换到 hi 位置（简化分区逻辑）
L123|        if (pivot == arr[lo]) {
L124|            swap(arr, lo, hi);
L125|        } else if (pivot == arr[mid]) {
L126|            swap(arr, mid, hi);
L127|        }
L128|        // 如果 pivot == arr[hi]，无需交换
L129|
L130|        int pivotValue = arr[hi];
L131|        int i = lo - 1;
L132|
L133|        for (int j = lo; j < hi; j++) {
L134|            boolean shouldMove;
L135|            if (ascending) {
L136|                shouldMove = arr[j] < pivotValue;  // <-- 问题：严格小于
L137|            } else {
L138|                shouldMove = arr[j] > pivotValue;
L139|            }
L140|            if (shouldMove) {
L141|                i++;
L142|                swap(arr, i, j);
L143|            }
L144|        }
L145|        swap(arr, i + 1, hi);
L146|        return i + 1;
L147|    }
```

**修复建议**：将 Lomuto 分区改为三路分区（Dutch National Flag），或使用 `<=` 比较配合 Hoare 分区方案，使相等元素均匀分布到两侧，避免退化。

---

## 8. 修复任务列表

### P0

- [ ] **P0** `my-spring-boot-app/src/main/java/com/example/myapp/util/QuickSort.java:133-143` — 修复 Lomuto 分区在全部相等元素场景下的 O(n²) 退化问题，改为三路分区或 Hoare 分区方案，确保与 spec 中 "分区均匀，O(n log n)" 的承诺一致。需同步修复 int[] 版本（QuickSort.java:133-143）、Comparable 版本（QuickSort.java:201-208）、Comparator 版本（QuickSort.java:274-278）。

### P2

- [ ] **P2** `my-spring-boot-app/src/main/java/com/example/myapp/util/QuickSort.java` — 考虑实现 R06 小数组插入排序优化（子数组长度 < 10 时切换），提升小规模数据排序性能。