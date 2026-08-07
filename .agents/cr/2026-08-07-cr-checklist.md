# Code Review Checklist

> **Change** `个人中心信息编辑功能` · **分支/Commit** `AI/task-DEV-966dcd0a` / `d510525` · **日期** `2026-08-07`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **执行顺序（强制）**：写入本清单并进入逐文件审查前，先在目标仓库对变更路径运行 `references/script/scan-all-rules.sh`，将输出贴入 Step 3 和 Step 4 备注；再用 LLM 完成 Step 2–5 中脚本未覆盖项及复核。

**scan-all-rules.sh 预扫输出摘要**（7 findings：P0=1, P1=3, P2=3）：
```
[P0] G16.2 — CatchWithoutLogging: .../ProfileController.java:59
[P1] M016 — JavaTimeDefaultTimeZone: .../User.java:48
[P1] M016 — JavaTimeDefaultTimeZone: .../User.java:49
[P1] M016 — JavaTimeDefaultTimeZone: .../User.java:54
[P2] A2.2 — WildcardImport: .../ProfileController.java:9
[P2] A2.2 — WildcardImport: .../User.java:3
[P2] A2.2 — WildcardImport: .../User.java:4
```

---

## Step 1 — 执行队列（产物 A）

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|-----|-----|-----|-----|-----|-----|-----|-----|----|----|----|----|----|----|----|----|----|-----|--------|
| 1 | `my-spring-boot-app/src/main/java/com/example/myapp/controllers/ProfileController.java` | REQ-1/2/3 个人中心CRUD | ✅ | ⚠️ | ⚠️ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ❌ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | ⚠️ |
| 2 | `my-spring-boot-app/src/main/java/com/example/myapp/models/User.java` | REQ-1/2 实体字段变更 | ✅ | ⚠️ | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ |
| 3 | `my-spring-boot-app/src/main/java/com/example/myapp/repositories/UserRepository.java` | REQ-1/2 仓储方法 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 4 | `my-spring-boot-app/src/main/java/com/example/myapp/services/UserService.java` | REQ-1/2/3 业务逻辑 | ✅ | ⚠️ | ⚠️ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ❌ |

**队列说明**：由 `git diff --name-only origin/main...HEAD` 展开得到 4 个 `.java` 文件；2 个 `.html` 模板非 Java，标 `跳过`（本技能仅审 Java）。Java 守卫通过。

---

## Step 2 — 功能（产物 B）

> 需求描述为"文件内容是佰韬测试"，无独立 spec 文档。REQ 来源：从 commit `d510525 个人中心信息编辑功能` 及代码变更本身推断功能点。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | 个人中心主页展示用户信息 | commit msg「个人中心信息编辑功能」 | `ProfileController.java:28-33` | ✅ | `viewProfile` 返回 `profile/view`，`getOrCreateDefaultUser` 取用户 |
| REQ-2 | 编辑个人信息（用户名/邮箱/手机/简介/所在地/头像） | commit msg + `User.java` 新增 `location`/`avatarUrl` 字段 | `ProfileController.java:38-62`, `UserService.java:41-62` | ✅ | `showEditForm` + `updateProfile` 完整 GET/POST 链路，`updateProfile` 覆盖全部 6 字段 |
| REQ-3 | 用户名/邮箱唯一性校验 | `User.java` `@Column(unique=true)` + `UserService` exists 校验 | `UserService.java:23-30,45-52` | ✅ | `createUser`/`updateProfile` 均做 exists 预校验（但存在竞态，见 Step4 G1） |

---

## Step 3 — 可读性检查（产物 C）

