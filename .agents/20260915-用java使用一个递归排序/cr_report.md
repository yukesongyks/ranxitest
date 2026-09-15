# Code Review Report

> **Change** `用java使用一个递归排序` · **分支/Commit** `AI/task-DEV-72ed08cb-78db-11f1-8f3f-75954cb1c56f-d4022f5f-a630-49fd-94f0-83a1074da133` / `HEAD` · **日期** `2026-09-15` · **审查者** AI
>
> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 `bug-pattern-checklist.md` 表头为准（Blocker→P0、Major→P1、Info→P2）。**须先**运行 `scan-all-rules.sh` 并将要点并入 §5，**再**写 LLM 结论。问题须含 `path:line` 或清单 ID：可读性 `A3.4`，安全 `S1.1`，可靠性 `G16.2`，Bug 模式 `B012` / `M005` 等。**每个 ❌/⚠️ 问题在 §7 后必须附 `.java` 问题片段**（见 §7.1）。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | 5 |
| 变更行数 | +N / -0 (新增实现) |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| `Sorter` | `my-spring-boot-app/src/main/java/com/example/myapp/sort/Sorter.java` | 排序统一接口 |
| `QuickSort` | `my-spring-boot-app/src/main/java/com/example/myapp/sort/QuickSort.java` | 快速排序实现 |
| `MergeSort` | `my-spring-boot-app/src/main/java/com/example/myapp/sort/MergeSort.java` | 归并排序实现 |
| `SortUtils` | `my-spring-boot-app/src/main/java/com/example/myapp/sort/SortUtils.java` | 排序通用工具类 |
| `SorterTest` | `my-spring-boot-app/src/test/java/com/example/myapp/sort/SorterTest.java` | 单元测试 |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 0 | 0 | 3 |

---

## 3. Step 2 — 功能（REQ）

### REQ-1: Sorter 统一接口定义

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 定义 `void sort(T[] array)` 接口 | ✅ | design.md §4.3 S01 | `Sorter.java:10` | 接口定义与系分一致 |
| 泛型约束 `T extends Comparable<T>` | ✅ | design.md §4.3 S01 | `Sorter.java:10` | 约束正确 |
| Javadoc 完整 | ✅ | design.md §5.1.2 | `Sorter.java:1-18` | 含 `@param`、`@throws` |

### REQ-2: QuickSort 递归实现

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 入参 null 抛 IllegalArgumentException | ✅ | design.md §5.1.3.1 异常场景 | `QuickSort.java:18-19` | 异常消息含 "null" |
| 空数组/单元素直接返回 | ✅ | design.md §5.1.3.1 异常场景 | `QuickSort.java:21-22` | 边界处理正确 |
| 分区操作 partition | ✅ | design.md §5.1.3.1 核心步骤 | `QuickSort.java:52-64` | 采用末尾元素为 pivot |
| 递归终止条件 low >= high | ✅ | design.md §5.1.3.1 规则 R02 | `QuickSort.java:35` | `if (low < high)` 正确 |

### REQ-3: MergeSort 递归实现

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 入参 null 抛 IllegalArgumentException | ✅ | design.md §5.1.3.2 异常场景 | `MergeSort.java:18-19` | 异常消息含 "null" |
| 空数组/单元素直接返回 | ✅ | design.md §5.1.3.2 异常场景 | `MergeSort.java:21-22` | 边界处理正确 |
| 二分边界正确性 | ✅ | design.md §5.1.3.2 规则 R04 | `MergeSort.java:36` | `left + (right - left) / 2` 防溢出 |
| 合并后数组有序性 | ✅ | design.md §5.1.3.2 规则 R05 | `MergeSort.java:54-90` | merge 方法正确合并 |

### REQ-4: SortUtils 工具方法

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 提供 swap 方法 | ✅ | design.md 功能架构 | `SortUtils.java:21-25` | 泛型 swap 实现正确 |
| 工具类禁止实例化 | ✅ | design.md 公共组件 | `SortUtils.java:8` | `final` 类 + 私有构造器 |

---

## 4. Step 3 — 可读性检查

> 无 Java：**N/A**。

| ID | 检查项 | 状态 | 备注（违规写 Ax.x 与 `path:行`） |
|----|--------|------|--------------------------------|
| A1 | 源文件格式 | ✅ | 文件名与类名一致，UTF-8 编码，无 Tab |
| A2 | 源文件结构/import 顺序 | ✅ | 无通配符 import，顺序正确 |
| A3 | 代码样式 | ✅ | K&R 大括号，4空格缩进，行宽≤120 |
| A4 | 命名规范 | ✅ | 类名 UpperCamelCase，方法名 lowerCamelCase |
| A5 | 编码实践 | ✅ | `@Override` 已加，无 finalize 重写 |
| A6 | 特定元素样式 | ✅ | 数组方括号属于类型，无 C 风格数组声明 |
| A7 | Javadoc 规范 | ✅ | public 类/方法均有 Javadoc，块标记顺序正确 |

