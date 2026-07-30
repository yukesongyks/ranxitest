#!/usr/bin/env node
/**
 * CLI 入口 —— 测试报告生成器（FR1/FR3/FR4）。
 *
 * 双模式：
 *   execute：触发测试运行 → 收集结果 → 生成报告
 *   parse  ：跳过执行，直接解析已有结果文件 → 生成报告
 *
 * 用法示例：
 *   node dist/index.js parse  --result-file junit.xml --output-path reports/
 *   node dist/index.js execute --test-command "npx jest --json"
 *
 * FR1.4：测试执行失败（非用例失败，而是命令无法运行）时给出明确诊断，不生成空报告。
 */
import { readFileSync, writeFileSync, mkdirSync, existsSync, statSync } from 'node:fs';
import { join, resolve } from 'node:path';
import { spawnSync } from 'node:child_process';

import { registerParser, parseAndSummarize, getFailedCases } from './registry.js';
import { JestVitestParser } from './parsers/jest-vitest.js';
import { JUnitXmlParser } from './parsers/junit-xml.js';
import { generateMarkdown, TOOL_VERSION } from './reporter.js';
import { detectFramework, findResultFile, detectProjectName } from './detect.js';
import { buildEnvironmentSummary } from './security.js';
import type { ReportConfig, TestRunResult } from './types.js';

// 注册所有内置解析器插件
registerParser(new JestVitestParser());
registerParser(new JUnitXmlParser());

interface ParsedArgs {
  mode: 'execute' | 'parse';
  testCommand?: string;
  resultFile?: string;
  outputFormat: string;
  outputPath: string;
  coverage: string;
  failThreshold?: number;
  help?: boolean;
}

function parseArgs(argv: string[]): ParsedArgs {
  const args = argv.slice(2);
  const out: ParsedArgs = {
    mode: 'parse',
    outputFormat: 'markdown',
    outputPath: 'reports/',
    coverage: 'auto',
  };
  for (let i = 0; i < args.length; i++) {
    const a = args[i];
    switch (a) {
      case 'execute':
      case 'parse':
        out.mode = a;
        break;
      case '--test-command':
        out.testCommand = args[++i];
        break;
      case '--result-file':
        out.resultFile = args[++i];
        break;
      case '--output-format':
        out.outputFormat = args[++i];
        break;
      case '--output-path':
        out.outputPath = args[++i];
        break;
      case '--coverage':
        out.coverage = args[++i];
        break;
      case '--fail-threshold': {
        const n = Number(args[++i]);
        if (Number.isFinite(n)) out.failThreshold = n;
        break;
      }
      case '--help':
      case '-h':
        out.help = true;
        break;
      default:
        if (a?.startsWith('--')) {
          // 未知参数，跳过其值
          i++;
        }
    }
  }
  return out;
}

function printHelp(): void {
  console.log(`test-report-generator ${TOOL_VERSION}
用法：
  node dist/index.js <mode> [options]

模式：
  execute    执行测试 → 收集结果 → 生成报告
  parse      仅解析已有结果文件 → 生成报告（默认）

选项：
  --test-command <cmd>   测试执行命令（execute 模式，默认自动检测）
  --result-file <path>   结果文件路径（parse 模式，默认自动检测）
  --output-format <fmt>  输出格式：markdown（默认）/ html / json
  --output-path <dir>    报告输出目录，默认 reports/
  --coverage <mode>      auto（默认）/ on / off
  --fail-threshold <pct> 通过率低于该值标记不达标
  -h, --help             显示帮助

示例：
  node dist/index.js parse --result-file junit.xml
  node dist/index.js execute --test-command "npx jest --json"
`);
}

/** 生成报告文件名：reports/test-report-<YYYYMMDD-HHmmss>.md */
function makeReportPath(outputDir: string, format: string): string {
  const d = new Date();
  const p = (n: number) => String(n).padStart(2, '0');
  const stamp = `${d.getFullYear()}${p(d.getMonth() + 1)}${p(d.getDate())}-${p(d.getHours())}${p(d.getMinutes())}${p(d.getSeconds())}`;
  const ext = format === 'json' ? 'json' : format === 'html' ? 'html' : 'md';
  return join(outputDir, `test-report-${stamp}.${ext}`);
}

/** 读取结果文件内容 */
function readResultFile(filePath: string): string {
  if (!existsSync(filePath)) {
    throw new Error(`结果文件不存在：${filePath}`);
  }
  const stat = (() => {
    try {
      return statSync(filePath);
    } catch {
      return null;
    }
  })();
  if (stat && stat.size === 0) {
    throw new Error(`结果文件为空（0 字节），可能测试未产出有效结果：${filePath}`);
  }
  return readFileSync(filePath, 'utf8');
}

