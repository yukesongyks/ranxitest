# Tasks: Add Test Report Generator Skill

## Implementation Tasks

- [x] 1. 创建 OpenSpec 规格产物（proposal.md + tasks.md）
- [x] 2. 搭建 Skill 骨架：目录结构、package.json、tsconfig、SKILL.md
- [x] 3. 实现统一中间模型与解析器插件注册表（types.ts + registry.ts）
- [x] 4. 实现 Jest/Vitest JSON 解析器插件
- [x] 5. 实现 JUnit XML 解析器插件
- [x] 6. 实现 Markdown 报告生成器（FR2 六大章节 + NFR3 敏感信息过滤 + NFR4 幂等）
- [x] 7. 实现框架检测与命令解析逻辑（detect.ts）
- [x] 8. 实现 CLI 入口（index.ts）：执行/解析双模式、配置项、诊断信息
- [x] 9. 编写内置样例结果文件，验证解析 + 报告生成端到端
  - [x] 9.1 JUnit XML 解析模式（AC3）：6 用例，通过 3，失败 2，跳过 1 ✅
  - [x] 9.2 Jest JSON 解析 + 覆盖率（AC1/AC5）：5 用例，通过 4，失败 1，覆盖率四项 ✅
  - [x] 9.3 损坏文件诊断（AC4）：返回明确错误，EXIT 4，不生成空报告 ✅
  - [x] 9.4 失败用例分析（AC2）：含用例名、文件路径、错误信息、堆栈 ✅
  - [x] 9.5 NFR3 敏感信息过滤：TOKEN=sk-... → TOKEN=***REDACTED*** ✅
  - [x] 9.6 NFR4 幂等性：同一输入两次生成，除时间戳外 diff 为空 ✅
  - [降级说明] 构建环境无 Maven（mvn: not found），Java 项目测试执行降级为静态代码审查 + 内置样例端到端验证。TypeScript 类型检查 `tsc --noEmit` 通过（EXIT 0）。
- [x] 10. 更新 tasks.md 标记完成，输出汇总

## Traceability

| Task | Requirement | AC |
|------|-------------|----|
| 3,4,5 | FR1.2 框架解析 | AC1, AC3 |
| 6 | FR2 报告结构 | AC1, AC2, AC5 |
| 7,8 | FR1.1/FR1.3 检测与双模式 | AC3, AC4 |
| 6 | NFR3 敏感过滤 / NFR4 幂等 | AC4 |
| 3 | NFR5 插件式 | - |

## Notes
- 构建环境无 mvn/npm（已验证），测试执行降级为静态代码审查 + 内置样例端到端验证
- P0 范围：Jest/Vitest JSON + JUnit XML + Markdown；pytest/HTML/JSON 伴随产物列 P1
