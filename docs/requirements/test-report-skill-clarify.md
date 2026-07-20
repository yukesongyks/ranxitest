# 测试报告 Skill 2.0 — 需求澄清文档（clarify 阶段产物）

> 阶段：clarify（需求澄清）
> 采用技能：/brainstorming
> 生成时间：2026-07-20
> 产物状态：已落盘，路径明确

---

## 1. 澄清背景与执行方式

本任务处于全自动流水线模式，按防阻塞协议，由 Agent 静默接管所有需用户确认的交互节点，依据「✅ 已验证事实（SSOT）」+ 安全兜底 + 行业最佳实践进行最优推断，不暂停、不等待人工确认。

brainstorming 技能流程要求：探索项目上下文 → 澄清问题 → 提方案 → 呈现设计 → 写设计文档。本澄清文档锁定澄清阶段结论，作为后续 design / implement 阶段的输入基线。

---

## 2. 已验证事实（SSOT）

| # | 事实 | 证据来源 |
|---|------|----------|
| F1 | 当前工作区为单一 Maven 项目 `my-spring-boot-app`（`pom.xml` + `src/`） | 工作区目录列表 |
| F2 | 技术栈为 Java 17 / Spring Boot（Spring Data JPA + Thymeleaf + H2 内存库） | `README.md` 第 1-3 行 |
| F3 | 仓库根无 `package.json` / `pyproject.toml` / `Cargo.toml`，无 Jest/Vitest/pytest 配置文件 | 工作区根目录列表 |
| F4 | 需求文档（输入项）假设「首期目标项目栈以 TypeScript/Node 为主」（Q1），与 SSOT F1/F2 直接冲突 | 需求描述 §8 Q1 |
| F5 | 输入文件 `docs/requirements/test-report-skill-clarify.md` 本身为本次需生成的产物 | read 返回 File not found |

---

## 3. 开放问题决策（Q1/Q2/Q3）

### Q1：首期目标项目栈是否以 TypeScript/Node 为主？

**决策：否。首期 P0 栈调整为以 Java / Maven（Surefire + JUnit）为主，JUnit XML 作为跨语言兜底格式保留；Jest/Vitest 下沉为 P2（后续迭代）。**

推断依据（按优先级）：
1. **上下文优先（F1/F2/F3）**：当前真实仓库为 Java/Maven，无任何 Node/TS 测试配置。若 P0 仅锚定 Jest/Vitest，则该 Skill 在当前仓库内无法被验收（AC1 直接落空）。
2. **安全兜底**：JUnit XML 是 Maven Surefire 经 `--report-format` / `surefire-report` 可稳定产出的通用格式，且是需求 §4.1 已列的「通用兜底格式」，改动最小、与现有需求结构兼容。
3. **行业最佳实践**：Java 生态测试报告标准 = Surefire `TEST-*.xml`（JUnit XML 变体）+ JaCoCo `jacoco.xml` 覆盖率。这与需求 §4.2「报告结构」与 §4.1「JUnit XML 兜底」天然吻合。

**对原需求 P0 范围的调整**：

| 原需求项 | 原优先级 | 调整后 | 理由 |
|----------|----------|--------|------|
| Jest / Vitest（JSON reporter） | P0 | **P2** | 当前仓库无 Node 栈，无法在 P0 验收 |
| pytest（JUnit XML / JSON） | P1 | P1（不变） | 维持原排序 |
| JUnit XML（跨语言兜底） | P0 | **P0（提升为主格式）** | Java/Maven 主栈的天然产格式 |
| **新增：Maven Surefire `TEST-*.xml` 解析** | — | **P0** | 匹配 SSOT 主栈 |
| **新增：JaCoCo `jacoco.xml` 覆盖率解析** | — | **P0（覆盖率主来源）** | Java 覆盖率事实标准 |

> 注：FR1.2 的「JUnit XML 兜底」与「Surefire TEST-*.xml」本质同构（Surefire 即 JUnit XML 变体），故解析器插件可共用，符合 NFR5 插件式、不增维护负担。

### Q2：报告是否需要中文/英文双语模板？

**决策：首期仅中文模板；英文模板列为 P2 候选。**

推断依据：
1. **上下文优先**：需求描述全篇为中文，当前交互语言为中文（Dynamic Intent Behavior 锁定）。
2. **安全兜底**：单语言模板复杂度最低，避免 i18n 抽象层过早引入（YAGNI）。
3. **行业最佳实践**：报告头时间戳、结论 ✅/❌ 符号、覆盖率百分比为语言无关；中文模板已满足 US1-US4 全部场景。

### Q3：是否需要将报告自动推送到 IM / 邮件等渠道？

**决策：不做（维持非目标）。**

推断依据：
1. 需求 §2.2 已明确列为非目标。
2. 推送涉及凭据/Token 管理，与 NFR3（安全：不得泄露凭据）存在张力，本期引入风险高、收益低。
3. FR3.3 已要求「生成后向用户返回报告路径 + 摘要」，满足人工同步需求，无需自动推送。