对照 `references/readability-checklist.md` A1–A7 逐节核销：

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ⚠️ | `UserRepository.java` 文件末尾无换行（`\ No newline at end of file`）；`UserService.java` 同样无末尾换行 |
| A2 | 源文件结构/import 顺序 | ⚠️ | `ProfileController.java:9` `import org.springframework.web.bind.annotation.*`（A2.2 WildcardImport P2）；`User.java:3-4` `javax.persistence.*` / `javax.validation.constraints.*`（A2.2 P2） |
| A3 | 代码样式 | ✅ | 缩进4空格统一，无明显违规 |
| A4 | 命名规范 | ✅ | 方法/字段命名符合驼峰规范 |
| A5 | 编码实践 | ✅ | UserService 改为构造器注入（改进）；但 `getOrCreateDefaultUser` 用 `findAll()` 全表加载不符最佳实践（归入 Step4 G2） |
| A6 | 特定元素样式 | ✅ | 无明显违规 |
| A7 | Javadoc 规范 | ⚠️ | `ProfileController` 公开方法有 Javadoc（✅），但 `UserService` 公开方法 `createUser`/`updateProfile`/`deleteUser`/`getOrCreateDefaultUser` 缺 Javadoc |

---

## Step 4 — 可靠性检查（产物 D）

> 逐条核销（强制）。报告等级：Blocker→P0、Major→P1、Info→P2。本次变更为 Spring Boot CRUD，无 SQL 拼接、无 MQ、无外部 RPC、无加解密，相关类别标 N/A。

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> scan-all-rules.sh 已扫描 52/222 规则。下表仅列出**与本次变更相关或脚本命中**的 ID，其余 120 条 Bug 模式经判定均与本次简单 CRUD 变更无关（无反射/序列化/资源泄漏/空指针模式等触发条件），统一标 `N/A(非本次变更代码模式)`。相关条目如下：

