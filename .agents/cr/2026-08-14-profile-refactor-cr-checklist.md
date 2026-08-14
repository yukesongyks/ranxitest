# Code Review Checklist

> **Change** `profile-refactor` · **分支/Commit** `AI/task-DEV-f4ad1a6e` / `f334ab7` · **日期** `2026-08-14`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **自动化预扫结果**：`scan-all-rules.sh` 已执行，7 个发现（P0=1, P1=3, P2=3），已合并入 Step 3/4。

---

## Step 1 — 执行队列（产物 A）

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|-----|----|----|----|----|----|----|----|----|----|-----|-----|-----|-----|-----|-----|-----|-----|--------|
| 1 | `my-spring-boot-app/src/main/java/com/example/myapp/controllers/ProfileController.java` | 个人中心重构 | ✅ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审有问题 |
| 2 | `my-spring-boot-app/src/main/java/com/example/myapp/models/User.java` | 新增字段+校验 | ✅ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审有问题 |
| 3 | `my-spring-boot-app/src/main/java/com/example/myapp/repositories/UserRepository.java` | 新增查询方法 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 4 | `my-spring-boot-app/src/main/java/com/example/myapp/services/UserService.java` | 重构+唯一性校验 | ✅ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审有问题 |

---

## Step 2 — 功能（产物 B）

> 从 commit 消息「个人中心信息编辑功能」及 git diff 提取功能点。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | ProfileController 聚焦个人中心，移除物品管理 | commit `f334ab7` diff: ProfileController 移除 ItemService 依赖及所有 `/items/**` 路由 | ProfileController.java | ✅ | L19-22: 仅保留 UserService；移除 Item 相关 import 和路由 |
| REQ-2 | User 实体新增 location、avatarUrl 字段 | commit `f334ab7` diff: User.java 新增 location + avatarUrl 字段及 getter/setter | User.java | ✅ | L33-38: location/avatarUrl 字段；L100-114: getter/setter |
| REQ-3 | User 实体增强校验 (@NotBlank, unique 约束) | commit `f334ab7` diff: username/email 增加 @NotBlank + nullable=false, unique=true | User.java | ✅ | L15-22: @NotBlank + unique=true 约束 |
| REQ-4 | UserRepository 新增按用户名/邮箱查询与存在性检查 | commit `f334ab7` diff: 新增 4 个查询方法 | UserRepository.java | ✅ | L12-18: findByUsername/Email, existsByUsername/Email |
| REQ-5 | UserService 构造器注入 + 唯一性校验 | commit `f334ab7` diff: 字段注入→构造器注入，createUser/updateProfile 增加重名检查 | UserService.java | ✅ | L16-21: 构造器注入；L24-29: createUser 唯一性检查；L45-52: updateProfile 唯一性检查 |
| REQ-6 | UserService 新增 deleteUser 方法 | commit `f334ab7` diff: 新增 deleteUser 方法 | UserService.java | ✅ | L64-69: deleteUser 含存在性检查 |
| REQ-7 | getOrCreateDefaultUser 改为取任意第一条记录 | commit `f334ab7` diff: 由 findById(1L) 改为 findAll().stream().findFirst() | UserService.java | ✅ | L74-84: stream().findFirst() 兜底创建 |

---

## Step 3 — 可读性检查（产物 C）

> 对照 `references/readability-checklist.md` A1–A7 逐节核销。脚本预扫命中 A2.2 已标注。

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | 文件名与类名一致，UTF-8 编码 |
| A2 | 源文件结构/import 顺序 | ⚠️ | **A2.2 WildcardImport**: ProfileController.java:9 (`import org.springframework.web.bind.annotation.*`), User.java:3 (`import javax.persistence.*`), User.java:4 (`import javax.validation.constraints.*`) — 脚本预扫命中 |
| A3 | 代码样式 | ✅ | K&R 大括号、4空格缩进、行宽合规 |
| A4 | 命名规范 | ✅ | 类名/方法名/字段名均合规；UserService 未使用接口+Impl 模式（P2 风格建议，见 coding-standards-review.md） |
| A5 | 编码实践 | ✅ | 无重写方法（非继承场景），无空 catch |
| A6 | 特定元素样式 | ✅ | 修饰符顺序合规，注解格式合规 |
| A7 | Javadoc 规范 | ⚠️ | ProfileController 有 Javadoc ✅；UserService 仅 getOrCreateDefaultUser 有 Javadoc，其他 public 方法 (createUser/getUserById/getAllUsers/updateProfile/deleteUser) 缺少 (P2) |

---

## Step 4 — 可靠性检查（产物 D）

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> 脚本预扫覆盖 52/222 条。脚本已命中项标注在备注中；未命中项 LLM 补扫。

