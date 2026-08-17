# Code Review Checklist

> **Change** helloworld · **分支/Commit** `AI/task-DEV-966dcd0a-7905-11f1-9649-3b4281182f10-2c19351b-9db2-478f-` / `<已提交>` · **日期** `2026-08-17`
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
| 1 | `my-spring-boot-app/src/main/java/com/example/myapp/controllers/HelloWorldController.java` | REQ-1 helloworld Controller | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 2 | `my-spring-boot-app/src/test/java/com/example/myapp/controllers/HelloWorldControllerTest.java` | REQ-1 测试 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |

- 由 `git diff --name-only …` 等展开；**禁止 glob**；非 Java 标 `跳过`（跳过文件的 Step4 各列可统一 `跳过` 或 `N/A(非 Java)`）。
- **守卫**：无 `.java` → 按技能终止。
- **收口**：每文件各 **Sn/Gn** 列均非 `⬜` 后，再与下方 Step 4 **逐条 ID 表** 核对一致；若某大类整节与当前文件无关，该列可一次性标 `N/A(无SQL/无MQ/…)`，但须在 Step 4 明细对应 ID 行同样标 `N/A` 并写原因。

---

## Step 2 — 功能（产物 B）

> 仅从 spec/tasks 提 **REQ**，勿臆造。不符 spec 标 **P0**。
> 每个 REQ 都必须填写 **spec 证据** 与 **关联文件**；若命中 P0，代码证据需落到 `path:line`、测试或接口行为。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | 提供 HelloWorld REST 端点，返回问候语 | 需求描述：`写一个helloworld` | `HelloWorldController.java:17-19` | ✅ | `@GetMapping("/api/hello")` 返回 `"Hello, World!"` |
| REQ-2 | 单元测试覆盖 HelloWorld 端点 | 需求描述：`写一个helloworld`（含测试） | `HelloWorldControllerTest.java:19-22` | ✅ | `shouldReturnHelloWorld_whenGetHelloEndpoint()` 验证 HTTP 200 + 响应内容 `"Hello, World!"` |

---

## Step 3 — 可读性检查（产物 C）

> 无 Java：**整节 N/A**。

对照 `references/readability-checklist.md` A1–A7 逐节核销：

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | 文件名=类名+`.java`，UTF-8，无Tab |
| A2 | 源文件结构/import 顺序 | ✅ | package→import→class；无 `import *`；静态/非静态分组正确，字典序排列 |
| A3 | 代码样式 | ✅ | K&R大括号，4空格缩进，行宽≤120 |
| A4 | 命名规范 | ✅ | UpperCamelCase类名，lowerCamelCase方法名，测试类名`*Test` |
| A5 | 编码实践 | ✅ | 无重写方法，无空catch |
| A6 | 特定元素样式 | ✅ | 无switch/数组/注解违规 |
| A7 | Javadoc 规范 | ✅ | public类`HelloWorldController`有Javadoc，`sayHello`有Javadoc并含`@return` |

---

## Step 4 — 可靠性检查（产物 D）

> **逐条核销（强制）**：G/S 每个 ID **独占一行**，禁止合并为区间（例如 ~~`G1.1 ~ G14.3`~~）。**Bug 模式** 按 `bug-pattern-checklist.md` 中 **每条 B*/M*/I*** 独占一行核销（120 条）**；无关变更可对该 ID 标 `N/A` 并写原因。报告等级：**Blocker→P0、Major→P1、Info→P2**。

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> 可先运行 `references/script/scan-all-rules.sh`（对变更目录）将命中写入备注，再人工/LLM 补全脚本未覆盖规则。

**预扫结果**：`scan-all-rules.sh` 对 2 个变更文件执行 52/222 条规则扫描，**无任何命中**。

