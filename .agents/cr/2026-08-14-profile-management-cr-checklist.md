# Code Review Checklist

> **Change** `personal-center-profile-management` · **分支/Commit** `AI/task-DEV-f4ad1a6e-...-1abc0d43` / `793cd2c` (vs `main` `406a564`) · **日期** `2026-08-14`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **预扫**：已对 4 个变更 `.java` 文件运行 `scan-all-rules.sh`，输出 7 条（P0=1, P1=3, P2=3），已并入 Step 3/Step 4 备注。

---

## Step 1 — 执行队列（产物 A）

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|-----|----|----|----|----|----|----|----|----|----|-----|-----|-----|-----|-----|-----|-----|-----|--------|
| 1 | `my-spring-boot-app/src/main/java/com/example/myapp/controllers/ProfileController.java` | REQ-1/2/3/7 | ✅ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | ⚠️ | ⚠️ 已审有问题 |
| 2 | `my-spring-boot-app/src/main/java/com/example/myapp/models/User.java` | REQ-4/5/6 | ✅ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审有问题 |
| 3 | `my-spring-boot-app/src/main/java/com/example/myapp/repositories/UserRepository.java` | REQ-4 | ✅ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 4 | `my-spring-boot-app/src/main/java/com/example/myapp/services/UserService.java` | REQ-3/4 | ✅ | ⚠️ | ⚠️ | N/A | ⚠️ | ⚠️ | N/A | N/A | N/A | ⚠️ | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | ⚠️ | N/A | ⚠️ 已审有问题 |

> 队列展开自 `git diff --name-only main -- '*.java'`。非 Java 变更（`edit.html`/`view.html`/`README.md`）未纳入本技能审查范围（模板/文档另审）。

---

## Step 2 — 功能（产物 B）

> REQ 来源：commit `8c6e331 feat: add personal center (profile) management feature` 与 `d510525 个人中心信息编辑功能`（仓库无独立 spec 文档，以变更 commit message + diff 为 spec 等价来源）。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | Given 访问 `/profile`，When GET，Then 展示当前用户信息 | commit: "add personal center (profile) management feature" / 主页展示用户信息 | `ProfileController.java` | ✅ | `ProfileController.java:28-33` viewProfile → `profile/view` |
| REQ-2 | Given 访问 `/profile/edit`，When GET，Then 进入编辑页 | commit: "个人中心信息编辑功能" | `ProfileController.java` | ✅ | `ProfileController.java:38-43` showEditForm → `profile/edit` |
| REQ-3 | Given 提交编辑表单，When POST `/profile/edit`，Then 保存修改并重定向 | commit: "个人中心信息编辑功能" | `ProfileController.java` / `UserService.java` | ✅ | `ProfileController.java:48-62` → `UserService.updateProfile:41-62` |
| REQ-4 | Given 用户名/邮箱，Then 唯一性约束（DB + 应用层校验） | diff: `@Column(nullable=false, unique=true)` + `existsByUsername/existsByEmail` | `User.java` / `UserRepository.java` / `UserService.java` | ✅ | `User.java:17,22`；`UserRepository.java:16-18`；`UserService.java:24-29,45-52` |
| REQ-5 | Given 用户名/邮箱，Then 非空校验 | diff: `@NotBlank` | `User.java` | ✅ | `User.java:15,20` |
| REQ-6 | Given 用户资料，Then 新增 location 与 avatarUrl 字段 | diff: 新增 `location`/`avatarUrl` 字段及 getter/setter | `User.java` | ✅ | `User.java:33-38,100-114`；模板 `edit.html:298,305` / `view.html:256,291` |
| REQ-7 | Given 个人中心，Then 移除物品管理子功能、聚焦个人资料 | diff: 删除 `listMyItems/createItem/updateItem/deleteItem` | `ProfileController.java` | ✅ | `ProfileController.java` 仅保留 view/edit/update 三方法 |

> 功能性结论：所有 REQ 均满足，无 P0 功能性不符。