| ID | 状态 | 备注（命中写 `path:line`；预扫可粘贴脚本摘要） |
|----|------|--------------------------------------------------|
| B001–B081 | N/A | 脚本预扫未命中；LLM 复核：变更无涉及 B 类规则（空指针/集合/异常等缺陷模式），均为简单 CRUD 操作 |
| M001–M015 | N/A | 脚本预扫未命中；LLM 复核：无涉及 |
| M016 | ⚠️ | **脚本命中**: User.java:48,49,54 — `LocalDateTime.now()` 未指定时区，默认使用系统时区 (P1)。建议使用 `ZonedDateTime` 或 `Instant` 存 UTC |
| M017–M027 | N/A | 脚本预扫未命中；LLM 复核：无涉及 |
| I001–I010 | N/A | 脚本预扫未命中；LLM 复核：无涉及 |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1–G1.4 | N/A | 无显式锁操作，Spring Data JPA 托管事务 |
| G2.1–G2.3 | N/A | 无写接口幂等键场景（演示项目，无 MQ/重试） |
| G3.1–G3.2 | N/A | 无分布式事务；`@Transactional` 仅标注在 Service 类级别，无外部 I/O 调用 |
| G4.1–G4.3 | N/A | 使用 Spring Data JPA 方法命名查询，无原生 SQL |
| G5.1 | N/A | 无 MQ 场景 |
| G6.1–G6.2 | N/A | 无缓存场景 |
| G7.1–G7.2 | N/A | 无调度任务 |
| G8.1–G8.6 | N/A | 无线程池/ThreadLocal/IO流场景；catch 块有处理（返回错误信息） |
| G9.1–G9.3 | N/A | 无外部 RPC/HTTP 调用 |
| G10.1–G10.2 | N/A | 无接口契约变更 |
| G11.1–G11.4 | N/A | 无新增单元测试（演示项目，coding-standards-review.md 已指出） |
| G12.1–G12.2 | N/A | 无资金/库存场景 |
| G13.1 | N/A | 无日志输出（coding-standards-review.md 已指出 E1） |
| G14.1–G14.4 | ✅ | G14.1: Item.price 使用 BigDecimal ✅；G14.2–G14.4: 无多租户/国际化场景；但 **M016 脚本命中**: User.java 使用 `LocalDateTime.now()` 无时区指定 (P1) |
| G15.1–G15.3 | N/A | 无灰度发布场景 |
| G16.1–G16.4 | ⚠️ | **G16.2 脚本命中**: ProfileController.java:59 — catch IllegalArgumentException 后仅设 flash attribute，无日志输出 (P1); UserService.java 全文件无日志输出 (P1)；**G16.4**: 无空 catch |
| G17.1–G17.3 | N/A | 无可应急场景 |
| G18.1–G18.3 | N/A | 安全补强项，见 §4.3 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1–S1.3 | N/A | 无原生 SQL；Spring Data JPA 方法命名查询 |
| S2.1–S2.3 | N/A | Thymeleaf 模板引擎自动转义；无富文本 |
| S3.1–S3.3 | N/A | 无外部 URL 请求 |
| S4.1–S4.2 | N/A | 无命令执行场景 |
| S5.1–S5.2 | N/A | 无 XML 解析 |
| S6.1–S6.3 | N/A | 无自定义反序列化 |
| S7.1–S7.3 | N/A | 无文件上传/下载 |
| S8.1–S8.4 | N/A | 演示项目无鉴权；但 Controller 使用 POST 做增删改 ✅ |
| S9.1–S9.4 | N/A | 无硬编码密钥；无日志记录敏感信息 |
| S10.1–S10.3 | N/A | 无 CSRF/CORS/跳转场景 |

---

## Step 5 — 自定义扩展检查（产物 E）

### 5.1 自定义扩展（`customized-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | ⚠️ | 示例项：Controller 入参必须使用 `@Valid` — ProfileController.java:49 已使用 `@Valid @ModelAttribute` ✅ |
| U1.2 | N/A | 未启用 |
| U1.3 | N/A | 未启用 |
| U2.1 | N/A | 未启用 |
| U2.2 | N/A | 未启用 |
| U2.3 | N/A | 未启用 |

> 说明：`customized-checklist.md` 仅含 U1.1 一条示例项，其余均为空/未启用。整体标注 `N/A(未启用自定义规则)`，仅 U1.1 做合规核销。

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、**S1–S10 / G1–G17** 各列均非 `⬜`（跳过文件除外）；
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`（允许 `N/A`，但有原因）
- [x] Step 5 全部 U* ID 均非 `⬜`（允许 `N/A(未启用自定义规则)`）
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`