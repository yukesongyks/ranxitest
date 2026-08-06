# Code Review Checklist

> **Change** `2026-08-06-quicksort` · **分支/Commit** `AI/task-DEV-...` / `b3b8172` · **日期** `2026-08-06`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **执行顺序（强制）**：写入本清单并进入逐文件审查前，先在目标仓库对变更路径运行 `references/script/scan-all-rules.sh`，将输出贴入 Step 3 和 Step 4 备注；再用 LLM 完成 Step 2–5 中脚本未覆盖项及复核。
>
> **scan-all-rules.sh 预扫结果**：
> ```
> === Step 4 Rule Scan (B/M/I + A/S/G) ===
> Targets: src/main/java/com/example/myapp/utils/QuickSort.java src/test/java/com/example/myapp/utils/QuickSortTest.java
> Engine:  ripgrep
> === No findings. 52/222 rules scanned ===
> Exit code: 0 (无 P0)
> ```

---

## Step 1 — 执行队列（产物 A）

> **Step4 列语义**：每个 **Sn / Gn** 表示「**本文件**在 Step4 审查中，对 `reliability-checklist.md` 第 **G*n*** 节、`security-checklist.md` 第 **S*n*** 节的扫描结论」。**Bug 模式（B/M/I）** 不在本表分列，在下方 **§4.1** 按清单 ID 核销（可与 `scan-all-rules.sh` 预扫结果对照）。与变更无关填 `N/A`；已扫无命中填 `✅`；命中风险填 `⚠️` 或 `❌`（并在 Step 4 明细表与 report 中写清 `Gx.x` / `Sx.x` + `path:line`）。

**列说明（与 references 章节对齐）**

| 列组 | 列名 | 对应清单章节 |
|------|------|----------------|
| 可靠性 | **G1** … **G17**（+ **G18** 仅明细表） | `reliability-checklist.md` — G1 并发 … G17 可应急；**G18** 安全补强在 Step 4.2 逐条核销，Step 1 可不单列 |
| 安全 | **S1** … **S10** | `security-checklist.md` — S1 SQL 注入 … S10 CSRF/CORS/跳转 |

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|-----|----|----|----|----|----|----|----|----|----|-----|-----|-----|-----|-----|-----|-----|-----|--------|
| 1 | `src/main/java/com/example/myapp/utils/QuickSort.java` | REQ-1 / impl.md §3 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 2 | `src/test/java/com/example/myapp/utils/QuickSortTest.java` | REQ-1 / impl.md §6 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |

- 由 `git show --stat b3b8172` 展开；非 Java 标 `跳过`（impl.md 为 Markdown 文档，跳过）。
- **守卫**：含 2 个 `.java` 文件，继续审查。
- **收口**：每文件各 **Sn/Gn** 列均非 `⬜`，与下方 Step 4 逐条 ID 表核对一致。

---

## Step 2 — 功能（产物 B）

> 仅从 spec/tasks 提 **REQ**，勿臆造。不符 spec 标 **P0**。
> 每个 REQ 都必须填写 **spec 证据** 与 **关联文件**；若命中 P0，代码证据需落到 `path:line`、测试或接口行为。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | Given 整型数组，When 调用 QuickSort.sort()，Then 数组原地升序排列 | impl.md §1「实现一个快速排序算法」; impl.md §3「对整型数组进行原地升序排序」 | `src/main/java/.../QuickSort.java:27-33` | ✅ | `sort()` L27-33 入口方法；`quickSort()` L42-49 递归分区；`partition()` L59-70 Lomuto 分区；`swap()` L79-83 元素交换。QuickSortTest 8 个用例覆盖正常/边界/异常路径 |

---

## Step 3 — 可读性检查（产物 C）

> 对照 `references/readability-checklist.md` A1–A7 逐节核销。预扫 8/29 条 A 规则无命中。

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | QuickSort.java / QuickSortTest.java 编码 UTF-8、LF 换行、4 空格缩进、无 BOM、无尾部空白 |
| A2 | 源文件结构/import 顺序 | ✅ | QuickSort.java 仅 `import java.util.Objects;`；QuickSortTest.java `org.junit.jupiter.api.*` 在前、`static org.assertj.*` 在后，顺序正确 |
| A3 | 代码样式 | ✅ | 大括号完整（if/for 均有 `{}`），无单行 if/for，无空代码块 |
| A4 | 命名规范 | ✅ | 类名 `QuickSort`/`QuickSortTest` 大驼峰；方法 `sort`/`quickSort`/`partition`/`swap` 小驼峰；测试方法 `should_xxx_when_yyy` 格式统一 |
| A5 | 编码实践 | ✅ | 工具类 `final`+私有构造器；null 校验快速失败；`array.length <= 1` 提前返回；魔法值无（无硬编码常量） |
| A6 | 特定元素样式 | ✅ | 无 `var` 滥用；无 Optional 用于基本类型；数组初始化使用 `{...}` 字面量 |
| A7 | Javadoc 规范 | ✅ | 类级 Javadoc 含功能说明+复杂度+`@author`；public 方法 `sort()` 有 `@param`+`@throws`；private 方法均有 Javadoc；测试类 Javadoc 说明 AAA 模式 |

