# Code Review Checklist

> **Change** `quick-sort-util` · **分支/Commit** `AI/task-DEV-966dcd0a` / `a878a34` · **日期** `2026-07-31`

> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。

> **执行顺序（强制）**：写入本清单并进入逐文件审查前，先在目标仓库对变更路径运行 `references/script/scan-all-rules.sh`，将输出贴入 Step 3 和 Step 4 备注；再用 LLM 完成 Step 2–5 中脚本未覆盖项及复核。

---

## Step 1 — 执行队列（产物 A）

> **Step4 列语义**：每个 **Sn / Gn** 表示「**本文件**在 Step4 审查中，对 `reliability-checklist.md` 第 **G*n*** 节、`security-checklist.md` 第 **S*n*** 节的扫描结论」。**Bug 模式（B/M/I）** 不在本表分列，在下方 **§4.1** 按清单 ID 核销。与变更无关填 `N/A`；已扫无命中填 `✅`；命中风险填 `⚠️` 或 `❌`。

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | 总状态 |
|---|--------------------|----------|-------|-------|--------|
| 1 | `my-spring-boot-app/src/main/java/com/example/myapp/common/util/QuickSortUtil.java` | 编码实现主类 | ✅ | ✅ | ✅ |
| 2 | `my-spring-boot-app/src/test/java/com/example/myapp/common/util/QuickSortUtilTest.java` | 编码实现单测 | ✅ | ⚠️ | ✅ |

---

## Step 2 — 功能性检查（产物 B）

> **需求来源**：`<requirement_section>` — 「实现一个快速排序算法」
> **功能不符统一标 P0**。每个 REQ 必须来自 change/ 原文，记录原文摘录，并绑定至少一个变更文件。

### REQ-1: 实现快速排序算法

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| int[] 数组升序排序 | ✅ | 「实现一个快速排序算法」 | `QuickSortUtil.java:22-27` sort(int[]) | Lomuto 分区 + 三数取中，原地排序 |
| List<T> 泛型列表升序排序 | ✅ | 同上（泛化扩展） | `QuickSortUtil.java:35-46` sort(List<T>) | 转数组排序后回写，自然顺序比较 |
| 递归分区 | ✅ | 快速排序定义 | `QuickSortUtil.java:55-62`, `132-139` quickSort | 标准 low/high 递归，low>=high 终止 |
| Lomuto 分区 | ✅ | 快速排序定义 | `QuickSortUtil.java:72-83`, `150-161` partition | 小于等于 pivot 左移，pivot 归位 |
| 三数取中选基准 | ✅ | 类级 Javadoc 声明 | `QuickSortUtil.java:93-106` medianOfThree | 首/中/尾中位数交换至末尾 |
| 空值/空集合防御 | ✅ | 工具类健壮性 | `QuickSortUtil.java:23,36` | null/size<=1 直接返回 |

**结论**：✅ 功能完整，与需求一致。int[] 和 List<T> 双路径均实现快速排序核心逻辑（递归分区 + Lomuto），边界处理到位。

---

## Step 3 — 可读性检查（产物 C）

> 对照 `references/readability-checklist.md`（A1–A7）。违规标 P2（一般风格）或 P1（明显影响可读性）。

### 文件 1: QuickSortUtil.java

| ID | 规则 | 结果 | 说明 |
|----|------|------|------|
| A1.1 | 文件名=顶层类名 | ✅ | `QuickSortUtil.java` |
| A1.3 | 空白仅 ASCII 空格 | ✅ | 无 Tab |
| A2.1 | 文件顺序 package→import→类 | ✅ | 符合 |
| A2.2 | 禁止 import * | ✅ | 显式 import java.util.List |
| A2.3 | import 分静态/非静态组 | ✅ | 仅一个非静态 import，无静态 import |
| A3.1 | K&R 大括号 | ✅ | 符合 |
| A3.3 | 缩进4空格 | ✅ | 符合 |
| A3.4 | 行宽≤120 | ✅ | 最长行约90字符 |
| A3.6 | 类成员间空行 | ✅ | 方法间均有空行 |
| A4.1 | 包名全小写 | ✅ | `com.example.myapp.common.util` |
| A4.2 | 类名 UpperCamelCase | ✅ | `QuickSortUtil` |
| A4.3 | 方法名 lowerCamelCase | ✅ | sort, quickSort, partition, swap, medianOfThree |
| A5.1 | @Override 必须加 | N/A | 无重写方法 |
| A6.1 | 数组方括号属于类型 | ✅ | `int[] array`, `T[] array` |
| A6.3 | 修饰符顺序 | ✅ | `public final class`, `public static`, `private static` |
| A7.1 | public 成员必须有 Javadoc | ✅ | sort(int[]), sort(List<T>) 均有 Javadoc |
| A7.2 | 块标记顺序 | ✅ | @param → @return 顺序正确 |

**文件1结论**：✅ 可读性优秀，完全符合阿里规范。

### 文件 2: QuickSortUtilTest.java

