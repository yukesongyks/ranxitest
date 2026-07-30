# Proposal: Add Test Report Generator Skill

## Intent
提供一个 Skill，Agent 在执行测试后能够自动解析测试结果并生成结构化、可读性强的标准测试报告，解决测试结果散落在终端输出、CI 日志或框架原生产物中、需人工收集汇总的痛点。

## Background
当前团队在完成测试执行后，测试结果散落在终端输出、CI 日志或框架原生产物（如 JUnit XML、coverage 目录）中，存在以下痛点：
- 测试结果需要人工收集、整理、汇总，耗时且易遗漏；
- 缺乏统一格式的测试报告，跨项目/跨团队沟通成本高；
- 失败用例的上下文（错误信息、堆栈、关联代码）需要人工回溯；
- 覆盖率、通过率等质量指标无法沉淀为可追踪的历史数据。

## Goals
- G1：一条指令（如"生成测试报告"）即可自动完成：执行测试 → 收集结果 → 生成报告；
- G2：报告内容标准化，包含摘要、明细、失败分析、覆盖率四大板块；
- G3：支持主流测试框架的结果解析（Jest、Vitest、JUnit XML、pytest）；
- G4：报告支持多种输出格式，默认 Markdown。

## Non-Goals
- 不做测试用例的自动生成或修复（仅报告）；
- 不做报告的在线托管 / Web 服务化展示；
- 不做多次运行结果的趋势对比分析（列为后续迭代候选）；
- 不做非测试类质量报告（如 lint、安全扫描）的聚合。

## Scope (P0)
- 新增 Skill `test-report-generator`，含 CLI 入口、框架检测、解析器插件、报告生成器
- 首期（P0）支持的框架/结果格式：
  - JavaScript/TypeScript：Jest、Vitest（JSON reporter）
  - Python：pytest（JUnit XML / JSON report）
  - 通用：JUnit XML（作为跨语言兜底格式）
- 执行模式与解析模式两种工作模式
- 标准报告结构（六大章节）
- 默认 Markdown 输出，路径 `reports/test-report-<YYYYMMDD-HHmmss>.md`
- 插件式解析器结构（NFR5）

## Design / Approach
- 解析器采用插件式注册表结构，每个框架一个 parser 插件，产出统一的 `TestRunResult` 中间模型
- 报告生成器只消费中间模型，与具体框架解耦
- CLI 支持 `execute`（触发测试）与 `parse`（仅解析已有结果）双模式
- 敏感信息过滤：报告生成阶段过滤环境变量、密钥类内容
- 幂等性：同一结果文件多次生成报告内容一致（时间戳字段除外）

## Open Questions (resolved by assumption)
- Q1：首期目标项目栈以 TypeScript/Node 为主（按此假设制定 P0 范围，Skill 用 Node/TS 实现）
- Q2：报告仅中文模板（默认中文）
- Q3：不推送 IM / 邮件（非目标）
