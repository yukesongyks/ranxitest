# Code Review Checklist

> **Change** `hello-world` · **分支/Commit** `AI/task-DEV-f4ad1a6e` · **日期** `2025-07-16`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **执行顺序（强制）**：`scan-all-rules.sh` 因运行环境 bwrap 命名空间限制无法执行，本次审查采用 **LLM 全量逐条核销** 替代。

---

## Step 1 — 执行队列（产物 A）

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|-----|----|----|----|----|----|----|----|----|----|-----|-----|-----|-----|-----|-----|-----|-----|--------|
| 1 | `my-spring-boot-app/src/main/java/com/example/myapp/controllers/HelloController.java` | REQ-1 REST 接口 | ✅ | ✅ | N/A(无并发) | N/A(无写操作) | N/A(无事务) | N/A(无SQL) | N/A(无MQ) | N/A(无缓存) | N/A(无调度) | N/A(无I/O) | N/A(无外部调用) | N/A(简单接口) | ✅ | N/A(无资金) | N/A(无监控需求) | N/A(无国际化) | N/A(无灰度) | N/A(简单应用) | N/A(无应急) | N/A(无SQL) | N/A(无HTML) | N/A(无外部URL) | N/A(无命令) | N/A(无XML) | N/A(无反序列化) | N/A(无文件) | ⚠️ | N/A(无敏感数据) | N/A(无CSRF) | ✅ 已审 |
| 2 | `my-spring-boot-app/src/main/java/com/example/myapp/services/HelloService.java` | REQ-2 问候逻辑 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | ✅ 已审 |
| 3 | `my-spring-boot-app/src/test/java/com/example/myapp/services/HelloServiceTest.java` | REQ-3 单元测试 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |

---

## Step 2 — 功能（产物 B）

> 需求原文："helloword"——实现一个简单的问候功能。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口行为） |
|-----|----------|----------------------|----------|------|-------------------------------|
| REQ-1 | 提供 REST API 返回问候消息 | 需求 "helloword" | HelloController.java | ✅ | `@GetMapping("/hello")` → `Map.of("message", message)` :34-37 |
| REQ-2 | 支持可选名字参数，缺省返回 "Hello, World!" | 需求 "helloword" 隐含默认行为 | HelloService.java | ✅ | `DEFAULT_NAME = "World"` :13; `greet(null)` → `"Hello, World!"` :23-25 |
| REQ-3 | 核心逻辑有单元测试覆盖 | 质量隐含要求 | HelloServiceTest.java | ✅ | 5 个测试方法覆盖 null/空/空白/正常/带空格 :22-71 |

---

## Step 3 — 可读性检查（产物 C）

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | 文件名=类名，UTF-8，空格缩进 |
| A2 | 源文件结构/import 顺序 | ✅ | package→import→class，无通配符，静态/非静态分组正确 |
| A3 | 代码样式 | ✅ | K&R 大括号，4空格缩进，行宽≤120 |
| A4 | 命名规范 | ✅ | 包名小写，类名 UpperCamelCase，方法 lowerCamelCase，常量 UPPER_SNAKE_CASE |
| A5 | 编码实践 | ✅ | 无重写方法需 @Override，无空 catch |
| A6 | 特定元素样式 | ✅ | 修饰符顺序正确，注解规范 |
| A7 | Javadoc 规范 | ✅ | 所有 public 类和 public 方法均有 Javadoc，@param/@return 齐全 |

---

## Step 4 — 可靠性检查（产物 D）

