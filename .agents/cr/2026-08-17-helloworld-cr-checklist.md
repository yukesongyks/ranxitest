# Code Review Checklist

> **Change** `helloworld` · **分支/Commit** `AI/task-DEV-966dcd0a` · **日期** `2026-08-17`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **执行顺序（强制）**：写入本清单并进入逐文件审查前，先在目标仓库对变更路径运行 `scan-all-rules.sh`，将输出贴入 Step 3 和 Step 4 备注；再用 LLM 完成 Step 2–5 中脚本未覆盖项及复核。

---

## Step 1 — 执行队列（产物 A）

> **Step4 列语义**：每个 **Sn / Gn** 表示「**本文件**在 Step4 审查中，对 `reliability-checklist.md` 第 **G*n*** 节、`security-checklist.md` 第 **S*n*** 节的扫描结论」。**Bug 模式（B/M/I）** 不在本表分列，在下方 **§4.1** 按清单 ID 核销。

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|-----|----|----|----|----|----|----|----|----|----|-----|-----|-----|-----|-----|-----|-----|-----|--------|
| 1 | `my-spring-boot-app/src/main/java/com/example/myapp/controllers/HelloController.java` | REQ-1 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 2 | `my-spring-boot-app/src/test/java/com/example/myapp/controllers/HelloControllerTest.java` | REQ-1 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |

---

## Step 2 — 功能（产物 B）

> 仅从 spec/tasks 提 **REQ**，勿臆造。不符 spec 标 **P0**。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | 写一个 Hello World：提供 HTTP 接口返回问候语 | 需求描述："写一个helloworld" | HelloController.java, HelloControllerTest.java | ✅ | HelloController.java:19-20 返回 `"Hello, World!"`；测试 HelloControllerTest.java:21-24 验证状态码 200 且响应体为 `"Hello, World!"` |

---

## Step 3 — 可读性检查（产物 C）

> 对照 `references/readability-checklist.md` A1–A7 逐节核销。预扫脚本 `scan-all-rules.sh` 无命中。

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | 文件名=类名，UTF-8 编码，无 Tab |
| A2 | 源文件结构/import 顺序 | ✅ | 静态/非静态 import 分组正确，无 `import *`，字典序排列 |
| A3 | 代码样式 | ✅ | K&R 大括号，4空格缩进，行宽≤120，成员间空行 |
| A4 | 命名规范 | ✅ | 包名全小写，类名 UpperCamelCase，方法名 lowerCamelCase，测试类名 HelloControllerTest |
| A5 | 编码实践 | ✅ | 无 catch 块，无 finalize()，无静态方法实例调用 |
| A6 | 特定元素样式 | ✅ | 修饰符顺序正确，注解每行一个 |
| A7 | Javadoc 规范 | ✅ | HelloController 及 hello() 方法均有 Javadoc；测试类有 @DisplayName 替代 |

---

## Step 4 — 可靠性检查（产物 D）

> **逐条核销（强制）**。预扫脚本 `scan-all-rules.sh` 输出：`No findings. 52/222 rules scanned`。

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

