/**
 * Markdown 报告生成器（FR2 标准结构）。
 *
 * 章节顺序固定：
 *   1. 报告头：项目名、生成时间、执行命令、框架/版本、执行环境摘要
 *   2. 结果摘要：用例总数、通过/失败/跳过数、通过率、总耗时；整体结论 ✅/❌
 *   3. 失败用例分析（有失败时必选）：用例名、所属文件、错误信息、堆栈关键行
 *   4. 用例明细：按测试文件分组，超过 200 条截断并注明
 *   5. 覆盖率（若可获取）：语句/分支/函数/行覆盖率总表，及低于阈值文件清单
 *   6. 附录：原始结果文件路径、生成工具版本
 *
 * NFR3：报告内容已在解析阶段脱敏；此处补充缺失项标注。
 * NFR4：幂等——同一输入多次生成除时间戳字段外内容一致。
 */
import type { TestRunResult, TestCase } from './types.js';
import { getFailedCases, flattenCases, normalizeStatus } from './registry.js';

const TOOL_VERSION = 'test-report-generator v1.0.0';
const DETAIL_LIMIT = 200;

/** 格式化耗时（毫秒 → 友好字符串） */
function fmtDuration(ms?: number): string {
  if (ms == null) return '未获取';
  if (ms < 1000) return `${ms}ms`;
  const s = ms / 1000;
  if (s < 60) return `${s.toFixed(2)}s`;
  const m = Math.floor(s / 60);
  const rem = (s % 60).toFixed(1);
  return `${m}m${rem}s`;
}

/** 计算通过率（0-100，保留 2 位小数） */
function passRate(total: number, passed: number): number {
  if (total === 0) return 0;
  return Math.round((passed / total) * 10000) / 100;
}

/** 状态图标 */
function statusIcon(status: string): string {
  switch (normalizeStatus(status)) {
    case 'passed':
      return '✅';
    case 'failed':
      return '❌';
    case 'skipped':
      return '⏭️';
    case 'todo':
      return '📝';
    default:
      return '❓';
  }
}

function esc(s: string | undefined): string {
  if (s == null) return '未获取';
  // 转义 Markdown 表格管道符与换行
  return String(s).replace(/\|/g, '\\|').replace(/\r?\n/g, '<br>');
}

