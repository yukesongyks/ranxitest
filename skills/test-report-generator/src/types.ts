/**
 * 统一中间模型 —— 所有解析器插件产出的标准结构。
 * 报告生成器只消费此模型，与具体框架解耦。
 */

/** 单条测试用例状态 */
export type TestCaseStatus = 'passed' | 'failed' | 'skipped' | 'todo' | 'unknown';

/** 单条测试用例 */
export interface TestCase {
  /** 用例名（如 "should sort empty array"） */
  name: string;
  /** 所属测试套件/文件（如 "src/utils/sort.test.ts" 或类名） */
  suite: string;
  /** 归属文件路径（用于失败分析定位） */
  file?: string;
  /** 用例状态 */
  status: TestCaseStatus;
  /** 耗时（毫秒），未获取时为 undefined */
  durationMs?: number;
  /** 失败时的错误信息（已脱敏） */
  errorMessage?: string;
  /** 失败时的堆栈关键行（已截断脱敏） */
  stackTrace?: string;
}

/** 测试套件（按文件/类分组） */
export interface TestSuite {
  /** 套件名/文件路径 */
  name: string;
  /** 文件路径 */
  file?: string;
  /** 该套件下的用例 */
  cases: TestCase[];
  /** 套件总耗时（毫秒） */
  durationMs?: number;
}

/** 覆盖率汇总（若可获取） */
export interface CoverageSummary {
  /** 语句覆盖率（0-100） */
  lines?: number;
  /** 分支覆盖率（0-100） */
  branches?: number;
  /** 函数覆盖率（0-100） */
  functions?: number;
  /** 行覆盖率（0-100） */
  statements?: number;
  /** 低于阈值的文件清单 */
  lowCoverageFiles?: Array<{ file: string; lines?: number; branches?: number }>;
}

/** 一次测试运行的统一结果 */
export interface TestRunResult {
  /** 框架名（如 "jest"、"vitest"、"junit"） */
  framework: string;
  /** 框架版本（若可获取） */
  frameworkVersion?: string;
  /** 项目名（取自 package.json 或目录名） */
  projectName?: string;
  /** 测试套件列表 */
  suites: TestSuite[];
  /** 用例总数（= passed + failed + skipped，由汇总器计算） */
  total: number;
  /** 通过数 */
  passed: number;
  /** 失败数 */
  failed: number;
  /** 跳过数 */
  skipped: number;
  /** 总耗时（毫秒） */
  durationMs?: number;
  /** 覆盖率（若可获取） */
  coverage?: CoverageSummary;
  /** 原始结果文件路径 */
  sourceFile?: string;
  /** 执行命令 */
  command?: string;
  /** 执行环境摘要（Node 版本、OS 等，不含密钥） */
  environment?: string;
}

/** 解析器插件接口 —— 新增框架只需实现此接口并注册 */
export interface TestResultParser {
  /** 解析器名称（如 "jest"、"vitest"、"junit-xml"） */
  name: string;
  /** 支持的文件扩展名或特征（如 ["json"], ["xml"]） */
  extensions: string[];
  /** 判断给定的原始内容/文件是否能被本解析器处理 */
  canParse(raw: string, filePath?: string): boolean;
  /** 执行解析，返回统一中间模型 */
  parse(raw: string, filePath?: string): TestRunResult;
}

/** CLI 配置项 */
export interface ReportConfig {
  mode: 'execute' | 'parse';
  testCommand?: string;
  resultFile?: string;
  outputFormat: 'markdown' | 'html' | 'json';
  outputPath: string;
  coverage: 'auto' | 'on' | 'off';
  failThreshold?: number;
}

/** 诊断信息（解析失败时返回） */
export interface ParseError {
  ok: false;
  error: string;
  detail?: string;
}
