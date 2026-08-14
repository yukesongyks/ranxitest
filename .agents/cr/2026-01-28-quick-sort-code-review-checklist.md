# Code Review Checklist

> **Change** `quick-sort` · **分支/Commit** `AI/task-DEV-f4ad1a6e-...` · **日期** `2026-01-28`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **执行说明**：`scan-all-rules.sh` 因执行环境限制（bwrap 不可用）未运行，由 LLM 全量覆盖所有规则。

---

## Step 1 — 执行队列（产物 A）

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|-----|----|----|----|----|----|----|----|----|----|-----|-----|-----|-----|-----|-----|-----|-----|--------|
| 1 | `my-spring-boot-app/src/main/java/com/example/myapp/util/QuickSort.java` | REQ-1~6 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 2 | `my-spring-boot-app/src/test/java/com/example/myapp/util/QuickSortTest.java` | REQ-1~6 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |

- **守卫**：变更包含 2 个 `.java` 文件，继续审查。
- **收口**：所有文件 Step2/Step3/Step4/Step5 均已核销。

---

## Step 2 — 功能（产物 B）

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | `sort(T[])` 对泛型 Comparable 数组排序 | `docs/modules/util/README.md:23` — `static <T extends Comparable<T>> void sort(T[] array)` | `QuickSort.java` | ✅ | `QuickSort.java:25-30`；测试 `should_sortArray_when_unsortedIntegers` 等 |
| REQ-2 | `sort(int[])` 对 int 基本类型数组排序 | `docs/modules/util/README.md:24` — `static void sort(int[] array)` | `QuickSort.java` | ✅ | `QuickSort.java:38-43`；测试 `should_sortIntArray_when_unsorted` 等 |
| REQ-3 | 原地排序（in-place），不分配新数组 | `docs/modules/util/README.md:11` — "原地排序" | `QuickSort.java` | ✅ | 所有方法通过索引交换操作，无 `new T[]` 或 `Arrays.copyOf` |
| REQ-4 | 无 Spring 依赖，纯 JDK 标准库 | `docs/modules/util/README.md:16` — "无 Spring 依赖"；`:17` — "纯 JDK 标准库" | `QuickSort.java` | ✅ | 仅依赖 `java.lang.Comparable`（JDK 内置），无 `org.springframework` 导入 |
| REQ-5 | 工具类不可实例化 | `docs/modules/util/README.md:11` — "工具类" | `QuickSort.java` | ✅ | `QuickSort.java:12` `final class`；`:14` `private QuickSort()` |
| REQ-6 | null 入参抛出 `IllegalArgumentException` | `QuickSort.java:23` — `@throws IllegalArgumentException 如果 array 为 {@code null}` | `QuickSort.java` | ✅ | `QuickSort.java:26-28`（泛型版）；`:39-41`（int 版）；测试 `should_throwException_when_arrayIsNull` |

---

## Step 3 — 可读性检查（产物 C）

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | A1.1 文件名=类名+`.java`；A1.2 UTF-8；A1.3 无 Tab |
| A2 | 源文件结构/import 顺序 | ✅ | A2.1 package→class 有空行；A2.2 无 `import *`；A2.5 重载方法连续 |
| A3 | 代码样式 | ✅ | A3.1 K&R 大括号；A3.3 4空格缩进；A3.4 行宽≤120；A3.7 `if (` 空格；A3.8 二元运算符两侧空格 |
| A4 | 命名规范 | ✅ | A4.1 包名全小写；A4.2 类名 UpperCamelCase；A4.3 方法名 lowerCamelCase；A4.5 无前缀后缀；A4.6 泛型 `T`；A4.7 测试类 `QuickSortTest` |
| A5 | 编码实践 | ✅ | A5.1 无重写需 `@Override`；A5.3 静态方法类名调用 |
| A6 | 特定元素样式 | ✅ | A6.1 `T[] array` 括号在类型；A6.3 修饰符顺序正确 |
| A7 | Javadoc 规范 | ✅ | A7.1 public 类/方法均有 Javadoc；A7.2 `@param→@return→@throws` 顺序正确；A7.4 使用 `<p>` 分段 |

---

## Step 4 — 可靠性检查（产物 D）

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> 脚本未运行，由 LLM 逐条核销。仅列出与变更相关的规则，其余 N/A。

