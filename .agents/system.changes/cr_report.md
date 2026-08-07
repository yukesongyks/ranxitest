# Code Review Report

> **Change** `fixed-asset-management` · **分支/Commit** `AI/task-DEV-966dcd0a-7905-11f1-9649-3b4281182f10-4a75d390-4551-4eb5-9f91-06505b9dd1e3-4a75d390-4551-4eb5-9f91-06505b9dd1e3` / `8eeb3ca` · **日期** `2026-08-07` · **审查者** AI
>
> **轮次**：Round 2（问题修复后复审）
>
> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 `bug-pattern-checklist.md` 表头为准（Blocker→P0、Major→P1、Info→P2）。**须先**运行 `scan-all-rules.sh` 并将要点并入 §5，**再**写 LLM 结论。问题须含 `path:line` 或清单 ID：可读性 `A3.4`，安全 `S1.1`，可靠性 `G16.2`，Bug 模式 `B012` / `M005` 等。**每个 ❌/⚠️ 问题在 §7 后必须附 `.java` 问题片段**（见 §7.1）。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | `4`（不含测试） / `6`（含测试） |
| 变更行数 | `+1271`（code.md 文档，含嵌入式 Java/HTML 代码块）；Round 2 修复 commit `8eeb3ca` 新增 `+22` 行 |

> **说明**：本次变更为 SDD 文档驱动——Java 源码以代码块形式嵌入 `.agents/system.changes/code.md`，尚未落盘为独立 `.java` 文件。审查基于 code.md 中提取的代码块内容，路径以 code.md §1.2 声明的仓库相对路径为准。`scan-all-rules.sh` 对提取后的临时 `.java` 文件执行。

| 类/接口 | 路径 | 角色（可选） |
|---------|------|--------------|
| `FixedAsset` | `my-spring-boot-app/src/main/java/com/example/myapp/models/FixedAsset.java` | JPA 实体 |
| `FixedAssetRepository` | `my-spring-boot-app/src/main/java/com/example/myapp/repositories/FixedAssetRepository.java` | Spring Data JPA 接口 |
| `FixedAssetService` | `my-spring-boot-app/src/main/java/com/example/myapp/services/FixedAssetService.java` | 业务服务层 |
| `FixedAssetController` | `my-spring-boot-app/src/main/java/com/example/myapp/controllers/FixedAssetController.java` | Web 控制器 |
| `FixedAssetServiceTest` | `my-spring-boot-app/src/test/java/com/example/myapp/services/FixedAssetServiceTest.java` | Service 层测试 |
| `FixedAssetControllerTest` | `my-spring-boot-app/src/test/java/com/example/myapp/controllers/FixedAssetControllerTest.java` | Controller 层测试 |

### Round 1 → Round 2 修复对照

| Round 1 问题 | 等级 | Round 2 验证结果 |
|-------------|------|-----------------|
| 校验失败返回视图缺少 `statuses` Model 属性（createAsset + updateAsset） | P0 | ✅ 已修复：两个方法均添加 `Model model` 参数 + `model.addAttribute("statuses", getAllStatuses())` |
| G16.2 catch 未记录日志（Controller 5处 + Service 2处） | P0 | ✅ 已修复：所有 7 处 catch 块均已添加 `logger.warn(...)` |
| M016 JavaTimeDefaultTimeZone（`LocalDateTime.now()` 系统默认时区） | P1 | ✅ 已标注：`@PrePersist`/`@PreUpdate` 回调中添加注释说明时区风险，与 `Item.java` 现有模式一致 |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 0 | 1 | 2 |

---

## 3. Step 2 — 功能（REQ）

### REQ-F01: 固定资产登记（创建）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| Given 用户填写资产表单 When POST /assets Then 创建成功重定向 `/assets` + flash success | ✅ | DIMA §1「可登记一条固定资产并校验唯一资产编号」；design §W03 | `FixedAssetController.createAsset()` code.md:472-487 | `@Valid @ModelAttribute` + `BindingResult.hasErrors()` + `save()` 编号唯一性预检 + DB 兜底 |
| Given 校验失败 When POST /assets Then 返回 assets/form 回填 | ✅ | DIMA §7「BindingResult.hasErrors() 时回到 assets/form，Thymeleaf 内联展示字段错误」；design §W03 | `FixedAssetController.createAsset()` code.md:476-478 `model.addAttribute("statuses", getAllStatuses()); return "assets/form";` | **Round 2 修复确认**：校验失败路径已添加 `statuses` 属性，Thymeleaf 渲染不再异常 |

