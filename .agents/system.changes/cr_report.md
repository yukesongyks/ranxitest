# Code Review Report

> **Change** `fixed-asset-management` · **分支/Commit** `AI/task-DEV-966dcd0a-7905-11f1-9649-3b4281182f10-4a75d390-4551-4eb5-9f91-06505b9dd1e3-4a75d390-4551-4eb5-9f91-06505b9dd1e3` / `.agents/system.changes/code.md` · **日期** `2026-08-07` · **审查者** AI

> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 `bug-pattern-checklist.md` 表头为准（Blocker→P0、Major→P1、Info→P2）。**须先**运行 `scan-all-rules.sh` 并将要点并入 §5，**再**写 LLM 结论。问题须含 `path:line` 或清单 ID。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | `4`（不含测试） / `6`（含测试） |
| 变更行数 | `+1249`（code.md 文档，含嵌入式 Java/HTML 代码块） |

> **说明**：本次变更为 SDD 文档驱动——Java 源码以代码块形式嵌入 `.agents/system.changes/code.md`，尚未落盘为独立 `.java` 文件。审查基于 code.md 中提取的代码块内容，路径以 code.md §1.2 声明的仓库相对路径为准。`scan-all-rules.sh` 对提取后的临时 `.java` 文件执行。

| 类/接口 | 路径 | 角色（可选） |
|---------|------|--------------|
| `FixedAsset` | `my-spring-boot-app/src/main/java/com/example/myapp/models/FixedAsset.java` | JPA 实体 |
| `FixedAssetRepository` | `my-spring-boot-app/src/main/java/com/example/myapp/repositories/FixedAssetRepository.java` | Spring Data JPA 接口 |
| `FixedAssetService` | `my-spring-boot-app/src/main/java/com/example/myapp/services/FixedAssetService.java` | 业务服务层 |
| `FixedAssetController` | `my-spring-boot-app/src/main/java/com/example/myapp/controllers/FixedAssetController.java` | Web 控制器 |
| `FixedAssetServiceTest` | `my-spring-boot-app/src/test/java/com/example/myapp/services/FixedAssetServiceTest.java` | Service 层测试 |
| `FixedAssetControllerTest` | `my-spring-boot-app/src/test/java/com/example/myapp/controllers/FixedAssetControllerTest.java` | Controller 层测试 |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 2 | 2 | 2 |

---

## 3. Step 2 — 功能（REQ）

### REQ-F01: 固定资产登记（创建）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| Given 用户填写资产表单 When POST /assets Then 创建成功重定向 `/assets` + flash success | ✅ | DIMA §1「可登记一条固定资产并校验唯一资产编号」；design §W03 | `FixedAssetController.createAsset()` code.md:458-473 | `@Valid @ModelAttribute` + `BindingResult.hasErrors()` + `save()` 编号唯一性预检 + DB 兜底 |
| Given 校验失败 When POST /assets Then 返回 assets/form 回填 | ❌ | DIMA §7「BindingResult.hasErrors() 时回到 assets/form，Thymeleaf 内联展示字段错误」；design §W03 | `FixedAssetController.createAsset()` code.md:462-463 `return "assets/form"` | **P0**: 校验失败时直接返回视图但未向 Model 设置 `statuses` 属性，form.html 第771行 `th:each="s : ${statuses}"` 会因属性缺失抛 Thymeleaf 渲染异常（500），与 spec「回填并提示中文错误」矛盾 |

### REQ-F04: 资产编辑更新

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| Given 校验失败 When POST /assets/{id} Then 返回 assets/form 回填 | ❌ | DIMA §7「BindingResult.hasErrors() 时回到 assets/form」；design §W05 | `FixedAssetController.updateAsset()` code.md:494-495 `return "assets/form"` | **P0**: 同 F01——校验失败返回视图未设置 `statuses`，导致 Thymeleaf 渲染异常 |