---

## 5. Step 4 — 可靠性检查

| 域 | 参考 | 结果 | 等级 | 说明（列命中 ID 或「已扫无命中」） |
|----|------|------|------|-------------------------------------|
| 可靠性 | `reliability-checklist.md` G1–G17 | ✅ | — | 纯算法库，无并发/事务/MQ/缓存/调度等场景；G11.1/G11.2 测试覆盖充分；G11.3 入参 null 已校验；其余 G 项 N/A(纯算法库) |
| 安全 | `security-checklist.md` S1–S10 | ✅ | — | 纯算法库，无 SQL/XSS/SSRF/命令执行/反序列化/文件上传/访问控制等场景；所有 S 项 N/A(纯算法库) |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120） | ✅ | — | `scan-all-rules.sh` 预扫 52/222 条，无命中；LLM 逐条复核 B001–B081/M001–M027/I001–I010，均无命中 |

**scan-all-rules.sh 预扫摘要**：
- 引擎: ripgrep
- 扫描规则: 52/222 条
- 命中: **0 条**
- P0=0, P1=0, P2=0

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明（列命中 ID 或「未启用自定义规则」） |
|----|------|------|------|------------------------------------------|
| 自定义扩展 | `customized-checklist.md` U* | N/A | — | 未启用自定义规则 |

---

## 7. 结论

- **合并建议**：通过
- **P0**：无
- **P1**：无
- **P2**：
  1. `QuickSort.java:53` — pivot 始终取末尾元素，对已排序数据性能退化为 O(n²)，建议后续优化为三数取中法或随机化 pivot
  2. `SorterTest.java:5-6` — 导入了未使用的 `@ParameterizedTest`、`@CsvSource`、`@ValueSource`
  3. `SortUtils.java:21` — `swap` 方法可添加 `i == j` 短路避免无意义赋值（极小优化）
- **一句话**：代码结构清晰，功能完整覆盖设计文档要求，边界条件和异常处理到位，测试覆盖充分，可直接合并；仅存在 3 处 P2 优化建议，不影响功能正确性。

---

## 7.1 问题片段（必填）

### P2-1: QuickSort pivot 选择策略可优化

- **P2** — `my-spring-boot-app/src/main/java/com/example/myapp/sort/QuickSort.java:53` — pivot 始终取末尾元素，对已排序数据性能退化为 O(n²)
  片段范围：`my-spring-boot-app/src/main/java/com/example/myapp/sort/QuickSort.java:52-56`

```java
L52|    private int partition(T[] array, int low, int high) {
L53|        T pivot = array[high];
L54|        int i = low - 1;
L55|
L56|        for (int j = low; j < high; j++) {
```

### P2-2: SorterTest 存在未使用的 import

- **P2** — `my-spring-boot-app/src/test/java/com/example/myapp/sort/SorterTest.java:4-6` — 导入了未使用的注解
  片段范围：`my-spring-boot-app/src/test/java/com/example/myapp/sort/SorterTest.java:1-10`

```java
L1| package com.example.myapp.sort;
L2|
L3| import org.junit.jupiter.api.Test;
L4| import org.junit.jupiter.params.ParameterizedTest;
L5| import org.junit.jupiter.params.provider.CsvSource;
L6| import org.junit.jupiter.params.provider.ValueSource;
L7|
L8| import static org.assertj.core.api.Assertions.assertThat;
L9| import static org.assertj.core.api.Assertions.assertThatThrownBy;
L10|
```

### P2-3: SortUtils.swap 可添加 i==j 短路

- **P2** — `my-spring-boot-app/src/main/java/com/example/myapp/sort/SortUtils.java:21` — 相同下标交换可短路
  片段范围：`my-spring-boot-app/src/main/java/com/example/myapp/sort/SortUtils.java:21-25`

```java
L21|    public static <T> void swap(T[] array, int i, int j) {
L22|        T temp = array[i];
L23|        array[i] = array[j];
L24|        array[j] = temp;
L25|    }
```

---

## 8. 修复任务列表

> 供后续改代码时逐项执行与核销；须与 §3–§7 中 ❌/⚠️ 及结论中的可执行项对应。

### P0

- 无待修复项。

### P1

- 无待修复项。

### P2（可选）

- [ ] **P2** `my-spring-boot-app/src/main/java/com/example/myapp/sort/QuickSort.java:53` — 优化 pivot 选择策略（如三数取中或随机化），避免已排序数据上的 O(n²) 退化
- [ ] **P2** `my-spring-boot-app/src/test/java/com/example/myapp/sort/SorterTest.java:4-6` — 移除未使用的 import（`ParameterizedTest`、`CsvSource`、`ValueSource`）
- [ ] **P2** `my-spring-boot-app/src/main/java/com/example/myapp/sort/SortUtils.java:21` — 在 swap 方法开头添加 `if (i == j) return;` 短路相同下标交换