| ID | 规则 | 结果 | 说明 |
|----|------|------|------|
| A2.3 | import 分静态/非静态组，组间空行 | ✅ | 第9行空行分隔非静态组（6-8行）与静态组（10-12行），符合；非静态组内按 org.junit / java.util 子分组（第5行空行）为常见可读性增强，可接受 |
| A2.4 | 组内 ASCII 字典序 | ⚠️ P2 | 静态 import 组内顺序 `assertArrayEquals, assertEquals, assertDoesNotThrow` 非字典序：`assertDoesNotThrow`（D=0x44）应排在 `assertEquals`（E=0x45）之前，二者顺序颠倒。位置 `QuickSortUtilTest.java:11-12` |
| A3.4 | 行宽≤120 | ✅ | 符合 |
| A4.7 | 测试类名=被测类+Test | ✅ | `QuickSortUtilTest` |
| A7.1 | public 成员必须有 Javadoc | N/A | 测试方法非 public，@DisplayName 已自解释 |

**文件2结论**：⚠️ 可读性有1处 P2 风格问题（静态 import 组内非字典序），不影响功能。

---

## Step 4 — 可靠性检查（产物 D）

> **可靠性（军规）** 参考 `reliability-checklist.md`；**安全** 参考 `security-checklist.md`；**Bug 模式** 参考 `bug-pattern-checklist.md`。
> **自动化预扫**：`scan-all-rules.sh` 已执行，52/222 规则扫描，**无命中**。

### §4.1 Bug 模式（B/M/I）核销

| 扫描来源 | 结果 | 说明 |
|----------|------|------|
| scan-all-rules.sh | ✅ | 52条可程序化规则无命中 |
| LLM 人工复核 | ✅ | 未见 B001-B120 / M001-M027 / I001-I010 命中。无数组equals误用、无toString误用、无parse字面量等 |

### §4.2 可靠性（G）核销

| 域 | ID | 结果 | 等级 | 说明 |
|----|-----|------|------|------|
| G1 并发 | G1.1-G1.4 | N/A | 无并发/事务/锁场景，纯内存排序工具类 |
| G2 幂等 | G2.1-G2.3 | N/A | 无写接口/MQ消费，纯内存计算 |
| G3 事务 | G3.1-G3.2 | N/A | 无事务 |
| G4 SQL | G4.1-G4.3 | N/A | 无SQL |
| G5 消息 | G5.1 | N/A | 无MQ |
| G6 缓存 | G6.1-G6.2 | N/A | 无缓存 |
| G7 调度 | G7.1-G7.2 | N/A | 无调度任务 |
| G8 防御 | G8.1-G8.6 | ✅ | 无I/O/连接/锁/ThreadLocal/线程池；swap 有 i==j 防御；null/空防御到位 |
| G9 网络 | G9.1-G9.3 | N/A | 无外部调用 |
| G10 契约 | G10.1-G10.2 | N/A | 无接口契约变更 |
| G11 自测 | G11.1 | ✅ | 有14个单测，均有断言 |
| G11 自测 | G11.2 | ✅ | 覆盖空/单元素/null/重复/负数边界 |
| G11 自测 | G11.3 | ✅ | null/空集合防御性校验已实现（line 23,36） |
| G11 自测 | G11.4 | N/A | 无数值溢出/除零/金额运算，仅比较交换 |
| G12 资损 | G12.1-G12.2 | N/A | 无资金场景 |
| G13 监控 | G13.1 | N/A | 无日志埋点需求 |
| G14 国际化 | G14.1-G14.4 | N/A | 无金额/多租户/时区 |
| G15 灰度 | G15.1-G15.3 | N/A | 无DB/接口变更 |
| G16 监控 | G16.1-G16.4 | ✅ | 无异常捕获/空catch，纯计算无异常路径 |
| G17 应急 | G17.1-G17.3 | N/A | 无功能开关/降级/回滚需求 |

### §4.3 可靠性问题明细

| 等级 | ID | 简述 | 位置 |
|------|-----|------|------|
| P1 | — | 泛型 partition 未使用三数取中，有序输入存在 O(n²) 退化与递归栈溢出风险 | `QuickSortUtil.java:150-161` |

**说明**：类级 Javadoc（line 8-10）声明「采用 Lomuto 分区策略，并结合三数取中选取基准值，以降低在接近有序输入下退化为 O(n^2) 的概率」。int[] 路径（line 73）调用了 `medianOfThree`，但泛型 `partition(T[], int, int)`（line 151）直接取 `array[high]` 作基准，**未使用三数取中**。当传入已有序或接近有序的 List 时，泛型路径仍会退化为 O(n²)，极端情况下（如数万元素有序列表）可能触发栈溢出（StackOverflowError）。这与类级声明不一致，且是可靠性隐患。

### §4.4 安全（S）核销

| 域 | ID | 结果 | 说明 |
|----|-----|------|------|
| S1-S10 | 全部 | N/A | 排序工具类无 SQL/XSS/SSRF/命令执行/XXE/反序列化/文件/访问控制/数据安全/CSRF 场景。纯内存计算，无外部输入处理、无网络、无持久化 |

---

## Step 5 — 自定义扩展检查（产物 E）

> 参考 `references/customized-checklist.md`。

**结论**：`N/A(未启用自定义规则)` — 自定义检查清单为示例项，未配置团队私有规则。

---

## 核销验证

- [x] Step 2 所有 REQ 已核销（✅）
- [x] Step 3 所有文件已扫描，违规已标注
- [x] Step 4 G1-G17 + S1-S10 + B/M/I 全部核销
- [x] Step 5 已标注 N/A
- [x] scan-all-rules.sh 已执行（52/222 规则，无命中）
- [x] 所有 `⬜ 待审` 为零