### REQ-F02: 资产列表（带分类/状态筛选）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| Given 访问 /assets When GET Then 返回列表+categories+statuses | ✅ | DIMA §6「GET /assets 列表，带分类/状态筛选」；design §W01 | `FixedAssetController.listAssets()` code.md:444-449 | `model.addAttribute("assets/categories/statuses")` 齐全 |

### REQ-F03: 资产详情查看

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| Given 访问 /assets/{id} When GET Then 返回详情视图 | ✅ | DIMA §6「GET /assets/{id} 详情」；design §W10 | `FixedAssetController.viewAsset()` code.md:545-555 | `findById().orElseThrow()` + 不存在 flash error 重定向 |

### REQ-F05: 资产删除（物理删除）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| Given POST /assets/{id}/delete When 不存在 Then flash error 重定向 | ✅ | DIMA §7「existsById 预检，不存在抛 IllegalArgumentException」；design §W06 | `FixedAssetController.deleteAsset()` code.md:507-516 | `deleteById` 先 `existsById` 预检 |

### REQ-F06/F07/F08: 分类/状态/关键字检索

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| Given keyword 空白 When GET /assets/search Then 回退 findAll() | ✅ | DIMA §5.4「关键字为空时回退到 findAll()」；design §5.1.3.4 | `FixedAssetService.searchByKeyword()` code.md:385-390 | `keyword == null || keyword.trim().isEmpty()` 回退 `findAll()` |
| Given category When GET /assets/category/{c} Then 按分类筛选 | ✅ | DIMA §6「GET /assets/category/{category}」 | `FixedAssetController.getAssetsByCategory()` code.md:527-534 | 派生查询 `findByCategory` |
| Given status When GET /assets/status/{s} Then 按状态筛选 | ✅ | DIMA §6「GET /assets/status/{status}」 | `FixedAssetController.getAssetsByStatus()` code.md:536-543 | 派生查询 `findByStatus` |

### REQ-F09: 分类聚合

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| Given 列表页 When 渲染 Then categories 下拉填充 | ✅ | DIMA §5.3「findAllCategories SELECT DISTINCT」 | `FixedAssetService.getAllCategories()` code.md:400-402 → `FixedAssetRepository.findAllCategories()` code.md:289-290 | `@Query("SELECT DISTINCT a.category ...")` |

### REQ-编号唯一性双保险

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| Given 并发登记相同 assetNo When save Then DataIntegrityViolationException 兜底 | ✅ | DIMA §5.1「existsByAssetNo 预检 + DataIntegrityViolationException 兜底」；design §5.1.3.1 R01 | `FixedAssetService.save()` code.md:338-347 | `existsByAssetNo` 预检 + catch `DataIntegrityViolationException` 转 `IllegalArgumentException` |
| Given 更新时编号变更 When update Then findByAssetNoForUpdate 预检 | ✅ | DIMA §5.1「findByAssetNoForUpdate（@Lock）预检重复」；design §5.1.3.2 | `FixedAssetService.update()` code.md:354-358 | `@Lock(PESSIMISTIC_WRITE)` 悲观读预检 |

---

## 4. Step 3 — 可读性检查

| 结果 | 说明（违规写 Ax.x 与 `path:行`） |
|------|--------------------------------|
| ⚠️ | A2.2 WildcardImport — `FixedAsset.java:3-4`（`import javax.persistence.*; import javax.validation.constraints.*;`）；`FixedAssetController.java:9`（`import org.springframework.web.bind.annotation.*;`）；测试文件同类通配导入。与现有 `Item.java` 风格一致，属 P2 建议改进 |

> **备注**：scan-all-rules.sh 预扫命中 A2.2 共 6 处（去重后）。代码整体可读性良好：命名规范、中文校验消息一致、构造器注入、`@Transactional` 类级声明、时间戳回调模式复用 `Item.java`。A1/A3-A7 未发现违规。

---

## 5. Step 4 — 可靠性检查

> **预扫**：`scan-all-rules.sh` 对提取的 `.java` 文件执行（52/222 条可程序化规则），去重后命中：P0×7（G16.2）、P1×3（M016）、P2×6（A2.2）。

