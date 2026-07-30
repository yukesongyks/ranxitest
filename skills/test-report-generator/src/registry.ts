/**
 * 解析器插件注册表。
 * 新增框架支持只需实现 TestResultParser 接口并调用 registerParser，不影响既有解析器（NFR5）。
 */
import type { TestResultParser, TestRunResult, TestCase, TestCaseStatus } from './types.js';

const parsers: TestResultParser[] = [];

/** 注册一个解析器插件 */
export function registerParser(parser: TestResultParser): void {
  if (parsers.some((p) => p.name === parser.name)) {
    return; // 幂等：同名不重复注册
  }
  parsers.push(parser);
}

/** 获取所有已注册解析器 */
export function listParsers(): TestResultParser[] {
  return [...parsers];
}

/**
 * 根据原始内容与文件路径自动选择合适的解析器。
 * 遍历所有已注册解析器，返回第一个声明可处理的。
 */
export function selectParser(raw: string, filePath?: string): TestResultParser | null {
  for (const p of parsers) {
    try {
      if (p.canParse(raw, filePath)) return p;
    } catch {
      // 单个解析器探测失败不影响后续
    }
  }
  return null;
}

/**
 * 执行解析并自动汇总用例计数（total/passed/failed/skipped）。
 * 解析器只需提供 suites + cases，汇总由本函数完成，保证计数一致。
 */
export function parseAndSummarize(raw: string, filePath?: string):
  | { ok: true; result: TestRunResult }
  | { ok: false; error: string } {
  const parser = selectParser(raw, filePath);
  if (!parser) {
    return {
      ok: false,
      error: `无可用的解析器处理该结果文件${filePath ? `：${filePath}` : ''}。已注册解析器：${parsers.map((p) => p.name).join(', ') || '(无)'}。请检查文件格式是否为支持的格式（Jest/Vitest JSON、JUnit XML）。`,
    };
  }
  let result: TestRunResult;
  try {
    result = parser.parse(raw, filePath);
  } catch (e) {
    const msg = e instanceof Error ? e.message : String(e);
    return { ok: false, error: `解析器 "${parser.name}" 处理失败：${msg}` };
  }
  // 自动汇总计数，确保 total/passed/failed/skipped 一致
  const counts = summarize(result);
  result.total = counts.total;
  result.passed = counts.passed;
  result.failed = counts.failed;
  result.skipped = counts.skipped;
  return { ok: true, result };
}

/** 从 suites 中汇总用例计数 */
export function summarize(result: TestRunResult): {
  total: number;
  passed: number;
  failed: number;
  skipped: number;
} {
  let total = 0,
    passed = 0,
    failed = 0,
    skipped = 0;
  for (const suite of result.suites) {
    for (const tc of suite.cases) {
      total++;
      const s = normalizeStatus(tc.status);
      if (s === 'passed') passed++;
      else if (s === 'failed') failed++;
      else if (s === 'skipped') skipped++;
    }
  }
  return { total, passed, failed, skipped };
}

/** 规范化状态值，容忍解析器产出的大小写/别名 */
export function normalizeStatus(s: string): TestCaseStatus {
  const v = String(s || '').toLowerCase();
  if (['pass', 'passed', 'success', 'ok'].includes(v)) return 'passed';
  if (['fail', 'failed', 'error'].includes(v)) return 'failed';
  if (['skip', 'skipped', 'pending', 'disabled'].includes(v)) return 'skipped';
  if (['todo'].includes(v)) return 'todo';
  return 'unknown';
}

/** 展平所有用例（用于明细与失败分析） */
export function flattenCases(result: TestRunResult): TestCase[] {
  return result.suites.flatMap((s) => s.cases);
}

/** 获取失败用例列表 */
export function getFailedCases(result: TestRunResult): TestCase[] {
  return flattenCases(result).filter((tc) => normalizeStatus(tc.status) === 'failed');
}