/** 执行测试命令（FR1.3 执行模式） */
function executeTest(command: string, cwd: string): {
  ok: boolean;
  output?: string;
  error?: string;
} {
  // 拆分命令为程序与参数（简单分词，支持引号）
  const parts = command.match(/[^\s"']+|"([^"]*)"|'([^']*)'/g) || [];
  const clean = parts.map((p) => p.replace(/^"(.*)"$/, '$1').replace(/^'(.*)'$/, '$1'));
  const cmd = clean[0];
  const rest = clean.slice(1);
  if (!cmd) return { ok: false, error: '无效的测试命令' };

  try {
    const result = spawnSync(cmd, rest, {
      cwd,
      encoding: 'utf8',
      timeout: 600000, // 10 分钟上限
      maxBuffer: 50 * 1024 * 1024,
    });
    if (result.error) {
      // FR1.4：命令无法运行
      return { ok: false, error: `测试命令无法运行：${result.error.message}` };
    }
    // 用例失败时退出码非 0，但 stdout 仍有 JSON —— 只要 stdout 非空即视为可解析
    const out = (result.stdout || '') + (result.stderr ? `\n${result.stderr}` : '');
    if (!result.stdout || result.stdout.trim() === '') {
      if (result.status !== 0) {
        return { ok: false, error: `测试命令退出码 ${result.status}，且无 stdout 输出。${result.stderr || ''}` };
      }
      return { ok: false, error: '测试命令未产生 stdout 输出' };
    }
    return { ok: true, output: result.stdout };
  } catch (e) {
    const msg = e instanceof Error ? e.message : String(e);
    return { ok: false, error: `测试执行异常：${msg}` };
  }
}

function run(args: string[]): number {
  const cfg = parseArgs(args);
  if (cfg.help) {
    printHelp();
    return 0;
  }

  const cwd = process.cwd();
  const projectName = detectProjectName(cwd);
  const environment = buildEnvironmentSummary();

  let raw: string;
  let sourceFile: string | undefined;
  let command: string | undefined;

  if (cfg.mode === 'execute') {
    // 执行模式：检测或使用用户命令 → 运行 → 取 stdout
    const detected = detectFramework(cwd, cfg.testCommand);
    if (!detected.testCommand) {
      // FR1.4：无法识别测试命令，明确诊断，不生成空报告
      console.error(`[诊断] 无法自动检测测试命令。请通过 --test-command 显式指定，或在项目根目录提供 package.json（含 scripts.test）、jest/vitest 配置文件。`);
      return 2;
    }
    command = detected.testCommand;
    console.error(`[执行] ${command}（来源：${detected.source}）`);
    const execRes = executeTest(command, cwd);
    if (!execRes.ok || !execRes.output) {
      console.error(`[诊断] ${execRes.error || '测试执行失败'}`);
      return 3;
    }
    raw = execRes.output;
    // 执行模式无 sourceFile，结果来自 stdout
  } else {
    // 解析模式：定位结果文件
    const found = findResultFile(cwd, cfg.resultFile);
    if (!found) {
      // FR1.4 / AC4：文件不存在/损坏，返回明确错误而非空报告
      const hint = cfg.resultFile
        ? `指定的结果文件不存在：${resolve(cwd, cfg.resultFile)}`
        : `未在常见路径找到结果文件（results/junit.xml、test-results.json 等）。请通过 --result-file 指定。`;
      console.error(`[诊断] ${hint}`);
      return 2;
    }
    try {
      raw = readResultFile(found);
    } catch (e) {
      const msg = e instanceof Error ? e.message : String(e);
      console.error(`[诊断] ${msg}`);
      return 2;
    }
    sourceFile = found;
  }

  // 解析 + 汇总
  const parsed = parseAndSummarize(raw, sourceFile);
  if (!parsed.ok) {
    // AC4：结果文件损坏，返回明确错误说明而非空报告
    console.error(`[诊断] ${parsed.error}`);
    return 4;
  }

  let result: TestRunResult = parsed.result;
  result.projectName = projectName;
  result.command = command;
  result.environment = environment;

  // 覆盖率控制
  if (cfg.coverage === 'off') {
    result.coverage = undefined;
  }

  // 生成报告
  const outputDir = resolve(cwd, cfg.outputPath);
  mkdirSync(outputDir, { recursive: true });
  const reportPath = makeReportPath(outputDir, cfg.outputFormat);

  if (cfg.outputFormat === 'json') {
    writeFileSync(reportPath, JSON.stringify(result, null, 2) + '\n', 'utf8');
  } else {
    // P0 默认 markdown；html 复用 markdown 主体（P1 扩展）
    const md = generateMarkdown(result, { failThreshold: cfg.failThreshold });
    writeFileSync(reportPath, md, 'utf8');
  }

  // FR3.3：向用户返回报告路径 + 结果摘要（通过率、失败数），失败时附关键 1~3 条原因
  const rate = result.total === 0 ? 0 : Math.round((result.passed / result.total) * 10000) / 100;
  console.log(`\n报告已生成：${reportPath}`);
  console.log(`摘要：${result.total} 用例，通过 ${result.passed}，失败 ${result.failed}，跳过 ${result.skipped}，通过率 ${rate}%`);
  if (result.failed > 0) {
    const fails = getFailedCases(result).slice(0, 3);
    console.log(`关键失败原因：`);
    for (const f of fails) {
      console.log(`  - [${f.suite}] ${f.name}`);
      if (f.errorMessage) {
        const firstLine = f.errorMessage.split(/\r?\n/)[0].slice(0, 120);
        console.log(`    ${firstLine}`);
      }
    }
  }
  return result.failed > 0 ? 1 : 0;
}

// 入口：直接执行（本文件作为 CLI 入口通过 node dist/index.js 运行）
const code = run(process.argv);
process.exit(code);
export { run };
