# 固定资产配置管理 — 设计文档（DIMA）

> 阶段：需求澄清 / brainstorming 产出
> 范围决议：采纳模型推荐 —— 「核心配置 CRUD + 分类」（YAGNI 剔除折旧、领用、盘点、处置流程）
> 日期：2026-08-07
> 关联仓库：my-spring-boot-app（Spring Boot 2.x + Thymeleaf + H2）

---

## 1. 背景与目标

现有应用已具备 `Item`（物品）与 `User`（用户）两个领域，采用经典分层架构。本设计在**不改动现有 Item/User 代码**的前提下，新增「固定资产配置管理」领域模块，提供资产卡片的增删改查、分类与状态筛选、关键字搜索能力。

**成功标准**
- 可登记一条固定资产并校验唯一资产编号（assetNo 由用户录入，服务层预检 + DB 唯一约束兜底）
- 可按分类、状态、关键字检索资产列表
- 表单校验失败回填并提示中文错误，成功后 flash 提示
- 复用现有 `GlobalExceptionHandler`，无新增异常类
- 现有 Item/User 功能不受影响

## 2. 非目标（明确排除）

- 折旧计算与月折旧报表
- 领用/归还流程、审批流
- 资产盘点、盘点单
- 资产处置/报废审批流程
- 与外部财务系统的对接

以上能力若后续需要，各自独立立项、独立 spec → plan → 实现。

## 3. 架构与组件

沿用现有分层约定，新增组件均位于 `com.example.myapp` 包下：

| 层 | 新增文件 | 职责 |
|---|---|---|
| Model | `models/FixedAsset.java` | JPA 实体，表 `fixed_assets` |
| Repository | `repositories/FixedAssetRepository.java` | Spring Data JPA + `@Query` 检索 |
| Service | `services/FixedAssetService.java` | `@Service @Transactional` + 编号唯一性校验 |
| Controller | `controllers/FixedAssetController.java` | `@RequestMapping("/assets")` + Thymeleaf 视图 |
| View | `templates/assets/{list,form,view}.html` | 复用 items 模板布局风格 |
| 异常 | 复用 `exception/GlobalExceptionHandler` | 不新增异常类 |

**隔离性**：`FixedAsset` 与 `Item`/`User` 仅通过 `userId` 弱关联（可空 Long），不引入 JPA 外键约束，避免与现有 schema 耦合。每个新增单元单一职责，可独立理解与测试。

## 4. 数据模型 — FixedAsset

表名 `fixed_assets`，字段如下：

| 字段 | Java 类型 | 列约束 | 校验 | 说明 |
|---|---|---|---|---|
| id | Long | `@Id @GeneratedValue(IDENTITY)` | — | 主键 |
| assetNo | String(50) | `nullable=false, unique=true` | `@NotBlank` `@Size(1..50)` | 资产编号，业务唯一键 |
| name | String(100) | `nullable=false` | `@NotBlank` `@Size(1..100)` | 资产名称 |
| category | String(50) | `nullable=false` | `@NotBlank` `@Size(<=50)` | 资产分类 |
| spec | String(200) | 可空 | `@Size(<=200)` | 规格型号 |
| status | String(20) | `nullable=false` | `@NotBlank` `@Size(<=20)` | 状态：在用/闲置/维修/报废 |
| originalValue | BigDecimal | `nullable=false, precision=10, scale=2` | `@NotNull` `@DecimalMin("0.0")` `@Digits(10,2)` | 原值 |
| userId | Long | 可空 | — | 归属使用人（弱关联 User） |
| purchaseDate | LocalDate | `nullable=false` | `@NotNull` | 购置日期 |
| location | String(100) | 可空 | `@Size(<=100)` | 存放地点 |
| remark | String(500) | 可空 | `@Size(<=500)` | 备注 |
| createdAt | LocalDateTime | `nullable=false, updatable=false` | — | `@PrePersist` 填充 |
| updatedAt | LocalDateTime | 可空 | — | `@PrePersist`+`@PreUpdate` 填充 |

沿用 `Item` 的时间戳回调模式（`@PrePersist onCreate()` / `@PreUpdate onUpdate()`）。校验消息使用中文，与现有 `Item.java` 风格一致（如「资产编号不能为空」「原值不能为负数」）。

## 5. 关键行为

