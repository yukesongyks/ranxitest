# Code Review Report

> **Change** `quick-sort-coding` · **分支/Commit** `AI/task-DEV-966dcd0a` / `77ba6c8` · **日期** `2026-07-28` · **审查者** AI
>
> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以预扫 + LLM 判定。

**审查范围**

| 序号 | 文件 | 行数 | 类型 |
|------|------|------|------|
| 1 | `my-spring-boot-app/src/main/java/com/example/myapp/util/QuickSort.java` | 176 | 生产代码 |
| 2 | `my-spring-boot-app/src/test/java/com/example/myapp/util/QuickSortTest.java` | 123 | 测试代码 |

**预扫**：`scan-all-rules.sh` → 52/222 规则，**No findings**。
**需求**：实现一个快速排序算法（编码实现阶段）。

---

## 审查结论总览

| 等级 | 数量 | 说明 |
|------|------|------|
| **P0 阻塞** | 0 | 无功能性不符、无安全漏洞 |
| **P1 推荐** | 1 | Lomuto 最坏情况递归栈溢出隐患（可靠性） |
| **P2 参考** | 2 | 类 Javadoc 能力范围描述不一致；测试 DisplayName「稳定」用词不严谨 |

**合并建议**：**有条件通过（Conditional Pass）**。功能正确、测试覆盖充分、无 P0；建议合并前修复 P1（或显式标注栈深度限制），P2 可选改进。

---

## 问题清单

### [P1] 可靠性 — Lomuto 末元素 pivot 在最坏情况下递归栈溢出

- **位置**：`QuickSort.java:100-111`（`partition(int[])`）、`QuickSort.java:137-149`（`partition(List)`）
- **现象**：Lomuto 分区固定以序列末元素 `arr[high]` 为 pivot。当输入**已升序**或**已降序**时，每次分区产生极不均衡划分（一侧为空），递归深度退化为 **O(n)** 而非平均 O(log n)。
- **影响**：对大数组（约 10⁵ 量级以上已序输入），递归层数线性增长，Java 默认线程栈（通常 512KB–1MB）存在 `StackOverflowError` 风险。
- **证据**：
  - 类 Javadoc `QuickSort.java:15` 仅声明「时间复杂度最坏 O(n²)」「空间复杂度 O(log n)」，未提示栈深度最坏为 O(n)。
  - 测试 `QuickSortTest.java:42-46` `alreadySorted` 仅覆盖 5 元素已序场景，未覆盖大数组栈深度边界。
- **等级**：**P1**（可靠性隐患，合并前应修复或显式约束）
- **建议**（任选其一，按风险最小原则优先 ①）：
  1. **最小改动**：在公开方法 Javadoc 补充「最坏递归深度 O(n)，不建议对超大规模已序输入直接使用」的约束说明，并修正空间复杂度描述为「平均 O(log n)，最坏 O(n)」。
  2. **三数取中**：取 `low/mid/high` 中位数为 pivot，显著降低已序输入的最坏概率（仍非理论最劣界）。
  3. **随机化 pivot**：`pivot = arr[low + random(high-low)]` 并与 `arr[high]` 交换，期望深度 O(log n)。
  4. **尾递归优化 / 小区间改插入排序**：对右半段循环化递归，小区间切换插入排序，工程级优化。

---

### [P2] 可读性 — 类 Javadoc 能力范围描述与实际签名不一致

- **位置**：`QuickSort.java:10`
- **现象**：类 Javadoc 写「提供对 `int[]` 原始数组与 `List<Integer>` 列表的快速排序实现」，但列表方法实际签名为泛型 `public static <E extends Comparable<E>> List<E> sort(List<E> list)`，支持**任意 `Comparable` 元素**（如 `String`、自定义 `Comparable` 类型）。
- **影响**：文档窄化了方法能力范围，误导调用方认为仅支持 `List<Integer>`，降低可维护性。
- **等级**：**P2**（文档与实现一致性）
- **建议**：将 `QuickSort.java:10` 改为「提供对 `int[]` 原始数组与 `List<E extends Comparable<E>>` 泛型列表的快速排序实现」。

---

### [P2] 可读性 — 测试 DisplayName「稳定」用词与不稳定排序契约相悖

- **位置**：`QuickSortTest.java:56`
- **现象**：`duplicateElements` 的 `@DisplayName` 写「含重复元素：**稳定**升序排列」，但类 Javadoc `QuickSort.java:17` 明确声明「快速排序为**不稳定**排序，相等元素相对次序可能改变」。
- **影响**：测试名暗示稳定性，与算法契约矛盾；虽然测试体仅用 `assertArrayEquals` 校验值（未真正验证稳定性），无功能 bug，但命名易误导维护者。
- **等级**：**P2**（命名/注释一致性）
- **建议**：将 DisplayName 改为「含重复元素：正确升序排列」或「含重复元素：去重后值序列正确」。

---

## 通过项核验

| 维度 | 结论 | 证据 |
|------|------|------|
| 功能正确性 | ✅ | 数组/列表双实现，边界（空/单元素/已序/逆序/重复/负数）单测全绿 |
| 入参不可变性 | ✅ | `Arrays.copyOf` / `new ArrayList<>(list)` 返回副本，测试 `returnsNewArrayImmutableInput` 验证原数组不变 |
| null 防御 | ✅ | 入参 null → `IllegalArgumentException`；元素 null → `NullPointerException`，契约与测试一致 |
| 工具类规范 | ✅ | `final class` + 私有构造器抛 `AssertionError`，无实例化路径 |
| 线程安全声明 | ✅ | 无状态静态方法，Javadoc:20-22 正确声明并发约束 |
| 测试结构 | ✅ | `@Nested` 分组、`@DisplayName` 中文语义、`assertThrows` 异常覆盖 |

---

## 自动化预扫结果（强制）

```
=== Step 4 Rule Scan (B/M/I + A/S/G) ===
Targets: QuickSort.java QuickSortTest.java
Engine:  ripgrep
=== No findings. 52/222 rules scanned ===
```

程序化规则无命中；上述 P1/P2 均为 LLM 人工补充审查发现，已在 `{T}-cr-checklist.md` 逐项勾选核销。

---

## 修复优先级建议

1. **P1（合并前）**：`QuickSort.java` 补充最坏递归深度约束文档 + 修正空间复杂度描述（最小改动方案 ①）。
2. **P2（可选）**：修正 `QuickSort.java:10` Javadoc 泛型描述；修正 `QuickSortTest.java:56` DisplayName「稳定」用词。
