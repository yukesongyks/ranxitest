# Code Review Checklist

> **Change** `helloword` · **分支/Commit** `AI/task-DEV-f4ad1a6e-7360-11f1-8c66-df5563d236aa-71b63e0a-2975-430b-` · **日期** `2025-07-21`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **执行顺序（强制）**：`scan-all-rules.sh` 因运行环境 bwrap 命名空间限制不可用，降级为 LLM 全量审查。

---

## Step 1 — 执行队列（产物 A）

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|-----|----|----|----|----|----|----|----|----|----|-----|-----|-----|-----|-----|-----|-----|-----|--------|
| 1 | `my-spring-boot-app/src/main/java/com/example/myapp/controllers/HelloController.java` | REQ-1/REQ-2 | ✅ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | ⚠️ | ✅ | N/A | ⚠️ 已审有问题 |
| 2 | `my-spring-boot-app/src/main/java/com/example/myapp/services/HelloService.java` | REQ-1/REQ-2 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | ✅ | ✅ | N/A | ✅ | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | ✅ 已审 |
| 3 | `my-spring-boot-app/src/test/java/com/example/myapp/services/HelloServiceTest.java` | REQ-1/REQ-2 | ✅ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审有问题 |

---

## Step 2 — 功能（产物 B）

> 需求原文："helloword" — 实现 Hello World 问候功能。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | 提供默认问候消息接口 GET /api/hello → "Hello, World!" | 需求 "helloword" | HelloController.java, HelloService.java | ✅ | `HelloController.java:42` sayHello() → `HelloService.java:26` 返回 DEFAULT_GREETING="Hello, World!" |
| REQ-2 | 提供带姓名问候接口 GET /api/hello/to?name=xxx → "Hello, {name}!" | 需求 "helloword" 隐含 | HelloController.java, HelloService.java | ✅ | `HelloController.java:54` sayHelloTo(@RequestParam name) → `HelloService.java:38` sayHelloTo(name) 返回格式化消息 |

---

## Step 3 — 可读性检查（产物 C）

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | 文件名=类名，UTF-8，空格缩进 |
| A2 | 源文件结构/import 顺序 | ⚠️ | HelloController.java:3-12 import 未按 ASCII 字典序（java.util 应在 org.springframework 之前）；HelloServiceTest.java:7 静态 import 应在非静态 import 之前（A2.3），且使用了通配符 `Assertions.*`（A2.2） |
| A3 | 代码样式 | ✅ | K&R 大括号、4空格缩进、行宽合规 |
| A4 | 命名规范 | ✅ | 类名 UpperCamelCase、方法名 lowerCamelCase、常量 UPPER_SNAKE_CASE |
| A5 | 编码实践 | ✅ | 无重写方法、无空 catch |
| A6 | 特定元素样式 | ✅ | 修饰符顺序正确、注解规范 |
| A7 | Javadoc 规范 | ✅ | public 类和方法均有 Javadoc，@param/@return 顺序正确 |

---

## Step 4 — 可靠性检查（产物 D）

> **降级说明**：`scan-all-rules.sh` 因运行环境 bwrap 命名空间限制无法执行，以下全部由 LLM 逐条核销。

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> 本次变更代码量小（共 211 行）、逻辑简单，绝大多数 Bug 模式规则不适用。以下仅列出与变更相关的规则，其余标 N/A。