### 5.1 编号唯一性
- `FixedAssetService.save`：新增时 `existsByAssetNo` 预检 + `DataIntegrityViolationException` 兜底，抛 `IllegalArgumentException("资产编号 'xxx' 已存在")`，复用 `ItemService.save` 模式。
- `FixedAssetService.update`：变更编号时 `findByAssetNoForUpdate`（`@Lock` 乐观/悲观读）预检重复，兜底同上。

### 5.2 状态字典
状态取值固定为四项：`在用`、`闲置`、`维修`、`报废`。由 `FixedAssetController` 暴露 `getAllStatuses()` 返回有序列表，供表单下拉与列表筛选；不建独立字典表（YAGNI）。

### 5.3 分类聚合
`FixedAssetRepository.findAllCategories()` 采用 `SELECT DISTINCT a.category FROM FixedAsset a ORDER BY a.category`，复用 `ItemRepository.findAllCategories` 模式。

### 5.4 检索
- `searchByKeyword(keyword)`：在 `assetNo`/`name`/`spec` 上模糊匹配（`@Query` JPQL `LOWER(...) LIKE`）。
- `findByCategory(category)`、`findByStatus(status)`：派生查询方法。
- 关键字为空时回退到 `findAll()`，与 `ItemService.searchByKeyword` 一致。

## 6. 控制器与视图

`FixedAssetController`（`@RequestMapping("/assets")`）端点：
| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/assets` | 列表，带分类/状态筛选 |
| GET | `/assets/new` | 新增表单 |
| POST | `/assets` | 创建（`@Valid @ModelAttribute`） |
| GET | `/assets/{id}/edit` | 编辑表单 |
| POST | `/assets/{id}` | 更新 |
| POST | `/assets/{id}/delete` | 删除 |
| GET | `/assets/search` | 关键字搜索 |
| GET | `/assets/category/{category}` | 按分类 |
| GET | `/assets/status/{status}` | 按状态 |
| GET | `/assets/{id}` | 详情 |

视图 `templates/assets/list.html`、`form.html`、`view.html` 复用现有 `items/*` 的 Thymeleaf 布局与 flash 消息（`success`/`error`）约定。表单校验失败返回 `assets/form` 并保留输入。

## 7. 错误处理与边界

- 业务异常统一抛 `IllegalArgumentException`，由 `GlobalExceptionHandler` 接管（不新增异常类）。
- 表单 `@Valid` 校验错误：`BindingResult.hasErrors()` 时回到 `assets/form`，Thymeleaf 内联展示字段错误。
- 不存在记录：`findById(...).orElseThrow(() -> new IllegalArgumentException("固定资产不存在，ID: x"))`。
- 删除：`existsById` 预检，不存在抛 `IllegalArgumentException`。
- 软删除不做（YAGNI），物理删除即可；如未来需要审计再扩展。

## 8. 测试策略

沿用 H2 内存库 + 现有测试风格（`ItemServiceTest`）：
- `FixedAssetServiceTest`：CRUD 全路径、编号重复校验、关键字/分类/状态检索、删除不存在记录异常。
- `FixedAssetControllerTest`（`@WebMvcTest` 或 `@SpringBootTest`）：表单校验失败回显、创建成功重定向、详情 404/异常处理。
- 数据隔离：每个测试方法独立事务回滚，避免交叉污染。

## 9. 影响面与风险

**影响面**：仅新增文件，不修改 `Item`/`User`/`GlobalExceptionHandler`/`application.properties`。`spring.jpa.hibernate.ddl-auto`（现有配置）控制 `fixed_assets` 建表。

**风险**：
- `assetNo` 唯一性依赖应用层预检 + DB 唯一约束双保险，并发极端场景由 `DataIntegrityViolationException` 兜底为可读错误。
- `userId` 弱关联不做外键，若 `User` 被删除，`FixedAsset.userId` 指向悬空 id（设计取舍：配置管理阶段允许，后续如需强约束再增强）。
- 状态取值硬编码，若未来需动态扩展再引入字典表。

**回滚**：纯新增模块，回滚即删除新增文件 + 撤销 git commit，无既有行为变更。

## 10. 后续步骤

设计文档经用户确认后，按 brainstorming 技能终点约定，转入实现计划编写（writing-plans），生成详细实施计划与任务拆解。实现阶段遵循现有 Java 编码规范（`dtazziboot-java-coding-standards`）。