| 域 | 参考 | 结果 | 等级 | 说明（列命中 ID 或「已扫无命中」） |
|----|------|------|------|-------------------------------------|
| 可靠性 | `reliability-checklist.md` G1–G17 | ❌ | P0 | **G16.2 CatchWithoutLogging** — `FixedAssetController.java:55,69,87,98,138`（5处 catch IllegalArgumentException 未记录日志）；`FixedAssetService.java:42,71`（2处 catch DataIntegrityViolationException 未记录日志）。共7处命中。**复核**：Controller 层 catch 后通过 flash error 向用户反馈，可读性尚可；Service 层 catch 后转 `IllegalArgumentException` 也未记录原始异常。排障可观测性不足 |
| 安全 | `security-checklist.md` S1–S10 | ✅ | N/A | 本应用无鉴权/无 REST JSON 层，S1-S10 场景不适用（design §6.4 已标注不涉及）。`searchByKeyword` 使用 `@Query` + `:keyword` 参数绑定，无 SQL 注入风险 |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120） | ⚠️ | P1 | **M016 JavaTimeDefaultTimeZone** — `FixedAsset.java:70,71,76`（`LocalDateTime.now()` 使用系统默认时区）。**复核**：H2 内存库学习应用，`LocalDateTime.now()` 与 `Item.java` 现有模式一致；生产环境如部署多时区需改用 `Instant.now()` 或指定时区。当前阶段标 P1 建议 |

### 5.1 Bug 模式逐条核销（脚本覆盖 + LLM 补扫）

> 脚本覆盖 B(25/81)+M(6/27)+I(2/10)。以下仅列命中项和 LLM 补扫命中项，其余未命中项标 ✅（已扫无命中）或 N/A（与变更无关）。

| ID | 状态 | 备注 |
|----|------|------|
| M016 | ❌ | `FixedAsset.java:70,71,76` — `LocalDateTime.now()` 使用系统默认时区，Major→P1 |
| B001-B081 | ✅ | 已扫无命中（脚本覆盖 25 条 + LLM 复核：无 `LocalDateTime.parse` 字面量、无 `BigDecimal(double)`、无 `Executors`、无 `SimpleDateFormat` 静态域等） |
| M001-M015 | ✅ | 已扫无命中 |
| M017-M027 | ✅ | 已扫无命中 |
| I001-I010 | ✅ | 已扫无命中 |

### 5.2 可靠性逐条核销

| ID | 状态 | 备注 |
|----|------|------|
| G16.2 | ❌ | `FixedAssetController.java:55,69,87,98,138` + `FixedAssetService.java:42,71` — catch 块未记录日志，P0 |
| G1.1-G1.4 | ✅ | 并发控制：`@Lock(PESSIMISTIC_WRITE)` 预检 + DB 唯一约束双保险，符合 design |
| G2.1-G2.3 | ✅ | 事务边界：`@Transactional` 类级声明，save/update/delete 原子 |
| G3.1-G3.2 | ✅ | 资源释放：无手动连接/流管理，Spring Data JPA 自动管理 |
| G4.1-G4.4 | N/A | 无超时/重试/限流场景（单体 H2 学习应用） |
| G5.1 | ✅ | 边界条件：`originalValue` `@DecimalMin("0.0")` + `@Digits(10,2)` 防溢出 |
| G6.1-G6.2 | N/A | 无 MQ/异步消息 |
| G7.1-G7.2 | N/A | 无分布式锁/幂等场景 |
| G8.1-G8.7 | N/A | 无灰度/监控/APM（学习应用） |
| G9.1-G9.3 | N/A | 无缓存层 |
| G10.1-G10.3 | N/A | 无限流/熔断 |
| G11.1-G11.4 | N/A | 无外部依赖调用 |
| G12.1-G12.2 | ✅ | 日志：沿用 Spring Boot 默认日志（但 G16.2 命中：catch 未记录） |
| G13.1 | ✅ | 异常处理：复用 `GlobalExceptionHandler`，不新增异常类 |
| G14.1-G14.4 | N/A | 无线程池/并发框架 |
| G15.1-G15.3 | N/A | 无配置中心/动态配置 |
| G16.1 | ✅ | 异常分类：`IllegalArgumentException` 为业务异常 |
| G16.3 | ✅ | 异常不吞：catch 后转为可读中文消息抛出/flash |
| G16.4 | ✅ | 异常链：`new IllegalArgumentException(..., e)` 保留 cause |
| G17.1-G17.3 | N/A | 无应急开关/降级（纯新增模块，design §7.3 已标注） |
| G18.1-G18.3 | N/A | 安全补强：无鉴权场景 |