### REQ-F04: 资产编辑更新

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| Given 校验失败 When POST /assets/{id} Then 返回 assets/form 回填 | ✅ | DIMA §7「BindingResult.hasErrors() 时回到 assets/form」；design §W05 | `FixedAssetController.updateAsset()` code.md:511-513 `model.addAttribute("statuses", getAllStatuses()); return "assets/form";` | **Round 2 修复确认**：添加了 `Model model` 参数 + `statuses` 属性，Thymeleaf 渲染正常 |

### REQ-F02: 资产列表（带分类/状态筛选）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| Given 访问 /assets When GET Then 返回列表+categories+statuses | ✅ | DIMA §6「GET /assets 列表，带分类/状态筛选」；design §W01 | `FixedAssetController.listAssets()` code.md:456-461 | `model.addAttribute("assets/categories/statuses")` 齐全 |

### REQ-F03: 资产详情查看

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| Given 访问 /assets/{id} When GET Then 返回详情视图 | ✅ | DIMA §6「GET /assets/{id} 详情」；design §W10 | `FixedAssetController.viewAsset()` code.md:567-577 | `findById().orElseThrow()` + 不存在 flash error 重定向 |

### REQ-F05: 资产删除（物理删除）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| Given POST /assets/{id}/delete When 不存在 Then flash error 重定向 | ✅ | DIMA §7「existsById 预检，不存在抛 IllegalArgumentException」；design §W06 | `FixedAssetController.deleteAsset()` code.md:527-537 | `deleteById` 先 `existsById` 预检 |

### REQ-F06/F07/F08: 分类/状态/关键字检索

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| Given keyword 空白 When GET /assets/search Then 回退 findAll() | ✅ | DIMA §5.4「关键字为空时回退到 findAll()」；design §5.1.3.4 | `FixedAssetService.searchByKeyword()` code.md:97-101 | `keyword == null || keyword.trim().isEmpty()` 回退 `findAll()` |
| Given category When GET /assets/category/{c} Then 按分类筛选 | ✅ | DIMA §6「GET /assets/category/{category}」 | `FixedAssetController.getAssetsByCategory()` code.md:549-556 | 派生查询 `findByCategory` |
| Given status When GET /assets/status/{s} Then 按状态筛选 | ✅ | DIMA §6「GET /assets/status/{status}」 | `FixedAssetController.getAssetsByStatus()` code.md:558-565 | 派生查询 `findByStatus` |

### REQ-F09: 分类聚合

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| Given 列表页 When 渲染 Then categories 下拉填充 | ✅ | DIMA §5.3「findAllCategories SELECT DISTINCT」 | `FixedAssetService.getAllCategories()` code.md:105-107 → `FixedAssetRepository.findAllCategories()` code.md:289-290 | `@Query("SELECT DISTINCT a.category ...")` |

### REQ-编号唯一性双保险

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| Given 并发登记相同 assetNo When save Then DataIntegrityViolationException 兜底 | ✅ | DIMA §5.1「existsByAssetNo 预检 + DataIntegrityViolationException 兜底」；design §5.1.3.1 R01 | `FixedAssetService.save()` code.md:41-53 | `existsByAssetNo` 预检 + catch `DataIntegrityViolationException` 转 `IllegalArgumentException` + `logger.warn` 日志 |
| Given 更新时编号变更 When update Then findByAssetNoForUpdate 预检 | ✅ | DIMA §5.1「findByAssetNoForUpdate（@Lock）预检重复」；design §5.1.3.2 | `FixedAssetService.update()` code.md:58-81 | `@Lock(PESSIMISTIC_WRITE)` 悲观读预检 |

---

## 4. Step 3 — 可读性检查

| 结果 | 说明（违规写 Ax.x 与 `path:行`） |
|------|--------------------------------|
| ⚠️ | A2.2 WildcardImport — `FixedAsset.java:3-4`（`import javax.persistence.*; import javax.validation.constraints.*;`）；`FixedAssetController.java:11`（`import org.springframework.web.bind.annotation.*;`）；测试文件同类通配导入。与现有 `Item.java` 风格一致，属 P2 建议改进 |

> **备注**：scan-all-rules.sh 预扫命中 A2.2 共 6 处（去重后）。代码整体可读性良好：命名规范、中文校验消息一致、构造器注入、`@Transactional` 类级声明、时间戳回调模式复用 `Item.java`。A1/A3-A7 未发现违规。

---

## 5. Step 4 — 可靠性检查

> **预扫**：`scan-all-rules.sh` 对提取的 `.java` 文件执行（52/222 条可程序化规则），去重后命中：P0×7（G16.2）、P1×4（M016）、P2×6（A2.2）。
>
> **人工核销**：G16.2 的 7 处命中经逐行复核均为**误报**——每个 catch 块中均已包含 `logger.warn(...)` 日志调用（Round 1 → Round 2 修复成果）。脚本仅匹配 catch 行号但不分析块体内部是否有日志语句。

