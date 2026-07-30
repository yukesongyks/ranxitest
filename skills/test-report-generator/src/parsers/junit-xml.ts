/**
 * JUnit XML 解析器插件（跨语言兜底格式）。
 *
 * JUnit XML 结构：
 *   <testsuites tests="" failures="" errors="" skipped="" time="">
 *     <testsuite name="" tests="" failures="" errors="" skipped="" time="">
 *       <testcase classname="" name="" time="">
 *         <failure message="" type="">堆栈文本</failure>
 *         <error message="" type="">错误文本</error>
 *         <skipped/>
 *       </testcase>
 *     </testsuite>
 *   </testsuites>
 *
 * 实现采用轻量正则状态机解析，零外部依赖（NFR5 可维护性 + 无 XML 库依赖）。
 */
import type { TestResultParser, TestRunResult, TestSuite, TestCase, TestCaseStatus } from '../types.js';
import { sanitize, truncate } from '../security.js';

/** 安全地解析 XML 属性值（处理 &lt; &gt; &amp; &quot; &apos;） */
function decodeXml(s: string): string {
  return s
    .replace(/&lt;/g, '<')
    .replace(/&gt;/g, '>')
    .replace(/&quot;/g, '"')
    .replace(/&apos;/g, "'")
    .replace(/&amp;/g, '&');
}

/** 提取标签的属性字符串 */
function extractAttr(attrs: string, name: string): string | undefined {
  const re = new RegExp(`\\b${name}\\s*=\\s*"([^"]*)"`, 'i');
  const m = attrs.match(re);
  return m ? decodeXml(m[1]) : undefined;
}

function toNum(s?: string): number | undefined {
  if (s == null || s === '') return undefined;
  const n = Number(s);
  return Number.isFinite(n) ? n : undefined;
}

function mapCaseStatus(testcaseXml: string): TestCaseStatus {
  if (/<failure\b/i.test(testcaseXml)) return 'failed';
  if (/<error\b/i.test(testcaseXml)) return 'failed';
  if (/<skipped\b/i.test(testcaseXml)) return 'skipped';
  return 'passed';
}

/** 提取 failure/error 的 message 与内部文本（堆栈） */
function extractFailure(testcaseXml: string): {
  message?: string;
  stack?: string;
} {
  // <failure message="..." type="...">堆栈文本</failure>
  const failMatch = testcaseXml.match(
    /<(failure|error)\b([^>]*)>([\s\S]*?)<\/\1>/i
  );
  if (!failMatch) return {};
  const [, , attrs, inner] = failMatch;
  const message = extractAttr(attrs, 'message');
  const stackText = inner.trim();
  return {
    message: message ? sanitize(truncate(message, 800)) : undefined,
    stack: stackText ? sanitize(truncate(stackText, 1200)) : undefined,
  };
}

export class JUnitXmlParser implements TestResultParser {
  name = 'junit-xml';
  extensions = ['xml'];

  canParse(raw: string, filePath?: string): boolean {
    const trimmed = raw.trimStart();
    if (trimmed.startsWith('<?xml') || /<testsuites?\b/i.test(trimmed)) return true;
    if (filePath && /\.xml$/i.test(filePath) && /<testsuite\b/i.test(raw)) return true;
    return false;
  }

  parse(raw: string, filePath?: string): TestRunResult {
    if (!/<testsuites?\b/i.test(raw) && !/<testsuite\b/i.test(raw)) {
      throw new Error('未找到 <testsuites> 或 <testsuite> 根元素，不是有效的 JUnit XML。');
    }

    const suites: TestSuite[] = [];
    let totalDuration: number | undefined;

    // 匹配所有 <testsuite ...>...</testsuite>（含自闭合与嵌套 testcase）
    const suiteRe = /<testsuite\b([^>]*)>([\s\S]*?)(?:<\/testsuite>|(?=<testsuite\b|<\/testsuites>|$))/gi;
    let suiteMatch: RegExpExecArray | null;

    while ((suiteMatch = suiteRe.exec(raw)) !== null) {
      const suiteAttrs = suiteMatch[1] || '';
      const suiteInner = suiteMatch[2] || '';

      const suiteName = extractAttr(suiteAttrs, 'name') || '(unnamed suite)';
      const suiteFile = extractAttr(suiteAttrs, 'file');
      const suiteTime = toNum(extractAttr(suiteAttrs, 'time'));
      if (typeof suiteTime === 'number') {
        totalDuration = (totalDuration || 0) + suiteTime * 1000;
      }

      // 匹配该 suite 内的所有 <testcase ...>...</testcase>（含自闭合）
      const cases: TestCase[] = [];
      const caseRe = /<testcase\b([^>]*?)(\/?)(?:>|(?:>[\s\S]*?<\/testcase>))/gi;
      let caseMatch: RegExpExecArray | null;
      while ((caseMatch = caseRe.exec(suiteInner)) !== null) {
        const caseAttrs = caseMatch[1] || '';
        const selfClosing = caseMatch[2] === '/';
        // 若自闭合，用空内容；否则提取完整 testcase 块以读取 failure/error 文本
        let caseBlock = '';
        if (!selfClosing) {
          const startIdx = caseMatch.index + caseMatch[0].length;
          // 重新定位完整块（caseMatch[0] 可能不含内部内容）
          const fullBlockRe = /<testcase\b[^>]*>([\s\S]*?)<\/testcase>/i;
          const sub = suiteInner.slice(caseMatch.index);
          const fm = sub.match(fullBlockRe);
          caseBlock = fm ? fm[1] : '';
        }
        const classname = extractAttr(caseAttrs, 'classname') || suiteName;
        const name = extractAttr(caseAttrs, 'name') || '(unnamed)';
        const time = toNum(extractAttr(caseAttrs, 'time'));
        const status = mapCaseStatus(selfClosing ? '' : caseBlock);
        const { message, stack } = selfClosing
          ? {}
          : extractFailure(caseBlock);

        cases.push({
          name,
          suite: classname,
          file: extractAttr(caseAttrs, 'file') || suiteFile || classname,
          status,
          durationMs: typeof time === 'number' ? Math.round(time * 1000) : undefined,
          errorMessage: message,
          stackTrace: stack,
        } as TestCase);
      }

      suites.push({
        name: suiteName,
        file: suiteFile,
        cases,
        durationMs: typeof suiteTime === 'number' ? Math.round(suiteTime * 1000) : undefined,
      } as TestSuite);
    }

    if (suites.length === 0) {
      throw new Error('JUnit XML 中未解析到任何 <testsuite>，文件可能损坏或格式不标准。');
    }

    return {
      framework: 'junit',
      frameworkVersion: undefined,
      projectName: undefined,
      suites,
      total: 0,
      passed: 0,
      failed: 0,
      skipped: 0,
      durationMs: totalDuration,
      coverage: undefined, // JUnit XML 标准不含覆盖率
      sourceFile: filePath,
    };
  }
}
