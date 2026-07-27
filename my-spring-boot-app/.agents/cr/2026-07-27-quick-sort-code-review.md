# Code Review Report

> **Change** `实现快速排序算法` · **分支/Commit** `AI/task-DEV-966dcd0a` · **日期** `2026-07-27` · **审查者** AI
>
> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 B/M/I 标注。

## 1. 审查范围

| # | 文件 | 类型 | 行数 |
|---|------|------|------|
| 1 | `my-spring-boot-app/src/main/java/com/example/myapp/util/QuickSort.java` | main | 103 |
| 2 | `my-spring-boot-app/src/test/java/com/example/myapp/util/QuickSortTest.java` | test | 135 |

## 2. 预扫与验证

- **`scan-all-rules.sh`**（B/M/I + A/S/G，52/222 规则扫描）：**No findings**。
- **构建/测试验证**：环境缺少 `mvn`/`mvnw`（`sh: 1: mvn: not found`），触发降级。

### [降级说明]
- **原因**：被审环境无 Maven 构建工具，属环境问题，非本次变更范围代码缺陷；符合「预存错误/环境不可用」降级触发条件。
- **已审查的逻辑点**（静态审查覆盖原 TDD 对应分支）：
  1. 正常无序排序路径（`sort` → `quickSort` → `partition` + `swap`）
  2. 递归终止边界 `low >= high`（空数组 `low=0, high=-1`、单元素 `low==high`）
  3. 重复元素处理（`partition` 使用 `<=` 比较，保证重复元素正确归位）
  4. null 入参校验（`sort`/`sortedCopy` 均抛 `IllegalArgumentException`）
  5. `sortedCopy` 不变性（先 `Arrays.copyOf` 再排序副本，原数组未触碰）
  6. 数组越界安全性（`i` 范围 `[low-1, high-1]`，最终 `swap(i+1, high)` 落在 `[low, high]`）

## 3. 功能性核对

| REQ | 功能点 | 实现位置 | 结论 |
|-----|--------|----------|------|
| REQ-1 | 升序快速排序 | `QuickSort.java:31` | ✅ |
| REQ-2 | 原地排序语义 | `QuickSort.java:31,35` | ✅ |
| REQ-3 | 副本排序不改原数组 | `QuickSort.java:45-51` | ✅ |
| REQ-4 | null 抛 `IllegalArgumentException` | `QuickSort.java:32,46` | ✅ |
| REQ-5 | 空/单元素数组边界 | `QuickSort.java:35` + 测试 | ✅ |
| REQ-6 | 含重复元素 | `QuickSort.java:82` + 测试 | ✅ |

**结论**：需求「实现一个快速排序算法」全部功能点已实现并测试覆盖。

## 4. 问题清单

| 等级 | 类别 | ID | 问题描述 | 位置 | 建议 |
|------|------|----|----------|------|------|
| P2 | I（测试） | assert-usage | null 异常测试使用 `assert exception.getMessage().contains("null")`，依赖 JVM `-ea`，未启用时该断言静默失效 | `QuickSortTest.java:120` | 改用 `assertTrue(exception.getMessage().contains("null"))` 以保证测试在任意 JVM 下生效 |
| P2 | G（可靠性） | recursion-depth | Lomuto 以末尾为基准，对已排序/逆序输入退化至 O(n) 递归深度，极端大数组有栈溢出风险 | `QuickSort.java:78-89` | 可选：随机化基准或对大数组改用迭代+显式栈；当前规模可接受 |

> 无 P0/P1 问题。

## 5. 优点

- 工具类设计规范：`final class` + 私有构造，不可实例化、不可继承。
- Javadoc 完整，含复杂度（平均 O(n log n)、最坏 O(n²)、空间 O(log n)）与不变性说明。
- null 校验前置，异常信息清晰（中文提示符合项目语言惯例）。
- `sortedCopy` 严格保证不修改原数组，语义明确。
- 测试覆盖全面：无序/已序/逆序/重复/单元素/空数组/null 异常，AAA 模式 + `@DisplayName`。

## 6. 收口结论

- **执行队列**：2/2 已审，待审 0。
- **问题统计**：P0=0，P1=0，P2=2（均为可选改进，非阻塞）。
- **审查结论**：**通过，可合并**。建议在后续迭代中采纳 P2 改进项（测试 `assert` 改 `assertTrue`；大数组基准随机化）。
