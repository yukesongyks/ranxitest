# Code Review Report

> **Change** `quick-sort-util` · **分支/Commit** `AI/task-DEV-966dcd0a` / `a878a34` · **日期** `2026-07-31` · **审查者** AI

> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 `bug-pattern-checklist.md` 表头为准（Blocker→P0、Major→P1、Info→P2）。**须先**运行 `scan-all-rules.sh` 并将要点并入 §5，**再**写 LLM 结论。问题须含 `path:line` 或清单 ID。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | `2` |
| 变更行数 | `+314 / -0` |

| 类/接口 | 路径 | 角色（可选） |
|---------|------|--------------|
| `QuickSortUtil` | `my-spring-boot-app/src/main/java/com/example/myapp/common/util/QuickSortUtil.java` | 快速排序工具类 |
| `QuickSortUtilTest` | `my-spring-boot-app/src/test/java/com/example/myapp/common/util/QuickSortUtilTest.java` | 单元测试 |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 0 | 1 | 1 |

---

## 3. Step 2 — 功能（REQ）

### REQ-1: 实现一个快速排序算法

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| int[] 数组升序排序 | ✅ | 「实现一个快速排序算法」 | `QuickSortUtil.java:22-27` | Lomuto 分区 + 三数取中，原地排序 |
| List<T> 泛型列表升序排序 | ✅ | 同上（泛化扩展） | `QuickSortUtil.java:35-46` | 转数组排序后回写，自然顺序比较 |
| 递归分区 | ✅ | 快速排序定义 | `QuickSortUtil.java:55-62`,`132-139` | 标准 low/high 递归 |
| 空值/空集合防御 | ✅ | 工具类健壮性 | `QuickSortUtil.java:23`,`36` | null/size<=1 直接返回 |

**结论**：✅ 功能完整，与需求一致。

---

## 4. Step 3 — 可读性检查

| 结果 | 说明（违规写 Ax.x 与 `path:行`） |
|------|--------------------------------|
| ✅ | QuickSortUtil.java：完全符合阿里规范 |
| ⚠️ P2 | `QuickSortUtilTest.java:11-12` — A2.4：静态 import 组内非字典序，`assertDoesNotThrow` 应排在 `assertEquals` 之前 |

---

## 5. Step 4 — 可靠性检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|-----|
| Bug 模式 | scan-all-rules.sh + 人工 | ✅ | — | 52/222 规则扫描无命中；人工复核无 B/M/I 命中 |
| 可靠性 G | reliability-checklist.md G1-G17 | ✅/⚠️ | P1 | G8/G11/G16 符合；G1-G7/G9-G10/G12-G15/G17 标 N/A（纯内存工具类，无并发/事务/SQL/MQ/缓存/网络/资金/灰度场景） |
| 安全 S | security-checklist.md S1-S10 | N/A | — | 排序工具类无安全场景 |

### §5.1 可靠性问题明细

| 等级 | ID | 简述 | 位置 |
|------|-----|------|------|
| P1 | — | 泛型 partition 未使用三数取中，有序输入存在 O(n²) 退化与递归栈溢出风险 | `QuickSortUtil.java:150-161` |

---

## 6. Step 5 — 自定义扩展检查

| 结果 | 说明 |
|------|------|
| N/A | `N/A(未启用自定义规则)` — customized-checklist.md 为示例项，未配置团队私有规则 |

---

## 7. 问题汇总与修复建议

### P0

无。

### P1

- [ ] **P1** `my-spring-boot-app/src/main/java/com/example/myapp/common/util/QuickSortUtil.java:150-161` — 泛型 `partition(T[], int, int)` 直接取 `array[high]` 作基准，未使用三数取中，与类级 Javadoc 声明（line 8-10「并结合三数取中选取基准值，以降低在接近有序输入下退化为 O(n^2) 的概率」）不一致。有序/接近有序 List 输入会退化为 O(n²)，极端情况（大量元素有序）可能触发 `StackOverflowError`。

  **修复建议**：为泛型路径补充三数取中逻辑（参照 int[] 路径 `medianOfThree`，line 93-106），将中位数交换至 `high` 后再以 `array[high]` 为基准；或对泛型 partition 也调用等价的三数取中选 pivot。

#### 7.1 问题片段

**P1 — `QuickSortUtil.java:150-161`（泛型 partition 缺三数取中）**

```java
private static <T extends Comparable<? super T>> int partition(T[] array, int low, int high) {
    T pivot = array[high];          // ← 直接取末位元素，未做三数取中
    int i = low - 1;
    for (int j = low; j < high; j++) {
        if (array[j].compareTo(pivot) <= 0) {
            i++;
            swap(array, i, j);
        }
    }
    swap(array, i + 1, high);
    return i + 1;
}
```

对比 int[] 路径（line 72-73）调用了 `medianOfThree(array, low, high)` 选基准，泛型路径未对齐。

### P2（可选）

- [ ] **P2** `my-spring-boot-app/src/test/java/com/example/myapp/common/util/QuickSortUtilTest.java:11-12` — 静态 import 组内非字典序：`assertDoesNotThrow`（D）应排在 `assertEquals`（E）之前。可选调整顺序即可。

---

## 8. 审查结论

| 维度 | 结论 |
|------|------|
| 功能 | ✅ 完整实现快速排序（int[] + List<T> 双路径） |
| 可读性 | ✅ 主类优秀；测试类 1 处 P2 风格问题（import 排序） |
| 可靠性 | ⚠️ 1 处 P1：泛型 partition 缺三数取中，有序输入有 O(n²)/栈溢出风险 |
| 安全 | N/A 无安全场景 |
| Bug 模式 | ✅ 无命中 |

**合并建议**：**有条件通过**。P1 问题（泛型路径缺三数取中）建议合并前修复，以与类级 Javadoc 声明保持一致并消除退化风险；P2 可选改进。
