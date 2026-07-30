/**
 * 框架检测与命令解析逻辑（FR1.1）。
 *
 * 识别优先级：
 *   a. 用户显式指定的命令（最高）
 *   b. 项目配置文件（package.json scripts.test、pyproject.toml、Cargo.toml）
 *   c. 框架特征文件推断（jest.config.*、vitest.config.*、pytest.ini）
 *
 * 零外部依赖：仅用 Node 内置 fs。
 */
import { readFileSync, existsSync, statSync } from 'node:fs';
import { join, resolve } from 'node:path';

export interface DetectedFramework {
  /** 框架标识：jest / vitest / pytest / cargo / junit */
  framework: string;
  /** 检测到的测试命令（执行模式用） */
  testCommand: string;
  /** 检测置信度：user-specified > config-file > feature-file */
  source: 'user-specified' | 'config-file' | 'feature-file' | 'fallback';
  /** 结果文件名（执行后产物，解析模式探测用） */
  resultFileHint?: string;
}

/**
 * 在指定目录下检测测试框架与命令。
 * @param cwd 项目根目录
 * @param userCommand 用户显式指定的命令（优先级 a），可选
 */
export function detectFramework(
  cwd: string = process.cwd(),
  userCommand?: string
): DetectedFramework {
  // a. 用户显式指定
  if (userCommand && userCommand.trim()) {
    return {
      framework: 'user',
      testCommand: userCommand.trim(),
      source: 'user-specified',
      resultFileHint: inferResultFileFromCommand(userCommand),
    };
  }

  // b. 项目配置文件
  const pkgPath = join(cwd, 'package.json');
  if (existsSync(pkgPath)) {
    try {
      const pkg = JSON.parse(readFileSync(pkgPath, 'utf8'));
      const scriptsTest = pkg?.scripts?.test;
      const devDeps = { ...(pkg?.devDependencies || {}), ...(pkg?.dependencies || {}) };
      if (scriptsTest && typeof scriptsTest === 'string') {
        // 优先级 b：package.json scripts.test 存在
        let framework = 'jest';
        let cmd = scriptsTest;
        if (/\bvitest\b/.test(scriptsTest) || 'vitest' in devDeps) {
          framework = 'vitest';
          cmd = 'npx vitest run --reporter=json';
        } else if (/\bjest\b/.test(scriptsTest) || 'jest' in devDeps) {
          framework = 'jest';
          cmd = 'npx jest --json';
        }
        return { framework, testCommand: cmd, source: 'config-file', resultFileHint: 'results.json' };
      }
      // 无 scripts.test，但有 jest/vitest 依赖
      if ('vitest' in devDeps) {
        return { framework: 'vitest', testCommand: 'npx vitest run --reporter=json', source: 'config-file', resultFileHint: 'results.json' };
      }
      if ('jest' in devDeps) {
        return { framework: 'jest', testCommand: 'npx jest --json', source: 'config-file', resultFileHint: 'results.json' };
      }
    } catch {
      // package.json 解析失败，降级继续
    }
  }

  // b. Python: pyproject.toml
  const pyprojectPath = join(cwd, 'pyproject.toml');
  if (existsSync(pyprojectPath)) {
    try {
      const content = readFileSync(pyprojectPath, 'utf8');
      if (/\[tool\.pytest\]/i.test(content) || /\bpytest\b/i.test(content)) {
        return {
          framework: 'pytest',
          testCommand: 'pytest --junitxml=results/junit.xml',
          source: 'config-file',
          resultFileHint: 'junit.xml',
        };
      }
    } catch {
      // 降级
    }
  }

  // b. Rust: Cargo.toml
  const cargoPath = join(cwd, 'Cargo.toml');
  if (existsSync(cargoPath)) {
    return {
      framework: 'cargo',
      testCommand: 'cargo test',
      source: 'config-file',
      resultFileHint: 'junit.xml',
    };
  }

  // c. 框架特征文件推断
  const featureChecks: Array<{ file: string; framework: string; cmd: string }> = [
    { file: 'jest.config.js', framework: 'jest', cmd: 'npx jest --json' },
    { file: 'jest.config.ts', framework: 'jest', cmd: 'npx jest --json' },
    { file: 'jest.config.cjs', framework: 'jest', cmd: 'npx jest --json' },
    { file: 'jest.config.mjs', framework: 'jest', cmd: 'npx jest --json' },
    { file: 'vitest.config.ts', framework: 'vitest', cmd: 'npx vitest run --reporter=json' },
    { file: 'vitest.config.js', framework: 'vitest', cmd: 'npx vitest run --reporter=json' },
    { file: 'pytest.ini', framework: 'pytest', cmd: 'pytest --junitxml=results/junit.xml' },
    { file: 'setup.cfg', framework: 'pytest', cmd: 'pytest --junitxml=results/junit.xml' },
  ];
  for (const fc of featureChecks) {
    if (existsSync(join(cwd, fc.file))) {
      return { framework: fc.framework, testCommand: fc.cmd, source: 'feature-file', resultFileHint: 'results.json' };
    }
  }

  // 兜底：无法识别框架
  return {
    framework: 'unknown',
    testCommand: '',
    source: 'fallback',
  };
}

/** 从命令推断结果文件名 */
function inferResultFileFromCommand(cmd: string): string | undefined {
  if (/--junitxml=([^\s]+)/.test(cmd)) {
    const m = cmd.match(/--junitxml=([^\s]+)/);
    return m ? m[1] : 'junit.xml';
  }
  if (/--json\b/.test(cmd)) return 'results.json';
  return undefined;
}

/**
 * 在解析模式下，尝试定位已有结果文件。
 * 搜索优先级：用户指定 > 仓库常见路径 > 当前目录 xml/json。
 */
export function findResultFile(
  cwd: string = process.cwd(),
  userSpecified?: string
): string | null {
  if (userSpecified) {
    const p = resolve(cwd, userSpecified);
    if (existsSync(p) && statSync(p).isFile()) return p;
    return null; // 用户指定但不存在，返回 null（调用方报错而非静默）
  }
  // 常见结果文件路径
  const candidates = [
    'results/junit.xml',
    'test-results/junit.xml',
    'reports/junit.xml',
    'junit.xml',
    'results.json',
    'test-results.json',
  ];
  for (const c of candidates) {
    const p = resolve(cwd, c);
    if (existsSync(p) && statSync(p).isFile()) return p;
  }
  return null;
}

/**
 * 读取项目名（取自 package.json name 或目录名）。
 */
export function detectProjectName(cwd: string = process.cwd()): string | undefined {
  const pkgPath = join(cwd, 'package.json');
  if (existsSync(pkgPath)) {
    try {
      const pkg = JSON.parse(readFileSync(pkgPath, 'utf8'));
      if (pkg?.name) return String(pkg.name);
    } catch {
      // 降级
    }
  }
  // 降级为目录名
  return resolve(cwd).split(/[\\/]/).pop() || undefined;
}
