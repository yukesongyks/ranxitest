# Code Review Checklist

> **Change** `实现一个快速排序算法` · **分支/Commit** `AI/task-DEV-966dcd0a` · **日期** `2026-08-06`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **scan-all-rules.sh 预扫结果**：`=== No findings. 52/222 rules scanned ===`（引擎 ripgrep，目标：QuickSort.java + QuickSortTest.java）

---

## Step 1 — 执行队列（产物 A）

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | 总状态 |
|---|----------------------|----------|-------|-------|--------|
| 1 | `my-spring-boot-app/src/main/java/com/example/myapp/util/QuickSort.java` | REQ-1 快速排序实现 | ✅ | ⚠️ | ⚠️ 已审有问题 |
| 2 | `my-spring-boot-app/src/test/java/com/example/myapp/util/QuickSortTest.java` | REQ-1 测试验证 | ✅ | ⚠️ | ⚠️ 已审有问题 |

---

## Step 2 — 功能（产物 B）

> REQ 来源：需求描述「实现一个快速排序算法」

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | Given 乱序数组，When 调用 sort()，Then 返回升序排序数组 | 需求描述原文：「实现一个快速排序算法」 | `QuickSort.java:32` `QuickSortTest.java:24` | ✅ | `sort()` 方法 (L32-38)、`quickSort()` (L48-56)、`partition()` (L67-80)、`swap()` (L90-94)；测试覆盖乱序/空/null/单元素/已排序/逆序/重复/泛型 8 个场景 |

---

## Step 3 — 可读性检查（产物 C）

对照 `references/readability-checklist.md` A1–A7 逐节核销：

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | 文件名=类名+.java；UTF-8；无 Tab |
| A2 | 源文件结构/import 顺序 | ⚠️ | `QuickSort.java:3-4` 未使用 import（`java.util.Arrays`、`java.util.Collections`）；`QuickSortTest.java:6-7` 同样未使用 |
| A3 | 代码样式 | ✅ | K&R 大括号；4 空格缩进；行宽 ≤120；运算符两侧空格 |
| A4 | 命名规范 | ✅ | 包名全小写；类名 UpperCamelCase；方法名 lowerCamelCase；泛型 `T` |
| A5 | 编码实践 | ✅ | 静态方法用类名调用；无私有构造器外的不当实践 |
| A6 | 特定元素样式 | ✅ | 数组方括号属于类型 `T[]`；修饰符顺序正确 |
| A7 | Javadoc 规范 | ⚠️ | `QuickSort.java:10` 空间复杂度描述「O(log n)」未标注为平均情况，最坏情况为 O(n) |

---

## Step 4 — 可靠性检查（产物 D）

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> 预扫：scan-all-rules.sh 52/222 条无命中。以下为 LLM 逐条补扫结果。

