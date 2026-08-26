# Code Review Checklist

> **Change** `simple-document.txt` · **分支/Commit** `AI/task-DEV-f4ad1a6e-7360-11f1-8c66-df5563d236aa-6d72014f-a6c6-4e84-bd73-550117f50313` / `9a4c138` · **日期** `2025-08-26`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **技能守卫**：本次变更不包含 Java 文件（`.java`），按照 `dtazziboot-java-code-review` 技能规范，Java 代码审查应终止。现提供简化的文档规范性审查。

---

## Step 1 — 执行队列（产物 A）

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | Step4 | Step5 | 总状态 |
|---|----------------------|----------|-------|-------|-------|-------|--------|
| 1 | `simple-document.txt` | 需求产物 | ✅ | N/A(非Java) | N/A(非Java) | N/A(非Java) | ✅ 已审 |

- **Java 守卫**：无 `.java` 文件，按技能应终止，现执行简化文档审查。

---

## Step 2 — 功能（产物 B）

> 需求：生成一个简单的txt文档

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | 生成的文件为txt格式 | 需求描述："生成一个简单的txt文档" | `simple-document.txt` | ✅ | 文件扩展名为 `.txt`，已验证 |
| REQ-2 | 文档内容简单明了 | 需求描述："简单的" | `simple-document.txt` | ✅ | 文档包含标题、创建时间、内容列表，结构清晰 |

---

## Step 3 — 可读性检查（产物 C）

> 无 Java：**整节 N/A**。

对照 `references/readability-checklist.md` A1–A7 逐节核销：

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | N/A | 非Java文件 |
| A2 | 源文件结构/import 顺序 | N/A | 非Java文件 |
| A3 | 代码样式 | N/A | 非Java文件 |
| A4 | 命名规范 | N/A | 非Java文件 |
| A5 | 编码实践 | N/A | 非Java文件 |
| A6 | 特定元素样式 | N/A | 非Java文件 |
| A7 | Javadoc 规范 | N/A | 非Java文件 |

---

## Step 4 — 可靠性检查（产物 D）

> 无 Java：**整节 N/A**。

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> 非Java文件，所有检查项标记为 N/A(非Java)。

所有 B/M/I 规则：**N/A(非Java)**

### 4.2 可靠性（`reliability-checklist.md`）

所有 G* 规则：**N/A(非Java)**

### 4.3 安全（`security-checklist.md`）

所有 S* 规则：**N/A(非Java)**

---

## Step 5 — 自定义扩展检查（产物 E）

> 无自定义规则配置，整节 N/A。

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | N/A | 未启用自定义规则 |
| U1.2 | N/A | 未启用自定义规则 |
| U1.3 | N/A | 未启用自定义规则 |
| U2.1 | N/A | 未启用自定义规则 |
| U2.2 | N/A | 未启用自定义规则 |
| U2.3 | N/A | 未启用自定义规则 |

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、`Step4`、`Step5` 各列均非 `⬜`（N/A已标注原因）
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`（已审）
- [x] Step 3 的 A1–A7 均非 `⬜`（已标N/A）
- [x] Step 4 全部 **G/S** 与 **B/M/I** ID 均非 `⬜`（已标N/A）
- [x] Step 5 全部 U* ID 均非 `⬜`（已标N/A）
- [x] 所有 `❌/⚠️` 已写入 report（无问题）