| ID | 状态 | 备注 |
|----|------|------|
| B001 | N/A | 无 `parse`/`of` 字面量调用 |
| B002 | N/A | 无 `array.equals()` |
| B006 | N/A | 使用 AssertJ `assertThat().containsExactly()`，非 JUnit `assertEquals` |
| B007 | N/A | 无 `catch(Throwable)` |
| B008 | N/A | 无 `Executors` |
| B016 | ✅ | `QuickSort.java:25` — `T extends Comparable<T>`，泛型约束正确，非实现类 |
| B017 | N/A | 无 `this == null` |
| B038 | ✅ | `QuickSort.java:53` — 有 `left >= right` 终止条件，非无条件递归 |
| B046 | ✅ | `QuickSort.java:94` — `for (int j = left; j < right; j++)`，j 每轮递增 |
| B053 | N/A | 测试用 `assertThatThrownBy`，非 try-fail-catch |
| B060 | N/A | 无 null 三目表达式 |
| B075 | ✅ | `QuickSort.java:94` — `j < right` 且 `j++`，方向一致 |
| B080 | ✅ | `QuickSortTest.java` — 所有测试方法均含 `assertThat` 断言 |
| M004 | N/A | 无 `printStackTrace()` |
| M005 | N/A | 无内部类 |
| M007 | N/A | 无 catch 块 |
| M011 | N/A | 无 switch 语句 |
| M020 | N/A | 无重写方法 |
| 其余 B/M/I | N/A | 与纯算法工具类无关（涉及 JDBC/线程池/日期/集合/Spring 等） |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1–G1.4 | N/A | 无并发/锁场景 |
| G2.1–G2.3 | N/A | 无写接口/消息消费 |
| G3.1–G3.2 | N/A | 无事务/数据库 |
| G4.1–G4.3 | N/A | 无 SQL |
| G5.1 | N/A | 无 MQ |
| G6.1–G6.2 | N/A | 无缓存 |
| G7.1–G7.2 | N/A | 无调度任务 |
| G8.1 | ✅ | 代码处理了 null/空数组/单元素等边界 |
| G8.2 | N/A | 无外部依赖 |
| G8.3 | N/A | 无 I/O 资源 |
| G8.4–G8.6 | N/A | 无线程池/ThreadLocal |
| G9.1–G9.3 | N/A | 无网络调用 |
| G10.1–G10.2 | N/A | 无接口契约变更 |
| G11.1 | ✅ | 有完整单测（`QuickSortTest.java`，16 个测试方法） |
| G11.2 | ✅ | 覆盖空数组/单元素/已排序/逆序/重复/大数据集(1000) |
| G11.3 | ✅ | `QuickSort.java:26-28` / `:39-41` — null 入参抛 `IllegalArgumentException` |
| G11.4 | N/A | 无数值运算（仅比较与交换） |
| G12.1–G12.2 | N/A | 无资金场景 |
| G13.1 | N/A | 无日志 |
| G14.1–G14.4 | N/A | 无金额/多租户/时区 |
| G15.1–G15.3 | N/A | 无灰度/接口变更 |
| G16.1–G16.4 | N/A | 无日志/监控 |
| G17.1–G17.3 | N/A | 无应急/开关 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1–S1.3 | N/A | 无 SQL |
| S2.1–S2.3 | N/A | 无 Web 输出 |
| S3.1–S3.3 | N/A | 无外部 URL 请求 |
| S4.1–S4.2 | N/A | 无系统命令 |
| S5.1–S5.2 | N/A | 无 XML 解析 |
| S6.1–S6.3 | N/A | 无反序列化 |
| S7.1–S7.3 | N/A | 无文件上传/下载 |
| S8.1–S8.4 | N/A | 无 Web 接口 |
| S9.1–S9.4 | N/A | 无密钥/敏感数据 |
| S10.1–S10.3 | N/A | 无 CSRF/CORS/跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

### 5.1 自定义扩展（`customized-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | N/A | 示例项（Controller `@Valid`），与 util 工具类无关 |
| U2.* | N/A | 未定义业务红线规则 |

**结论**：`N/A(未启用自定义规则)` — 自定义清单仅含示例项，无项目特定规则。

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、**S1–S10 / G1–G17** 各列均非 `⬜`
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`（6 个 REQ 全部 ✅）
- [x] Step 3 的 A1–A7 均非 `⬜`（全部 ✅）
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`
- [x] Step 5 全部 U* ID 均非 `⬜`
- [x] 无 `❌/⚠️` 项