| 域 | 参考 | 结果 | 等级 | 说明（列命中 ID 或「已扫无命中」） |
|----|------|------|------|-------------------------------------|
| 可靠性 | `reliability-checklist.md` G1–G17 | ✅ | N/A | **G16.2 误报已核销**：Controller 5处（createAsset/showEditForm/updateAsset/deleteAsset/viewAsset）+ Service 2处（save/update）catch 块均已添加 `logger.warn(...)`。**G1.1-G1.4**：并发控制 `@Lock(PESSIMISTIC_WRITE)` 预检 + DB 唯一约束双保险。**G2.1-G2.3**：N/A（无 MQ/幂等场景）。**G3.1-G3.2**：`@Transactional` 类级声明，save/update/delete 原子，无外部 I/O。**G5.1**：`originalValue` `@DecimalMin("0.0")` + `@Digits(10,2)` 防溢出。**G12.1-G12.2**：日志已补齐。**G13.1**：复用 `GlobalExceptionHandler`。**G16.1/G16.3/G16.4**：异常分类/不吞/链保留均 ✅。**G4/G6-G11/G14-G15/G17-G18**：N/A（学习应用无相关场景） |
| 安全 | `security-checklist.md` S1–S10 | ✅ | N/A | 本应用无鉴权/无 REST JSON 层，S1-S10 场景不适用（design §6.4 已标注不涉及）。`searchByKeyword` 使用 `@Query` + `:keyword` 参数绑定，无 SQL 注入风险。S1.1 ✅（JPQL 参数化查询） |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120） | ⚠️ | P1 | **M016 JavaTimeDefaultTimeZone** — `FixedAsset.java:70,72,73,79`（`LocalDateTime.now()` 使用系统默认时区）。**复核**：Round 2 已在 `@PrePersist`/`@PreUpdate` 回调中添加注释说明时区风险，与 `Item.java` 现有模式一致。H2 内存库学习应用，当前阶段标 P1 建议改进（生产多时区部署前评估改用 `Instant.now()` 或指定时区） |

### 5.1 Bug 模式逐条核销（脚本覆盖 + LLM 补扫）

> 脚本覆盖 B(25/81)+M(6/27)+I(2/10)。以下仅列命中项和 LLM 补扫命中项，其余未命中项标 ✅（已扫无命中）或 N/A（与变更无关）。

| ID | 状态 | 备注 |
|----|------|------|
| M016 | ⚠️ | `FixedAsset.java:70,72,73,79` — `LocalDateTime.now()` 使用系统默认时区，Major→P1。Round 2 已添加注释标注风险 |
| B001-B081 | ✅ | 已扫无命中（脚本覆盖 25 条 + LLM 复核：无 `LocalDateTime.parse` 字面量、无 `BigDecimal(double)`、无 `Executors`、无 `SimpleDateFormat` 静态域等） |
| M001-M015 | ✅ | 已扫无命中 |
| M017-M027 | ✅ | 已扫无命中 |
| I001-I010 | ✅ | 已扫无命中 |

### 5.2 可靠性逐条核销

| ID | 状态 | 备注 |
|----|------|------|
| G16.2 | ✅ | **误报已核销**：`FixedAssetController.java` catch 行（createAsset:61/showEditForm:76/updateAsset:97/deleteAsset:109/viewAsset:150）+ `FixedAssetService.java` catch 行（save:46/update:76）均已添加 `logger.warn(...)` 日志记录。Round 1 P0 已修复 |
| G1.1-G1.4 | ✅ | 并发控制：`@Lock(PESSIMISTIC_WRITE)` 预检 + DB 唯一约束双保险，符合 design |
| G2.1-G2.3 | N/A | 无 MQ/异步消息场景（单体 H2 学习应用） |
| G3.1-G3.2 | ✅ | 事务边界：`@Transactional` 类级声明，save/update/delete 原子，无外部 I/O |
| G4.1-G4.3 | ✅ | SQL 与索引：`@Query` JPQL 参数化，`searchByKeyword` 使用 `LOWER(...) LIKE CONCAT('%', :keyword, '%')`，无 `${}` 拼接 |
| G5.1 | ✅ | 边界条件：`originalValue` `@DecimalMin("0.0")` + `@Digits(10,2)` 防溢出 |
| G6.1-G6.2 | N/A | 无 MQ/异步消息 |
| G7.1-G7.2 | N/A | 无分布式锁/幂等场景 |
| G8.1-G8.7 | N/A | 无灰度/监控/APM（学习应用） |
| G9.1-G9.3 | N/A | 无缓存层 |
| G10.1-G10.3 | N/A | 无限流/熔断 |
| G11.1-G11.4 | N/A | 无外部依赖调用 |
| G12.1-G12.2 | ✅ | 日志：Service + Controller 均已添加 `logger.warn(...)`（Round 2 修复成果） |
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