> **预扫说明**：`scan-all-rules.sh` 因运行环境 bwrap 命名空间限制无法执行，以下全部由 LLM 逐条核销。

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| B001 | N/A | 无 parse/of 字面量调用 |
| B002 | N/A | 无数组比较 |
| B003 | N/A | 无 Arrays.fill |
| B004 | N/A | 无数组 toString |
| B005 | N/A | 无 Arrays.asList 基本类型 |
| B006 | ✅ | assertEquals(expected, actual) 参数顺序正确 |
| B007 | N/A | 无 catch Throwable |
| B008 | N/A | 无线程池创建 |
| B009 | N/A | 无移位运算 |
| B010 | N/A | 无 BigDecimal |
| B011 | N/A | 无包装类型 == 比较 |
| B012 | N/A | 无 Calendar |
| B013 | N/A | 无 Calendar |
| B014 | N/A | 无集合不兼容类型查询 |
| B015 | N/A | 无 toArray 不兼容 |
| B016 | N/A | 无 Comparable 实现 |
| B017 | N/A | 无 this==null |
| B018 | N/A | 无三目数值提升 |
| B019 | N/A | 无 Money API |
| B020 | N/A | 无常量溢出 |
| B021 | N/A | 无 Jedis |
| B022 | N/A | 无 SimpleDateFormat |
| B023 | N/A | 无未抛出异常 |
| B024 | N/A | 无未启动线程 |
| B025 | N/A | 无双括号初始化 |
| B026 | N/A | 无 equals(null) |
| B027 | N/A | 无 equals 错误属性 |
| B028 | N/A | 无 DateUtil |
| B029 | N/A | 无 setter 赋错字段 |
| B030 | N/A | 无浮点 == 比较 |
| B031 | ✅ | String.format 占位符与参数数量一致（1个 %s，1个参数） |
| B032 | N/A | 无注解 getClass |
| B033 | N/A | 无 Unsafe |
| B034 | N/A | 无 Hashtable |
| B035 | N/A | 无恒等二元表达式 |
| B036 | N/A | 无 IdentityHashMap |
| B037 | N/A | 无可变参数条件 |
| B038 | N/A | 无无限递归 |
| B039 | N/A | 无 indexOf 参数颠倒 |
| B040 | N/A | 无 isInstance 不兼容 |
| B041 | N/A | 无 JDBC |
| B042 | N/A | 非 JUnit3 |
| B043 | N/A | 无内部类 @Test |
| B044 | N/A | 无 JUnit3+4 混用 |
| B045 | N/A | 无包装类型加锁 |
| B046 | N/A | 无不变循环条件 |
| B047 | N/A | 无损失性 compare |
| B048 | N/A | 无 Math.round 整型 |
| B049 | N/A | 无日期格式 |
| B050 | N/A | 无小时格式 |
| B051 | N/A | 无 Boolean.getBoolean |
| B052 | N/A | 无周年格式 |
| B053 | N/A | 无 try 缺 fail |
| B054 | N/A | 无 EqualsTester |
| B055 | N/A | 无 Mockito 误用 |
| B056 | N/A | 无 Arrays.asList 修改 |
| B057 | N/A | 无增强 for 修改集合 |
| B058 | N/A | 无集合自操作 |
| B059 | N/A | 无 nCopies 参数颠倒 |
| B060 | N/A | 无 null 拆箱 |
| B061 | N/A | 无 sun.misc.BASE64 |
| B062 | N/A | 无 URLClassLoader 强转 |
| B063 | N/A | 无 javax.xml.bind |
| B064 | N/A | 无 Optional == |
| B065 | N/A | 无 Pojo 自赋值 |
| B066 | N/A | 无 (int)Math.random() |
| B067 | N/A | 无 Random 取余 |
| B068 | N/A | 无变量自赋值 |
| B069 | N/A | 无自比较 |
| B070 | N/A | 无自 equals |
| B071 | N/A | 无 size()>=0 |
| B072 | N/A | 无 Stream.toString |
| B073 | N/A | 无 StringBuilder(char) |
| B074 | N/A | 无 substring(0) |
| B075 | N/A | 无 for 方向矛盾 |
| B076 | N/A | 无 @Transactional 非 public |
| B077 | N/A | 无 catch Throwable |
| B078 | N/A | 无 assertThat 自比较 |
| B079 | N/A | 无 @Mock 显式赋值 |
| B080 | ✅ | 所有测试方法均包含 assertEquals 断言 |
| B081 | N/A | 无未使用集合修改 |
| M001 | N/A | 无重复条件判断 |
| M002 | N/A | 无子类 instanceof 父类 |
| M003 | N/A | 无包装类构造器 |
| M004 | N/A | 无 printStackTrace |
| M005 | N/A | 无内部类 |
| M006 | N/A | 无编译期布尔常量 |
| M007 | N/A | 无空 catch |
| M008 | N/A | 无 equals/hashCode 重写 |
| M009 | N/A | 无不兼容类型 equals |
| M010 | N/A | 无误用位运算 |
| M011 | N/A | 无 switch |
| M012 | N/A | 无 finally return |
| M013 | N/A | 无类型转换优先级问题 |
| M014 | N/A | 无枚举 getClass |
| M015 | N/A | 无隐藏字段 |
| M016 | N/A | 无默认时区问题 |
| M017 | N/A | 所有测试方法均有 @Test |
| M018 | N/A | 无锁操作 |
| M019 | N/A | 无枚举 switch |
| M020 | N/A | 无遗漏 @Override |
| M021 | N/A | 无 equals 签名错误 |
| M022 | N/A | 无 Optional.of(null) |
| M023 | N/A | 无 Object.toString 打印 |
| M024 | N/A | 无 Optional.get 未检查 |
| M025 | N/A | 无 final 类 protected |
| M026 | N/A | 无 @Mock static |
| M027 | N/A | 无 ThreadLocal |
| I001 | N/A | 无异常断言需求 |
| I002 | N/A | 无 @DoNotMock |
| I003 | N/A | 无 @AutoValue |
| I004 | N/A | 无 java.util.Date |
| I005 | N/A | 无 JUnit4 特性在 JUnit3 |
| I006 | N/A | 使用 JUnit5 @BeforeEach |
| I007 | N/A | 使用 JUnit5 |
| I008 | N/A | 无 dataProvider |
| I009 | N/A | 统计项 |
| I010 | N/A | 无 Spring 容器启动 |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | N/A | 无并发场景 |
| G1.2 | N/A | 无锁操作 |
| G1.3 | N/A | 无乐观锁 |
| G1.4 | N/A | 无多资源锁 |
| G2.1 | N/A | 无写接口 |
| G2.2 | N/A | 无重试/定时任务 |
| G2.3 | N/A | 无幂等键 |
| G3.1 | N/A | 无分布式事务 |
| G3.2 | N/A | 无 @Transactional |
| G4.1 | N/A | 无 SQL |
| G4.2 | N/A | 无 SQL |
| G4.3 | N/A | 无列表查询 |
| G4.4 | N/A | 无 SQL |
| G5.1 | N/A | 无 MQ |
| G6.1 | N/A | 无缓存 |
| G6.2 | N/A | 无缓存 |
| G7.1 | N/A | 无调度 |
| G7.2 | N/A | 无调度 |
| G8.1 | N/A | 无 catch 块 |
| G8.2 | N/A | 无强依赖 |
| G8.3 | N/A | 无 I/O 流 |
| G8.4 | N/A | 无线程池 |
| G8.5 | N/A | 无 ThreadLocal |
| G8.6 | N/A | 无 Executors |
| G8.7 | N/A | 无 |
| G9.1 | N/A | 无外部调用 |
| G9.2 | N/A | 无外部调用 |
| G9.3 | N/A | 无重试 |
| G10.1 | N/A | 无 null 歧义 |
| G10.2 | N/A | 无契约变更 |
| G10.3 | N/A | 无 |
| G11.1 | ✅ | 有单元测试且包含断言 |
| G11.2 | ⚠️ | 仅覆盖 Service 层，Controller 层无测试（边界：无参数/有参数/特殊字符） |
| G11.3 | ✅ | Service 层对 null/空/空白均有防御 |
| G11.4 | N/A | 无数值运算 |
| G12.1 | N/A | 无资金场景 |
| G12.2 | N/A | 无资金场景 |
| G13.1 | N/A | 无日志 |
| G14.1 | N/A | 无金额 |
| G14.2 | N/A | 无多租户 |
| G14.3 | N/A | 无时区 |
| G14.4 | N/A | 无日期格式化 |
| G15.1 | N/A | 无表结构变更 |
| G15.2 | N/A | 无接口版本 |
| G15.3 | N/A | 无开关 |
| G16.1 | N/A | 非核心链路 |
| G16.2 | N/A | 无异常路径 |
| G16.3 | N/A | 无日志 |
| G16.4 | N/A | 无 catch |
| G17.1 | N/A | 无功能开关 |
| G17.2 | N/A | 无降级需求 |
| G17.3 | N/A | 无数据变更 |
| G18.1 | N/A | 无 |
| G18.2 | N/A | 无 |
| G18.3 | N/A | 无 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | N/A | 无 SQL |
| S1.2 | N/A | 无 SQL |
| S1.3 | N/A | 无 SQL |
| S2.1 | ⚠️ | `name` 参数直接拼入响应 JSON，若下游以 HTML 渲染存在 XSS 风险（当前 JSON 响应风险低，但建议关注） |
| S2.2 | N/A | 无富文本 |
| S2.3 | N/A | 无模板引擎 |
| S3.1 | N/A | 无外部 URL |
| S3.2 | N/A | 无跳转 |
| S3.3 | N/A | 无外部调用 |
| S4.1 | N/A | 无命令执行 |
| S4.2 | N/A | 无文件操作 |
| S5.1 | N/A | 无 XML |
| S5.2 | N/A | 无 XPath |
| S6.1 | N/A | 无反序列化 |
| S6.2 | N/A | 无 JSON 多态 |
| S6.3 | N/A | 无敏感字段 |
| S7.1 | N/A | 无文件上传 |
| S7.2 | N/A | 无文件下载 |
| S7.3 | N/A | 无文件操作 |
| S8.1 | ⚠️ | 接口未接入鉴权（`/api/hello` 公开访问）。对于 hello world 示例可接受，生产环境需接入认证 |
| S8.2 | ✅ | GET 仅查询，无增删改 |
| S8.3 | N/A | 无数据 ID |
| S8.4 | N/A | 无 Cookie |
| S9.1 | ✅ | 无硬编码密钥 |
| S9.2 | ✅ | 无日志记录敏感信息 |
| S9.3 | N/A | 无加密需求 |
| S9.4 | N/A | 无随机数 |
| S10.1 | N/A | 无 CSRF 需求（GET 只读） |
| S10.2 | N/A | 无 CORS 配置 |
| S10.3 | N/A | 无跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

### 5.1 自定义扩展（`customized-checklist.md`）

N/A(未启用自定义规则)

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | N/A | 未启用自定义规则 |
| U1.2 | N/A | 未启用自定义规则 |
| U1.3 | N/A | 未启用自定义规则 |
| U2.1 | N/A | 未启用自定义规则 |
| U2.2 | N/A | 未启用自定义规则 |
| U2.3 | N/A | 未启用自定义规则 |

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、**S1–S10 / G1–G17** 各列均非 `⬜`（跳过文件除外）；
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`（允许 `N/A`，但有原因）
- [x] Step 5 全部 U* ID 均非 `⬜`（允许 `N/A(未启用自定义规则)`）
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`