| ID | 状态 | 备注 |
|----|------|------|
| B001 | N/A | 无 parse/of 字面量调用 |
| B002 | N/A | 无 array.equals() 比较 |
| B003 | N/A | 无 Arrays.fill |
| B004 | N/A | 无数组 toString() |
| B005 | N/A | 无 Arrays.asList |
| B006 | N/A | 使用 AssertJ 非 assertEquals |
| B007 | N/A | 无 catch Throwable/Error |
| B008 | N/A | 无 Executors 线程池 |
| B009 | N/A | 无移位操作 |
| B010 | N/A | 无 BigDecimal |
| B011 | ✅ | 使用 compareTo 非包装类 == 比较 |
| B012 | N/A | 无 Calendar 操作 |
| B013 | N/A | 无 Calendar.HOUR |
| B014 | N/A | 无集合泛型不兼容查询 |
| B015 | N/A | 无 toArray 调用 |
| B016 | ✅ | `Comparable<? super T>` 泛型约束正确 |
| B017 | N/A | 无 this == null 判断 |
| B018 | N/A | 无三目运算数值提升 |
| B019 | N/A | 无 Money API |
| B020 | N/A | 无编译期常量乘法溢出 |
| B021 | N/A | 无 Jedis |
| B022 | N/A | 无 SimpleDateFormat |
| B023 | N/A | 无未抛出的异常实例 |
| B024 | N/A | 无未 start 的 Thread |
| B025 | N/A |无双括号初始化 |
| B026 | N/A | 无 .equals(null) |
| B027 | N/A | 无 equals 实现 |
| B028 | N/A | 无 DateUtil |
| B029 | N/A | 无 setter |
| B030 | N/A | 无浮点 == 比较 |
| B031 | N/A | 无 String.format |
| B032 | N/A | 无注解 getClass() |
| B033 | N/A | 无 Unsafe |
| B034 | N/A | 无 Hashtable |
| B035 | N/A | 无同一对象二元运算 |
| B036 | N/A | 无 IdentityHashMap |
| B037 | N/A | 无可变参数条件表达式 |
| B038 | ✅ | quickSort 有终止条件 `low >= high` (L49) |
| B039 | N/A | 无 indexOf 参数颠倒 |
| B040 | N/A | 无 isInstance 不兼容类型 |
| B041 | N/A | 无 JDBC |
| B042 | N/A | JUnit5 非 JUnit3 |
| B043 | N/A | 无内部类 @Test |
| B044 | N/A | 无 JUnit3/4 混用 |
| B045 | N/A | 无包装类型加锁 |
| B046 | ✅ | for 循环 `j < high; j++` 条件正确 (L70) |
| B047 | N/A | 无数值 compare 类型转换 |
| B048 | N/A | 无 Math.round 整型 |
| B049 | N/A | 无日期格式 DD |
| B050 | N/A | 无日期格式 hh/HH |
| B051 | N/A | 无 Boolean.getBoolean |
| B052 | N/A | 无 YYYY 格式 |
| B053 | N/A | 无期望异常测试缺 fail() |
| B054 | N/A | 无 EqualsTester |
| B055 | N/A | 无 Mockito |
| B056 | N/A | 无 Arrays.asList 修改 |
| B057 | N/A | 无增强 for 循环修改集合 |
| B058 | N/A | 无集合传入自身 |
| B059 | N/A | 无 nCopies |
| B060 | N/A | 无三目运算 null 拆箱 |
| B061 | N/A | 无 sun.misc.BASE64Encoder |
| B062 | N/A | 无 ClassLoader 强转 |
| B063 | N/A | 无 javax.xml |
| B064 | N/A | 无 Optional == 比较 |
| B065 | N/A | 无 Pojo 自赋值 |
| B066 | N/A | 无 Math.random 强转 |
| B067 | N/A | 无 Random 取余 |
| B068 | N/A | 无变量自赋值 |
| B069 | N/A | 无 compareTo 自身比较 |
| B070 | N/A | 无 equals 自身比较 |
| B071 | N/A | 无 size() >= 0 |
| B072 | N/A | 无 Stream.toString() |
| B073 | N/A | 无 StringBuilder(char) |
| B074 | N/A | 无 substring(0) |
| B075 | ✅ | for 循环条件与增量方向一致 (L70) |
| B076 | N/A | 无 @Transactional |
| B077 | N/A | 无单测 catch Throwable |
| B078 | ✅ | `isSameAs` 非 isEqualTo，为引用同一性测试 (Test:144) |
| B079 | N/A | 无 @Mock 赋值 |
| B080 | ✅ | 所有测试含 assertThat 断言 |
| B081 | N/A | 无新建集合原地修改 |
| M001 | N/A | 无连续相同条件判断 |
| M002 | N/A | 无子类 instanceof 父类 |
| M003 | N/A | 无包装类构造器 |
| M004 | N/A | 无 printStackTrace |
| M005 | N/A | 无非 static 内部类 |
| M006 | N/A | 无编译期常量布尔表达式 |
| M007 | N/A | 无空 catch |
| M008 | N/A | 未重写 equals/hashCode |
| M009 | N/A | 无不兼容类型 equals |
| M010 | N/A | 无位运算结果恒 0 |
| M011 | N/A | 无 switch fall-through |
| M012 | N/A | 无 finally return/throw |
| M013 | N/A | 无类型转换优先级问题 |
| M014 | N/A | 无枚举 getClass() |
| M015 | N/A | 无字段隐藏 |
| M016 | N/A | 无 LocalDateTime.now() 默认时区 |
| M017 | N/A | 测试均有 @Test 注解 |
| M018 | N/A | 无 lock() 后非 try |
| M019 | N/A | 无枚举 switch 缺 default |
| M020 | N/A | 无重写方法缺 @Override |
| M021 | N/A | 无非重写 equals |
| M022 | N/A | 无 Optional.of(null) |
| M023 | N/A | 无 Object.toString() 打印 |
| M024 | N/A | 无 Optional.get() 空值 |
| M025 | ✅ | final 类无 protected 成员 |
| M026 | N/A | 无 static @Mock |
| M027 | N/A | 无非 static ThreadLocal |
| I001 | N/A | 无异常类型断言 |
| I002 | N/A | 无 @DoNotMock |
| I003 | N/A | 无 @AutoValue |
| I004 | N/A | 无 java.util.Date |
| I005 | N/A | 非 JUnit3 |
| I006 | N/A | 无 setUp() 缺 @Before |
| I007 | N/A | 无 tearDown() 缺 @After |
| I008 | N/A | 无 @DataProvider |
| I009 | N/A | 仅供统计 |
| I010 | N/A | 无 @RunWith(PandoraBootRunner) |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | N/A | 无并发先读后写 |
| G1.2 | N/A | 无加锁后未二次校验 |
| G1.3 | N/A | 无乐观锁重试 |
| G1.4 | N/A | 无多锁顺序不一致 |
| G2.1 | N/A | 无写接口幂等 |
| G2.2 | N/A | 无重试/MQ 重投 |
| G2.3 | N/A | 无幂等键约定 |
| G3.1 | N/A | 无分布式事务 |
| G3.2 | N/A | 无 @Transactional |
| G4.1 | N/A | 无 SQL |
| G4.2 | N/A | 无 SQL 索引 |
| G4.3 | N/A | 无大列表查询 |
| G5.1 | N/A | 无 MQ 消费 |
| G6.1 | N/A | 无缓存 |
| G6.2 | N/A | 无缓存双写 |
| G7.1 | N/A | 无调度任务 |
| G7.2 | N/A | 无调度熔断 |
| G8.1 | N/A | 无 catch 吞异常 |
| G8.2 | N/A | 无核心链路强依赖 |
| G8.3 | N/A | 无 I/O 流/连接/锁需释放 |
| G8.4 | N/A | 无线程池 shutdown |
| G8.5 | N/A | 无 ThreadLocal |
| G8.6 | N/A | 无 Executors 无界队列线程池 |
| G9.1 | N/A | 无外部调用 |
| G9.2 | N/A | 无外部调用超时 |
| G9.3 | N/A | 无重试前状态查询 |
| G10.1 | N/A | 无接口 null 语义 |
| G10.2 | N/A | 无契约变更 |
| G11.1 | ✅ | 新逻辑有单测且含断言 |
| G11.2 | ✅ | 覆盖边界：null、空、单元素、已排序、逆序、重复 |
| G11.3 | ✅ | 入参 null/空数组有防御性校验 (L33-35) |
| G11.4 | N/A | 无数值溢出/除零/精度问题 |
| G12.1 | N/A | 无资金场景 |
| G12.2 | N/A | 无止血手段需求 |
| G13.1 | N/A | 无日志级别问题 |
| G14.1 | N/A | 无金额 double |
| G14.2 | N/A | 无多租户 |
| G14.3 | N/A | 无时区存储 |
| G14.4 | N/A | 无格式化未指定时区 |
| G15.1 | N/A | 无数据库变更 |
| G15.2 | N/A | 无新旧接口共存 |
| G15.3 | N/A | 无开关控制切换 |
| G16.1 | N/A | 无核心链路埋点 |
| G16.2 | N/A | 无异常路径日志 |
| G16.3 | N/A | 无日志级别问题 |
| G16.4 | N/A | 无空 catch/printStackTrace |
| G17.1 | N/A | 无功能开关 |
| G17.2 | N/A | 无降级预案 |
| G17.3 | N/A | 无数据回滚脚本 |
| G18.1 | N/A | 无安全补强项 |
| G18.2 | N/A | 无安全补强项 |
| G18.3 | N/A | 无安全补强项 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | N/A | 无 SQL（纯算法工具类） |
| S1.2 | N/A | 无 SQL |
| S1.3 | N/A | 无 SQL |
| S2.1 | N/A | 无 HTML/JS 输出 |
| S2.2 | N/A | 无富文本 |
| S2.3 | N/A | 无模板引擎 |
| S3.1 | N/A | 无外部 URL 请求 |
| S3.2 | N/A | 无 302 跳转 |
| S3.3 | N/A | 无超时设置需求 |
| S4.1 | N/A | 无系统命令拼接 |
| S4.2 | N/A | 无外部命令调用 |
| S5.1 | N/A | 无 XML 解析 |
| S5.2 | N/A | 无 XPath |
| S6.1 | N/A | 无反序列化 |
| S6.2 | N/A | 无 JSON 反序列化多态 |
| S6.3 | N/A | 无敏感字段 transient |
| S7.1 | N/A | 无文件上传 |
| S7.2 | N/A | 无路径过滤 |
| S7.3 | N/A | 无文件重命名 |
| S8.1 | N/A | 无接口鉴权（工具类） |
| S8.2 | N/A | 无 GET 增删改 |
| S8.3 | N/A | 无数据 ID 预测性 |
| S8.4 | N/A | 无 Cookie |
| S9.1 | N/A | 无密钥硬编码 |
| S9.2 | N/A | 无日志敏感信息 |
| S9.3 | N/A | 无传输加密 |
| S9.4 | N/A | 无 SecureRandom |
| S10.1 | N/A | 无 CSRF Token |
| S10.2 | N/A | 无 CORS |
| S10.3 | N/A | 无 URL 跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

> `customized-checklist.md` 仅含示例项（U1.1 Controller 入参校验），与本变更无关。

### 5.1 自定义扩展（`customized-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | N/A | 示例项，不适用于算法工具类 |
| U1.2 | N/A | 未启用自定义规则 |
| U1.3 | N/A | 未启用自定义规则 |
| U2.1 | N/A | 未启用自定义规则 |
| U2.2 | N/A | 未启用自定义规则 |
| U2.3 | N/A | 未启用自定义规则 |

N/A(未启用自定义规则) — customized-checklist.md 仅含示例项

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3` 各列均非 `⬜`（跳过文件除外）；
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`（允许 `N/A`，但有原因）
- [x] Step 5 全部 U* ID 均非 `⬜`（`N/A(未启用自定义规则)`）
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`
