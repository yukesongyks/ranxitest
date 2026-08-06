# Code Review Checklist

> **Change** `个人中心信息编辑功能` · **分支/Commit** `AI/task-DEV` / `d510525` · **日期** `2026-08-06`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **执行顺序（强制）**：写入本清单并进入逐文件审查前，先在目标仓库对变更路径运行 `references/script/scan-all-rules.sh`，将输出贴入 Step 3 和 Step 4 备注；再用 LLM 完成 Step 2–5 中脚本未覆盖项及复核。

> **scan-all-rules.sh 预扫输出**（2026-08-06，从仓库根执行）：
>
> ```
> === Step 4 Rule Scan (B/M/I + A/S/G) ===
> Targets: ProfileController.java User.java UserRepository.java UserService.java
> Engine:  ripgrep
>
> [P0] G16.2 — CatchWithoutLogging: .../controllers/ProfileController.java:59
> [P1] M016 — JavaTimeDefaultTimeZone: .../models/User.java:48
> [P1] M016 — JavaTimeDefaultTimeZone: .../models/User.java:49
> [P1] M016 — JavaTimeDefaultTimeZone: .../models/User.java:54
> [P2] A2.2 — WildcardImport: .../controllers/ProfileController.java:9
> [P2] A2.2 — WildcardImport: .../models/User.java:3
> [P2] A2.2 — WildcardImport: .../models/User.java:4
>
> === Summary: 7 findings (P0=1, P1=3, P2=3) | 52/222 rules scanned ===
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
| 1 | `my-spring-boot-app/src/main/java/com/example/myapp/controllers/ProfileController.java` | REQ-1/2/3 | ✅ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | ⚠️ | N/A | ⚠️ | ⚠️ 已审有问题 |
| 2 | `my-spring-boot-app/src/main/java/com/example/myapp/models/User.java` | REQ-1/2/3 | ✅ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审有问题 |
| 3 | `my-spring-boot-app/src/main/java/com/example/myapp/repositories/UserRepository.java` | REQ-1/2/3 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 4 | `my-spring-boot-app/src/main/java/com/example/myapp/services/UserService.java` | REQ-1/2/3 | ⚠️ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | ⚠️ | N/A | ⚠️ | ⚠️ 已审有问题 |

- 由 `git diff --name-only …` 等展开；**禁止 glob**；非 Java 标 `跳过`（跳过文件的 Step4 各列可统一 `跳过` 或 `N/A(非 Java)`）。
- **守卫**：无 `.java` → 按技能终止。
- **收口**：每文件各 **Sn/Gn** 列均非 `⬜` 后，再与下方 Step 4 **逐条 ID 表** 核对一致；若某大类整节与当前文件无关，该列可一次性标 `N/A(无 SQL/无 MQ/…)`，但须在 Step 4 明细对应 ID 行同样标 `N/A` 并写原因。

---

## Step 2 — 功能（产物 B）

> 仅从 spec/tasks 提 **REQ**，勿臆造。不符 spec 标 **P0**。
> 每个 REQ 都必须填写 **spec 证据** 与 **关联文件**；若命中 P0，代码证据需落到 `path:line`、测试或接口行为。
>
> **说明**：仓库中无独立 spec 文档，功能点从提交信息 `d510525 个人中心信息编辑功能` 与代码实现推断。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | Given 用户访问个人中心主页 When 点击查看 Then 展示当前用户信息 | 提交信息「个人中心信息编辑功能」—查看主页 | `ProfileController.java` | ✅ | `ProfileController.java:28-33` `viewProfile` 返回 `profile/view`，model 注入 user |
| REQ-2 | Given 用户进入编辑页 When GET /profile/edit Then 展示编辑表单 | 提交信息「个人中心信息编辑功能」—编辑入口 | `ProfileController.java` | ✅ | `ProfileController.java:38-43` `showEditForm` 返回 `profile/edit` |
| REQ-3 | Given 用户提交编辑表单 When POST /profile/edit 校验通过 Then 持久化并重定向；校验失败返回编辑页 | 提交信息「个人中心信息编辑功能」—提交修改 | `ProfileController.java` `UserService.java` | ⚠️ | `ProfileController.java:48-62` `updateProfile` 调用 `userService.updateProfile`；但 `UserService.java:45` `!user.getUsername().equals(profileDetails.getUsername())` 存在 NPE 风险（username 可能为 null 时 `.equals` 反转可避免），逻辑上功能实现但可靠性有缺陷 |

---

## Step 3 — 可读性检查（产物 C）

> 无 Java：**整节 N/A**。

对照 `references/readability-checklist.md` A1–A7 逐节核销：

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | 4 文件均为标准 Java 源文件格式，编码合规 |
| A2 | 源文件结构/import 顺序 | ⚠️ | `A2.2` 通配符 import 命中：`ProfileController.java:9`(`org.springframework.web.bind.annotation.*`)、`User.java:3`(`javax.persistence.*`)、`User.java:4`(`javax.validation.constraints.*`) |
| A3 | 代码样式 | ✅ | 缩进4空格，大括号风格一致 |
| A4 | 命名规范 | ✅ | 类名/方法名/变量名符合驼峰规范，常量无违规 |
| A5 | 编码实践 | ✅ | 构造器注入取代字段注入（`UserService:18-21`），符合推荐实践 |
| A6 | 特定元素样式 | ✅ | 无 enum/常量/注解排列违规 |
| A7 | Javadoc 规范 | ⚠️ | `ProfileController` 公共方法有 Javadoc（`25-27`/`35-37`/`45-47`）；但 `UserService.updateProfile:41` 等公共方法缺 Javadoc；`User` getter/setter 全缺 Javadoc（简单 getter 可省，但 `User` 实体无类级 Javadoc） |

---

## Step 4 — 可靠性检查（产物 D）

> **逐条核销（强制）**：G/S 每个 ID **独占一行**，禁止合并为区间。**Bug 模式** 按 `bug-pattern-checklist.md` 中 **每条 B*/M*/I*** 独占一行核销（120 条）**；无关变更可对该 ID 标 `N/A` 并写原因。报告等级：**Blocker→P0、Major→P1、Info→P2**。

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> 可先运行 `references/script/scan-all-rules.sh`（对变更目录）将命中写入备注，再人工/LLM 补全脚本未覆盖规则。
> 脚本覆盖 25/81 B + 6/27 M + 2/10 I = 33/120 条；其余 87 条由 LLM 按变更范围核销，与本次 4 文件无关的标 N/A。

| ID | 状态 | 备注（命中写 `path:line`；预扫可粘贴脚本摘要） |
|----|------|--------------------------------------------------|
| B001 | N/A | 本次变更无 ThreadPool/线程池创建，不涉及 |
| B002 | N/A | 无 SimpleDateFormat 共享使用 |
| B003 | N/A | 无手动线程中断 |
| B004 | N/A | 无 volatile/双重检查锁场景 |
| B005 | N/A | 无非线程安全的 SimpleDateFormat 静态字段 |
| B006 | N/A | 无 ThreadLocal 使用 |
| B007 | N/A | 无非线程安全集合用于并发（findAll().stream() 单线程读，无并发共享） |
| B008 | N/A | 无并发 Map 操作 |
| B009 | N/A | 无 Lock 使用 |
| B010 | N/A | 无 wait/notify |
| B011 | N/A | 无序列化场景中的非 transient 敏感字段 |
| B012 | N/A | 无 Random 指定种子 |
| B013 | N/A | 无 equals 方法实现 |
| B014 | N/A | 无 hashCode 实现（JPA 实体用默认，存在理论隐患但非本次变更引入） |
| B015 | N/A | 无 compareTo 实现 |
| B016 | N/A | 无 clone 方法 |
| B017 | N/A | 无序列化自定义逻辑 |
| B018 | N/A | 无 finalize 方法 |
| B019 | N/A | 无 println 调试残留 |
| B020 | N/A | 无 System.exit |
| B021 | N/A | 无异常控制流（IllegalArgumentException 为业务校验，非控制流） |
| B022 | N/A | 无 catch(Exception) 后吞异常（ProfileController:59 catch 具体 IllegalArgumentException） |
| B023 | N/A | 无 new Exception 抛出 |
| B024 | N/A | 无 RuntimeException 无原因链抛出 |
| B025 | N/A | 无静态集合内存泄漏 |
| B026 | N/A | 无未关闭的资源（无 Stream/Reader/Writer） |
| B027 | N/A | 无 Class.forName / 反射 |
| B028 | N/A | 无原生方法调用 |
| B029 | N/A | 无 Object 边界泛型 |
| B030 | N/A | 无 instanceof 后未做类型转换优化 |
| B031 | N/A | 无数组转 List 视图 |
| B032 | N/A | 无 subList 修改 |
| B033 | N/A | 无 Arrays.asList 原始类型数组 |
| B034 | N/A | 无 List<Object> 接收原始类型 |
| B035 | N/A | 无 Integer 缓存比较 |
| B036 | N/A | 无 BigDecimal 等值用 equals |
| B037 | N/A | 无 Long 字面量 L 后缀 |
| B038 | N/A | 无位运算符优先级问题 |
| B039 | N/A | 无自增/自减在表达式中 |
| B040 | N/A | 无位运算目标类型问题 |
| B041 | N/A | 无三元运算符空值拆箱 |
| B042 | N/A | 无 switch 缺 default |
| B043 | N/A | 无 switch 字符串 null 判断 |
| B044 | N/A | 无 switch enum 无 default |
| B045 | N/A | 无魔法值常量（@Size/@Column 数值为框架约定，非魔法值） |
| B046 | N/A | 无字符串拼接 SQL（无原生 SQL） |
| B047 | N/A | 无硬编码文件路径 |
| B048 | N/A | 无硬编码 IP |
| B049 | N/A | 无硬编码密码 |
| B050 | N/A | 无 Map.containsKey 后 get 竞态 |
| B051 | N/A | 无 Map.computeIfAbsent |
| B052 | N/A | 无 ConcurrentHashMap 复合操作 |
| B053 | N/A | 无 JPA 懒加载在事务外（UserService 整体 @Transactional） |
| B054 | N/A | 无乐观锁版本字段缺失 |
| B055 | N/A | 无 N+1 查询（本次变更新增 findByUsername/findByEmail/existsBy 均为单查询） |
| B056 | N/A | 无 JPA 实体缺无参构造——`User.java:57-58` 有显式无参构造 |
| B057 | N/A | 无 JPA 实体字段为 public |
| B058 | N/A | 无 JPA 实体缺 @Id |
| B059 | N/A | 无 JPA @OneToMany 缺 FetchType |
| B060 | N/A | 无 JPA 关联缺级联配置 |
| B061 | N/A | 无 @Controller 直接操作 Repository（均经 UserService） |
| B062 | N/A | 无 @Service 持有状态字段（UserService 仅有 final 注入） |
| B063 | N/A | 无 @Component 扫包过宽 |
| B064 | N/A | 无 @Value 注入敏感信息 |
| B065 | N/A | 无 @Autowired 字段注入——`UserService:16-21` 为构造器注入 ✅ |
| B066 | N/A | 无 @Transactional 标注 private 方法 |
| B067 | N/A | 无 @Transactional 缺 rollbackFor（UserService:13 @Transactional 默认仅对 RuntimeException 回滚；updateProfile 抛 IllegalArgumentException(运行时) 虽覆盖，但建议加 rollbackFor=Exception 明确）—标记 N/A（默认覆盖运行时异常） |
| B068 | N/A | 无 @Transactional 方法非 final 非 public 可被代理失效 |
| B069 | N/A | 无 @RequestParam 缺 required/默认值 |
| B070 | N/A | 无 @PathVariable 缺 required |
| B071 | N/A | 无 @RequestBody 缺 @Valid（`ProfileController:49` 有 @Valid ✅） |
| B072 | N/A | 无 @ModelAttribute 缺 @Valid——`ProfileController:49` 有 @Valid ✅ |
| B073 | N/A | 无返回值类型不匹配 |
| B074 | N/A | 无 @RequestMapping 同时含 path 和 method |
| B075 | N/A | 无 GET 请求执行写操作（viewProfile/showEditForm 均为读） |
| B076 | N/A | 无 @PostMapping 缺 method 指定 |
| B077 | N/A | 无 Controller 返回 void |
| B078 | N/A | 无 @RequestParam/PathVariable 未校验类型 |
| B079 | N/A | 无文件上传缺大小/类型限制 |
| B080 | N/A | 无文件下载缺路径校验 |
| B081 | N/A | 无 HTTP 重定向 URL 来自外部输入（redirect 为硬编码路径 ✅） |
| M001 | N/A | 无 Math.random 使用 |
| M002 | N/A | 无 new Date() 作为业务时间（User.java:48-49 用 LocalDateTime.now()） |
| M003 | N/A | 无 Calendar.getInstance |
| M004 | N/A | 无 Thread.sleep |
| M005 | N/A | 无 String.format |
| M006 | N/A | 无正则 Pattern.compile 未复用 |
| M007 | N/A | 无 System.currentTimeMillis |
| M008 | N/A | 无 System.nanoTime |
| M009 | N/A | 无 Runtime.getRuntime |
| M010 | N/A | 无 ProcessBuilder |
| M011 | N/A | 无 InetAddress |
| M012 | N/A | 无 new Object() 作为锁 |
| M013 | N/A | 无 Class.forName |
| M014 | N/A | 无 ClassLoader 自定义 |
| M015 | N/A | 无安全管理器使用 |
| M016 | ⚠️ | **命中** `User.java:48` `createdAt = LocalDateTime.now()`；`User.java:49` `updatedAt = LocalDateTime.now()`；`User.java:54` `updatedAt = LocalDateTime.now()` — `LocalDateTime.now()` 使用系统默认时区，多时区部署时时间基准不一致；建议显式指定时区（如 `ZonedDateTime` 或 `Instant`）。P1 |
| M017 | N/A | 无 Optional.get 未 isPresent |
| M018 | N/A | 无 Optional 字段 |
| M019 | N/A | 无 Optional 作参数 |
| M020 | N/A | 无 Optional.orElse(null) |
| M021 | N/A | 无 Stream.collect(toList) 后不可变 |
| M022 | N/A | 无 Stream.peek 副作用 |
| M023 | N/A | 无 parallelStream |
| M024 | N/A | 无 Stream.count |
| M025 | N/A | 无 Collectors.toMap 键冲突 |
| M026 | N/A | 无 reduce 缺 identity |
| M027 | N/A | 无 Collectors.groupingBy 并发 |
| I001 | N/A | 无 System.out.println |
| I002 | N/A | 无 e.printStackTrace |
| I003 | N/A | 无空 catch 块（ProfileController:59 catch 有业务处理） |
| I004 | N/A | 无 catch 后不抛不返回——`ProfileController:59-62` catch 后 return ✅ |
| I005 | N/A | 无日志框架使用异常（详见 G16.2） |
| I006 | N/A | 无 System.err |
| I007 | N/A | 无注释掉的代码块 |
| I008 | N/A | 无 TODO/FIXME 残留 |
| I009 | N/A | 无空方法体（User:57-58 构造器除外，为 JPA 要求） |
| I010 | N/A | 无未使用变量/方法 |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | N/A | 无显式线程创建 |
| G1.2 | N/A | 无线程池 |
| G1.3 | N/A | 无 volatile |
| G1.4 | N/A | 无 synchronized |
| G2.1 | N/A | 无远程调用超时配置 |
| G2.2 | N/A | 无 RPC 调用 |
| G2.3 | N/A | 无 MQ 消费 |
| G3.1 | N/A | 无 DB 查询超时配置 |
| G3.2 | N/A | 无 DB 连接泄漏（JPA 管理连接） |
| G4.1 | N/A | 无文件资源管理 |
| G4.2 | N/A | 无网络资源管理 |
| G4.3 | N/A | 无数据库连接手动管理 |
| G4.4 | N/A | 无线程资源手动管理 |
| G5.1 | N/A | 无外部 API 调用 |
| G6.1 | N/A | 无锁机制 |
| G6.2 | N/A | 无分布式锁 |
| G7.1 | N/A | 无幂等校验（createUser/updateProfile 非幂等写操作，但非幂等场景要求） |
| G7.2 | N/A | 无重试机制需求 |
| G8.1 | N/A | 无数组边界 |
| G8.2 | N/A | 无除法运算 |
| G8.3 | N/A | 无空指针防护—**注**：`UserService.java:45` `!user.getUsername().equals(profileDetails.getUsername())` 若 `user.getUsername()` 返回 null 则 NPE；`UserService:49` 同理 email。但因 `User` 实体 `username`/`email` 标注 `@NotBlank` 且 `@Column(nullable=false)`，DB 层保证非 null，JPA 从 DB 加载时不会为 null；`profileDetails` 来自表单 `@Valid`，`@NotBlank` 保证非 null。故标记 N/A（已由校验+约束覆盖），但在报告中提示反转 equals 更稳妥 |
| G8.4 | N/A | 无类型转换 |
| G8.5 | N/A | 无数值溢出 |
| G8.6 | N/A | 无正则边界 |
| G8.7 | N/A | 无集合边界 |
| G9.1 | N/A | 无灰度发布 |
| G9.2 | N/A | 无限流配置 |
| G9.3 | N/A | 无熔断 |
| G10.1 | N/A | 无日志记录框架引入（本轮无 SLF4J/logback 变更） |
| G10.2 | N/A | 无监控埋点 |
| G10.3 | N/A | 无告警配置 |
| G11.1 | N/A | 无事务回滚自定义—`UserService:13` @Transactional 默认 RuntimeException 回滚 |
| G11.2 | N/A | 无事务传播 |
| G11.3 | N/A | 无事务隔离级别配置 |
| G11.4 | N/A | 无只读事务优化 |
| G12.1 | N/A | 无定时任务 |
| G12.2 | N/A | 无异步任务 |
| G13.1 | N/A | 无数据迁移 |
| G14.1 | N/A | 无密码加密存储 |
| G14.2 | N/A | 无密码强度校验 |
| G14.3 | N/A | 无敏感信息日志输出 |
| G14.4 | N/A | 无敏感信息明文传输 |
| G15.1 | N/A | 无配置文件敏感信息 |
| G15.2 | N/A | 无密钥硬编码 |
| G15.3 | N/A | 无证书硬编码 |
| G16.1 | N/A | 无异常吞没（无 catch(Exception)） |
| G16.2 | ❌ | **命中** `ProfileController.java:59` `catch (IllegalArgumentException e)` 仅 `redirectAttributes.addFlashAttribute("error", e.getMessage())` 后重定向，**未记录日志**，排障时无法定位异常堆栈。P0（脚本预扫确认） |
| G16.3 | N/A | 无异常信息泄露—`ProfileController:60` `e.getMessage()` 直接返回前端，理论上可泄露内部校验逻辑（用户名/邮箱已存在信息），但属业务校验消息，风险较低 |
| G16.4 | N/A | 无异常后状态不一致 |
| G17.1 | N/A | 无降级策略 |
| G17.2 | N/A | 无应急开关 |
| G17.3 | N/A | 无回滚预案 |
| G18.1 | N/A | 无 SQL 注入（无原生 SQL） |
| G18.2 | N/A | 无 XSS 防护（Thymeleaf 模板默认转义，非 Java 变更范围） |
| G18.3 | N/A | 无 CSRF（Spring Security 未引入，但本次变更不含配置） |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | N/A | 无原生 SQL 拼接 |
| S1.2 | N/A | 无 JDBC Statement 拼接 |
| S1.3 | N/A | 无 JPQL/HQL 拼接 |
| S2.1 | N/A | 无认证逻辑变更 |
| S2.2 | N/A | 无授权变更 |
| S2.3 | ⚠️ | **提示** `ProfileController` 所有接口无认证检查，`getOrCreateDefaultUser` 返回首个用户。注释已标注"用于演示，实际项目应接入认证系统"（`UserService:71-73`），但作为生产代码存在越权风险。P1 |
| S3.1 | N/A | 无输入校验绕过—`@Valid` 已启用 |
| S3.2 | N/A | 无 XML 外部实体 |
| S3.3 | N/A | 无 SSRF |
| S4.1 | N/A | 无密钥硬编码 |
| S4.2 | N/A | 无密钥日志输出 |
| S5.1 | N/A | 无依赖漏洞（本次变更不涉及 pom） |
| S5.2 | N/A | 无反序列化 |
| S6.1 | N/A | 无文件上传 |
| S6.2 | N/A | 无文件下载 |
| S6.3 | N/A | 无路径遍历 |
| S7.1 | N/A | 无 XSS（Java 层无直接渲染） |
| S7.2 | N/A | 无 Open Redirect—`ProfileController:58/61` 重定向路径硬编码 ✅ |
| S7.3 | N/A | 无 CSV 注入 |
| S8.1 | N/A | 无 CSRF 配置变更 |
| S8.2 | ⚠️ | `ProfileController:48` `@PostMapping("/edit")` 未加 CSRF Token 校验，Spring Security 未引入时 POST 接口无 CSRF 防护。P2 |
| S8.3 | N/A | 无 CORS 配置变更 |
| S8.4 | N/A | 无 SameSite Cookie 配置 |
| S9.1 | N/A | 无日志注入 |
| S9.2 | N/A | 无会话固定 |
| S9.3 | N/A | 无会话超时配置 |
| S9.4 | N/A | 无 Cookie 安全标志 |
| S10.1 | N/A | 无跳转白名单 |
| S10.2 | N/A | 无外部 URL |
| S10.3 | N/A | 无 open redirect 参数 |

---

## Step 5 — 自定义扩展检查（产物 E）

> 按 `customized-checklist.md` 逐条核销；若未启用可整节写 `N/A(未启用自定义规则)`。

### 5.1 自定义扩展（`customized-checklist.md`）

> `customized-checklist.md` 为空或全为示例项，本节标 `N/A(未启用自定义规则)`。

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | N/A(未启用自定义规则) | customized-checklist.md 未配置实际规则 |
| U1.2 | N/A(未启用自定义规则) | 同上 |
| U1.3 | N/A(未启用自定义规则) | 同上 |
| U2.1 | N/A(未启用自定义规则) | 同上 |
| U2.2 | N/A(未启用自定义规则) | 同上 |
| U2.3 | N/A(未启用自定义规则) | 同上 |

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、**S1–S10 / G1–G17** 各列均非 `⬜`（跳过文件除外）；
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`（允许 `N/A`，但有原因）
- [x] Step 5 全部 U* ID 均非 `⬜`（允许 `N/A(未启用自定义规则)`）
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`
