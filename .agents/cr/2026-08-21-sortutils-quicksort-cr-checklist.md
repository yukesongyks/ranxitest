# Code Review Checklist

> **Change** SortUtils 快速排序实现 · **分支/Commit** `AI/task-DEV-966dcd0a-7905-11f1-9649-3b4281182f10-d0e973d6-65c6-41c9-b878-397062caa9c2` / `HEAD` · **日期** `2026-08-21`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **执行顺序（强制）**：写入本清单并进入逐文件审查前，先在目标仓库对变更路径运行 `references/script/scan-all-rules.sh`，将输出贴入 Step 3 和 Step 4 备注；再用 LLM 完成 Step 2–5 中脚本未覆盖项及复核。

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
| 1 | `my-spring-boot-app/src/main/java/com/example/myapp/utils/SortUtils.java` | 快速排序实现主类 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 2 | `my-spring-boot-app/src/test/java/com/example/myapp/utils/SortUtilsTest.java` | 快速排序单元测试 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |

- 由 `git diff --name-only …` 等展开；**禁止 glob**；非 Java 标 `跳过`（跳过文件的 Step4 各列可统一 `跳过` 或 `N/A(非 Java)`）。
- **守卫**：无 `.java` → 按技能终止。
- **收口**：每文件各 **Sn/Gn** 列均非 `⬜` 后，再与下方 Step 4 **逐条 ID 表** 核对一致；若某大类整节与当前文件无关，该列可一次性标 `N/A(无 SQL/无 MQ/…)`，但须在 Step 4 明细对应 ID 行同样标 `N/A` 并写原因。

---

## Step 2 — 功能（产物 B）

> 仅从 spec/tasks 提 **REQ**，勿臆造。不符 spec 标 **P0**。
> 每个 REQ 都必须填写 **spec 证据** 与 **关联文件**；若命中 P0，代码证据需落到 `path:line`、测试或接口行为。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | 实现快速排序算法，使用 in-place 排序，平均时间 O(n log n) | 需求描述：「实现一个快速排序算法」 | `SortUtils.java:24` | ✅ | `quickSort(int[])` 入口方法，递归+分区实现，median-of-three 优化 |
| REQ-2 | 处理 null 输入（抛出 NullPointerException） | 需求描述：「实现一个快速排序算法」（隐含需要健壮性） | `SortUtils.java:25` | ✅ | `Objects.requireNonNull(array, "array must not be null")` + 测试 `should_throwException_whenInputIsNull` |
| REQ-3 | 处理边界情况：空数组和单元素数组 | 需求描述：「实现一个快速排序算法」（隐含边界处理） | `SortUtils.java:26-28` | ✅ | `if (array.length <= 1) return;` + 测试 `should_sortEmptyArray` 和 `should_sortSingleElement` |
| REQ-4 | 测试覆盖多种场景：已排序、逆序、含重复值、负数、全相同值 | 需求描述：「实现一个快速排序算法」（隐含正确性验证） | `SortUtilsTest.java` | ✅ | 7 个测试用例覆盖全场景 |

---

## Step 3 — 可读性检查（产物 C）

> 无 Java：**整节 N/A**。

对照 `references/readability-checklist.md` A1–A7 逐节核销：

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | 文件名=类名, UTF-8, 无Tab |
| A2 | 源文件结构/import 顺序 | ✅ | package→import→类, 无通配符import, 静态/非静态分组, ASCII序 |
| A3 | 代码样式 | ✅ | K&R大括号, 4空格缩进, 行宽≤120, 运算符空格规范 |
| A4 | 命名规范 | ✅ | 包名全小写, 类名UpperCamelCase, 方法名lowerCamelCase, 测试类名SortUtilsTest |
| A5 | 编码实践 | ✅ | 静态方法通过类名调用(`SortUtils.quickSort`), 无空catch, 无finalize |
| A6 | 特定元素样式 | ✅ | 数组方括号类型风格(`int[]`), 修饰符顺序正确, 注解每行一个 |
| A7 | Javadoc 规范 | ✅ | SortUtils类和quickSort有Javadoc, 块标记顺序正确(@param→@throws) |