### 5.3 安全逐条核销

| ID | 状态 | 备注 |
|----|------|------|
| S1.1-S1.3 | ✅ | SQL 注入：`searchByKeyword` 使用 `@Query` + `:keyword` 参数绑定，JPQL 参数化查询，无注入风险 |
| S2.1-S2.3 | N/A | 认证/授权：无鉴权系统（design §6.4.2） |
| S3.1-S3.3 | N/A | 输入校验：`@Valid` + `@NotBlank/@Size/@DecimalMin/@Digits` 已覆盖；无额外 XSS/CSRF 场景 |
| S4.1-S4.2 | N/A | 密钥泄露：无密钥/令牌 |
| S5.1-S5.2 | N/A | 依赖安全：无新增外部依赖 |
| S6.1-S10 | N/A | 无 CSRF/CORS/跳转校验场景（design §6.4 已标注不涉及） |

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | `customized-checklist.md` U* | N/A | N/A | N/A(未启用自定义规则) |

---

## 7. 结论

- **合并建议**：修复后合并
- **P0**：
  1. **校验失败返回视图缺少 `statuses` Model 属性** — `FixedAssetController.createAsset()` code.md:462-463 和 `updateAsset()` code.md:494-495 在 `result.hasErrors()` 时直接 `return "assets/form"` 未设置 `statuses` 属性，导致 form.html `th:each="s : ${statuses}"` 渲染异常（500）。需在返回前 `model.addAttribute("statuses", getAllStatuses())`
  2. **G16.2 catch 未记录日志** — Controller 5处 + Service 2处 catch 块未记录日志，排障可观测性不足。需添加 `log.warn(...)` 或等价日志记录
- **P1/P2**：
  1. **P1 M016** `FixedAsset.java:70,71,76` — `LocalDateTime.now()` 使用系统默认时区，多时区部署风险
  2. **P2 A2.2** — 6处通配导入（`javax.persistence.*` 等），建议展开为具体导入
- **一句话**：功能实现完整、架构复用得当，但校验失败路径的 Model 属性缺失为 P0 阻塞项，须修复后方可合并；catch 日志缺失为 P0 可观测性问题建议同步修复

---

## 7.1 问题片段（必填）

### P0-1: 校验失败返回视图缺少 `statuses` Model 属性

- **P0** `FixedAssetController.createAsset()` `my-spring-boot-app/src/main/java/com/example/myapp/controllers/FixedAssetController.java:458-464` — 校验失败返回 `assets/form` 但未设置 `statuses` 属性，form.html 第771行 `th:each="s : ${statuses}"` 会抛 Thymeleaf 渲染异常。
  片段范围：`FixedAssetController.java:458-464`

```java
L458|    @PostMapping
L459|    public String createAsset(@Valid @ModelAttribute FixedAsset asset,
L460|                              BindingResult result,
L461|                              RedirectAttributes redirectAttributes) {
L462|        if (result.hasErrors()) {
L463|            return "assets/form"; // 问题：未设置 statuses 属性，form.html th:each="${statuses}" 渲染异常
L464|        }
```

- **P0** `FixedAssetController.updateAsset()` `my-spring-boot-app/src/main/java/com/example/myapp/controllers/FixedAssetController.java:489-496` — 同上，更新校验失败路径同样缺少 `statuses`。
  片段范围：`FixedAssetController.java:489-496`

