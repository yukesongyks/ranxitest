# Code Review Checklist

> **Change** `hello world` · **分支/Commit** `AI/task-DEV-f4ad1a6e-7360-11f1-8c66-df5563d236aa-7f289739-596d-40b9-` / `9f33bea` · **日期** `2026-08-06`

> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。

> **执行顺序（强制）**：写入本清单并进入逐文件审查前，先在目标仓库对变更路径运行 `references/script/scan-all-rules.sh`，将输出贴入 Step 3 和 Step 4 备注；再用 LLM 完成 Step 2–5 中脚本未覆盖项及复核。

> **scan-all-rules.sh 预扫结果**：`Targets: HelloController.java HelloControllerTest.java · Engine: ripgrep · No findings. 52/222 rules scanned · EXIT:0（无 P0）`

---

## Step 1 — 执行队列（产物 A）

> **Step4 列语义**：每个 **Sn / Gn** 表示「**本文件**在 Step4 审查中，对 `reliability-checklist.md` 第 **G*n*** 节、`security-checklist.md` 第 **S*n*** 节的扫描结论」。**Bug 模式（B/M/I）** 不在本表分列，在下方 **§4.1** 按清单 ID 核销（可与 `scan-all-rules.sh` 预扫结果对照）。与变更无关填 `N/A`；已扫无命中填 `✅`；命中风险填 `⚠️` 或 `❌`（并在 Step 4 明细表与 report 中写清 `Gx.x` / `Sx.x` + `path:line`）。

**列说明（与 references 章节对齐）**

| 列组 | 列名 | 对应清单章节 |
|------|------|----------------|
| 可靠性 | **G1** … **G17**（+ **G18** 仅明细表） | `reliability-checklist.md` — G1 并发 … G17 可应急；**G18** 安全补强在 Step 4.2 逐条核销，Step 1 可不单列 |
| 安全 | **S1** … **S10** | `security-checklist.md` — S1 SQL 注入 … S10 CSRF/CORS/跳转 |

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|-----|-----|-----|-----|-----|-----|-----|-----|----|----|----|----|----|----|----|----|----|----|--------|
| 1 | `src/main/java/com/example/myapp/controllers/HelloController.java` | 本次新增主代码，实现 hello 接口 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 2 | `src/test/java/com/example/myapp/controllers/HelloControllerTest.java` | 本次新增测试，验证 hello 接口 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |

> 说明：本变更为最小化 Hello World 示例，不涉及并发（G1）、事务（G2）、异常处理（G3）、资源（G4）、空指针（G5）、集合（G6）、日期/序列化（G7）、日志（G8）、魔法值（G9）、分支/复杂度（G10）、并发原语（G11）、缓存（G12）、IO（G13）、反射/动态（G14）、配置（G15）、DB/SQL（G16）、可应急（G17）；不涉及 SQL 注入（S1）、XSS（S2）、反序列化（S3）、命令/路径穿越（S4）、敏感信息（S5）、弱加密（S6）、不安全随机（S7）、SSRF/重定向（S8）、权限（S9）、CSRF/CORS（S10）。全部 `N/A`，原因为「示例代码不涉及该风险域」。

---

## Step 2 — 功能（REQ）绑定

| REQ | 名称 | 场景结果 | Spec 证据 | 代码证据 | 说明 |
|-----|------|----------|------------|----------|------|
| REQ-1 | hello world 问候接口 | ✅ | `requirement_section: hello world` | `HelloController.java:18-20` `@GetMapping("/hello")` 返回 `"Hello, World!"` | 实现固定字符串返回，符合需求描述 |
| REQ-2 | 接口可用性验证 | ✅ | 隐含：验证服务可用性（`HelloController.java:7` 注释） | `HelloControllerTest.java:24-27` MockMvc 断言 200 + 内容 | 测试覆盖接口契约 |

---

## Step 3 — 可读性检查

> scan-all-rules.sh 预扫：A 类规则无命中（52/222 中可读性相关 8 条已扫）。

| 文件 | 结果 | 说明 |
|------|------|------|
| `HelloController.java` | ✅ | 命名规范（类名 `HelloController`、方法名 `hello` 符合驼峰）；注释完整（类注释 + 方法 Javadoc 含 `@return`）；魔法值 `"Hello, World!"` 为业务字面量，符合最小示例场景；无超长行、无空方法、无重复块 |
| `HelloControllerTest.java` | ✅ | 测试类名 `HelloControllerTest` 符合 `<Class>Test` 约定；方法 `helloShouldReturnGreeting` 语义清晰；Javadoc `{@link HelloController}` 引用正确；无魔幻数字 |

---

## Step 4 — 可靠性检查

### §4.1 Bug 模式核销

> scan-all-rules.sh 预扫：B/M/I 类规则无命中（25/81 Blocker + 6/27 Major + 2/10 Info）。

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| Bug 模式 | `bug-pattern-checklist.md` B001–B120 | ✅ | — | 已扫无命中；本变更不含 parse/of 字面量、数组比较、`Arrays.asList` 装箱等任何 Bug 模式触发结构 |

### §4.2 可靠性 G 清单

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 可靠性 | `reliability-checklist.md` G1–G17 | N/A | — | 示例代码不涉及并发/事务/异常/资源/空指针/集合/日志/配置/DB 等风险域；scan-all-rules.sh G 类（4/45）已扫无命中 |
| 安全补强 | `reliability-checklist.md` G18 | N/A | — | 无安全敏感操作 |

---

## Step 5 — 安全检查

> scan-all-rules.sh 预扫：S 类规则无命中（7/30）。

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 安全 | `security-checklist.md` S1–S10 | N/A | — | 示例代码无用户输入、无 DB、无网络调用、无认证授权、无敏感信息；scan-all-rules.sh S 类已扫无命中 |

---

## 收口

- **所有核销项**已从 `⬜` 转为 `✅` 或 `N/A`（含原因），满足完成标准。
- **P0/P1/P2 问题数**：0 / 0 / 0。
- **结论**：本次 hello world 变更通过代码评审，可进入下一阶段。