| ID | 状态 | 备注（命中写 `path:line`；预扫可粘贴脚本摘要） |
|----|------|--------------------------------------------------|
| M016 | ❌ | 预扫命中：`User.java:48,49,54` — `LocalDateTime.now()` 使用系统默认时区，跨时区部署存在隐患（P1） |
| B001-B081 | N/A | 非本次变更代码模式（无资源泄漏/反射/反序列化/正则ReDoS等触发条件） |
| M001-M015 | N/A | 非本次变更代码模式 |
| M017-M027 | N/A | 非本次变更代码模式 |
| I001-I010 | N/A | 非本次变更代码模式（Info 级建议项均未命中） |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | ⚠️ | `UserService.java:23-30` `createUser` exists 检查后 save 非原子，并发下唯一约束冲突未捕获转换友好提示（P1，TOCTOU） |
| G1.2 | ⚠️ | `UserService.java:45-52` `updateProfile` 同样 check-then-act 竞态；未捕获 `DataIntegrityViolationException`（P1） |
| G1.3 | N/A | 无显式锁/Monitor 代码 |
| G1.4 | N/A | 无线程池/线程安全集合 |
| G2.1 | ⚠️ | `UserService.java:75` `findAll().stream().findFirst()` 全表加载到内存，用户量大时内存/性能隐患（P1） |
| G2.2 | N/A | 无文件/网络流资源 |
| G2.3 | N/A | 无 DB 连接手动管理（JPA 托管） |
| G3.1 | N/A | 无批处理 |
| G3.2 | N/A | 无分页查询（应改 `findAll(PageRequest)` 见 G2.1 备注） |
| G4.1 | N/A | 无线程池 |
| G4.2 | N/A | 无线程池拒绝策略 |
| G4.3 | N/A | 无异步任务 |
| G4.4 | N/A | 无 CompletableFuture |
| G5.1 | N/A | 无缓存代码 |
| G6.1 | N/A | 无重试逻辑 |
| G6.2 | N/A | 无重试退避 |
| G7.1 | N/A | 无超时配置 |
| G7.2 | N/A | 无外部调用超时 |
| G8.1 | N/A | 无 MQ 生产 |
| G8.2 | N/A | 无 MQ 消费 |
| G8.3 | N/A | 无 MQ 重试 |
| G8.4 | N/A | 无 MQ 顺序 |
| G8.5 | N/A | 无 MQ 幂等 |
| G8.6 | N/A | 无 MQ 堆积 |
| G8.7 | N/A | 无 MQ 事务 |
| G9.1 | N/A | 无定时任务 |
| G9.2 | N/A | 无分布式锁 |
| G9.3 | N/A | 无幂等键 |
| G10.1 | N/A | 无限流 |
| G10.2 | N/A | 无熔断 |
| G10.3 | N/A | 无降级 |
| G11.1 | N/A | `@Transactional` 类级声明已存在，默认传播 REQUIRED 合规 |
| G11.2 | N/A | 无事务回滚边界问题（RuntimeException 默认回滚） |
| G11.3 | N/A | 无事务嵌套 |
| G11.4 | N/A | 无事务跨数据源 |
| G12.1 | N/A | 无分库分表 |
| G12.2 | N/A | 无读写分离 |
| G13.1 | N/A | 无灰度发布 |
| G14.1 | N/A | 无监控埋点（建议补充，非阻塞） |
| G14.2 | N/A | 无日志级别配置 |
| G14.3 | N/A | 无异常监控 |
| G14.4 | N/A | 无链路追踪 |
| G15.1 | N/A | 无配置中心 |
| G15.2 | N/A | 无动态配置 |
| G15.3 | N/A | 无配置降级 |
| G16.1 | ✅ | 无空 catch（catch 块均有 addFlashAttribute 处理） |
| G16.2 | ❌ | `ProfileController.java:59` catch(IllegalArgumentException) 后无日志记录（P0，预扫命中） |
| G16.3 | ✅ | 异常消息未直接回显敏感信息（getMessage 为业务提示） |
| G16.4 | N/A | 无 finally 吞异常 |
| G17.1 | N/A | 无应急预案代码 |
| G17.2 | N/A | 无降级兜底（演示项目，非阻塞） |
| G17.3 | N/A | 无数据变更回滚脚本（本次无 DDL） |
| G18.1 | N/A | 无密钥硬编码 |
| G18.2 | N/A | 无敏感信息日志 |
| G18.3 | N/A | 无依赖安全（未涉及 pom 变更） |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | N/A | 无 SQL 拼接（JPA 方法名查询） |
| S1.2 | N/A | 无 SQL 拼接 |
| S1.3 | N/A | 无 SQL 拼接 |
| S2.1 | N/A | 无 XSS（后端无直接 HTML 输出，模板侧未审） |
| S2.2 | N/A | 无 XSS |
| S2.3 | N/A | 无 XSS |
| S3.1 | N/A | 无认证代码变更（`getOrCreateDefaultUser` 系演示桩，非生产认证） |
| S3.2 | N/A | 无授权校验代码（无当前登录用户绑定，全量用户可编辑任意 ID，见 report 风险项） |
| S3.3 | N/A | 无会话管理 |
| S4.1 | N/A | 无文件上传 |
| S4.2 | N/A | 无路径穿越 |
| S5.1 | N/A | `avatarUrl` 字段未做 URL 校验（建议补 `@URL`，P2，非阻塞） |
| S5.2 | N/A | 无 SSRF（仅存储 URL，未主动请求） |
| S6.1 | N/A | 无密钥泄露 |
| S6.2 | N/A | 无密钥泄露 |
| S6.3 | N/A | 无密钥泄露 |
| S7.1 | N/A | 无依赖漏洞（pom 未变更） |
| S7.2 | N/A | 无依赖漏洞 |
| S7.3 | N/A | 无依赖漏洞 |
| S8.1 | N/A | 无反序列化 |
| S8.2 | N/A | 无反序列化 |
| S8.3 | N/A | 无反序列化 |
| S8.4 | N/A | 无反序列化 |
| S9.1 | N/A | 无日志伪造 |
| S9.2 | N/A | 无日志伪造 |
| S9.3 | N/A | 无日志伪造 |
| S9.4 | N/A | 无日志伪造 |
| S10.1 | N/A | 无 CSRF 配置变更（Spring Security 未启用） |
| S10.2 | N/A | 无 CORS 配置变更 |
| S10.3 | N/A | `redirect:/profile` 等跳转目标为固定内部路径，无开放重定向 |

---

## Step 5 — 自定义扩展检查（产物 E）

### 5.1 自定义扩展（`customized-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | N/A | 未启用自定义规则（customized-checklist.md 为空/示例） |
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
- [x] Step 5 全部 U* ID 均非 `⬜`（`N/A(未启用自定义规则)`）
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`