---

## Step 3 — 可读性检查（产物 C）

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ⚠️ | `UserRepository.java`/`UserService.java` 文件末尾无换行（diff 标注 `\ No newline at end of file`）；P2 |
| A2 | 源文件结构/import 顺序 | ⚠️ | A2.2 通配符 import：`ProfileController.java:9`(`web.bind.annotation.*`)、`User.java:3`(`persistence.*`)、`User.java:4`(`validation.constraints.*`)；P2 |
| A3 | 代码样式 | ✅ | 缩进 4 空格、K&R 大括号、行宽 ≤120，均符合 |
| A4 | 命名规范 | ✅ | 类/方法/字段命名符合 lowerCamelCase/UpperCamelCase |
| A5 | 编码实践 | ✅ | catch 块非空（有 flash 反馈）；无 `finalize` 重写 |
| A6 | 特定元素样式 | ✅ | 注解每行一个；无 switch/数组/long 字面量问题 |
| A7 | Javadoc 规范 | ⚠️ | `UserService` public 方法 `createUser/getUserById/getAllUsers/updateProfile/deleteUser` 缺 Javadoc（仅 `getOrCreateDefaultUser` 有）；A7.1 P2。`ProfileController` 三方法已有 Javadoc ✅；`User` getter/setter 按 A7.3 可省略 |

---

## Step 4 — 可靠性检查（产物 D）

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> 预扫（`scan-all-rules.sh`）命中：M016 ×3（`User.java:48,49,54`）。其余 B/M/I 经 LLM 逐条核对，本次变更未命中（无数组/BigDecimal/线程/JDBC/JUnit/Mockito/日期格式化等场景）。