| ID | 状态 | 备注（命中写 `path:line`；预扫可粘贴脚本摘要） |
|----|------|--------------------------------------------------|
| B001 | N/A | 无 switch 语句 |
| B002 | N/A | 无 switch 语句 |
| B003 | N/A | 无条件表达式 |
| B004 | N/A | 无分支覆盖问题 |
| B005 | N/A | 无数组越界风险 |
| B006 | N/A | 无集合并发修改 |
| B007 | N/A | 无日期格式化 |
| B008 | N/A | 无 equals 覆写 |
| B009 | N/A | 无 hashCode 覆写 |
| B010 | N/A | 无空指针风险（已校验） |
| B011 | N/A | 无不恰当类型转换 |
| B012 | N/A | 无字符串比较问题 |
| B013 | N/A | 无资源泄漏 |
| B014 | N/A | 无线程安全问题 |
| B015 | N/A | 无死锁风险 |
| B016 | N/A | 无竞态条件 |
| B017 | N/A | 无双重检查锁定 |
| B018 | N/A | 无 volatile 误用 |
| B019 | N/A | 无 wait/notify 误用 |
| B020 | N/A | 无 Thread.stop 调用 |
| B021 | N/A | 无 finalize 覆写 |
| B022 | N/A | 无 clone 问题 |
| B023 | N/A | 无序列化问题 |
| B024 | N/A | 无 transient 误用 |
| B025 | N/A | 无 BigDecimal 精度问题 |
| B026 | N/A | 无浮点比较 |
| B027 | N/A | 无整数溢出 |
| B028 | N/A | 无随机数安全问题 |
| B029 | N/A | 无 SQL 拼接 |
| B030 | N/A | 无 XPath 注入 |
| B031 | N/A | 无 LDAP 注入 |
| B032 | N/A | 无命令注入 |
| B033 | N/A | 无 XXE 风险 |
| B034 | N/A | 无反序列化漏洞 |
| B035 | N/A | 无路径穿越 |
| B036 | N/A | 无硬编码凭证 |
| B037 | N/A | 无日志敏感信息 |
| B038 | N/A | 无弱加密算法 |
| B039 | N/A | 无不安全的随机数 |
| B040 | N/A | 无 CORS 配置 |
| B041 | N/A | 无 CSRF 问题 |
| B042 | N/A | 无开放重定向 |
| B043 | N/A | 无信息泄露 |
| B044 | N/A | 无调试代码残留 |
| B045 | N/A | 无 TODO/FIXME |
| B046 | N/A | 无魔法数字 |
| B047 | N/A | 无常量定义问题 |
| B048 | N/A | 无枚举问题 |
| B049 | N/A | 无注解使用问题 |
| B050 | N/A | 无泛型擦除 |
| B051 | N/A | 无自动装箱问题 |
| B052 | N/A | 无字符串拼接性能问题 |
| B053 | N/A | 无集合初始化问题 |
| B054 | N/A | 无数组.asList 问题 |
| B055 | N/A | 无 subList 问题 |
| B056 | N/A | 无 toArray 问题 |
| B057 | N/A | 无 Collections.synchronized 问题 |
| B058 | N/A | 无 ConcurrentHashMap 问题 |
| B059 | N/A | 无 String.intern 问题 |
| B060 | N/A | 无 String.replace 性能问题 |
| B061 | N/A | 无正则表达式性能问题 |
| B062 | N/A | 无 SimpleDateFormat 线程安全 |
| B063 | N/A | 无 Calendar 问题 |
| B064 | N/A | 无时区处理问题 |
| B065 | N/A | 无 URL 编码问题 |
| B066 | N/A | 无文件编码问题 |
| B067 | N/A | 无流关闭问题 |
| B068 | N/A | 无连接池问题 |
| B069 | N/A | 无 DNS 缓存问题 |
| B070 | N/A | 无 HTTP 连接问题 |
| B071 | N/A | 无 SSL/TLS 问题 |
| B072 | N/A | 无证书校验问题 |
| B073 | N/A | 无会话管理问题 |
| B074 | N/A | 无权限校验问题 |
| B075 | N/A | 无输入校验问题 |
| B076 | N/A | 无输出编码问题 |
| B077 | N/A | 无错误处理问题 |
| B078 | N/A | 无异常吞没问题 |
| B079 | N/A | 无日志级别问题 |
| B080 | N/A | 无监控埋点问题 |
| B081 | N/A | 无降级预案问题 |
| M001 | N/A | 无魔法数字（常量已提取） |
| M002 | N/A | 无过长方法 |
| M003 | N/A | 无过大类 |
| M004 | N/A | 无过多参数 |
| M005 | N/A | 无重复代码 |
| M006 | N/A | 无耦合过紧 |
| M007 | N/A | 无内聚过低 |
| M008 | N/A | 无过度复杂 |
| M009 | N/A | 无抽象类使用不当 |
| M010 | N/A | 无接口设计问题 |
| M011 | N/A | 无继承层次过深 |
| M012 | N/A | 无 God Class |
| M013 | N/A | 无 Feature Envy |
| M014 | N/A | 无 Data Class |
| M015 | N/A | 无 Lazy Class |
| M016 | N/A | 无 Speculative Generality |
| M017 | N/A | 无 Switch Statements |
| M018 | N/A | 无 Refused Bequest |
| M019 | N/A | 无 Alternative Classes |
| M020 | N/A | 无 Data Clumps |
| M021 | N/A | 无 Primitive Obsession |
| M022 | N/A | 无 Large Class |
| M023 | N/A | 无 Long Method |
| M024 | N/A | 无 Long Parameter List |
| M025 | N/A | 无 Message Chains |
| M026 | N/A | 无 Middle Man |
| M027 | N/A | 无 Inappropriate Intimacy |
| I001 | N/A | 无注释缺失 |
| I002 | N/A | 无命名不清晰 |
| I003 | N/A | 无代码格式问题 |
| I004 | N/A | 无冗余代码 |
| I005 | N/A | 无过时注释 |
| I006 | N/A | 无未使用变量 |
| I007 | N/A | 无未使用 import |
| I008 | N/A | 无未使用参数 |
| I009 | N/A | 无代码重复 |
| I010 | N/A | 无优化建议 |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | N/A | 无并发场景 |
| G1.2 | N/A | 无锁操作 |
| G1.3 | N/A | 无乐观锁 |
| G1.4 | N/A | 无多资源加锁 |
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
| G7.1 | N/A | 无调度任务 |
| G7.2 | N/A | 无调度任务 |
| G8.1 | N/A | 无 catch 块 |
| G8.2 | N/A | 无强依赖 |
| G8.3 | ✅ | 无 I/O 资源需释放 |
| G8.4 | N/A | 无线程池 |
| G8.5 | N/A | 无 ThreadLocal |
| G8.6 | N/A | 无线程池 |
| G8.7 | N/A | 无防御编程场景 |
| G9.1 | N/A | 无外部调用 |
| G9.2 | N/A | 无外部调用 |
| G9.3 | N/A | 无重试 |
| G10.1 | ✅ | 响应体结构清晰，message 字段含义明确 |
| G10.2 | N/A | 无契约变更 |
| G10.3 | N/A | 无接口版本 |
| G11.1 | ✅ | 单元测试存在且有断言 |
| G11.2 | ✅ | 覆盖 null、空串、前后空格等边界 |
| G11.3 | ✅ | HelloService.java:39 对 null/blank 做了防御校验 |
| G11.4 | N/A | 无数值运算 |
| G12.1 | N/A | 无资金场景 |
| G12.2 | N/A | 无资金场景 |
| G13.1 | ✅ | 日志级别正确：debug 用于正常流程，warn 用于异常入参 |
| G14.1 | N/A | 无金额 |
| G14.2 | N/A | 无多租户 |
| G14.3 | N/A | 无时区 |
| G14.4 | N/A | 无日期格式化 |
| G15.1 | N/A | 无数据库变更 |
| G15.2 | N/A | 无新旧接口 |
| G15.3 | N/A | 无开关控制 |
| G16.1 | N/A | Hello World 示例，非核心链路 |
| G16.2 | ✅ | HelloService.java:40 异常路径有日志输出 |
| G16.3 | ✅ | 日志级别正确 |
| G16.4 | ✅ | 无空 catch |
| G17.1 | N/A | 无功能开关需求 |
| G17.2 | N/A | 无降级需求 |
| G17.3 | N/A | 无数据变更 |
| G18.1 | N/A | 无安全补强场景 |
| G18.2 | N/A | 无安全补强场景 |
| G18.3 | N/A | 无安全补强场景 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | N/A | 无 SQL |
| S1.2 | N/A | 无 SQL |
| S1.3 | N/A | 无 SQL |
| S2.1 | ✅ | JSON 响应由 Jackson 自动转义，无 XSS 风险 |
| S2.2 | N/A | 无富文本 |
| S2.3 | N/A | 无模板引擎 |
| S3.1 | N/A | 无外部 URL 请求 |
| S3.2 | N/A | 无跳转 |
| S3.3 | N/A | 无外部调用 |
| S4.1 | N/A | 无系统命令 |
| S4.2 | N/A | 无文件操作 |
| S5.1 | N/A | 无 XML 解析 |
| S5.2 | N/A | 无 XPath |
| S6.1 | N/A | 无反序列化 |
| S6.2 | N/A | 无 JSON 多态 |
| S6.3 | N/A | 无敏感字段 |
| S7.1 | N/A | 无文件上传 |
| S7.2 | N/A | 无文件下载 |
| S7.3 | N/A | 无文件操作 |
| S8.1 | ⚠️ | `HelloController.java:22` 接口未接入鉴权，任何人均可访问。Hello World 示例场景下风险可控，但生产化时需接入认证。 |
| S8.2 | ✅ | 仅使用 GET 读取数据 |
| S8.3 | N/A | 无数据 ID |
| S8.4 | N/A | 无 Cookie |
| S9.1 | ✅ | 无硬编码凭证 |
| S9.2 | ✅ | 日志未记录敏感信息 |
| S9.3 | N/A | 无加密需求 |
| S9.4 | N/A | 无随机数 |
| S10.1 | N/A | 无 CSRF 场景 |
| S10.2 | N/A | 无 CORS 配置 |
| S10.3 | N/A | 无 URL 跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

> N/A(未启用自定义规则)

### 5.1 自定义扩展（`customized-checklist.md`）

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

- [x] 执行队列中每个文件 Step2、Step3、S1–S10 / G1–G17 各列均非 `⬜`（跳过文件除外）
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 G/S 与 B001–B081 / M001–M027 / I001–I010 ID 均非 `⬜`（全部 N/A 或 ✅/⚠️，有原因）
- [x] Step 5 全部 U* ID 均非 `⬜`（N/A(未启用自定义规则)）
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`
