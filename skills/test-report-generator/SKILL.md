---
name: test-report-generator
description: 测试执行后自动解析测试结果（Jest/Vitest JSON、JUnit XML）并生成结构化 Markdown 测试报告。支持执行/解析双模式、插件式解析器、覆盖率章节。触发意图示例：生成测试报告、跑一下测试并出报告、把这个 junit.xml 转成测试报告。
activation: auto
tags:
  - testing
  - report
  - junit
  - jest
  - vitest
---

# Skill: Test Report Generator

测试执行后自动解析结果并生成结构化、可读性强的标准测试报告。

## 触发意图

- "生成测试报告"
- "跑一下测试并出报告"
- "把这个 junit.xml 转成测试报告"

## 两种工作模式

### 执行模式（execute）
触发测试运行并收集结果，再生成报告。
适用于本地开发完成后"帮我跑测试并生成报告"场景（US1）。

```bash
node dist/index.js execute --test-command "npx jest --json" --output-path reports/
```

### 解析模式（parse）
跳过测试执行，直接解析用户指定的已有结果文件（满足 US4 / CI 复用场景）。
不重复跑测试，仅把 JUnit XML / JSON 结果转成报告。

```bash
node dist/index.js parse --result-file results/junit.xml --output-path reports/
```

## 可配置项

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| test_command | 自动检测 | 测试执行命令 |
| result_file | 自动检测 | 解析模式下的结果文件路径 |
| output_format | markdown | markdown（P1: html）/ json |
| output_path | reports/ | 报告输出目录 |
| coverage | auto | auto / on / off |
| fail_threshold | 无 | 通过率低于该值时报告结论标记为不达标 |

## 报告标准结构（FR2）

报告固定包含以下章节，顺序固定：

1. **报告头**：项目名、生成时间、执行命令、框架/版本、执行环境摘要
2. **结果摘要**：用例总数、通过/失败/跳过数、通过率、总耗时；整体结论用 ✅ / ❌ 标识
3. **失败用例分析**（有失败时必选）：每条失败用例含用例名、所属文件、错误信息、堆栈关键行
4. **用例明细**：按测试文件分组的用例列表与各自耗时，超过 200 条截断并注明
5. **覆盖率**（若可获取）：语句/分支/函数/行覆盖率总表，及低于阈值文件清单
6. **附录**：原始结果文件路径、生成工具版本

## 支持框架（P0）

- JavaScript/TypeScript：Jest、Vitest（JSON reporter）
- Python：pytest（JUnit XML / JSON report）
- 通用：JUnit XML（跨语言兜底格式）

## 非功能约束

- 解析与报告生成（不含测试执行）5 秒内完成（1000 用例规模）
- 结果文件格式异常/字段缺失时降级输出（缺失项标注"未获取"），不崩溃
- 不泄露环境变量、密钥；错误堆栈过滤敏感路径外的凭据信息
- 同一结果文件多次生成报告内容一致（时间戳除外）

## 插件式扩展

解析器采用插件式注册表结构（`src/registry.ts`）。新增框架支持只需实现 `TestResultParser` 接口并注册，不影响既有解析器（NFR5）。

```typescript
import { registerParser } from './registry.js';
registerParser(new MyFrameworkParser());
```