---

## 4. 澄清后的范围基线（供 design 阶段继承）

### 4.1 P0 范围（锁定）

- **框架解析**：Maven Surefire `TEST-*.xml`（= JUnit XML 变体，跨语言兜底）+ JaCoCo `jacoco.xml` 覆盖率。
- **双模式**：执行模式（`mvn test`）+ 解析模式（指定已有 `TEST-*.xml` / `junit.xml`）。
- **报告**：Markdown，§4.2 六大章节（报告头 / 摘要 / 失败分析 / 明细 / 覆盖率 / 附录）。
- **输出**：`reports/test-report-<YYYYMMDD-HHmmss>.md`。

### 4.2 P1（维持）

- pytest 支持、fail_threshold、HTML 输出、JSON 伴随产物。

### 4.3 P2（后续迭代）

- Jest/Vitest、Go test / cargo test、英文模板、历史趋势对比、IM/邮件推送。

### 4.4 配置项默认值调整

| 配置项 | 原默认 | 调整后默认 | 理由 |
|--------|--------|------------|------|
| `test_command` | 自动检测 | `mvn test`（Java/Maven 仓库自动识别） | SSOT 主栈 |
| `result_file` | 自动检测 | 自动检测 `target/surefire-reports/TEST-*.xml` | Surefire 默认产物路径 |
| `coverage` | auto | auto（执行模式自动 `mvn jacoco:report`，解析模式读 `target/site/jacoco/jacoco.xml`） | Java 覆盖率标准链路 |
| `output_format` | markdown | markdown（不变） | — |
| `output_path` | `reports/` | `reports/`（不变） | — |
| `fail_threshold` | 无 | 无（不变） | — |

---

## 5. 框架识别优先级调整（FR1.1）

原识别优先级 a→b→c 维持，但 b/c 适配 Java 生态：

| 优先级 | 来源 | Java/Maven 对应 |
|--------|------|-----------------|
| a | 用户显式指定命令 | 不变 |
| b | 项目配置文件 | `pom.xml`（`<properties>` / `maven-surefire-plugin`） |
| c | 框架特征文件推断 | `target/surefire-reports/` 目录存在即判定为 Maven Surefire 产物 |

> Node/TS 的 `package.json` scripts、`jest.config.*` 识别逻辑保留代码骨架，待 P2 启用，不影响 P0 主路径。

---

## 6. 验收标准对照（AC1-AC5 适配 Java 栈）

| AC | 原表述 | 适配后 P0 验收 |
|----|--------|---------------|
| AC1 | 在含 Jest/Vitest 的 TS 项目中执行"生成测试报告" | 在 `my-spring-boot-app`（Maven/Surefire）中执行"生成测试报告"，产出符合 §4.2 结构的 Markdown 报告，摘要数据与 Surefire 原始 `TEST-*.xml` 一致 |
| AC2 | 失败用例含错误信息、堆栈摘要、源文件路径 | 不变（Surefire XML 含 `<failure message>` + `<system-err>` 堆栈） |
| AC3 | 提供 JUnit XML 走解析模式 | 不变（`TEST-*.xml` 即 JUnit XML 变体） |
| AC4 | 结果文件损坏返回明确错误 | 不变 |
| AC5 | 覆盖率存在则呈现，不存在标注"未获取" | JaCoCo `jacoco.xml` 存在则呈现四项覆盖率，不存在标注"未获取" |

---

## 7. 风险更新

| 编号 | 风险 | 状态 |
|------|------|------|
| R1 | 各框架 reporter 差异大 | 缓解：NFR5 插件式；P0 仅 Surefire/JaCoCo 两类解析器，复杂度可控 |
| R2 | 测试执行耗时不可控 | 缓解：`mvn test` 长任务交后台执行 + 轮询（依赖 Agent 后台任务能力） |
| **R3（新增）** | Jest/Vitest 下沉 P2 后，原需求 §4.1 FR1.2 P0 列表与实际栈不符 | 已在本澄清文档显式调整 P0/P2，需需求方在 design 阶段确认范围基线（非阻塞，已按安全兜底执行） |

---

## 8. 下游继承约束（交付给 design 阶段）

1. design 阶段须以本澄清文档 §4「范围基线」为输入，不得回退到原 TS/Node P0 假设。
2. 解析器插件接口须保证 Surefire/JUnit XML 共用，JaCoCo 独立，为 P2 Jest/Vitest 预留挂载点（NFR5）。
3. 执行模式默认命令 `mvn test`，但须保留 `test_command` 覆盖能力（FR4.2）。
4. 覆盖率章节默认数据源 `target/site/jacoco/jacoco.xml`，缺失时降级标注"未获取"（NFR2/AC5）。

---

## 9. 澄清结论一句话

**首期锁定 Java/Maven（Surefire + JaCoCo）为主栈，JUnit XML 为跨语言兜底，Jest/Vitest 下沉 P2；报告中文、默认 Markdown、不自动推送；产物路径 `docs/requirements/test-report-skill-clarify.md`。**
