# Code Review Checklist

> **Change** `实现快速排序算法` · **分支/Commit** `AI/task-DEV-966dcd0a` · **日期** `2026-07-27`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。

## 审查范围

| # | 文件 | 归属原因 | 队列状态 |
|---|------|----------|----------|
| 1 | `my-spring-boot-app/src/main/java/com/example/myapp/util/QuickSort.java` | 快速排序算法实现（main 代码） | ✅ 已审 |
| 2 | `my-spring-boot-app/src/test/java/com/example/myapp/util/QuickSortTest.java` | 快速排序单元测试 | ✅ 已审 |

## 预扫结果

- `scan-all-rules.sh`（B/M/I + A/S/G，52/222 规则）：**No findings** ✅
- 构建验证：环境无 `mvn`/`mvnw`，触发降级 → 静态审查（见报告 `[降级说明]`）。

---

## Step 1：执行队列（产物 A）

- [x] `QuickSort.java` — main 实现
- [x] `QuickSortTest.java` — test 覆盖

## Step 2：功能性检查（产物 B）

> REQ 来源：需求描述「实现一个快速排序算法」+ 需求标题。功能点由 change 原文/方法签名推断。

- [x] **REQ-1** 提供升序快速排序能力 — `QuickSort.sort()`：`QuickSort.java:31`
- [x] **REQ-2** 原地排序语义（修改入参） — `QuickSort.java:31,35`
- [x] **REQ-3** 提供不修改原数组的副本排序能力 — `QuickSort.sortedCopy()`：`QuickSort.java:45`
- [x] **REQ-4** null 入参抛 `IllegalArgumentException` — `QuickSort.java:32,46`
- [x] **REQ-5** 支持空数组与单元素数组（边界） — `QuickSort.java:35` + `QuickSortTest.java:82,69`
- [x] **REQ-6** 支持含重复元素数组 — `partition` 使用 `<=`：`QuickSort.java:82` + `QuickSortTest.java:56`

## Step 3：可读性检查（产物 C，A1–A7）

- [x] **A1 源文件格式** — package/import 顺序规范、`final class` 工具类不可实例化
- [x] **A2 命名** — `sort`/`sortedCopy`/`quickSort`/`partition`/`swap` 语义清晰
- [x] **A3 注释/Javadoc** — 类/方法/参数 Javadoc 完整，含复杂度说明
- [x] **A4 方法长度/职责** — 单一职责，方法短小
- [x] **A5 控制流** — 递归终止 `low >= high` 清晰
- [x] **A6 常量/魔法值** — 无魔法值
- [x] **A7 测试可读性** — AAA（Arrange/Act/Assert）+ `@DisplayName` 清晰

> 测试 `assert exception.getMessage().contains("null")`（`QuickSortTest.java:120`）使用 `assert` 语句：若 JVM 未启用 `-ea` 则该断言不生效，仅为软断言（见 Step 4 备注，未阻塞）。

## Step 4：可靠性检查（产物 D）

### 可靠性（G，参考 reliability-checklist）

- [x] **G 并发控制** N/A — 纯静态无状态方法，无共享可变状态
- [x] **G 资源释放** N/A — 无外部资源
- [x] **G 边界条件** — `low >= high` 递归终止正确；空数组 `length-1=-1`，`low=0,high=-1` 直接返回，无越界 ✅
- [x] **G 递归深度** ⚠️ — 最坏情况（已排序/逆序输入）递归深度 O(n)，未做尾递归优化或随机化基准选择；当前规模可接受，大数组存在栈溢出隐患（见报告 B 类备注）

### 安全（S，参考 security-checklist）

- [x] **S 输入校验** ✅ — null 校验已覆盖
- 其余 S 类别 N/A（无 SQL/认证/密钥/外部依赖场景）

### Bug 模式（B/M/I，参考 bug-pattern-checklist）

- [x] **B 数组越界** ✅ — `partition` 中 `swap(array, i+1, high)`，`i` 初始 `low-1`，循环后 `i+1 >= low`，且 `i+1 <= high`（`j < high`），无越界
- [x] **B 整数溢出** ✅ — 仅下标加减，无乘法/累加溢出风险
- [x] **B 空指针** ✅ — 已显式校验
- [x] **M 异常信息** ✅ — 中文提示清晰
- [x] **I 测试断言** ⚠️ — `QuickSortTest.java:120` 使用 `assert` 而非 `assertTrue`，依赖 `-ea`；建议改用 JUnit 断言（P2，非阻塞）

## Step 5：自定义扩展检查（产物 E）

- N/A（未启用自定义规则）

---

## 收口核销

- 执行队列 `⬜ 待审`：**0** ✅
- 报告审查范围文件数：**2**（与队列一致）✅
- 严重性问题：**P0 = 0**，**P1 = 0**，**P2 = 1**（测试 `assert` 用法建议）
- 结论：**可合并**，P2 为可选改进。