---

## Step 4 — 可靠性检查（产物 D）

> **逐条核销（强制）**：G/S 每个 ID **独占一行**，禁止合并为区间（例如 ~~`G1.1 ~ G14.3`~~）。**Bug 模式** 按 `bug-pattern-checklist.md` 中 **每条 B*/M*/I*** 独占一行核销（120 条）**；无关变更可对该 ID 标 `N/A` 并写原因。报告等级：**Blocker→P0、Major→P1、Info→P2**。

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> 可先运行 `references/script/scan-all-rules.sh`（对变更目录）将命中写入备注，再人工/LLM 补全脚本未覆盖规则。
> **预扫结果**：`scan-all-rules.sh` 对两个目标文件扫描 52/222 条规则，**无任何命中**。

| ID | 状态 | 备注（命中写 `path:line`；预扫可粘贴脚本摘要） |
|----|------|--------------------------------------------------|
| B001 | N/A | 无 parse/of 调用 |
| B002 | N/A | 无数组 equals 比较 |
| B003 | N/A | 无 Arrays.fill |
| B004 | N/A | 无数组 toString |
| B005 | N/A | 无 Arrays.asList |
| B006 | N/A | 无 assertEquals 参数顺序隐患 (使用 AssertJ) |
| B007 | N/A | 无捕获 Throwable |
| B008 | N/A | 无 Executors |
| B009 | N/A | 无移位操作 |
| B010 | N/A | 无 BigDecimal |
| B011 | N/A | 无包装类型 == 比较 |
| B012 | N/A | 无 Calendar |
| B013 | N/A | 无 Calendar |
| B014 | N/A | 无集合查询 |
| B015 | N/A | 无 toArray |
| B016 | N/A | 无 Comparable |
| B017 | N/A | 无 this == null |
| B018 | N/A | 无三目运算符隐患 |
| B019 | N/A | 无 Money API |
| B020 | N/A | 无常量溢出 |
| B021 | N/A | 无 Jedis |
| B022 | N/A | 无 SimpleDateFormat |
| B023 | N/A | 无死异常 |
| B024 | N/A | 无 Thread |
| B025 | N/A | 无双括号初始化 |
| B026 | N/A | 无 equals(null) |
| B027 | N/A | 无 equals 实现 |
| B028 | N/A | 无 DateUtil |
| B029 | N/A | 无 setter |
| B030 | N/A | 无浮点 == |
| B031 | N/A | 无 String.format |
| B032 | N/A | 无注解 getClass |
| B033 | N/A | 无 Unsafe |
| B034 | N/A | 无 Hashtable |
| B035 | N/A | 无恒等二元表达式 |
| B036 | N/A | 无 IdentityHashMap |
| B037 | N/A | 无可变参数三目 |
| B038 | ✅ | `quickSort` 递归有 left < right 终止条件，不会无限递归 |
| B039 | N/A | 无 indexOf |
| B040 | N/A | 无 isInstance |
| B041 | N/A | 无 JDBC |
| B042 | N/A | 非 JUnit3 |
| B043 | N/A | 无内部类测试 |
| B044 | N/A | 无混合 JUnit 风格 |
| B045 | N/A | 无锁对象 |
| B046 | ✅ | partition 循环 `for (int j = left; j < right; j++)` 条件正确，j 递增 |
| B047 | N/A | 无 Float.compare |
| B048 | N/A | 无 Math.round |
| B049 | N/A | 无日期格式 |
| B050 | N/A | 无日期格式 |
| B051 | N/A | 无 Boolean.getBoolean |
| B052 | N/A | 无日期格式 |
| B053 | N/A | 测试使用 `assertThatThrownBy`，不需要 fail |
| B054 | N/A | 无 EqualsTester |
| B055 | N/A | 无 Mockito |
| B056 | N/A | 无 Arrays.asList add/remove |
| B057 | N/A | 无增强 for 循环修改集合 |
| B058 | N/A | 无集合自修改 |
| B059 | N/A | 无 Collections.nCopies |
| B060 | ✅ | 无三目含 null 场景 |
| B061 | N/A | 无 BASE64 |
| B062 | N/A | 无 ClassLoader |
| B063 | N/A | 无 javax.xml |
| B064 | N/A | 无 Optional == |
| B065 | N/A | 无自赋值 |
| B066 | N/A | 无 Math.random 强转 |
| B067 | N/A | 无 Random 取余 |
| B068 | ✅ | 无自赋值 |
| B069 | N/A | 无 compareTo 自比较 |
| B070 | N/A | 无 equals 自比较 |
| B071 | N/A | 无 size() >= 0 |
| B072 | N/A | 无 Stream.toString |
| B073 | N/A | 无 StringBuilder |
| B074 | N/A | 无 substring(0) |
| B075 | ✅ | for 循环 `for (int j = left; j < right; j++)` 方向正确，自增 |
| B076 | N/A | 无 @Transactional |
| B077 | N/A | 测试无捕获 Throwable |
| B078 | N/A | 无 Truth self equals |
| B079 | N/A | 无 @Mock |
| B080 | ✅ | 每个测试方法包含断言 (assertThat / assertThatThrownBy) |
| B081 | N/A | 无集合原地修改 |
| M001 | N/A | 无重复条件判断 |
| M002 | N/A | 无 instanceof |
| M003 | N/A | 无包装类构造器 |
| M004 | N/A | 无 printStackTrace |
| M005 | N/A | 无内部类 |
| M006 | N/A | 无编译期常量布尔表达式 |
| M007 | N/A | 无空 catch |
| M008 | N/A | 无 equals/hashCode |
| M009 | N/A | 无跨类型 equals |
| M010 | N/A | 无比位运算 |
| M011 | N/A | 无 switch |
| M012 | N/A | 无 finally |
| M013 | N/A | 无类型转换 |
| M014 | N/A | 无枚举 |
| M015 | N/A | 无继承字段隐藏 |
| M016 | N/A | 无 LocalDateTime.now |
| M017 | N/A | 测试方法有 @Test 注解 |
| M018 | N/A | 无 Lock |
| M019 | N/A | 无枚举 switch |
| M020 | N/A | 无重写方法 |
| M021 | N/A | 无 equals |
| M022 | N/A | 无 Optional |
| M023 | N/A | 无 toString |
| M024 | N/A | 无 Optional |
| M025 | N/A | 无 final 类 protected 成员 |
| M026 | N/A | 无 @Mock |
| M027 | N/A | 无 ThreadLocal |
| I001 | N/A | 无异常断言 (测试用 assertThatThrownBy 已足够) |
| I002 | N/A | 无 @Mock |
| I003 | N/A | 无 @Mock |
| I004 | N/A | 无 java.util.Date |
| I005 | N/A | 非 JUnit3 |
| I006 | N/A | 无 setUp |
| I007 | N/A | 无 tearDown |
| I008 | N/A | 无 DataProvider |
| I009 | N/A | 不适用 |
| I010 | N/A | 无 Spring 容器 |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | N/A | 无并发/事务场景 |
| G1.2 | N/A | 同上 |
| G1.3 | N/A | 同上 |
| G1.4 | N/A | 同上 |
| G2.1 | N/A | 无写接口/消息消费 |
| G2.2 | N/A | 无重试/定时任务 |
| G2.3 | N/A | 无幂等键 |
| G3.1 | N/A | 无事务 |
| G3.2 | N/A | 无事务 |
| G4.1 | N/A | 无 SQL |
| G4.2 | N/A | 无 SQL |
| G4.3 | N/A | 无 SQL |
| G5.1 | N/A | 无 MQ |
| G6.1 | N/A | 无缓存 |
| G6.2 | N/A | 无缓存 |
| G7.1 | N/A | 无调度任务 |
| G7.2 | N/A | 无调度任务 |
| G8.1 | N/A | 无异常处理 |
| G8.2 | N/A | 无核心链路 |
| G8.3 | N/A | 无 I/O 流 |
| G8.4 | N/A | 无线程池 |
| G8.5 | N/A | 无 ThreadLocal |
| G8.6 | N/A | 无线程池 |
| G9.1 | N/A | 无网络调用 |
| G9.2 | N/A | 无网络调用 |
| G9.3 | N/A | 无重试 |
| G10.1 | N/A | 无接口契约 |
| G10.2 | N/A | 无接口契约 |
| G11.1 | ✅ | 测试类包含 7 个测试方法，均有断言 |
| G11.2 | ✅ | 覆盖边界：空数组、单元素、null |
| G11.3 | ✅ | `Objects.requireNonNull` 防御性校验 |
| G11.4 | ✅ | 仅操作 int 数组，无溢出/除零/精度问题 |
| G12.1 | N/A | 无资金场景 |
| G12.2 | N/A | 无资金场景 |
| G13.1 | N/A | 无日志 |
| G14.1 | N/A | 无金额 |
| G14.2 | N/A | 无多租户 |
| G14.3 | N/A | 无时区 |
| G14.4 | N/A | 无日期格式化 |
| G15.1 | N/A | 无数据库变更 |
| G15.2 | N/A | 无接口共存 |
| G15.3 | N/A | 无开关 |
| G16.1 | N/A | 无监控埋点 |
| G16.2 | N/A | 无日志 |
| G16.3 | N/A | 无日志 |
| G16.4 | N/A | 无 catch |
| G17.1 | N/A | 无功能开关 |
| G17.2 | N/A | 无降级 |
| G17.3 | N/A | 无数据变更 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | N/A | 无 SQL |
| S1.2 | N/A | 无 SQL |
| S1.3 | N/A | 无 SQL |
| S2.1 | N/A | 无 HTML/JS 输出 |
| S2.2 | N/A | 无富文本 |
| S2.3 | N/A | 无模板引擎 |
| S3.1 | N/A | 无外部 URL |
| S3.2 | N/A | 无外部 URL |
| S3.3 | N/A | 无外部 URL |
| S4.1 | N/A | 无系统命令 |
| S4.2 | N/A | 无文件操作 |
| S5.1 | N/A | 无 XML |
| S5.2 | N/A | 无 XML |
| S6.1 | N/A | 无反序列化 |
| S6.2 | N/A | 无反序列化 |
| S6.3 | N/A | 反序列化 |
| S7.1 | N/A | 无文件上传 |
| S7.2 | N/A | 无文件上传 |
| S7.3 | N/A | 无文件上传 |
| S8.1 | N/A | 无鉴权 |
| S8.2 | N/A | 无 GET |
| S8.3 | N/A | 无数据 ID |
| S8.4 | N/A | 无 Cookie |
| S9.1 | N/A | 无密钥 |
| S9.2 | N/A | 无日志 |
| S9.3 | N/A | 无传输加密 |
| S9.4 | N/A | 无随机数 |
| S10.1 | N/A | 无 CSRF |
| S10.2 | N/A | 无 CORS |
| S10.3 | N/A | 无跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

> 按 `customized-checklist.md` 逐条核销；若未启用可整节写 `N/A(未启用自定义规则)`。

### 5.1 自定义扩展（`customized-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | N/A(未启用自定义规则) | 仅示例项，未启用 |

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、**S1–S10 / G1–G17** 各列均非 `⬜`（跳过文件除外）；
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`（允许 `N/A`，但有原因）
- [x] Step 5 全部 U* ID 均非 `⬜`（允许 `N/A(未启用自定义规则)`）
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`