```java
L489|    @PostMapping("/{id}")
L490|    public String updateAsset(@PathVariable Long id,
L491|                             @Valid @ModelAttribute FixedAsset asset,
L492|                             BindingResult result,
L493|                             RedirectAttributes redirectAttributes) {
L494|        if (result.hasErrors()) {
L495|            return "assets/form"; // 问题：未设置 statuses 属性
L496|        }
```

### P0-2: G16.2 catch 未记录日志

- **P0** `G16.2` `my-spring-boot-app/src/main/java/com/example/myapp/services/FixedAssetService.java:338-347` — catch `DataIntegrityViolationException` 后转为 `IllegalArgumentException` 但未记录原始异常日志，排障可观测性不足。
  片段范围：`FixedAssetService.java:338-347`

```java
L338|    public FixedAsset save(FixedAsset asset) {
L339|        try {
L340|            if (asset.getId() == null && fixedAssetRepository.existsByAssetNo(asset.getAssetNo())) {
L341|                throw new IllegalArgumentException("资产编号 '" + asset.getAssetNo() + "' 已存在");
L342|            }
L343|            return fixedAssetRepository.save(asset);
L344|        } catch (DataIntegrityViolationException e) {
L345|            throw new IllegalArgumentException("资产编号 '" + asset.getAssetNo() + "' 已存在", e);
L346|            // 问题：catch 未记录日志，并发冲突时无可观测证据
L347|        }
L348|    }
```

### P1-1: M016 JavaTimeDefaultTimeZone

- **P1** `M016` `my-spring-boot-app/src/main/java/com/example/myapp/models/FixedAsset.java:126-135` — `LocalDateTime.now()` 使用系统默认时区，多时区部署时时间戳不一致。
  片段范围：`FixedAsset.java:126-135`

```java
L126|    @PrePersist
L127|    protected void onCreate() {
L128|        createdAt = LocalDateTime.now(); // 问题：使用系统默认时区
L129|        updatedAt = LocalDateTime.now();
L130|    }
L131|
L132|    @PreUpdate
L133|    protected void onUpdate() {
L134|        updatedAt = LocalDateTime.now(); // 问题：同上
L135|    }
```

---

## 8. 修复任务列表

### P0

- [ ] **P0** `my-spring-boot-app/src/main/java/com/example/myapp/controllers/FixedAssetController.java:462-463` — 在 `createAsset` 校验失败返回前添加 `model.addAttribute("statuses", getAllStatuses())`
- [ ] **P0** `my-spring-boot-app/src/main/java/com/example/myapp/controllers/FixedAssetController.java:494-495` — 在 `updateAsset` 校验失败返回前添加 `model.addAttribute("statuses", getAllStatuses())`（需补充 `Model` 参数）
- [ ] **P0** `G16.2` `my-spring-boot-app/src/main/java/com/example/myapp/services/FixedAssetService.java:344-346` — 在 catch `DataIntegrityViolationException` 块中添加日志记录
- [ ] **P0** `G16.2` `my-spring-boot-app/src/main/java/com/example/myapp/services/FixedAssetService.java:373-375` — 在 update 方法 catch 块中添加日志记录
- [ ] **P0** `G16.2` `my-spring-boot-app/src/main/java/com/example/myapp/controllers/FixedAssetController.java:469-472,483-486,501-504,512-514,552-555` — 在 Controller 各 catch 块中添加日志记录

### P1

- [ ] **P1** `M016` `my-spring-boot-app/src/main/java/com/example/myapp/models/FixedAsset.java:128-134` — 考虑使用 `Instant.now()` 或指定时区替代 `LocalDateTime.now()`（与 Item.java 现有模式一致的取舍，生产部署前评估）

### P2（可选）

- [ ] **P2** `A2.2` `my-spring-boot-app/src/main/java/com/example/myapp/models/FixedAsset.java:3-4` — 将 `javax.persistence.*` / `javax.validation.constraints.*` 通配导入展开为具体导入
- [ ] **P2** `A2.2` `my-spring-boot-app/src/main/java/com/example/myapp/controllers/FixedAssetController.java:9` — 将 `org.springframework.web.bind.annotation.*` 通配导入展开
