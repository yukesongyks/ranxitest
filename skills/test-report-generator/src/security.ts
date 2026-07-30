/**
 * 安全工具：脱敏处理（NFR3）。
 * 报告中不得泄露环境变量、密钥类内容；错误堆栈须过滤敏感路径外的凭据信息。
 */

/** 需过滤的敏感键名模式（大小写不敏感） */
const SENSITIVE_KEY_PATTERNS = [
  /password/i,
  /passwd/i,
  /secret/i,
  /token/i,
  /api[-_]?key/i,
  /access[-_]?key/i,
  /private[-_]?key/i,
  /credential/i,
  /auth/i,
];

/** 环境变量赋值模式：KEY=value、KEY=value 的 export、--key=value */
const ENV_ASSIGN_RE =
  /(?:export\s+)?([A-Z_][A-Z0-9_]*)=([^\s]+)/g;

/** 凭据模式：sk-xxxx、AKIAxxxx、Bearer xxx、token: xxx 等 */
const CREDENTIAL_PATTERNS = [
  /sk-[A-Za-z0-9]{16,}/g,
  /AKIA[0-9A-Z]{16}/g,
  /[Bb]earer\s+[A-Za-z0-9._\-]{8,}/g,
  /[Tt]oken[:\s]+[A-Za-z0-9._\-]{8,}/g,
];

/** 截断堆栈/错误信息至可读长度 */
export function truncate(text: string | undefined, maxLen = 1200): string {
  if (!text) return '';
  if (text.length <= maxLen) return text;
  const head = text.slice(0, maxLen);
  return `${head}\n...[已截断，共 ${text.length} 字符]`;
}

/** 过滤环境变量赋值中的值（KEY=VALUE → KEY=***REDACTED***） */
export function redactEnvValues(text: string): string {
  return text.replace(ENV_ASSIGN_RE, (match, key: string) => {
    if (SENSITIVE_KEY_PATTERNS.some((re) => re.test(key))) {
      return `${key}=***REDACTED***`;
    }
    return match;
  });
}

/** 过滤凭据模式 */
export function redactCredentials(text: string): string {
  return CREDENTIAL_PATTERNS.reduce(
    (acc, re) => acc.replace(re, '***REDACTED***'),
    text
  );
}

/** 综合脱敏：先过滤凭据，再过滤敏感环境变量赋值 */
export function sanitize(text: string | undefined): string {
  if (!text) return '';
  return redactCredentials(redactEnvValues(text));
}

/** 环境摘要：仅保留 Node 版本与 OS，不包含任何环境变量 */
export function buildEnvironmentSummary(): string {
  const nodeVer = process.versions?.node ? `Node v${process.versions.node}` : 'Node 未知';
  const platform = `${process.platform}/${process.arch}`;
  return `${nodeVer} on ${platform}`;
}