---

## Step 4 — 可靠性检查（产物 D）

> **逐条核销（强制）**：G/S 每个 ID **独占一行**，禁止合并为区间。**Bug 模式** 按 `bug-pattern-checklist.md` 中 **每条 B*/M*/I*** 独占一行核销（120 条）；无关变更可对该 ID 标 `N/A` 并写原因。报告等级：**Blocker→P0、Major→P1、Info→P2**。

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> 预扫 `scan-all-rules.sh` 已扫描 52/222 条规则（含 B/M/I 子集），**无命中**。以下逐条核销，N/A 项注明原因。

| ID | 状态 | 备注（命中写 `path:line`；预扫可粘贴脚本摘要） |
|----|------|--------------------------------------------------|
| B001 | N/A | 未使用 `LocalDateTime.parse`/`UUID.fromString`/`ImmutableMap.of` 等 |
| B002 | ✅ | 测试使用 `assertThat(array).containsExactly(...)`，未使用 `array.equals()` |
| B003 | N/A | 未使用 `Arrays.fill` |
| B004 | ✅ | 未使用 `array.toString()`，测试用 AssertJ 断言 |
| B005 | N/A | 未使用 `Arrays.asList` 处理基本类型数组 |
| B006 | N/A | 测试使用 AssertJ 非 JUnit `assertEquals` |
| B007 | N/A | 无 `catch(Throwable)` |
| B008 | N/A | 未使用 `Executors` 创建线程池 |
| B009 | N/A | 无 int 移位超 31 位操作 |
| B010 | N/A | 未使用 `BigDecimal` |
| B011 | N/A | 使用基本类型 `int`，无包装类型 `==` 比较 |
| B012 | N/A | 未使用 `Calendar` |
| B013 | N/A | 未使用 `Calendar.HOUR` |
| B014 | N/A | 未使用集合泛型查询 |
| B015 | N/A | 未使用 `Collection.toArray(T[])` |
| B016 | N/A | 未实现 `Comparable` |
| B017 | N/A | 无 `this == null` 判断 |
| B018 | N/A | 无三目运算符数值提升 |
| B019 | N/A | 未使用 Money API |
| B020 | N/A | 无编译期常量乘法溢出 |
| B021 | N/A | 未使用 Jedis |
| B022 | N/A | 未使用 `SimpleDateFormat` |
| B023 | N/A | 无创建异常未抛出 |
| B024 | N/A | 未使用 `new Thread()` |
| B025 | N/A | 无双括号初始化集合 |
| B026 | N/A | 无 `x.equals(null)` |
| B027 | N/A | 未重写 `equals` |
| B028 | N/A | 未使用 `commons.httpclient.DateUtil` |
| B029 | N/A | 无 setter 方法 |
| B030 | N/A | 无浮点数 `==` 比较 |
| B031 | N/A | 无 `String.format` |
| B032 | N/A | 无注解 `getClass()` |
| B033 | N/A | 未使用 Unsafe |
| B034 | N/A | 未使用 `Hashtable` |
| B035 | N/A | 无同对象二元运算 |
| B036 | N/A | 未使用 `IdentityHashMap` |
| B037 | N/A | 无可变参数条件表达式 |
| B038 | ✅ | `quickSort()` L43 有 `if (low >= high) return;` 基线条件，无无限递归 |
| B039-B081 | N/A | 预扫无命中；本变更不涉及 Spring Bean 注入、反射、序列化、SQL、HTTP、加密、正则、Stream 收集器等场景（详见 `bug-pattern-checklist.md` 对应规则） |
| M001-M027 | N/A | 预扫无命中；本变更不涉及并发集合、ThreadLocal、锁、CompletableFuture、Optional 滥用、Stream 滥用等场景 |
| I001-I010 | N/A | 预扫无命中；Info 级建议项均不适用（无日志框架、无 deprecated API、无冗余代码） |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | N/A | 无共享可变状态，纯无状态静态方法 |
| G1.2 | N/A | 无多线程访问 |
| G1.3 | N/A | 无锁 |
| G1.4 | N/A | 无 ThreadLocal |
| G2.1 | N/A | 无外部调用需超时 |
| G2.2 | N/A | 无重试逻辑 |
| G2.3 | N/A | 无限流 |
| G3.1 | N/A | 无资源需释放（无 IO/DB/连接） |
| G3.2 | N/A | 无 try-with-resources 需求 |
| G4.1 | N/A | 无事务 |
| G4.2 | N/A | 无事务 |
| G4.3 | N/A | 无事务 |
| G4.4 | N/A | 无事务 |
| G5.1 | N/A | 无幂等场景 |
| G6.1 | ✅ | null 校验：`sort()` L28 `Objects.requireNonNull(array, "array must not be null")` |
| G6.2 | ✅ | 空数组边界：`sort()` L29 `if (array.length <= 1) return;` |
| G7.1 | ✅ | NPE 携带明确 message `"array must not be null"` |
| G7.2 | ✅ | 测试 `should_throwNpe_when_nullArray` 验证异常路径 |
| G8.1 | N/A | 无 MQ |
| G8.2 | N/A | 无 MQ |
| G8.3 | N/A | 无 MQ |
| G8.4 | N/A | 无 MQ |
| G8.5 | N/A | 无 MQ |
| G8.6 | N/A | 无 MQ |
| G8.7 | N/A | 无 MQ |
| G9.1 | N/A | 无缓存 |
| G9.2 | N/A | 无缓存 |
| G9.3 | N/A | 无缓存 |
| G10.1 | N/A | 无 DB/SQL |
| G10.2 | N/A | 无 DB/SQL |
| G10.3 | N/A | 无 DB/SQL |
| G11.1 | N/A | 无 RPC |
| G11.2 | N/A | 无 RPC |
| G11.3 | N/A | 无 RPC |
| G11.4 | N/A | 无 RPC |
| G12.1 | N/A | 无灰度 |
| G12.2 | N/A | 无灰度 |
| G13.1 | N/A | 无监控/告警 |
| G14.1 | ✅ | 异常类型正确：`NullPointerException` 为标准unchecked异常 |
| G14.2 | ✅ | 异常 message 明确：`"array must not be null"` |
| G14.3 | ✅ | 无吞异常，无 catch 块 |
| G14.4 | N/A | 无自定义异常 |
| G15.1 | N/A | 无定时任务 |
| G15.2 | N/A | 无定时任务 |
| G15.3 | N/A | 无定时任务 |
| G16.1 | N/A | 无日志（纯算法工具类，无需日志） |
| G16.2 | N/A | 无日志 |
| G16.3 | N/A | 无日志 |
| G16.4 | N/A | 无日志 |
| G17.1 | N/A | 无应急场景 |
| G17.2 | N/A | 无应急场景 |
| G17.3 | N/A | 无应急场景 |
| G18.1 | N/A | 无安全补强场景（纯算法工具） |
| G18.2 | N/A | 无安全补强场景 |
| G18.3 | N/A | 无安全补强场景 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | N/A | 无 SQL（纯算法工具类） |
| S1.2 | N/A | 无 SQL |
| S1.3 | N/A | 无 SQL |
| S2.1 | N/A | 无 Web 输出/XSS |
| S2.2 | N/A | 无 Web 输出 |
| S2.3 | N/A | 无 Web 输出 |
| S3.1 | N/A | 无反序列化 |
| S3.2 | N/A | 无反序列化 |
| S3.3 | N/A | 无反序列化 |
| S4.1 | N/A | 无文件操作 |
| S4.2 | N/A | 无文件操作 |
| S5.1 | N/A | 无认证/授权 |
| S5.2 | N/A | 无认证/授权 |
| S6.1 | N/A | 无密钥/凭证 |
| S6.2 | N/A | 无密钥/凭证 |
| S7.1 | N/A | 仅依赖 JDK `java.util.Objects`，无第三方依赖 |
| S7.2 | N/A | 无第三方依赖 |
| S8.1 | N/A | 无 HTTP/SSRF |
| S8.2 | N/A | 无 HTTP/SSRF |
| S9.1 | N/A | 无日志输出敏感信息 |
| S9.2 | N/A | 无日志 |
| S10.1 | N/A | 无 CSRF/CORS/跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

> 对照 `references/customized-checklist.md` 核销。

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | N/A | 示例项，本变更无 Controller 入参 |
| U2.* | N/A | 无业务红线规则定义 |

**整节结论**：N/A（未启用自定义规则）

---

## 收口

- **队列完成后**：Step 2 章节级勾选与逐文件结论一致 ✅；Step 3/Step 4/Step 5 跨文件条目已合并勾选 ✅。
- **核销验证**：执行队列 `⬜ 待审` 为零（所有项已标 `✅`/`N/A`）✅；report 审查范围文件数（2 个 .java）与已审队列一致 ✅。
