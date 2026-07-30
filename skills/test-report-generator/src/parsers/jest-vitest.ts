/**
 * Jest / Vitest JSON reporter 解析器插件。
 *
 * Jest JSON reporter 结构（--json）：
 *   { numTotalTests, numPassedTests, numFailedTests, numPendingTests,
 *     testResults: [{ name, status, assertionResults: [{ title, status,
 *       fullName, durationMs, failureMessages, location }] }],
 *     coverageMap?, success, startTime }
 *
 * Vitest JSON reporter 结构（--reporter=json）：
 *   { numTotalTests, numPassedTests, numFailedTests, numPendingTests,
 *     testResults: [{ name, assertionResults: [...] ] }
 *   与 Jest 高度相似，故可共享解析逻辑。
 */
import type { TestResultParser, TestRunResult, TestSuite, TestCase, TestCaseStatus } from '../types.js';
import { sanitize, truncate } from '../security.js';

/** 宽松解析 JSON，失败抛出友好错误 */
function parseJson(raw: string): unknown {
  try {
    return JSON.parse(raw);
  } catch (e) {
    const msg = e instanceof Error ? e.message : String(e);
    throw new Error(`JSON 解析失败：${msg}。文件可能损坏或非 JSON 格式。`);
  }
}

interface JestAssertion {
  title?: string;
  fullName?: string;
  status?: string;
  durationMs?: number;
  failureMessages?: string[];
  location?: { line?: number; column?: number };
}

interface JestTestResult {
  name?: string;
  status?: string;
  assertionResults?: JestAssertion[];
  message?: string;
  startTime?: number;
  endTime?: number;
}

interface JestCoverage {
  total?: Record<string, { total?: number; covered?: number; pct?: number }>;
}

interface JestJsonReport {
  numTotalTests?: number;
  numPassedTests?: number;
  numFailedTests?: number;
  numPendingTests?: number;
  numTodoTests?: number;
  testResults?: JestTestResult[];
  coverageMap?: JestCoverage;
  success?: boolean;
  startTime?: number;
  name?: string;
  config?: { testEnvironment?: string; rootDir?: string };
}

function mapStatus(s?: string): TestCaseStatus {
  switch (String(s || '').toLowerCase()) {
    case 'passed':
      return 'passed';
    case 'failed':
      return 'failed';
    case 'pending':
    case 'skipped':
      return 'skipped';
    case 'todo':
      return 'todo';
    default:
      return 'unknown';
  }
}

/** 从 coverageMap 提取覆盖率汇总 */
function extractCoverage(cov?: JestCoverage): TestRunResult['coverage'] {
  if (!cov?.total) return undefined;
  const t = cov.total;
  const pick = (key: string): number | undefined => {
    const v = t[key];
    if (v == null) return undefined;
    if (typeof v.pct === 'number') return v.pct;
    if (typeof v.total === 'number' && typeof v.covered === 'number' && v.total > 0) {
      return Math.round((v.covered / v.total) * 10000) / 100;
    }
    return undefined;
  };
  return {
    lines: pick('lines'),
    branches: pick('branches'),
    functions: pick('functions'),
    statements: pick('statements'),
  };
}

export class JestVitestParser implements TestResultParser {
  name = 'jest-vitest';
  extensions = ['json'];

  canParse(raw: string, filePath?: string): boolean {
    const trimmed = raw.trimStart();
    if (!trimmed.startsWith('{')) return false;
    // 必须含 Jest/Vitest JSON reporter 特征字段
    if (!/(numTotalTests|assertionResults|testResults)/.test(trimmed)) return false;
    try {
      const obj = JSON.parse(trimmed) as JestJsonReport;
      return Array.isArray(obj.testResults);
    } catch {
      return false;
    }
  }

  parse(raw: string, filePath?: string): TestRunResult {
    const obj = parseJson(raw) as JestJsonReport;
    if (!Array.isArray(obj.testResults)) {
      throw new Error('JSON 缺少 testResults 数组，不是有效的 Jest/Vitest JSON 报告。');
    }

    const suites: TestSuite[] = obj.testResults.map((tr) => {
      const file = tr.name;
      const cases: TestCase[] = (tr.assertionResults || []).map((a) => {
        const status = mapStatus(a.status);
        const errMsg = a.failureMessages?.[0];
        return {
          name: a.title || a.fullName || '(unnamed)',
          suite: file || '(unknown suite)',
          file: file
            ? `${file}${a.location?.line ? `:${a.location.line}` : ''}`
            : undefined,
          status,
          durationMs: typeof a.durationMs === 'number' ? a.durationMs : undefined,
          errorMessage: errMsg ? sanitize(truncate(errMsg, 800)) : undefined,
          stackTrace: errMsg ? sanitize(truncate(errMsg, 1200)) : undefined,
        } as TestCase;
      });
      const dur =
        typeof tr.startTime === 'number' && typeof tr.endTime === 'number'
          ? Math.max(0, tr.endTime - tr.startTime)
          : undefined;
      return { name: file || '(unknown suite)', file, cases, durationMs: dur } as TestSuite;
    });

    // 区分 Jest / Vitest：Vitest 的 name 字段通常为 'vitest run'
    const isVitest =
      /vitest/i.test(obj.name || '') ||
      /vitest/i.test(filePath || '');

    // 总耗时：优先汇总各套件耗时；若无则累加用例耗时；均无则为 undefined
    let totalDuration: number | undefined;
    const suiteDurations = suites
      .map((s) => s.durationMs)
      .filter((d): d is number => typeof d === 'number');
    if (suiteDurations.length > 0) {
      totalDuration = suiteDurations.reduce((a, b) => a + b, 0);
    } else {
      const caseDurations = suites
        .flatMap((s) => s.cases)
        .map((c) => c.durationMs)
        .filter((d): d is number => typeof d === 'number');
      if (caseDurations.length > 0) {
        totalDuration = caseDurations.reduce((a, b) => a + b, 0);
      }
    }

    return {
      framework: isVitest ? 'vitest' : 'jest',
      frameworkVersion: undefined,
      projectName: undefined,
      suites,
      total: 0,
      passed: 0,
      failed: 0,
      skipped: 0,
      durationMs: totalDuration,
      coverage: extractCoverage(obj.coverageMap),
      sourceFile: filePath,
    };
  }
}