/** 生成完整 Markdown 报告 */
export function generateMarkdown(
  result: TestRunResult,
  opts: { generatedAt?: Date; failThreshold?: number } = {}
): string {
  const now = (opts.generatedAt ?? new Date());
  // 时间戳字段单独存在，其余内容对同一输入幂等（NFR4）
  const ts = now.toISOString().replace('T', ' ').slice(0, 19) + ' UTC';
  const rate = passRate(result.total, result.passed);
  const allFailed = getFailedCases(result);
  const allCases = flattenCases(result);
  const truncated = allCases.length > DETAIL_LIMIT;

  const lines: string[] = [];

  // ========== 1. 报告头 ==========
  lines.push(`# 测试报告`);
  lines.push('');
  lines.push(`| 项目 | 值 |`);
  lines.push(`|------|----|`);
  lines.push(`| 项目名 | ${esc(result.projectName)} |`);
  lines.push(`| 生成时间 | ${ts} |`);
  lines.push(`| 执行命令 | ${esc(result.command)} |`);
  lines.push(`| 框架 | ${esc(result.framework)}${result.frameworkVersion ? ` ${esc(result.frameworkVersion)}` : ''} |`);
  lines.push(`| 执行环境 | ${esc(result.environment)} |`);
  lines.push('');

  // ========== 2. 结果摘要 ==========
  let conclusion = result.failed === 0 ? '✅ 全部通过' : '❌ 存在失败';
  if (opts.failThreshold != null && rate < opts.failThreshold) {
    conclusion = `❌ 不达标（通过率 ${rate}% < 阈值 ${opts.failThreshold}%）`;
  }
  lines.push(`## 结果摘要`);
  lines.push('');
  lines.push(`| 指标 | 值 |`);
  lines.push(`|------|----|`);
  lines.push(`| 用例总数 | ${result.total} |`);
  lines.push(`| 通过 | ${result.passed} |`);
  lines.push(`| 失败 | ${result.failed} |`);
  lines.push(`| 跳过 | ${result.skipped} |`);
  lines.push(`| 通过率 | ${rate}% |`);
  lines.push(`| 总耗时 | ${fmtDuration(result.durationMs)} |`);
  lines.push(`| 整体结论 | ${conclusion} |`);
  lines.push('');

  // ========== 3. 失败用例分析 ==========
  if (allFailed.length > 0) {
    lines.push(`## 失败用例分析`);
    lines.push('');
    lines.push(`共 ${allFailed.length} 条失败用例：`);
    lines.push('');
    for (const tc of allFailed) {
      lines.push(`### ${statusIcon(tc.status)} ${esc(tc.name)}`);
      lines.push('');
      lines.push(`- **所属文件**：${esc(tc.file) || '未获取'}`);
      lines.push(`- **所属套件**：${esc(tc.suite)}`);
      lines.push(`- **错误信息**：`);
      lines.push('');
      lines.push('```');
      lines.push(tc.errorMessage || '未获取');
      lines.push('```');
      if (tc.stackTrace) {
        lines.push('');
        lines.push(`- **堆栈关键行**：`);
        lines.push('');
        lines.push('```');
        lines.push(tc.stackTrace);
        lines.push('```');
      }
      lines.push('');
    }
  }

  // ========== 4. 用例明细 ==========
  lines.push(`## 用例明细`);
  lines.push('');
  if (allCases.length === 0) {
    lines.push('> 未获取到用例数据。');
    lines.push('');
  } else {
    if (truncated) {
      lines.push(`> 共 ${allCases.length} 条用例，超过 ${DETAIL_LIMIT} 条上限，以下仅展示前 ${DETAIL_LIMIT} 条，详见附录原始结果文件。`);
      lines.push('');
    }
    // 按套件分组
    for (const suite of result.suites) {
      lines.push(`### ${esc(suite.name)}`);
      if (suite.file) lines.push(`> 文件：${esc(suite.file)}`);
      if (suite.durationMs != null) lines.push(`> 套件耗时：${fmtDuration(suite.durationMs)}`);
      lines.push('');
      lines.push(`| 状态 | 用例名 | 耗时 |`);
      lines.push(`|------|--------|------|`);
      let shown = 0;
      for (const tc of suite.cases) {
        if (truncated && shown >= DETAIL_LIMIT) break;
        lines.push(`| ${statusIcon(tc.status)} | ${esc(tc.name)} | ${fmtDuration(tc.durationMs)} |`);
        shown++;
      }
      lines.push('');
    }
  }

  // ========== 5. 覆盖率 ==========
  lines.push(`## 覆盖率`);
  lines.push('');
  const cov = result.coverage;
  if (!cov) {
    lines.push('> 未获取');
    lines.push('');
  } else {
    lines.push(`| 指标 | 覆盖率 |`);
    lines.push(`|------|--------|`);
    lines.push(`| 语句 | ${cov.statements != null ? cov.statements + '%' : '未获取'} |`);
    lines.push(`| 分支 | ${cov.branches != null ? cov.branches + '%' : '未获取'} |`);
    lines.push(`| 函数 | ${cov.functions != null ? cov.functions + '%' : '未获取'} |`);
    lines.push(`| 行 | ${cov.lines != null ? cov.lines + '%' : '未获取'} |`);
    lines.push('');
    if (cov.lowCoverageFiles && cov.lowCoverageFiles.length > 0) {
      lines.push(`### 低于阈值文件清单`);
      lines.push('');
      lines.push(`| 文件 | 行覆盖 | 分支覆盖 |`);
      lines.push(`|------|--------|----------|`);
      for (const f of cov.lowCoverageFiles) {
        lines.push(`| ${esc(f.file)} | ${f.lines != null ? f.lines + '%' : '未获取'} | ${f.branches != null ? f.branches + '%' : '未获取'} |`);
      }
      lines.push('');
    }
  }

  // ========== 6. 附录 ==========
  lines.push(`## 附录`);
  lines.push('');
  lines.push(`| 项目 | 值 |`);
  lines.push(`|------|----|`);
  lines.push(`| 原始结果文件 | ${esc(result.sourceFile)} |`);
  lines.push(`| 生成工具版本 | ${TOOL_VERSION} |`);
  lines.push('');

  return lines.join('\n');
}

export { TOOL_VERSION, DETAIL_LIMIT as DETAIL_LIMIT };