| ID | 状态 | 备注（命中写 `path:line`；预扫可粘贴脚本摘要） |
|----|------|--------------------------------------------------|
| B001 | N/A | 无 `LocalDateTime.parse`/`UUID.fromString` 等字面量调用 |
| B002 | N/A | 无数组 `equals` 调用 |
| B003 | N/A | 无 `Arrays.fill` |
| B004 | N/A | 无数组 `toString()` |
| B005 | N/A | 无 `Arrays.asList` |
| B006 | N/A | 测试使用 `andExpect(content().string(...))` 非 `assertEquals` |
| B007 | N/A | 无 `catch Throwable` |
| B008 | N/A | 无 `Executors` 线程池创建 |
| B009 | N/A | 无移位运算 |
| B010 | N/A | 无 `BigDecimal` |
| B011 | N/A | 无包装类型 `==` 比较 |
| B012 | N/A | 无 `Calendar` |
| B013 | N/A | 无 `Calendar.HOUR` |
| B014 | N/A | 无集合查询 |
| B015 | N/A | 无 `Collection.toArray` |
| B016 | N/A | 无 `Comparable` 实现 |
| B017 | N/A | 无 `this == null` |
| B018 | N/A | 无三目运算符数值分支 |
| B019 | N/A | 无 Money 类 |
| B020 | N/A | 无编译期常量乘法 |
| B021 | N/A | 无 Jedis |
| B022 | N/A | 无 `SimpleDateFormat` |
| B023 | N/A | 无创建异常未抛出 |
| B024 | N/A | 无 `Thread` 创建 |
| B025 | N/A | 无双括号初始化 |
| B026 | N/A | 无 `equals(null)` |
| B027 | N/A | 无自定义 `equals` |
| B028 | N/A | 无 `DateUtil` |
| B029 | N/A | 无 setter 方法 |
| B030 | N/A | 无浮点 `==` |
| B031 | N/A | 无 `String.format` |
| B032 | N/A | 无注解 `getClass()` |
| B033 | N/A | 无 Unsafe |
| B034 | N/A | 无 `Hashtable` |
| B035 | N/A | 无二元运算自身比较 |
| B036 | N/A | 无 `IdentityHashMap` |
| B037 | N/A | 无可变参数条件表达式 |
| B038 | N/A | 无递归调用 |
| B039 | N/A | 无 `String.indexOf` |
| B040 | N/A | 无 `isInstance` |
| B041 | N/A | 无 JDBC |
| B042 | N/A | 非 JUnit3 |
| B043 | N/A | 无内部类 `@Test` |
| B044 | N/A | 非 JUnit3+JUnit4 混用 |
| B045 | N/A | 无包装类型加锁 |
| B046 | N/A | 无循环条件 |
| B047 | N/A | 无数值 compare |
| B048 | N/A | 无 `Math.round` |
| B049 | N/A | 无日期格式 |
| B050 | N/A | 无小时格式 |
| B051 | N/A | 无 `Boolean.getBoolean` |
| B052 | N/A | 无 `YYYY` 格式 |
| B053 | N/A | 无期望抛异常的测试 |
| B054 | N/A | 无 `EqualsTester` |
| B055 | N/A | 使用 MockMvc 非 Mockito `when()`/`verify()` |
| B056 | N/A | 无 `Arrays.asList().add()` |
| B057 | N/A | 无增强 for 循环修改集合 |
| B058 | N/A | 无集合自引用方法 |
| B059 | N/A | 无 `Collections.nCopies` |
| B060 | N/A | 无三目运算符拆箱 |
| B061 | N/A | 无 `BASE64Encoder` |
| B062 | N/A | 无 `URLClassLoader` 转换 |
| B063 | N/A | 无 `javax.xml.bind` |
| B064 | N/A | 无 `Optional` |
| B065 | N/A | 无 Pojo 自赋值 |
| B066 | N/A | 无 `Math.random()` |
| B067 | N/A | 无 `Random.nextInt()` 取余 |
| B068 | N/A | 无自赋值 |
| B069 | N/A | 无 `compareTo` 自比较 |
| B070 | N/A | 无 `equals` 自比较 |
| B071 | N/A | 无 `size() >= 0` |
| B072 | N/A | 无 `Stream.toString()` |
| B073 | N/A | 无 `StringBuilder` 字符构造 |
| B074 | N/A | 无 `substring(0)` |
| B075 | N/A | 无 for 循环 |
| B076 | N/A | 无 `@Transactional` |
| B077 | N/A | 无 `catch Throwable` 在测试中 |
| B078 | N/A | 无 Truth `assertThat(x).isEqualTo(x)` |
| B079 | N/A | 无 `@Mock` 显式赋值 |
| B080 | ✅ | `HelloControllerTest.java:22-24` — 测试包含 `andExpect(status().isOk())` 和 `andExpect(content().string(...))` 断言 |
| B081 | N/A | 无集合原地修改 |
| M001 | N/A | 无连续条件判断 |
| M002 | N/A | 无 `instanceof` |
| M003 | N/A | 无包装类构造器 |
| M004 | N/A | 无 `printStackTrace()` |
| M005 | N/A | `HelloControllerTest` 是顶层类，无内部类 |
| M006 | N/A | 无编译期常量布尔表达式 |
| M007 | N/A | 无 catch 块 |
| M008 | N/A | 无 `equals`/`hashCode` 重写 |
| M009 | N/A | 无跨类型 `equals` |
| M010 | N/A | 无位运算 |
| M011 | N/A | 无 switch |
| M012 | N/A | 无 finally |
| M013 | N/A | 无浮点类型转换 |
| M014 | N/A | 无枚举 |
| M015 | N/A | 无继承 |
| M016 | N/A | 无时区依赖方法 |
| M017 | N/A | 测试方法有 `@Test` 注解 |
| M018 | N/A | 无 lock |
| M019 | N/A | 无 switch 枚举 |
| M020 | N/A | 无重写方法 |
| M021 | N/A | 无 `equals` 重写 |
| M022 | N/A | 无 `Optional.of()` |
| M023 | N/A | 无 `Object.toString()` 调用 |
| M024 | N/A | 无 `Optional.get()` |
| M025 | N/A | 无 final 类 |
| M026 | N/A | 无 `@Mock` 静态成员；`MockMvc` 使用 `@Autowired` |
| M027 | N/A | 无 `ThreadLocal` |
| I001 | N/A | 无异常断言 |
| I002 | N/A | 无 `@DoNotMock` |
| I003 | N/A | 无 `@AutoValue` |
| I004 | N/A | 无 `java.util.Date` |
| I005 | N/A | 非 JUnit3 |
| I006 | N/A | 非 JUnit4 `setUp()` |
| I007 | N/A | 非 JUnit4 `tearDown()` |
| I008 | N/A | 无 `dataProvider` |
| I009 | N/A | 统计项，无需核销 |
| I010 | N/A | 使用 `@WebMvcTest` 切片测试，非 Pandora 容器 |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | N/A | 无事务/并发写操作 |
| G1.2 | N/A | 无加锁后更新 |
| G1.3 | N/A | 无乐观锁 |
| G1.4 | N/A | 无多资源加锁 |
| G2.1 | N/A | GET 请求天然幂等 |
| G2.2 | N/A | 无重试/定时任务/MQ |
| G2.3 | N/A | 无幂等键约定 |
| G3.1 | N/A | 无分布式事务 |
| G3.2 | N/A | 无 `@Transactional` |
| G4.1 | N/A | 无 SQL |
| G4.2 | N/A | 无 SQL |
| G4.3 | N/A | 无 SQL |
| G5.1 | N/A | 无 MQ |
| G6.1 | N/A | 无缓存 |
| G6.2 | N/A | 无缓存双写 |
| G7.1 | N/A | 无调度任务 |
| G7.2 | N/A | 无调度任务 |
| G8.1 | ✅ | 无异常处理逻辑，仅返回简单字符串，无异常路径 |
| G8.2 | N/A | 无外部依赖 |
| G8.3 | N/A | 无 I/O 资源 |
| G8.4 | N/A | 无线程池/定时任务 |
| G8.5 | N/A | 无 ThreadLocal |
| G8.6 | N/A | 无 Executors |
| G9.1 | N/A | 无外部调用 |
| G9.2 | N/A | 无外部调用 |
| G9.3 | N/A | 无重试 |
| G10.1 | ✅ | 返回 `String`，无 null 语义歧义 |
| G10.2 | N/A | 无契约变更 |
| G11.1 | ✅ | 测试类 `HelloControllerTest.java:22-24` 包含 MockMvc 断言 |
| G11.2 | ⚠️ P2 | 仅覆盖 happy path（200 + 正确响应体），未测试边界：空响应体、异常路径 |
| G11.3 | N/A | 无入参 |
| G11.4 | N/A | 无数值运算 |
| G12.1 | N/A | 无资金操作 |
| G12.2 | N/A | 无资金操作 |
| G13.1 | N/A | 无日志 |
| G14.1 | N/A | 无金额 |
| G14.2 | N/A | 无多租户 |
| G14.3 | N/A | 无时区操作 |
| G14.4 | N/A | 无日期格式化 |
| G15.1 | N/A | 无 DB 变更 |
| G15.2 | N/A | 无新旧接口共存 |
| G15.3 | N/A | 无不兼容逻辑 |
| G16.1 | N/A | 简单 Hello World，无需指标埋点 |
| G16.2 | N/A | 无异常路径 |
| G16.3 | N/A | 无日志 |
| G16.4 | N/A | 无 catch 块 |
| G17.1 | N/A | 简单 Hello World，无需功能开关 |
| G17.2 | N/A | 无降级需求 |
| G17.3 | N/A | 无数据变更 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | N/A | 无 SQL |
| S1.2 | N/A | 无 SQL |
| S1.3 | N/A | 无 SQL |
| S2.1 | N/A | 返回固定字符串，无用户输入，无 XSS 风险 |
| S2.2 | N/A | 无富文本 |
| S2.3 | N/A | 无模板引擎 |
| S3.1 | N/A | 无外部 URL 请求 |
| S3.2 | N/A | 无外部 URL 请求 |
| S3.3 | N/A | 无外部 URL 请求 |
| S4.1 | N/A | 无系统命令 |
| S4.2 | N/A | 无文件操作 |
| S5.1 | N/A | 无 XML 解析 |
| S5.2 | N/A | 无 XPath |
| S6.1 | N/A | 无反序列化 |
| S6.2 | N/A | 无 JSON 反序列化 |
| S6.3 | N/A | 无敏感字段 |
| S7.1 | N/A | 无文件上传 |
| S7.2 | N/A | 无文件操作 |
| S7.3 | N/A | 无文件操作 |
| S8.1 | N/A | 简单 Hello World 端点，无鉴权需求（演示/示例代码） |
| S8.2 | ✅ | 使用 `@GetMapping`，GET 请求不执行增删改 |
| S8.3 | N/A | 无数据 ID |
| S8.4 | N/A | 无 Cookie |
| S9.1 | N/A | 无密钥/凭证 |
| S9.2 | N/A | 无日志 |
| S9.3 | N/A | 无传输加密需求 |
| S9.4 | N/A | 无随机数 |
| S10.1 | N/A | GET 请求无 CSRF 风险 |
| S10.2 | N/A | 无 CORS 配置 |
| S10.3 | N/A | 无 URL 跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

### 5.1 自定义扩展（`customized-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | N/A | 示例项：Controller 无入参，无需 `@Valid` |
| U2.1 | N/A | 未启用自定义业务红线 |

> 整体：`N/A(未启用自定义规则)` — customized-checklist.md 仅含一条示例项 U1.1，其余为空。

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、**S1–S10 / G1–G17** 各列均非 `⬜`
- [x] Step 2 的 REQ-1 非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`
- [x] Step 5 全部 U* ID 均非 `⬜`
- [x] 所有 `⚠️` 已写入 report，包含 `ID + path:line`