- **合并建议**：通过
- **P0**：无（Round 1 的 2 项 P0 阻塞问题均已修复并经 Round 2 复审确认）
- **P1/P2**：
  1. **P1 M016** `FixedAsset.java:70,72,73,79` — `LocalDateTime.now()` 使用系统默认时区，多时区部署风险（已添加注释标注，与 `Item.java` 现有模式一致，生产部署前评估）
  2. **P2 A2.2** — 6处通配导入（`javax.persistence.*` 等），建议展开为具体导入（与现有 `Item.java` 风格一致，可选改进）
- **一句话**：Round 1 的全部 P0 阻塞问题（校验失败 statuses 缺失 + catch 日志缺失）已修复确认，功能实现完整、架构复用得当，仅剩 P1 时区注释建议和 P2 通配导入风格项，可合并

---

## 7.1 问题片段（必填）

### P1-1: M016 JavaTimeDefaultTimeZone

- **P1** `M016` `my-spring-boot-app/src/main/java/com/example/myapp/models/FixedAsset.java:70-79` — `LocalDateTime.now()` 使用系统默认时区，多时区部署时时间戳不一致。Round 2 已添加注释标注风险。
  片段范围：`FixedAsset.java:70-79`

```java
L70|    @PrePersist
L71|    protected void onCreate() {
L72|        // 注意：LocalDateTime.now() 使用系统默认时区，与 Item.java 保持一致；
L73|        // 多时区部署前应评估改用 Instant.now() 或指定时区
L74|        createdAt = LocalDateTime.now();
L75|        updatedAt = LocalDateTime.now();
L76|    }
L77|
L78|    @PreUpdate
L79|    protected void onUpdate() {
L80|        // 同上：多时区部署前评估时区策略
L81|        updatedAt = LocalDateTime.now();
L82|    }
```

> **注**：Round 1 的 P0 问题（P0-1 statuses 缺失、P0-2 G16.2 catch 未记录日志）已在 commit `8eeb3ca` 中修复，Round 2 复审确认通过，不再列为问题项。以下为修复后代码片段供核验。

### Round 2 修复确认片段

**P0-1 已修复**：`FixedAssetController.createAsset()` code.md:476-478

```java
L476|        if (result.hasErrors()) {
L477|            model.addAttribute("statuses", getAllStatuses());  // 修复：添加 statuses 属性
L478|            return "assets/form";
L479|        }
```

**P0-2 已修复**：`FixedAssetService.save()` catch 块 code.md:50-52

```java
L50|        } catch (DataIntegrityViolationException e) {
L51|            logger.warn("保存固定资产时发生数据完整性冲突，assetNo={}", asset.getAssetNo(), e);  // 修复：添加日志
L52|            throw new IllegalArgumentException("资产编号 '" + asset.getAssetNo() + "' 已存在", e);
L53|        }
```

---

## 8. 修复任务列表

### P0

- 无待修复项（Round 1 的 P0 阻塞问题均已修复确认）

### P1

- [x] **P1** `M016` `my-spring-boot-app/src/main/java/com/example/myapp/models/FixedAsset.java:70-79` — 生产多时区部署前评估改用 `Instant.now()` 或指定时区替代 `LocalDateTime.now()`（与 Item.java 现有模式一致的取舍，当前阶段已标注注释）✅ 保持现状：编码规范 datetime.md §3【推荐】正例使用 `LocalDateTime.now()`，与 `Item.java` 现有模式一致，当前阶段注释标注已充分

### P2（可选）

- [x] **P2** `A2.2` `my-spring-boot-app/src/main/java/com/example/myapp/models/FixedAsset.java:3-4` — 将 `javax.persistence.*` / `javax.validation.constraints.*` 通配导入展开为具体导入 ✅ 已修复（Round 3）
- [x] **P2** `A2.2` `my-spring-boot-app/src/main/java/com/example/myapp/controllers/FixedAssetController.java:11` — 将 `org.springframework.web.bind.annotation.*` 通配导入展开 ✅ 已修复（Round 3）
- [x] **P2** `A2.2` `FixedAssetServiceTest.java` — 将 `import static org.junit.jupiter.api.Assertions.*` 通配导入展开为具体导入 ✅ 已修复（Round 3）
- [x] **P2** `A2.2` `FixedAssetControllerTest.java` — 将 `import static org.mockito.Mockito.*` / `import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*` 通配导入展开为具体导入 ✅ 已修复（Round 3）