| ID | 状态 | 备注（命中写 `path:line`；预扫可粘贴脚本摘要） |
|----|------|--------------------------------------------------|
| B001 | N/A | 无parse/of方法调用 |
| B002 | N/A | 无数组比较 |
| B003 | N/A | 无Arrays.fill |
| B004 | N/A | 无数组toString |
| B005 | N/A | 无Arrays.asList |
| B006 | N/A | 无assertEquals调用（使用MockMvc andExpect） |
| B007 | N/A | 无catch Throwable |
| B008 | N/A | 无Executors调用 |
| B009 | N/A | 无移位操作 |
| B010 | N/A | 无BigDecimal |
| B011 | N/A | 无包装类型==比较 |
| B012 | N/A | 无Calendar操作 |
| B013 | N/A | 无Calendar设置 |
| B014 | N/A | 无集合泛型不兼容查询 |
| B015 | N/A | 无Collection.toArray |
| B016 | N/A | 无Comparable实现 |
| B017 | N/A | 无this==null |
| B018 | N/A | 无条件表达式数值提升 |
| B019 | N/A | 无Money类调用 |
| B020 | N/A | 无常量溢出 |
| B021 | N/A | 无Jedis使用 |
| B022 | N/A | 无SimpleDateFormat |
| B023 | N/A | 无DeadException |
| B024 | N/A | 无Thread创建 |
| B025 | N/A | 无双括号初始化 |
| B026 | N/A | 无equals(null) |
| B027 | N/A | 无equals比较 |
| B028 | N/A | 无DateUtil |
| B029 | N/A | 无setter赋值错误 |
| B030 | N/A | 无浮点==比较 |
| B031 | N/A | 无String.format |
| B032 | N/A | 无注解getClass |
| B033 | N/A | 无Unsafe操作 |
| B034 | N/A | 无Hashtable |
| B035 | N/A | 无二元运算恒等 |
| B036 | N/A | 无IdentityHashMap |
| B037 | N/A | 无可变参数条件 |
| B038 | N/A | 无递归调用 |
| B039 | N/A | 无indexOf |
| B040 | N/A | 无isInstance |
| B041 | N/A | 无JDBC连接 |
| B042 | N/A | 使用JUnit5，非JUnit3 |
| B043 | N/A | 无内部类测试 |
| B044 | N/A | 无混合JUnit3/4 |
| B045 | N/A | 无锁操作 |
| B046 | N/A | 无循环 |
| B047 | N/A | 无数值compare |
| B048 | N/A | 无Math.round |
| B049 | N/A | 无日期格式DD |
| B050 | N/A | 无小时格式 |
| B051 | N/A | 无Boolean.getBoolean |
| B052 | N/A | 无YYYY格式 |
| B053 | N/A | 无期望异常测试 |
| B054 | N/A | 无EqualsTester |
| B055 | N/A | 无Mockito when/verify |
| B056 | N/A | 无Arrays.asList修改 |
| B057 | N/A | 无增强for修改集合 |
| B058 | N/A | 无集合自操作 |
| B059 | N/A | 无nCopies |
| B060 | N/A | 无条件表达式空拆箱 |
| B061 | N/A | 无BASE64Encoder |
| B062 | N/A | 无ClassLoader转型 |
| B063 | N/A | 无javax.xml类 |
| B064 | N/A | 无Optional==比较 |
| B065 | N/A | 无自赋值 |
| B066 | N/A | 无Math.random转型 |
| B067 | N/A | 无Random取余 |
| B068 | N/A | 无自赋值 |
| B069 | N/A | 无自比较 |
| B070 | N/A | 无自equals |
| B071 | N/A | 无size>=0 |
| B072 | N/A | 无Stream.toString |
| B073 | N/A | 无StringBuilder初始化 |
| B074 | N/A | 无substring |
| B075 | N/A | 无循环 |
| B076 | N/A | 无@Transactional |
| B077 | N/A | 无catch Throwable |
| B078 | N/A | 无Truth自比较 |
| B079 | N/A | 无@Mock赋值 |
| B080 | ✅ | 测试方法包含断言（andExpect） |
| B081 | N/A | 无集合原地修改 |
| M001 | N/A | 无重复条件判断 |
| M002 | N/A | 无instanceof |
| M003 | N/A | 无包装类构造器 |
| M004 | N/A | 无printStackTrace |
| M005 | N/A | 无内部类 |
| M006 | N/A | 无布尔常量表达式 |
| M007 | N/A | 无空catch |
| M008 | N/A | 无equals/hashCode |
| M009 | N/A | 无equals不兼容类型 |
| M010 | N/A | 无比位运算 |
| M011 | N/A | 无switch |
| M012 | N/A | 无finally |
| M013 | N/A | 无float转型 |
| M014 | N/A | 无枚举getClass |
| M015 | N/A | 无字段隐藏 |
| M016 | N/A | 无LocalDateTime.now()无参（无日期时间操作） |
| M017 | N/A | 测试方法有@Test注解 |
| M018 | N/A | 无lock操作 |
| M019 | N/A | 无枚举switch |
| M020 | N/A | 无重写方法 |
| M021 | N/A | 无equals方法 |
| M022 | N/A | 无Optional.of |
| M023 | N/A | 无对象toString打印 |
| M024 | N/A | 无Optional.get |
| M025 | N/A | 无protected在final类 |
| M026 | N/A | 无@Mock static |
| M027 | N/A | 无ThreadLocal |
| I001 | N/A | 无异常断言场景 |
| I002 | N/A | 无@DoNotMock |
| I003 | N/A | 无@AutoValue |
| I004 | N/A | 无java.util.Date |
| I005 | N/A | 使用JUnit5，非JUnit3 |
| I006 | N/A | 无setUp方法 |
| I007 | N/A | 无tearDown方法 |
| I008 | N/A | 无DataProvider |
| I009 | N/A | 统计项，不适用 |
| I010 | N/A | 使用@WebMvcTest，非集成测试 |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | N/A | 无并发/事务场景 |
| G1.2 | N/A | 无锁操作 |
| G1.3 | N/A | 无乐观锁 |
| G1.4 | N/A | 无多锁 |
| G2.1 | N/A | 无写接口/消息消费 |
| G2.2 | N/A | 无重试/定时任务 |
| G2.3 | N/A | 无幂等键 |
| G3.1 | N/A | 无事务 |
| G3.2 | N/A | 无@Transactional |
| G4.1 | N/A | 无SQL |
| G4.2 | N/A | 无SQL |
| G4.3 | N/A | 无SQL |
| G5.1 | N/A | 无MQ |
| G6.1 | N/A | 无缓存 |
| G6.2 | N/A | 无缓存 |
| G7.1 | N/A | 无调度任务 |
| G7.2 | N/A | 无调度任务 |
| G8.1 | N/A | 无异常处理 |
| G8.2 | N/A | 无核心链路依赖 |
| G8.3 | N/A | 无I/O流 |
| G8.4 | N/A | 无线程池 |
| G8.5 | N/A | 无ThreadLocal |
| G8.6 | N/A | 无线程池 |
| G9.1 | N/A | 无外部调用 |
| G9.2 | N/A | 无外部调用 |
| G9.3 | N/A | 无重试 |
| G10.1 | N/A | 无接口字段 |
| G10.2 | N/A | 无契约变更 |
| G11.1 | ✅ | 有单测且包含断言（andExpect） |
| G11.2 | N/A | 简单场景，边界已覆盖（HTTP 200+内容匹配） |
| G11.3 | N/A | 无入参 |
| G11.4 | N/A | 无数值运算 |
| G12.1 | N/A | 无资金场景 |
| G12.2 | N/A | 无资金场景 |
| G13.1 | N/A | 无日志 |
| G14.1 | N/A | 无金额/时区 |
| G14.2 | N/A | 无多租户 |
| G14.3 | N/A | 无日期时间 |
| G14.4 | N/A | 无日期格式化 |
| G15.1 | N/A | 无表结构变更 |
| G15.2 | N/A | 无接口共存 |
| G15.3 | N/A | 无开关控制 |
| G16.1 | N/A | 简单端点，无核心链路指标要求 |
| G16.2 | N/A | 无异常路径 |
| G16.3 | N/A | 无日志 |
| G16.4 | N/A | 无空catch |
| G17.1 | N/A | 无功能开关 |
| G17.2 | N/A | 无降级预案 |
| G17.3 | N/A | 无数据变更 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | N/A | 无SQL操作 |
| S1.2 | N/A | 无SQL |
| S1.3 | N/A | 无SQL |
| S2.1 | N/A | 无HTML/JS输出 |
| S2.2 | N/A | 无富文本 |
| S2.3 | N/A | 无模板引擎 |
| S3.1 | N/A | 无外部URL请求 |
| S3.2 | N/A | 无跳转 |
| S3.3 | N/A | 无超时设置 |
| S4.1 | N/A | 无系统命令 |
| S4.2 | N/A | 无文件操作 |
| S5.1 | N/A | 无XML解析 |
| S5.2 | N/A | 无XPath |
| S6.1 | N/A | 无反序列化 |
| S6.2 | N/A | 无JSON反序列化 |
| S6.3 | N/A | 无敏感字段 |
| S7.1 | N/A | 无文件上传 |
| S7.2 | N/A | 无文件路径 |
| S7.3 | N/A | 无文件操作 |
| S8.1 | N/A | 无鉴权（HelloWorld端点无需鉴权） |
| S8.2 | ✅ | 使用GET读取，无增删改 |
| S8.3 | N/A | 无数据ID |
| S8.4 | N/A | 无Cookie |
| S9.1 | N/A | 无密钥 |
| S9.2 | N/A | 无日志记录敏感信息 |
| S9.3 | N/A | 无传输加密场景 |
| S9.4 | N/A | 无随机数 |
| S10.1 | N/A | 无增删改操作 |
| S10.2 | N/A | 无CORS配置 |
| S10.3 | N/A | 无URL跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

> 按 `customized-checklist.md` 逐条核销；若未启用可整节写 `N/A(未启用自定义规则)`。

### 5.1 自定义扩展（`customized-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | N/A(未启用自定义规则) | 示例项，无实际团队约束 |
| U1.2 | N/A(未启用自定义规则) | 示例项，未启用 |
| U1.3 | N/A(未启用自定义规则) | 示例项，未启用 |
| U2.1 | N/A(未启用自定义规则) | 章节为空，无业务红线 |
| U2.2 | N/A(未启用自定义规则) | 章节为空 |
| U2.3 | N/A(未启用自定义规则) | 章节为空 |

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、**S1–S10 / G1–G17** 各列均非 `⬜`（跳过文件除外）；
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`（允许 `N/A`，但有原因）
- [x] Step 5 全部 U* ID 均非 `⬜`（允许 `N/A(未启用自定义规则)`）
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`