| ID | 状态 | 备注 |
|----|------|------|
| B001 | N/A | 无 LocalDateTime.parse/UUID.fromString 字面量调用 |
| B002–B081 | N/A | 逐条核对：无数组比较/填充/toString、无 `Arrays.asList`、无 JUnit/Mockito、无 BigDecimal/浮点比较、无 Calendar/DateFormat、无集合自身操作、无自赋值/自比较、无 `@Transactional` 非public方法（B076：UserService 方法均 public）、无死循环/死异常/死线程。变更未涉及相关模式 |
| M001 | N/A | 无连续相同条件判断 |
| M002 | N/A | 无冗余 instanceof |
| M003 | N/A | 无包装类构造器 |
| M004 | N/A | catch 块无 `printStackTrace()` |
| M005 | N/A | 无非静态内部类 |
| M006 | N/A | 无编译期布尔常量误用 |
| M007 | N/A | catch 块非空（`ProfileController.java:59-62` 有 flash 反馈） |
| M008 | N/A | 未重写 equals |
| M009 | N/A | 无不相容类型 equals |
| M010 | N/A | 无位运算恒 0 |
| M011 | N/A | 无 switch fall-through |
| M012 | N/A | 无 finally return/throw |
| M013 | N/A | 无浮点强转 |
| M014 | N/A | 无枚举 getClass |
| M015 | N/A | 无字段隐藏 |
| M016 | ⚠️ | `User.java:48,49,54` `LocalDateTime.now()` 依赖系统默认时区；P1（Major→P1） |
| M017–M027 | N/A | 无 JUnit4/锁/Optional.toString/ThreadLocal 等相关场景 |
| I001–I010 | N/A | 均为单测/容器相关，本次变更无测试代码 |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | ⚠️ | `UserService.java:41-61` 事务内先读后写（findById→set→save）无 `SELECT FOR UPDATE`/乐观锁，存在 TOCTOU；DB unique 约束兜底防重复，但建议捕获 `DataIntegrityViolationException` 或加版本号；P1 |
| G1.2 | N/A | 无已加锁场景 |
| G1.3 | N/A | 无乐观锁重试 |
| G1.4 | N/A | 无多锁顺序 |
| G2.1 | N/A | 无写接口幂等键要求（profile 编辑非资金/消息） |
| G2.2 | N/A | 无重试/MQ 重投 |
| G2.3 | N/A | 无幂等键约定 |
| G3.1 | N/A | 无分布式事务 |
| G3.2 | ⚠️ | `UserService.java:12-14` 类级 `@Transactional`，`getAllUsers:37`/`getOrCreateDefaultUser:74`(只读) 亦纳入事务，范围偏大；P2 |
| G4.1 | N/A | 无复杂 SQL 分支 |
| G4.2 | N/A | 无索引列函数/隐式转换 |
| G4.3 | ⚠️ | `UserService.java:37` `findAll()` 无分页；`UserService.java:75` `findAll().findFirst()` 全表加载取首条；P1 |
| G4.4 | N/A | 无深分页 |
| G5.1 | N/A | 无 MQ |
| G6.1 | N/A | 无缓存 |
| G6.2 | N/A | 无缓存双写 |
| G7.1 | N/A | 无调度任务 |
| G7.2 | N/A | 无调度开关/熔断 |
| G8.1 | ⚠️ | `ProfileController.java:59` catch 业务异常后仅 flash 反馈、无日志告警，可观测性不足（与 G16.2 同源）；P1 |
| G8.2 | N/A | 无核心链路强依赖 |
| G8.3 | N/A | 无 I/O 流/连接/锁未释放 |
| G8.4 | N/A | 无线程池关闭 |
| G8.5 | N/A | 无 ThreadLocal |
| G8.6 | N/A | 无 Executors 线程池 |
| G8.7 | N/A | 无线程池拒绝策略 |
| G9.1 | N/A | 无外部调用 |
| G9.2 | N/A | 无外部调用超时 |
| G9.3 | N/A | 无重试 |
| G10.1 | N/A | 无 null 兼表无数据/异常 |
| G10.2 | N/A | 无契约版本变更 |
| G10.3 | N/A | 无契约字段约定 |
| G11.1 | N/A | 本次变更未含单测（仓库无对应测试） |
| G11.2 | ⚠️ | `UserService.java:74-85` `getOrCreateDefaultUser` 用 `findAll().findFirst()` 选取"当前用户"，结果非确定（依赖 DB 返回顺序），与原 `findById(1L)` 语义变更，多用户场景会操作非预期用户；P1 |
| G11.3 | ⚠️ | `UserService.java:23,41` public 方法 `createUser/updateProfile` 入参未做 null 防御（依赖 Controller `@Valid`；public service 建议显式校验 username/email 非空）；P2 |
| G11.4 | N/A | 无数值运算/金额 |
| G12.1 | N/A | 无资金/库存/积分 |
| G12.2 | N/A | 无止血手段要求（非资金链路） |
| G13.1 | N/A | 无日志级别误用 |
| G14.1 | N/A | 无金额 |
| G14.2 | N/A | 无多租户 |
| G14.3 | ⚠️ | `User.java:48,49,54` `LocalDateTime.now()` 依赖系统默认时区，跨区/部署时区变更会产生偏差；建议 `Instant`/UTC 或显式 ZoneId；P1（与 M016 同源） |
| G14.4 | N/A | 无 SimpleDateFormat/DateTimeFormatter 解析 |
| G15.1 | ⚠️ | `User.java:17,22` username/email 改为 `nullable=false,unique=true`、新增 `location/avatarUrl` 列；schema 变更需确保现有数据满足唯一/非空约束并配迁移脚本，否则旧数据触发约束冲突；P1 |
| G15.2 | N/A | 无新旧接口共存 |
| G15.3 | N/A | 无不兼容逻辑开关 |
| G16.1 | N/A | 无核心链路指标埋点要求（demo） |
| G16.2 | ⚠️ | `ProfileController.java:59` 异常路径无日志输出（无可追溯 traceId/bizId）；脚本标 P0，按清单 G16.2=P1，且为预期业务异常，降级 P1 |
| G16.3 | ⚠️ | `ProfileController.java:59` 业务异常（IllegalArgumentException）未打 WARN 日志；P2 |
| G16.4 | N/A | catch 非空、无 `printStackTrace`、无关键路径吞异常继续执行 |
| G17.1 | N/A | 无功能开关 |
| G17.2 | N/A | 无降级预案要求（demo） |
| G17.3 | N/A | 无数据回滚脚本要求（schema 变更见 G15.1） |
| G18.1 | N/A | 无安全补强项（参考性） |
| G18.2 | N/A | 同上 |
| G18.3 | N/A | 同上 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | N/A | 无 SQL 拼接（Spring Data JPA 方法名查询） |
| S1.2 | N/A | 无动态表名/字段名 |
| S1.3 | N/A | 无 like/in 拼接 |
| S2.1 | N/A | 模板输出由 Thymeleaf 默认 HTML 转义，无属性突破 |
| S2.2 | N/A | 无富文本 |
| S2.3 | ⚠️ | `view.html:257` `th:src="${user.avatarUrl}"` 渲染用户可控 URL，`User.java:37-38` avatarUrl 无协议白名单校验（仅限 length=500）；建议限制 `http/https` 协议防 `javascript:`/非常规协议；P1 |
| S3.1 | N/A | 无外部 URL 请求 |
| S3.2 | N/A | 无跳转目标校验 |
| S3.3 | N/A | 无外部调用超时 |
| S4.1 | N/A | 无命令拼接 |
| S4.2 | N/A | 无文件/图片外部命令 |
| S5.1 | N/A | 无 XML 解析 |
| S5.2 | N/A | 无 XPath |
| S6.1 | N/A | 无反序列化 |
| S6.2 | N/A | 无 JSON 多态 |
| S6.3 | N/A | 无敏感字段 transient 要求 |
| S7.1 | N/A | 无文件上传后端（avatarUrl 为 URL 字符串） |
| S7.2 | N/A | 无路径穿越 |
| S7.3 | N/A | 无文件重命名存储 |
| S8.1 | ⚠️ | `ProfileController.java` 所有端点无鉴权，`getOrCreateDefaultUser` 无认证即返回用户并允许编辑（demo 限制，注释已提示接入认证）；P1 |
| S8.2 | N/A | 无 GET 执行增删改（POST 用于 update） |
| S8.3 | N/A | 无数据 ID 不可预测要求（demo） |
| S8.4 | N/A | 无 Cookie 设置 |
| S9.1 | ⚠️ | `UserService.java:79-80` 硬编码默认用户 `admin`/`admin@example.com`（非密钥，但建议配置化）；P2 |
| S9.2 | N/A | 无日志记录敏感信息 |
| S9.3 | N/A | 无传输/存储加密要求（demo） |
| S9.4 | N/A | 无随机数 |
| S10.1 | ⚠️ | `ProfileController.java:48` POST `/profile/edit` 无 CSRF Token 防护；P1 |
| S10.2 | N/A | 无 CORS 配置 |
| S10.3 | N/A | 无 URL 跳转白名单 |

---

## Step 5 — 自定义扩展检查（产物 E）

### 5.1 自定义扩展（`customized-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | ✅ | 示例项：Controller 入参已使用 `@Valid`（`ProfileController.java:49`） |
| U1.2 | N/A | 未启用自定义规则（清单仅含示例项） |
| U1.3 | N/A | 同上 |
| U2.1 | N/A | 未启用自定义规则 |
| U2.2 | N/A | 同上 |
| U2.3 | N/A | 同上 |

> `customized-checklist.md` 仅含示例项（U1.1 为示例），未承载团队私有规则，整节视为 `N/A(未启用自定义规则)`。

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、**S1–S10 / G1–G17** 各列均非 `⬜`（跳过文件除外）
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`（允许 `N/A`，但有原因）
- [x] Step 5 全部 U* ID 均非 `⬜`（`N/A(未启用自定义规则)`）
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`
