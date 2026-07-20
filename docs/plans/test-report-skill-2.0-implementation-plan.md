# 测试报告 Skill 2.0 实施计划

> 阶段：plan（实施计划）
> 采用技能：/writing-plans
> 输入依据：`docs/requirements/test-report-skill-clarify.md`（clarify 阶段产物）
> 生成时间：2026-07-20

---

> **For agentic workers:** REQUIRED SUB-SKILL: Use `superpowers:subagent-driven-development` (recommended) or `superpowers:executing-plans` to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 提供一个 Skill，Agent 在执行测试后能够自动解析测试结果并生成结构化、可读性强的标准测试报告（中文 Markdown，P0 聚焦 Java/Maven 栈）。

**Architecture:** Skill 以"插件式解析器 + 独立报告生成器 + 编排脚本"三层解耦构建。编排层（`skill run`）按"执行/解析双模式"决定是否触发测试运行，将框架原始产物（Surefire XML / JUnit XML / JaCoCo XML）交给解析器插件归一化为统一数据模型，再由报告生成器按固定章节顺序渲染为中文 Markdown。解析器为可插拔结构，新增框架不影响既有解析器（NFR5）。

**Tech Stack:**
- 宿主项目：Spring Boot 2.6.6 / Java 17 / Maven（`my-spring-boot-app/pom.xml`）
- 测试运行器：Maven Surefire（`mvn test`）
- 覆盖率：JaCoCo Maven 插件（`jacoco-maven-plugin`，需新增）
- 兜底解析格式：JUnit XML（Surefire 原生产物即 JUnit XML 兼容）
- Skill 实现语言：Bash（编排） + Python 3（解析与报告生成，仅用标准库，零外部依赖）
- 报告格式：Markdown（P0），HTML/JSON 列为 P1/M2-M3

---

## 范围说明（Scope）

本计划对应需求澄清文档锁定的 **P0 范围**（M1 里程碑）：

- **支持框架**：Java/Maven（Surefire 产出的 JUnit XML）+ JUnit XML 跨语言兜底。Jest/Vitest/pytest 下沉至 P2/M2-M4，本计划不实现，仅在解析器插件接口预留扩展位。
- **报告语言**：中文。
- **输出格式**：Markdown（默认 `.md`）；HTML/JSON 伴随产物列为 P1（M2-M3），本计划在 Task 5 仅保留接口占位与降级标注，不实现渲染逻辑。
- **覆盖率**：JaCoCo `jacoco.xml` 解析（auto 模式：存在则呈现，不存在标注"未获取"）。
- **不做**：用例自动生成/修复、Web 托管、趋势对比、lint/安全报告聚合（需求文档 §2.2 非目标）。

---

## 文件结构（File Structure）

Skill 安装于仓库根 `skills/test-report/`（Agent 通过 SKILL.md frontmatter 自动发现）。所有新增文件路径如下，每文件单一职责：

```
skills/test-report/
├── SKILL.md                          # Skill 入口与 frontmatter（name/description/triggers/config）
├── scripts/
│   ├── run.sh                        # 编排脚本：双模式分发（执行/解析）、调用解析器与生成器
│   ├── generate_report.py            # 报告生成器：归一化数据 -> 中文 Markdown 四板块
│   ├── parsers/
│   │   ├── __init__.py
│   │   ├── base.py                   # 解析器插件基类与注册表（NFR5 插件式）
│   │   ├── junit_xml.py              # JUnit XML 解析器（跨语言兜底 + Surefire 原生产物）
│   │   └── jacoco_xml.py             # JaCoCo 覆盖率 XML 解析器
│   ├── models.py                     # 统一数据模型（TestReport/TestSuite/TestCase/CoverageSummary）
│   ├── framework_detect.py           # 框架检测：mvn/pom.xml -> Surefire 命令与产物路径
│   └── test_runner.py                # 测试执行器：后台执行 mvn test + 产物路径定位 + 诊断
├── tests/
│   ├── fixtures/
│   │   ├── junit_ok.xml              # 全通过样例（验证摘要与明细）
│   │   ├── junit_failed.xml          # 含失败用例样例（验证失败分析章节）
│   │   ├── junit_malformed.xml        # 损坏文件样例（验证 AC4 降级）
│   │   └── jacoco_sample.xml         # 覆盖率样例（验证 AC5）
│   ├── test_junit_xml_parser.py      # JUnit XML 解析器单测
│   ├── test_jacoco_parser.py         # JaCoCo 解析器单测
│   ├── test_models.py                # 数据模型归一化与降级单测
│   ├── test_report_generator.py      # 报告生成器章节与降级单测
│   ├── test_framework_detect.py      # 框架检测单测
│   └── test_runner_dryrun.py         # 执行器诊断单测（不实际跑 mvn，mock 命令缺失场景）
└── README.md                         # Skill 使用文档（触发示例、配置项、产物路径）
```

**职责边界：**
- `models.py`：纯数据结构 + 归一化（如耗时秒数、通过率计算），无 IO。
- `parsers/base.py`：定义 `Parser` 抽象基类与全局 `PARSER_REGISTRY`，新增框架只需实现 `parse(path) -> TestReport` 并注册。
- `framework_detect.py` + `test_runner.py`：只关心"跑/不跑"和"产物在哪"，不解析内容。
- `generate_report.py`：只消费 `TestReport`，不关心来源框架，保证幂等（NFR4）。

---

## 全局约束（Global Constraints）

以下为贯穿所有 Task 的项目级硬约束，每个 Task 隐式包含：

- **Java 17**：宿主项目 JDK 版本，运行 `mvn test` 时须可用（本计划不要求在此环境实际编译，详见各 Task 验证策略）。
- **Spring Boot 2.6.6 / Maven**：`my-spring-boot-app/pom.xml` 为唯一被测工程；新增 JaCoCo 插件须在 Task 3 修改该 pom。
- **Python 3 仅用标准库**：解析器与报告生成器不得引入 `lxml`/`jinja2` 等第三方包；XML 用 `xml.etree.ElementTree`，避免依赖安装失败。
- **中文报告**：所有面向用户的报告文本、章节标题、降级标注均为中文；命令/路径/字段名保持原文。
- **产物路径**：报告默认 `reports/test-report-<YYYYMMDD-HHmmss>.md`（相对仓库根）；样例 fixture 不写入 `reports/`。
- **安全**（NFR3）：错误堆栈过滤环境变量名、密钥、token；`generate_report.py` 须有敏感词过滤步骤。
- **幂等**（NFR4）：同一输入产物内容一致，时间戳字段（生成时间、报告文件名后缀）除外。
- **不执行 git commit/push**：本计划所有"commit"步骤为 TDD 节奏建议，受运行期约束不得实际提交；改为"保存文件即视为该 Task 交付"。

---

## Task 1: Skill 骨架与目录

**Files:**
- Create: `skills/test-report/SKILL.md`
- Create: `skills/test-report/scripts/__init__.py`（空文件，标记 scripts 为可导入包）
- Create: `skills/test-report/scripts/parsers/__init__.py`（空文件）
- Create: `skills/test-report/tests/__init__.py`（空文件）
- Create: `skills/test-report/tests/fixtures/.gitkeep`
- Create: `skills/test-report/README.md`

**Interfaces:**
- Consumes: 无（首个 Task，为后续提供安装点）
- Produces: `SKILL.md`（含 frontmatter，Agent 据此发现并触发 Skill）；目录骨架供 Task 2-7 写入

**Why:** Skill 必须有标准入口文件 `SKILL.md` 及 frontmatter（name/description/triggers），Agent 才能在用户说"生成测试报告"时自动加载本 Skill。目录骨架先行，避免后续 Task 反复创建目录。

**Steps:**

- [ ] 1. 创建目录骨架（含 `scripts/parsers/`、`tests/fixtures/`）：
  ```bash
  mkdir -p skills/test-report/scripts/parsers skills/test-report/tests/fixtures
  touch skills/test-report/scripts/__init__.py \
        skills/test-report/scripts/parsers/__init__.py \
        skills/test-report/tests/__init__.py \
        skills/test-report/tests/fixtures/.gitkeep
  ```

- [ ] 2. 写入 `skills/test-report/SKILL.md`，frontmatter 与正文如下（**完整内容，勿省略**）：
  ```markdown
  ---
  name: test-report
  description: 执行测试或解析已有结果（JUnit XML / Surefire / JaCoCo），生成结构化中文 Markdown 测试报告，含摘要、明细、失败分析、覆盖率四大板块。
  triggers:
    - 生成测试报告
    - 跑一下测试并出报告
    - 把这个 junit.xml 转成测试报告
    - 生成 test report
  config:
    test_command: auto          # 测试执行命令，auto 表示自动检测
    result_file: auto           # 解析模式下的结果文件路径
    output_format: markdown     # markdown / html / json（P0 仅实现 markdown）
    output_path: reports/        # 报告输出目录
    coverage: auto              # auto / on / off
    fail_threshold: null        # 通过率低于该值时结论标记为不达标
  ---
  
  # 测试报告 Skill（test-report）
  
  在测试执行后自动解析结果并生成结构化、可读性强的标准测试报告。
  
  ## 用法
  
  - 执行模式（自动跑测试）：`bash skills/test-report/scripts/run.sh`
  - 解析模式（已有结果文件）：`bash skills/test-report/scripts/run.sh --parse <junit.xml 路径或目录>`
  - 指定输出路径：`bash skills/test-report/scripts/run.sh --output reports/my.md`
  
  默认产物：`reports/test-report-<YYYYMMDD-HHmmss>.md`
  
  ## 支持范围（P0）
  
  - Java/Maven（Surefire 产出的 JUnit XML）
  - JUnit XML 跨语言兜底
  - JaCoCo 覆盖率（`jacoco.xml`，auto 模式按存在性呈现）
  
  ## 报告章节（固定顺序）
  
  1. 报告头：项目名、生成时间、执行命令、框架/版本、执行环境摘要
  2. 结果摘要：用例总数、通过/失败/跳过数、通过率、总耗时；整体结论用 ✅ / ❌ 标识
  3. 失败用例分析：用例名、所属文件、错误信息、堆栈关键行
  4. 用例明细：按测试文件分组，含耗时
  5. 覆盖率：语句/分支/函数/行覆盖率总表 + 低于阈值文件清单（若可获取）
  6. 附录：原始结果文件路径、生成工具版本
  ```

- [ ] 3. 写入 `skills/test-report/README.md`（面向最终用户的简要说明）：
  ```markdown
  # test-report Skill
  
  生成结构化中文 Markdown 测试报告。详见 `SKILL.md`。
  
  ## 快速开始
  
  在含 `my-spring-boot-app/pom.xml` 的仓库根执行：
  
  ```bash
  bash skills/test-report/scripts/run.sh
  ```
  
  报告默认写入 `reports/test-report-<时间戳>.md`。
  ```

**Verify:**
- 命令：`test -f skills/test-report/SKILL.md && grep -q 'name: test-report' skills/test-report/SKILL.md && echo OK`
- 期望输出：`OK`
- 目录存在性：`test -d skills/test-report/scripts/parsers && test -d skills/test-report/tests/fixtures && echo OK`

**Commit:** 保存上述文件即视为 Task 1 交付（受运行期约束不实际执行 git commit）。

---

## Task 2: 核心数据模型

**Files:**
- Create: `skills/test-report/scripts/models.py`
- Create: `skills/test-report/tests/test_models.py`
- Test: `tests/test_models.py`

**Interfaces:**
- Consumes: 无（纯数据结构，后续 Task 依赖它）
- Produces:
  - `class TestCase(name, classname, status, time_sec, file=None, line=None, error_message=None, stack_trace=None)`
    - `status` ∈ `{"passed","failed","skipped","error"}`
    - 方法 `is_failure() -> bool`（`failed` 或 `error` 均视为失败）
  - `class TestSuite(name, cases: list[TestCase])`
    - 属性 `total/passed/failed/skipped/time_sec/pass_rate`（惰性计算）
  - `class CoverageMetric(instruction=None, branch=None, line=None, method=None, complexity=None)`：各字段为 `float | None`（百分比）
  - `class CoverageSummary(metrics: CoverageMetric, files: list[dict]`，其中 file dict 含 `path`、`metrics: CoverageMetric`）
  - `class TestReport(project_name, generated_at, command, framework, framework_version, env_summary, suites: list[TestSuite], coverage: CoverageSummary|None, source_files: list[str], tool_version: str)`
    - 方法 `summary() -> dict`：返回 `{total, passed, failed, skipped, pass_rate, time_sec}`
    - 方法 `failures() -> list[TestCase]`：返回所有失败用例
    - 方法 `to_dict() -> dict` / `from_dict(d) -> TestReport`：用于 JSON 伴随产物（P1 预留接口，P0 仅需可序列化）
  - `class ParseResult(report: TestReport|None, errors: list[str])`：解析结果容器，承载降级信息（NFR2：字段缺失时降级，不崩溃）
- 工具函数 `normalize_time(value) -> float`：将 `None`/`""`/"0.001"/`3` 等归一化为秒数 `float`，无法解析返回 `0.0` 并记录降级项

**Why:** 所有解析器归一化到同一模型，报告生成器只消费 `TestReport`，保证"换框架不换报告逻辑"（NFR5 可维护性的根基）。`ParseResult` 承载降级信息，使 NFR2（缺失字段降级标注"未获取"）有统一传递通道。

**Steps:**

- [ ] 1. 写 `tests/test_models.py` 失败测试（TDD 先行）。**完整测试代码：**
  ```python
  import sys, os
  sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'scripts'))
  from models import TestCase, TestSuite, TestReport, CoverageMetric, ParseResult, normalize_time
  
  def test_case_is_failure():
      assert TestCase("a", "C", "failed", 1.0).is_failure()
      assert TestCase("a", "C", "error", 1.0).is_failure()
      assert not TestCase("a", "C", "passed", 1.0).is_failure()
      assert not TestCase("a", "C", "skipped", 1.0).is_failure()
  
  def test_suite_counts():
      s = TestSuite("S", [
          TestCase("p","C","passed",1.0),
          TestCase("f","C","failed",2.0),
          TestCase("k","C","skipped",0.0),
      ])
      assert s.total == 3 and s.passed == 1 and s.failed == 1 and s.skipped == 1
      assert s.time_sec == 3.0
      assert s.pass_rate == round(1/3*100, 2)
  
  def test_report_summary_and_failures():
      r = TestReport(project_name="demo", generated_at="2026-07-20T06:00:00",
                     command="mvn test", framework="maven-surefire",
                     framework_version="3.0.0", env_summary="JDK 17",
                     suites=[TestSuite("S",[
                         TestCase("p","C","passed",1.0),
                         TestCase("f","C","failed",2.0,
                                  error_message="NPE", stack_trace="at Foo.bar(Foo.java:3)"),
                     ])], coverage=None, source_files=["a.xml"], tool_version="1.0")
      assert r.summary() == {"total":2,"passed":1,"failed":1,"skipped":0,
                             "pass_rate":50.0,"time_sec":3.0}
      assert len(r.failures()) == 1 and r.failures()[0].name == "f"
  
  def test_normalize_time_graceful():
      assert normalize_time("0.001") == 0.001
      assert normalize_time(None) == 0.0
      assert normalize_time("") == 0.0
      assert normalize_time("not-a-number") == 0.0
  
  def test_parse_result_carries_degradation():
      pr = ParseResult(report=None, errors=["junit xml parse error at line 3"])
      assert pr.report is None
      assert pr.errors == ["junit xml parse error at line 3"]
  ```

- [ ] 2. 运行测试，确认失败：
  ```bash
  cd skills/test-report && python3 -m pytest tests/test_models.py -x
  ```
  期望：`ModuleNotFoundError: No module named 'models'`（文件尚未实现）。

- [ ] 3. 写 `scripts/models.py` 实现上述接口。**完整代码骨架（数据类 + 计算逻辑，勿用占位符）：**
  ```python
  """测试报告统一数据模型。仅标准库，无 IO。"""
  from dataclasses import dataclass, field
  from typing import Optional
  
  FAIL_STATUS = {"failed", "error"}
  
  def normalize_time(value) -> float:
      """归一化耗时为秒(float)。None/空/非数字返回 0.0。"""
      if value is None or value == "":
          return 0.0
      try:
          return float(value)
      except (TypeError, ValueError):
          return 0.0
  
  @dataclass
  class TestCase:
      name: str
      classname: str
      status: str  # passed/failed/skipped/error
      time_sec: float = 0.0
      file: Optional[str] = None
      line: Optional[int] = None
      error_message: Optional[str] = None
      stack_trace: Optional[str] = None
      def is_failure(self) -> bool:
          return self.status in FAIL_STATUS
  
  @dataclass
  class TestSuite:
      name: str
      cases: list = field(default_factory=list)
      @property
      def total(self): return len(self.cases)
      @property
      def passed(self): return sum(1 for c in self.cases if c.status == "passed")
      @property
      def failed(self): return sum(1 for c in self.cases if c.is_failure())
      @property
      def skipped(self): return sum(1 for c in self.cases if c.status == "skipped")
      @property
      def time_sec(self): return round(sum(c.time_sec for c in self.cases), 3)
      @property
      def pass_rate(self):
          if self.total == 0: return 0.0
          return round(self.passed / self.total * 100, 2)
  
  @dataclass
  class CoverageMetric:
      instruction: Optional[float] = None
      branch: Optional[float] = None
      line: Optional[float] = None
      method: Optional[float] = None
      complexity: Optional[float] = None
  
  @dataclass
  class CoverageSummary:
      metrics: CoverageMetric
      files: list = field(default_factory=list)  # [{"path":..., "metrics": CoverageMetric}]
  
  @dataclass
  class TestReport:
      project_name: str
      generated_at: str
      command: str
      framework: str
      framework_version: str
      env_summary: str
      suites: list  # list[TestSuite]
      coverage: Optional[CoverageSummary]
      source_files: list
      tool_version: str
      def summary(self) -> dict:
          total = sum(s.total for s in self.suites)
          passed = sum(s.passed for s in self.suites)
          failed = sum(s.failed for s in self.suites)
          skipped = sum(s.skipped for s in self.suites)
          time_sec = round(sum(s.time_sec for s in self.suites), 3)
          pass_rate = round(passed / total * 100, 2) if total else 0.0
          return {"total": total, "passed": passed, "failed": failed,
                  "skipped": skipped, "pass_rate": pass_rate, "time_sec": time_sec}
      def failures(self) -> list:
          return [c for s in self.suites for c in s.cases if c.is_failure()]
      def to_dict(self) -> dict:
          # P1 JSON 伴随产物预留：用 dataclasses.asdict 即可
          from dataclasses import asdict
          return asdict(self)
  
  @dataclass
  class ParseResult:
      report: Optional[TestReport]
      errors: list  # list[str] 降级信息
  ```

- [ ] 4. 运行测试，确认通过：
  ```bash
  cd skills/test-report && python3 -m pytest tests/test_models.py -x
  ```
  期望：`5 passed`。

**Verify:** `python3 -c "import sys; sys.path.insert(0,'skills/test-report/scripts'); import models; print(models.TestReport.__name__)"` 输出 `TestReport`。

**Commit:** 保存 `models.py` 与 `test_models.py` 即视为 Task 2 交付。

---

## Task 3: 框架检测与测试执行器

**Files:**
- Modify: `my-spring-boot-app/pom.xml`（新增 JaCoCo 插件配置，使 `mvn test` 同时产出 `jacoco.xml`）
- Create: `skills/test-report/scripts/framework_detect.py`
- Create: `skills/test-report/scripts/test_runner.py`
- Create: `skills/test-report/tests/test_framework_detect.py`
- Create: `skills/test-report/tests/test_runner_dryrun.py`

**Interfaces:**
- Consumes: `models.py`（Task 2）
- Produces:
  - `framework_detect.detect(repo_root) -> FrameworkInfo`
    - `FrameworkInfo`：`{name: "maven", command: ["mvn","test"], surefire_dir: "<repo>/my-spring-boot-app/target/surefire-reports", jacoco_xml: "<repo>/my-spring-boot-app/target/site/jacoco/jacoco.xml" or None, framework_version: str}`
    - 识别优先级（FR1.1）：① 用户显式指定命令（由 run.sh 传入，不在此函数）；② `pom.xml` 存在 → Maven；③ 特征文件推断（P0 仅 Maven，其它框架留 `name=None`）
  - `test_runner.run(repo_root, framework_info, coverage_mode="auto", timeout=None) -> RunResult`
    - `RunResult`：`{success: bool, surefire_files: list[Path], jacoco_xml: Path|None, diagnostics: list[str], elapsed_sec: float}`
    - 执行模式：调用 `mvn -q -f <pom> test`（受 R2，长任务由编排层后台执行；本函数为前台同步封装，编排层决定前后台）
    - `coverage_mode`：`auto`/`on`/`off`；`off` 时跳过 JaCoCo 解析路径
    - 执行失败（命令不可运行，FR1.4）时 `success=False` 且 `diagnostics` 含诊断信息，**不得**返回空报告冒充成功

**Why:** FR1.1（框架自动识别）、FR1.3（执行/解析双模式中的执行模式）、FR1.4（执行失败须诊断）、FR3 中"覆盖率 auto 模式"都依赖此 Task。JaCoCo 插件配置是 P0 覆盖率章节的数据来源前提，必须在此 Task 落地。

**Steps:**

- [ ] 1. 修改 `my-spring-boot-app/pom.xml`，在 `<build><plugins>` 内追加 JaCoCo 插件。**精确 patch（在 `</plugins>` 前插入）：**
  ```xml
              <!-- JaCoCo：为测试报告 Skill 提供覆盖率数据 -->
              <plugin>
                  <groupId>org.jacoco</groupId>
                  <artifactId>jacoco-maven-plugin</artifactId>
                  <version>0.8.11</version>
                  <executions>
                      <execution>
                          <id>prepare-agent</id>
                          <goals><goal>prepare-agent</goal></goals>
                      </execution>
                      <execution>
                          <id>report</id>
                          <phase>test</phase>
                          <goals><goal>report</goal></goals>
                      </execution>
                  </executions>
              </plugin>
  ```
  位置参考：现有 `</plugins>` 在文件末尾，紧邻 `<artifactId>spring-boot-maven-plugin</artifactId>` 那个 plugin 之后。

- [ ] 2. 写 `tests/test_framework_detect.py`（TDD）。**完整测试代码：**
  ```python
  import sys, os, tempfile
  sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'scripts'))
  from framework_detect import detect, FrameworkInfo
  
  def test_detect_maven(tmp_path=None):
      import tempfile, os
      d = tempfile.mkdtemp()
      os.makedirs(os.path.join(d, "my-spring-boot-app"), exist_ok=True)
      open(os.path.join(d, "my-spring-boot-app", "pom.xml"), "w").write("<project></project>")
      info = detect(d)
      assert isinstance(info, FrameworkInfo)
      assert info.name == "maven"
      assert info.command == ["mvn", "test"]
      assert info.surefire_dir.endswith("my-spring-boot-app/target/surefire-reports")
      assert info.jacoco_xml is not None  # pom 存在即预期 jacoco 路径
  
  def test_detect_unknown_returns_none_name():
      import tempfile, os
      d = tempfile.mkdtemp()  # 无 pom.xml
      info = detect(d)
      assert info.name is None
      assert info.diagnostics != []
  ```

- [ ] 3. 写 `scripts/framework_detect.py`。**完整代码：**
  ```python
  """框架检测：依据项目配置文件识别测试框架与运行命令（FR1.1）。仅标准库。"""
  import os
  from dataclasses import dataclass, field
  from typing import Optional, List
  
  @dataclass
  class FrameworkInfo:
      name: Optional[str]
      command: List[str] = field(default_factory=list)
      surefire_dir: Optional[str] = None
      jacoco_xml: Optional[str] = None
      framework_version: str = "未获取"
      pom_path: Optional[str] = None
      diagnostics: List[str] = field(default_factory=list)
  
  def detect(repo_root: str) -> FrameworkInfo:
      """识别优先级：pom.xml -> maven；其它框架 P0 不支持，返回 name=None 与诊断。"""
      pom = os.path.join(repo_root, "my-spring-boot-app", "pom.xml")
      if os.path.isfile(pom):
          surefire = os.path.join(repo_root, "my-spring-boot-app", "target", "surefire-reports")
          jacoco = os.path.join(repo_root, "my-spring-boot-app", "target", "site", "jacoco", "jacoco.xml")
          return FrameworkInfo(name="maven", command=["mvn", "test"],
                               surefire_dir=surefire, jacoco_xml=jacoco,
                               framework_version="maven-surefire", pom_path=pom)
      return FrameworkInfo(name=None, diagnostics=["未识别到支持的测试框架（P0 仅支持 Maven/pom.xml）"])
  ```

- [ ] 4. 运行框架检测测试：
  ```bash
  cd skills/test-report && python3 -m pytest tests/test_framework_detect.py -x
  ```
  期望：`2 passed`。

- [ ] 5. 写 `tests/test_runner_dryrun.py`（验证 FR1.4 诊断，不实际跑 mvn）。**完整测试代码：**
  ```python
  import sys, os, tempfile
  sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'scripts'))
  from framework_detect import FrameworkInfo
  from test_runner import run
  
  def test_run_failure_diagnostics_no_empty_report():
      """FR1.4：测试命令不可运行时须诊断，不得返回空成功报告。"""
      info = FrameworkInfo(name="maven", command=["mvn", "test"],
                          pom_path="/nonexistent/pom.xml")
      result = run(repo_root="/nonexistent", framework_info=info, coverage_mode="off")
      assert result.success is False
      assert len(result.diagnostics) > 0
      assert result.surefire_files == []   # 不得冒充成功
  ```

- [ ] 6. 写 `scripts/test_runner.py`。**完整代码：**
  ```python
  """测试执行器：执行模式触发 mvn test 并定位产物（FR1.3 执行模式、FR1.4 诊断）。"""
  import os, subprocess, glob, time
  from dataclasses import dataclass, field
  from typing import Optional, List
  from pathlib import Path
  
  @dataclass
  class RunResult:
      success: bool
      surefire_files: list = field(default_factory=list)
      jacoco_xml: Optional[str] = None
      diagnostics: list = field(default_factory=list)
      elapsed_sec: float = 0.0
  
  def run(repo_root: str, framework_info, coverage_mode: str = "auto",
          timeout: Optional[int] = None) -> RunResult:
      if framework_info.name != "maven" or not framework_info.pom_path:
          return RunResult(success=False, diagnostics=[
              "无法确定测试命令：未识别到 Maven 项目（pom.xml 缺失）"])
      if not os.path.isfile(framework_info.pom_path):
          return RunResult(success=False, diagnostics=[
              f"pom.xml 不存在：{framework_info.pom_path}"])
      cmd = ["mvn", "-q", "-f", framework_info.pom_path, "test"]
      t0 = time.time()
      try:
          proc = subprocess.run(cmd, cwd=repo_root,
                                capture_output=True, text=True,
                                timeout=timeout)
      except FileNotFoundError:
          return RunResult(success=False, diagnostics=[
              "mvn 命令未找到，请确认 Maven 已安装并加入 PATH"])
      except subprocess.TimeoutExpired:
          return RunResult(success=False, diagnostics=[
              f"测试执行超时（{timeout}s），请改用解析模式或延长 timeout"])
      elapsed = round(time.time() - t0, 2)
      if proc.returncode != 0:
          # 注意：mvn test 在有失败用例时也可能返回非 0，这是"用例失败"非"命令不可运行"。
          # 用例失败时 surefire 仍会产出 XML，属正常；此处仅当无任何 surefire 产物时视为执行失败。
          pass
      surefire_files = sorted(glob.glob(os.path.join(
          framework_info.surefire_dir or "", "TEST-*.xml")))
      if not surefire_files:
          return RunResult(success=False, diagnostics=[
              "测试执行未产出 Surefire XML（命令可能无法运行或构建失败）。"
              f" stdout 末尾: {proc.stdout[-300:] if proc.stdout else ''}"],
              elapsed_sec=elapsed)
      jacoco = framework_info.jacoco_xml if coverage_mode != "off" else None
      jacoco_exists = jacoco and os.path.isfile(jacoco)
      return RunResult(success=True, surefire_files=surefire_files,
                       jacoco_xml=jacoco if jacoco_exists else None,
                       elapsed_sec=elapsed)
  ```

- [ ] 7. 运行执行器 dryrun 测试：
  ```bash
  cd skills/test-report && python3 -m pytest tests/test_runner_dryrun.py -x
  ```
  期望：`1 passed`。

**Verify:**
- 单元测试全绿：`cd skills/test-report && python3 -m pytest tests/test_framework_detect.py tests/test_runner_dryrun.py -x`
- pom 合法性（若环境有 xmllint）：`xmllint --noout my-spring-boot-app/pom.xml && echo OK`

**降级说明（防阻塞协议）：** 若本环境无 `mvn`/JDK 17，实际 `mvn test` 不可运行——不影响本 Task 单元测试（dryrun 用 `/nonexistent` 路径）。端到端"执行模式"留待 Task 7 在具备构建环境时验证；当前以"静态代码审查 + 单测"覆盖。

**Commit:** 保存 pom 改造与两个脚本及测试即视为 Task 3 交付。

---

## Task 4: 解析器插件层

**Files:**
- Create: `skills/test-report/scripts/parsers/base.py`
- Create: `skills/test-report/scripts/parsers/junit_xml.py`
- Create: `skills/test-report/scripts/parsers/jacoco_xml.py`
- Create: `skills/test-report/tests/fixtures/junit_ok.xml`
- Create: `skills/test-report/tests/fixtures/junit_failed.xml`
- Create: `skills/test-report/tests/fixtures/junit_malformed.xml`
- Create: `skills/test-report/tests/fixtures/jacoco_sample.xml`
- Create: `skills/test-report/tests/test_junit_xml_parser.py`
- Create: `skills/test-report/tests/test_jacoco_parser.py`

**Interfaces:**
- Consumes: `models.py`（Task 2 的 `TestCase/TestSuite/TestReport/CoverageMetric/CoverageSummary/ParseResult/normalize_time`）
- Produces:
  - `parsers.base.Parser`（抽象基类）：`name: str`、`parse(path) -> ParseResult`；类级注册表 `PARSER_REGISTRY`，子类装饰器 `@register_parser(name)`
  - `parsers.junit_xml.JUnitXmlParser(Parser)`：name="junit-xml"，解析单个或目录下所有 `TEST-*.xml`
  - `parsers.jacoco_xml.JaCoCoXmlParser(Parser)`：name="jacoco-xml"，解析 `jacoco.xml` 返回 `CoverageSummary`

**Why:** NFR5（插件式可维护性）与 FR4.1（框架结果解析）的核心落点。JUnit XML 既是 Surefire 原生产物又是跨语言兜底（FR1.2），单解析器即可覆盖 Java/Maven。损坏文件场景（AC4）靠 `ParseResult.errors` 降级传递。

**Steps:**

- [ ] 1. 写测试夹具 `tests/fixtures/junit_ok.xml`（全通过）。**完整内容：**
  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <testsuite name="com.demo.FooTest" tests="2" skipped="0" failures="0" errors="0" time="0.05">
    <testcase name="testOk" classname="com.demo.FooTest" time="0.02"/>
    <testcase name="testOk2" classname="com.demo.FooTest" time="0.03"/>
  </testsuite>
  ```

- [ ] 2. 写 `tests/fixtures/junit_failed.xml`（含失败）。**完整内容：**
  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <testsuite name="com.demo.BarTest" tests="2" skipped="0" failures="1" errors="0" time="0.08">
    <testcase name="testPass" classname="com.demo.BarTest" time="0.01"/>
    <testcase name="testFail" classname="com.demo.BarTest" time="0.07" file="src/test/java/com/demo/BarTest.java" line="12">
      <failure message="expected: &lt;true&gt; but was: &lt;false&gt;">org.opentest4j.AssertionFailedError: expected: <true> but was: <false>
  \tat com.demo.BarTest.testFail(BarTest.java:12)
  </failure>
    </testcase>
  </testsuite>
  ```

- [ ] 3. 写 `tests/fixtures/junit_malformed.xml`（损坏）。**完整内容：**
  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <testsuite name="com.demo.BadTest" tests="1">
    <testcase name="testX" classname="com.demo.BadTest"
  <!-- 缺少闭合 -->
  ```

- [ ] 4. 写 `tests/fixtures/jacoco_sample.xml`（覆盖率）。**完整内容：**
  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <report name="JaCoCo Coverage Report">
    <counter type="INSTRUCTION" missed="10" covered="90"/>
    <counter type="BRANCH" missed="5" covered="15"/>
    <counter type="LINE" missed="4" covered="36"/>
    <counter type="METHOD" missed="2" covered="8"/>
    <counter type="COMPLEXITY" missed="3" covered="7"/>
    <package name="com/demo">
      <sourcefile name="Foo.java">
        <counter type="INSTRUCTION" missed="2" covered="18"/>
        <counter type="LINE" missed="1" covered="9"/>
      </sourcefile>
    </package>
  </report>
  ```

- [ ] 5. 写 `tests/test_junit_xml_parser.py`（TDD）。**完整测试代码：**
  ```python
  import sys, os
  sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'scripts'))
  from parsers.junit_xml import JUnitXmlParser
  FX = os.path.join(os.path.dirname(__file__), 'fixtures')
  
  def test_parse_ok():
      pr = JUnitXmlParser().parse(os.path.join(FX, 'junit_ok.xml'))
      assert pr.report is not None and not pr.errors
      assert pr.report.summary() == {"total":2,"passed":2,"failed":0,"skipped":0,
                                     "pass_rate":100.0,"time_sec":0.05}
  
  def test_parse_failed_has_failure_analysis():
      pr = JUnitXmlParser().parse(os.path.join(FX, 'junit_failed.xml'))
      fs = pr.report.failures()
      assert len(fs) == 1 and fs[0].name == "testFail"
      assert fs[0].file == "src/test/java/com/demo/BarTest.java"
      assert fs[0].line == 12
      assert fs[0].error_message is not None and "expected" in fs[0].error_message
      assert fs[0].stack_trace is not None and "BarTest.java:12" in fs[0].stack_trace
  
  def test_parse_malformed_degrades_no_crash():
      pr = JUnitXmlParser().parse(os.path.join(FX, 'junit_malformed.xml'))
      # NFR2：损坏不得崩溃；降级为 errors，report 可为空或部分
      assert pr.errors != []
  ```

- [ ] 6. 写 `scripts/parsers/base.py`（插件基类与注册表）。**完整代码：**
  ```python
  """解析器插件基类与注册表（NFR5：新增框架不影响既有解析器）。"""
  from dataclasses import dataclass, field
  PARSER_REGISTRY = {}
  
  def register_parser(name):
      def deco(cls):
          cls.name = name
          PARSER_REGISTRY[name] = cls
          return cls
      return deco
  
  class Parser:
      name = None
      def parse(self, path):
          raise NotImplementedError
  ```

- [ ] 7. 写 `scripts/parsers/junit_xml.py`。**完整代码：**
  ```python
  """JUnit XML 解析器（Surefire 原生产物 + 跨语言兜底）。仅标准库。"""
  import os, glob
  import xml.etree.ElementTree as ET
  from models import (TestCase, TestSuite, TestReport, CoverageMetric,
                      CoverageSummary, ParseResult, normalize_time)
  from parsers.base import Parser, register_parser
  
  STACK_MAX_LINES = 20
  STACK_MAX_CHARS = 800
  
  @register_parser("junit-xml")
  class JUnitXmlParser(Parser):
      def parse(self, path) -> ParseResult:
          errors = []
          files = []
          if os.path.isdir(path):
              files = sorted(glob.glob(os.path.join(path, "TEST-*.xml")))
          elif os.path.isfile(path):
              files = [path]
          if not files:
              return ParseResult(None, [f"未找到 JUnit XML 文件：{path}"])
          suites = []
          source_files = []
          for f in files:
              source_files.append(f)
              try:
                  root = ET.parse(f).getroot()
              except ET.ParseError as e:
                  errors.append(f"解析失败 {f}: {e}（已跳过该文件，降级处理）")
                  continue
              # 兼容 <testsuites> 包裹与裸 <testsuite>
              suite_nodes = root if root.tag == "testsuite" else root.findall("testsuite")
              if root.tag == "testsuites" and not suite_nodes:
                  suite_nodes = []
              for snode in suite_nodes:
                  suites.append(self._parse_suite(snode))
          if not suites:
              return ParseResult(None, errors or ["未解析到任何 testsuite"])
          rep = TestReport(
              project_name=os.path.basename(os.getcwd()),
              generated_at="", command="", framework="junit-xml",
              framework_version="未获取", env_summary="未获取",
              suites=suites, coverage=None,
              source_files=source_files, tool_version="test-report 1.0")
          return ParseResult(rep, errors)
  
      def _parse_suite(self, snode):
          name = snode.get("name", "未获取")
          cases = []
          for tc in snode.findall("testcase"):
              cases.append(self._parse_case(tc))
          return TestSuite(name=name, cases=cases)
  
      def _parse_case(self, tc):
          classname = tc.get("classname", "未获取")
          cname = tc.get("name", "未获取")
          time_sec = normalize_time(tc.get("time"))
          file = tc.get("file")
          line = tc.get("line")
          line = int(line) if line and line.isdigit() else None
          status, err, stack = "passed", None, None
          fail = tc.find("failure")
          err_node = tc.find("error")
          skip = tc.find("skipped")
          if fail is not None:
              status = "failed"
              err = fail.get("message") or (fail.text or "").strip() or None
              stack = self._truncate((fail.text or "").strip())
          elif err_node is not None:
              status = "error"
              err = err_node.get("message") or (err_node.text or "").strip() or None
              stack = self._truncate((err_node.text or "").strip())
          elif skip is not None:
              status = "skipped"
          return TestCase(name=cname, classname=classname, status=status,
                          time_sec=time_sec, file=file, line=line,
                          error_message=err, stack_trace=stack)
  
      @staticmethod
      def _truncate(text):
          if not text: return None
          lines = text.splitlines()
          if len(lines) > STACK_MAX_LINES:
              lines = lines[:STACK_MAX_LINES] + ["...(堆栈已截断)"]
          out = "\n".join(lines)
          return out[:STACK_MAX_CHARS] + "...(已截断)" if len(out) > STACK_MAX_CHARS else out
  ```

- [ ] 8. 写 `tests/test_jacoco_parser.py`（TDD）。**完整测试代码：**
  ```python
  import sys, os
  sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'scripts'))
  from parsers.jacoco_xml import JaCoCoXmlParser
  FX = os.path.join(os.path.dirname(__file__), 'fixtures')
  
  def test_parse_coverage_summary():
      cs = JaCoCoXmlParser().parse(os.path.join(FX, 'jacoco_sample.xml'))
      # INSTRUCTION 10/90 -> 90%
      assert cs.metrics.instruction == 90.0
      assert cs.metrics.branch == 75.0   # 15/(5+15)
      assert cs.metrics.line == 90.0     # 36/(4+36)
      assert cs.metrics.method == 80.0   # 8/(2+8)
      assert len(cs.files) == 1
      assert cs.files[0]["path"].endswith("Foo.java")
  
  def test_parse_missing_returns_none_with_diag():
      pr = JaCoCoXmlParser().parse("/nonexistent/jacoco.xml")
      assert pr is None  # 文件不存在时返回 None（由编排层标注"未获取"）
  ```

- [ ] 9. 写 `scripts/parsers/jacoco_xml.py`。**完整代码：**
  ```python
  """JaCoCo 覆盖率 XML 解析器。仅标准库。"""
  import os
  import xml.etree.ElementTree as ET
  from models import CoverageMetric, CoverageSummary
  from parsers.base import Parser, register_parser
  
  COUNTER_TYPES = ["INSTRUCTION", "BRANCH", "LINE", "METHOD", "COMPLEXITY"]
  FIELD_MAP = {"INSTRUCTION":"instruction","BRANCH":"branch","LINE":"line",
               "METHOD":"method","COMPLEXITY":"complexity"}
  
  @register_parser("jacoco-xml")
  class JaCoCoXmlParser(Parser):
      def parse(self, path):
          if not path or not os.path.isfile(path):
              return None
          try:
              root = ET.parse(path).getroot()
          except ET.ParseError:
              return None
          metrics = self._counters_to_metric(root.findall("counter"))
          files = []
          for pkg in root.findall("package"):
              pkg_name = pkg.get("name", "")
              for sf in pkg.findall("sourcefile"):
                  fm = self._counters_to_metric(sf.findall("counter"))
                  files.append({"path": os.path.join(pkg_name.replace("/", os.sep), sf.get("name","")), "metrics": fm})
          return CoverageSummary(metrics=metrics, files=files)
  
      @staticmethod
      def _counters_to_metric(counters):
          kw = {}
          for c in counters:
              t = c.get("type")
              if t not in FIELD_MAP: continue
              missed = float(c.get("missed","0") or 0)
              covered = float(c.get("covered","0") or 0)
              total = missed + covered
              kw[FIELD_MAP[t]] = round(covered/total*100, 2) if total else 0.0
          return CoverageMetric(**kw)
  ```

- [ ] 10. 运行解析器全部测试：
  ```bash
  cd skills/test-report && python3 -m pytest tests/test_junit_xml_parser.py tests/test_jacoco_parser.py -x
  ```
  期望：`5 passed`（junit 3 + jacoco 2）。

**Verify:** `python3 -m pytest tests/ -x` 全绿（覆盖 Task 2-4）。

**Commit:** 保存解析器三文件、四个 fixture 与两测试文件即视为 Task 4 交付。

---

## Task 5: 报告生成器（中文 Markdown 四板块）

**Files:**
- Create: `skills/test-report/scripts/generate_report.py`
- Create: `skills/test-report/tests/test_report_generator.py`

**Interfaces:**
- Consumes: `models.py` 的 `TestReport`/`CoverageSummary`/`CoverageMetric`/`TestCase`
- Produces:
  - `generate_report(report: TestReport, fail_threshold=None, max_cases=200) -> str`：返回完整 Markdown 字符串
  - 内部子函数：`_header() / _summary() / _failures() / _details() / _coverage() / _appendix()`，对应 FR2 固定章节顺序
  - `sanitize_secret(text) -> str`：NFR3 敏感词过滤（环境变量值、token、key、password 等）

**Why:** FR2（报告固定六章节）、FR3.1（默认 Markdown）、NFR3（不泄露凭据）、NFR4（幂等——同输入同输出，时间戳除外）、AC1（结构符合且摘要与原始一致）、AC5（覆盖率缺失标注"未获取"）。报告生成器只吃 `TestReport`，与框架无关，保证换框架不换渲染逻辑。

**章节固定顺序（FR2）：**
1. 报告头 → 2. 结果摘要 → 3. 失败用例分析（有失败时）→ 4. 用例明细 → 5. 覆盖率 → 6. 附录

**Steps:**

- [ ] 1. 写 `tests/test_report_generator.py`（TDD）。**完整测试代码：**
  ```python
  import sys, os
  sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'scripts'))
  from models import (TestCase, TestSuite, TestReport, CoverageMetric, CoverageSummary)
  from generate_report import generate_report, sanitize_secret
  
  def _ok_report(coverage=None):
      s = TestSuite("com.demo.FooTest", [
          TestCase("testOk","com.demo.FooTest","passed",0.02)])
      return TestReport("demo","2026-07-20T06:00:00","mvn test","maven-surefire",
                       "3.0.0","JDK 17 / Linux",[s],coverage,["a.xml"],"1.0")
  
  def test_chapter_order_and_summary():
      md = generate_report(_ok_report())
      assert md.index("# 测试报告") >= 0
      assert md.index("## 1. 报告头") < md.index("## 2. 结果摘要")
      assert md.index("## 2. 结果摘要") < md.index("## 4. 用例明细")
      assert md.index("## 4. 用例明细") < md.index("## 5. 覆盖率")
      assert md.index("## 5. 覆盖率") < md.index("## 6. 附录")
      assert "100.0%" in md and "✅" in md
  
  def test_failures_section_present_when_failed():
      s = TestSuite("B",[
          TestCase("p","B","passed",0.01),
          TestCase("f","B","failed",0.07,file="src/Foo.java",line=3,
                   error_message="boom",stack_trace="at Foo.bar(Foo.java:3)")])
      r = TestReport("d","","mvn test","maven-surefire","3.0.0","E",[s],None,["x.xml"],"1.0")
      md = generate_report(r)
      assert "## 3. 失败用例分析" in md
      assert "f" in md and "src/Foo.java" in md and "boom" in md
      assert "❌" in md
  
  def test_fail_threshold_marks_unqualified():
      r = _ok_report()  # 通过率 100% 但阈值设 120 强制不达标
      md = generate_report(r, fail_threshold=120)
      assert "不达标" in md
  
  def test_coverage_missing_marks_unfetched():
      md = generate_report(_ok_report(coverage=None))
      assert "未获取" in md and "## 5. 覆盖率" in md
  
  def test_coverage_present_table():
      cs = CoverageSummary(metrics=CoverageMetric(instruction=90.0,line=90.0),
                            files=[{"path":"Foo.java","metrics":CoverageMetric(line=50.0)}])
      md = generate_report(_ok_report(coverage=cs))
      assert "| 语句覆盖率" in md and "90.0%" in md
      assert "低于阈值的文件" in md or "Foo.java" in md
  
  def test_idempotent_except_timestamp():
      r = _ok_report()
      a = generate_report(r); b = generate_report(r)
      assert a == b  # 同输入同输出（NFR4）
  
  def test_secret_sanitization():
      assert "REDACTED" in sanitize_secret("token=sk-1234567890abcdef")
      assert "REDACTED" in sanitize_secret("password=p@ssw0rd")
      assert "REDACTED" in sanitize_secret("API_KEY=AKIAxxxxxxxx")
  
  def test_details_truncate_over_200():
      cases = [TestCase(f"t{i}","C","passed",0.001) for i in range(201)]
      r = TestReport("d","","mvn","maven-surefire","3.0.0","E",
                     [TestSuite("S",cases)],None,["x.xml"],"1.0")
      md = generate_report(r)
      assert "截断" in md and "201" in md
  ```

- [ ] 2. 运行测试，确认失败：
  ```bash
  cd skills/test-report && python3 -m pytest tests/test_report_generator.py -x
  ```
  期望：`ModuleNotFoundError: No module named 'generate_report'`。

- [ ] 3. 写 `scripts/generate_report.py`。**完整代码：**
  ```python
  """测试报告生成器：TestReport -> 中文 Markdown（FR2 固定章节、NFR3 安全、NFR4 幂等）。仅标准库。"""
  import re
  from datetime import datetime
  from models import TestReport
  
  MAX_CASES = 200
  SECRET_PATTERNS = [
      (re.compile(r"(token|password|passwd|secret|api[_-]?key|access[_-]?key|private[_-]?key)\s*[:=]\s*\S+", re.I), r"\1=REDACTED"),
  ]
  
  def sanitize_secret(text):
      if not text: return text or ""
      out = str(text)
      for pat, repl in SECRET_PATTERNS:
          out = pat.sub(repl, out)
      return out
  
  def generate_report(report: TestReport, fail_threshold=None, max_cases=MAX_CASES) -> str:
      parts = []
      parts.append("# 测试报告\n")
      parts.append(_header(report))
      parts.append(_summary(report, fail_threshold))
      if report.failures():
          parts.append(_failures(report))
      parts.append(_details(report, max_cases))
      parts.append(_coverage(report))
      parts.append(_appendix(report))
      return "\n".join(parts)
  
  def _header(r: TestReport) -> str:
      return (f"## 1. 报告头\n\n"
              f"- 项目名：{r.project_name}\n"
              f"- 生成时间：{r.generated_at or '未获取'}\n"
              f"- 执行命令：`{r.command or '未获取'}`\n"
              f"- 框架/版本：{r.framework} / {r.framework_version}\n"
              f"- 执行环境摘要：{sanitize_secret(r.env_summary) or '未获取'}\n")
  
  def _summary(r: TestReport, fail_threshold) -> str:
      s = r.summary()
      ok = s["failed"] == 0
      verdict = "✅ 通过" if ok else "❌ 失败"
      if fail_threshold is not None and s["pass_rate"] < fail_threshold:
          verdict += "（不达标：通过率低于阈值）"
      return (f"## 2. 结果摘要\n\n"
              f"| 指标 | 值 |\n|---|---|\n"
              f"| 用例总数 | {s['total']} |\n"
              f"| 通过 | {s['passed']} |\n"
              f"| 失败 | {s['failed']} |\n"
              f"| 跳过 | {s['skipped']} |\n"
              f"| 通过率 | {s['pass_rate']}% |\n"
              f"| 总耗时 | {s['time_sec']} s |\n"
              f"| 整体结论 | {verdict} |\n")
  
  def _failures(r: TestReport) -> str:
      lines = ["## 3. 失败用例分析\n"]
      for c in r.failures():
          loc = f"{c.file}:{c.line}" if c.file else (c.classname or "未获取")
          lines.append(f"### {c.name}\n")
          lines.append(f"- 所属：`{c.classname}`")
          lines.append(f"- 定位：`{loc}`")
          lines.append(f"- 错误信息：{sanitize_secret(c.error_message) or '未获取'}")
          stack = c.stack_trace or "未获取"
          lines.append("- 堆栈关键行：\n```\n" + sanitize_secret(stack) + "\n```\n")
      return "\n".join(lines) + "\n"
  
  def _details(r: TestReport, max_cases) -> str:
      lines = ["## 4. 用例明细\n"]
      total_cases = sum(s.total for s in r.suites)
      for s in r.suites:
          lines.append(f"\n### {s.name}（{s.total} 条，耗时 {s.time_sec}s）\n")
          shown = s.cases[:max_cases] if total_cases > max_cases else s.cases
          lines.append("| 用例 | 状态 | 耗时(s) |")
          lines.append("|---|---|---|")
          for c in shown:
              lines.append(f"| {c.name} | {c.status} | {c.time_sec} |")
          if total_cases > max_cases:
              lines.append(f"\n> ⚠️ 用例数 {total_cases} 超过 {max_cases}，已截断展示。\n")
      return "\n".join(lines) + "\n"
  
  def _coverage(r: TestReport) -> str:
      if not r.coverage:
          return "## 5. 覆盖率\n\n> 未获取\n"
      m = r.coverage.metrics
      rows = [("语句覆盖率", m.instruction), ("分支覆盖率", m.branch),
              ("行覆盖率", m.line), ("函数覆盖率", m.method),
              ("复杂度覆盖率", m.complexity)]
      lines = ["## 5. 覆盖率\n", "| 指标 | 值 |", "|---|---|"]
      for label, v in rows:
          lines.append(f"| {label} | {f'{v}%' if v is not None else '未获取'} |")
      low = [f for f in r.coverage.files if any(
          getattr(f['metrics'], k, None) is not None and getattr(f['metrics'], k, None) < 80
          for k in ['instruction','branch','line','method'])]
      lines.append("\n### 低于阈值的文件清单（阈值 80%）\n")
      if low:
          for f in low:
              lines.append(f"- `{f['path']}`")
      else:
          lines.append("- 无")
      return "\n".join(lines) + "\n"
  
  def _appendix(r: TestReport) -> str:
      lines = ["## 6. 附录\n", "### 原始结果文件路径"]
      for f in r.source_files:
          lines.append(f"- `{f}`")
      lines.append(f"\n### 生成工具版本：{r.tool_version}")
      return "\n".join(lines) + "\n"
  
  if __name__ == "__main__":
      import sys, os, json
      # CLI：从 stdin 读 JSON(TestReport.to_dict) 或直接消费 pickle；P0 由 run.sh 调用
      sys.path.insert(0, os.path.dirname(__file__))
  ```

- [ ] 4. 运行测试，确认通过：
  ```bash
  cd skills/test-report && python3 -m pytest tests/test_report_generator.py -x
  ```
  期望：`8 passed`。

- [ ] 5. 幂等性专项校验（NFR4）：
  ```bash
  python3 -c "
  import sys; sys.path.insert(0,'skills/test-report/scripts')
  from models import TestCase,TestSuite,TestReport
  from generate_report import generate_report
  s=TestSuite('A',[TestCase('t','A','passed',0.1)])
  r=TestReport('d','2026-07-20T06:00:00','mvn','m','3.0.0','E',[s],None,['x'],'1.0')
  assert generate_report(r)==generate_report(r); print('IDEMPOTENT OK')
  "
  ```
  期望输出：`IDEMPOTENT OK`。

**Verify:** 全部报告生成器测试绿 + 幂等校验 OK。

**Commit:** 保存 `generate_report.py` 与 `test_report_generator.py` 即视为 Task 5 交付。

---

## Task 6: 编排脚本（双模式编排）

**Files:**
- Create: `skills/test-report/scripts/run.sh`
- Create: `skills/test-report/scripts/runner_cli.py`（run.sh 调用的 Python 入口，承载执行/解析双模式编排逻辑）

**Interfaces:**
- Consumes: Task 2-5 全部产物（`framework_detect`、`test_runner`、`parsers.junit_xml`、`parsers.jacoco_xml`、`generate_report`、`models`）
- Produces:
  - `run.sh`：命令行入口，参数 `--parse <path>` / `--output <dir>` / `--coverage auto|on|off` / `--fail-threshold <n>` / `--command "<cmd>"`
  - `runner_cli.main(argv) -> int`：编排核心，返回退出码（0 成功、2 执行失败诊断、3 解析失败降级）
  - 默认产物：`reports/test-report-<YYYYMMDD-HHmmss>.md`

**Why:** FR1.3（执行/解析双模式）、FR3.2（默认路径与用户可覆盖）、FR3.3（生成后返回报告路径+摘要+1~3 条失败原因）、R2（长任务后台执行与轮询）。编排层是唯一知道"跑/不跑"和"产物在哪"的组件，解析器与生成器保持纯净。

**Steps:**

- [ ] 1. 写 `scripts/runner_cli.py`。**完整代码：**
  ```python
  """编排入口：执行/解析双模式 -> 解析器 -> 报告生成器 -> 落盘并返回摘要。"""
  import sys, os, argparse
  from datetime import datetime
  sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
  from models import ParseResult
  from framework_detect import detect, FrameworkInfo
  import test_runner
  from parsers.junit_xml import JUnitXmlParser
  from parsers.jacoco_xml import JaCoCoXmlParser
  from generate_report import generate_report
  
  def build_report(surefire_files, jacoco_xml, command, framework, env_summary, errors, coverage_mode):
      """从产物路径构建 TestReport，返回 ParseResult。"""
      pr = ParseResult(None, list(errors))
      if not surefire_files:
          pr.errors.append("无 Surefire/JUnit XML 产物可解析")
          return pr
      # 合并多个 surefire 文件为一个目录解析
      parser = JUnitXmlParser()
      merged = parser.parse(surefire_files[0])
      for f in surefire_files[1:]:
          nxt = parser.parse(f)
          if nxt.report:
              merged.report.suites.extend(nxt.report.suites) if merged.report else None
              merged.report = merged.report or nxt.report
          merged.errors.extend(nxt.errors)
      if not merged.report:
          return merged
      merged.report.command = command
      merged.report.framework = framework
      merged.report.env_summary = env_summary
      merged.report.generated_at = datetime.now().strftime("%Y-%m-%dT%H:%M:%S")
      if coverage_mode != "off" and jacoco_xml:
          cs = JaCoCoXmlParser().parse(jacoco_xml)
          if cs:
              merged.report.coverage = cs
          else:
              merged.errors.append("覆盖率文件存在但解析为空，标注为未获取")
      return merged
  
  def main(argv=None) -> int:
      ap = argparse.ArgumentParser()
      ap.add_argument("--parse", help="解析模式：已有结果文件/目录路径")
      ap.add_argument("--output", default="reports", help="报告输出目录")
      ap.add_argument("--coverage", default="auto", choices=["auto","on","off"])
      ap.add_argument("--fail-threshold", type=float, default=None)
      ap.add_argument("--command", default=None, help="覆盖自动检测的测试命令")
      ap.add_argument("--timeout", type=int, default=None)
      args = ap.parse_args(argv)
      repo_root = os.getcwd()
      errors = []
      surefire_files, jacoco_xml, command, framework, env_summary = [], None, "", "", ""
      if args.parse:
          # 解析模式（FR1.3 / US4）：不触发测试执行
          target = args.parse
          if os.path.isdir(target):
              import glob
              surefire_files = sorted(glob.glob(os.path.join(target, "TEST-*.xml"))) or [target]
          else:
              surefire_files = [target]
          command = "(解析模式)"
          framework = "junit-xml"
          env_summary = "未获取"
      else:
          # 执行模式
          info = detect(repo_root)
          if info.name is None:
              print("错误：" + "; ".join(info.diagnostics), file=sys.stderr)
              return 2
          if args.command:
              info.command = args.command.split()
          result = test_runner.run(repo_root, info, coverage_mode=args.coverage,
                                   timeout=args.timeout)
          if not result.success:
              print("错误（测试执行失败）：" + "; ".join(result.diagnostics), file=sys.stderr)
              return 2
          surefire_files = result.surefire_files
          jacoco_xml = result.jacoco_xml
          command = " ".join(info.command)
          framework = info.framework_version
          env_summary = f"JDK/Maven（cwd={repo_root}）"
      pr = build_report(surefire_files, jacoco_xml, command, framework, env_summary, errors, args.coverage)
      if not pr.report:
          print("错误（解析失败，拒绝生成空报告）：" + "; ".join(pr.errors), file=sys.stderr)
          return 3
      md = generate_report(pr.report, fail_threshold=args.fail_threshold)
      os.makedirs(args.output, exist_ok=True)
      ts = datetime.now().strftime("%Y%m%d-%H%M%S")
      out_path = os.path.join(args.output, f"test-report-{ts}.md")
      with open(out_path, "w", encoding="utf-8") as f:
          f.write(md)
      # FR3.3：返回报告路径 + 摘要 + 1~3 条失败原因
      s = pr.report.summary()
      print(f"报告已生成：{out_path}")
      print(f"摘要：通过率 {s['pass_rate']}% | 通过 {s['passed']} | 失败 {s['failed']} | 跳过 {s['skipped']}")
      if pr.report.failures():
          for c in pr.report.failures()[:3]:
              print(f"失败：{c.name} - {c.error_message or '无错误信息'}")
      if pr.errors:
          print("降级提示：" + "; ".join(pr.errors))
      return 0
  
  if __name__ == "__main__":
      sys.exit(main())
  ```

- [ ] 2. 写 `scripts/run.sh`。**完整内容：**
  ```bash
  #!/usr/bin/env bash
  # 测试报告 Skill 编排入口：双模式（执行/解析）
  set -euo pipefail
  HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
  exec python3 "${HERE}/runner_cli.py" "$@"
  ```
  并赋予可执行：`chmod +x skills/test-report/scripts/run.sh`

- [ ] 3. 解析模式端到端（用 Task 4 的 fixture，无需 mvn/JDK）：
  ```bash
  bash skills/test-report/scripts/run.sh --parse skills/test-report/tests/fixtures/junit_failed.xml --output /tmp/tr-reports
  ```
  期望：退出码 0，输出含"报告已生成"、摘要含"通过率 50.0%"、"失败：testFail"。生成文件含"## 3. 失败用例分析"。

- [ ] 4. 损坏文件场景（AC4）端到端：
  ```bash
  bash skills/test-report/scripts/run.sh --parse skills/test-report/tests/fixtures/junit_malformed.xml --output /tmp/tr-reports; echo "exit=$?"
  ```
  期望：退出码 3，stderr 含"解析失败，拒绝生成空报告"，**不**生成报告文件。

- [ ] 5. 执行模式诊断（无 mvn 环境）端到端：
  ```bash
  bash skills/test-report/scripts/run.sh --output /tmp/tr-reports --timeout 5; echo "exit=$?"
  ```
  期望：退出码 2，stderr 含诊断信息（mvn 不可用或无 surefire 产物）。

**Verify:**
- `bash skills/test-report/scripts/run.sh --parse skills/test-report/tests/fixtures/junit_ok.xml --output /tmp/tr-reports` 退出 0 且报告含"## 1. 报告头"..."## 6. 附录"全章节。
- 全单元测试仍绿：`cd skills/test-report && python3 -m pytest tests/ -x`

**Commit:** 保存 `runner_cli.py` 与 `run.sh` 即视为 Task 6 交付。

---

## Task 7: 集成验收与 AC 对齐

**Files:**
- Create: `skills/test-report/tests/test_integration_ac.py`（AC1-AC5 端到端断言，基于 fixture，不依赖 mvn/JDK）
- 无新增生产代码：本 Task 仅用 Task 1-6 已有能力组合验证

**Interfaces:**
- Consumes: Task 6 的 `run.sh --parse` 与 Task 4 fixture
- Produces: 覆盖 AC1-AC5 的集成测试，证明计划交付物满足需求 §6 全部验收标准

**Why:** 需求 §6 的 AC1-AC5 必须有可执行断言，而非仅文字描述。解析模式 + fixture 可在无 mvn/JDK 环境下覆盖 AC1/AC2/AC3/AC4/AC5，执行模式端到端（AC1 的"执行模式"变体）依赖构建环境，列入"降级"项。

**Steps:**

- [ ] 1. 写 `tests/test_integration_ac.py`。**完整测试代码（用 subprocess 调 run.sh，断言退出码与报告内容）：**
  ```python
  import sys, os, subprocess, glob, tempfile, shutil
  HERE = os.path.dirname(__file__)
  RUN = os.path.join(HERE, '..', 'scripts', 'run.sh')
  FX = os.path.join(HERE, 'fixtures')
  
  def _run(args, out_dir):
      return subprocess.run(['bash', RUN] + args + ['--output', out_dir],
                            capture_output=True, text=True)
  
  def test_AC1_structure_and_summary_consistent():
      """AC1：解析模式产出符合 §4.2 结构的报告，摘要与原始输出一致。"""
      d = tempfile.mkdtemp()
      r = _run(['--parse', os.path.join(FX,'junit_ok.xml')], d)
      assert r.returncode == 0, r.stderr
      md = open(glob.glob(os.path.join(d,'test-report-*.md'))[0], encoding='utf-8').read()
      for sec in ["## 1. 报告头","## 2. 结果摘要","## 4. 用例明细",
                  "## 5. 覆盖率","## 6. 附录"]:
          assert sec in md, f"缺失章节 {sec}"
      assert "100.0%" in md and "通过 | 2" in md
      shutil.rmtree(d, ignore_errors=True)
  
  def test_AC2_failure_analysis_section():
      """AC2：失败用例报告含用例名、文件路径、错误信息。"""
      d = tempfile.mkdtemp()
      r = _run(['--parse', os.path.join(FX,'junit_failed.xml')], d)
      assert r.returncode == 0
      md = open(glob.glob(os.path.join(d,'test-report-*.md'))[0], encoding='utf-8').read()
      assert "## 3. 失败用例分析" in md
      assert "testFail" in md and "src/test/java/com/demo/BarTest.java" in md
      assert "expected" in md
      assert r.stdout.count("失败") >= 1  # FR3.3 失败原因返回
      shutil.rmtree(d, ignore_errors=True)
  
  def test_AC3_parse_mode_no_execution():
      """AC3：提供 JUnit XML 走解析模式，不触发测试执行即可产出报告。"""
      d = tempfile.mkdtemp()
      r = _run(['--parse', os.path.join(FX,'junit_ok.xml')], d)
      assert r.returncode == 0
      assert os.path.isfile(glob.glob(os.path.join(d,'test-report-*.md'))[0])
      assert "mvn" not in r.stdout or "(解析模式)" in r.stdout
      shutil.rmtree(d, ignore_errors=True)
  
  def test_AC4_corrupt_file_returns_error_no_empty_report():
      """AC4：结果文件损坏时返回明确错误，不生成空报告。"""
      d = tempfile.mkdtemp()
      r = _run(['--parse', os.path.join(FX,'junit_malformed.xml')], d)
      assert r.returncode == 3, r.stderr
      assert "解析失败" in r.stderr or "拒绝生成空报告" in r.stderr
      assert glob.glob(os.path.join(d,'test-report-*.md')) == []
      shutil.rmtree(d, ignore_errors=True)
  
  def test_AC5_coverage_present_and_missing():
      """AC5：覆盖率数据存在时正确呈现；不存在时标注'未获取'且其余章节正常。"""
      # 存在：构造含 coverage 的报告走生成器（不经 run.sh，因 fixture 无联动）
      sys.path.insert(0, os.path.join(HERE,'..','scripts'))
      from models import TestCase,TestSuite,TestReport,CoverageMetric,CoverageSummary
      from generate_report import generate_report
      cs = CoverageSummary(metrics=CoverageMetric(instruction=90.0,line=90.0),
                          files=[{"path":"Foo.java","metrics":CoverageMetric(line=50.0)}])
      r = TestReport("d","","mvn","m","3.0.0","E",
                     [TestSuite("S",[TestCase("t","S","passed",0.01)])],cs,["x"],"1.0")
      md = generate_report(r)
      assert "90.0%" in md and "Foo.java" in md
      # 不存在
      r2 = TestReport("d","","mvn","m","3.0.0","E",
                      [TestSuite("S",[TestCase("t","S","passed",0.01)])],None,["x"],"1.0")
      md2 = generate_report(r2)
      assert "未获取" in md2 and "## 2. 结果摘要" in md2  # 其余章节正常
  ```

- [ ] 2. 运行集成验收：
  ```bash
  cd skills/test-report && python3 -m pytest tests/test_integration_ac.py -x
  ```
  期望：`5 passed`。

- [ ] 3. 全量回归（仅变更范围，非全量构建）：
  ```bash
  cd skills/test-report && python3 -m pytest tests/ -x
  ```
  期望：全部测试通过（models + framework_detect + runner_dryrun + junit/jacoco parser + report generator + integration AC）。

**AC 对齐表（需求 §6 ↔ 本计划 Task）：**

| 验收标准 | 需求要点 | 实现于 | 验证方式 |
|---|---|---|---|
| AC1 | 结构符合 §4.2、摘要与原始一致 | Task 5（生成器）+ Task 6（编排） | `test_AC1_structure_and_summary_consistent` |
| AC2 | 失败用例含名/文件/错误信息 | Task 4（解析器 `_parse_case`）+ Task 5（`_failures`） | `test_AC2_failure_analysis_section` |
| AC3 | 解析模式不跑测试即出报告 | Task 6（`--parse` 分支） | `test_AC3_parse_mode_no_execution` |
| AC4 | 损坏文件返回错误非空报告 | Task 4（`ParseResult.errors`）+ Task 6（退出码 3） | `test_AC4_corrupt_file_returns_error_no_empty_report` |
| AC5 | 覆盖率存在则呈现、缺失标注未获取 | Task 4（JaCoCo 解析）+ Task 5（`_coverage`） | `test_AC5_coverage_present_and_missing` |

**降级说明（执行模式端到端）：** AC1 的"在含 Jest/Vitest 的 TS 项目中执行"变体在本仓库为 Maven 项目；执行模式 `mvn test` 端到端需 JDK 17 + Maven 可用。若本环境不具备构建能力，按防阻塞协议降级为"解析模式端到端 + 静态审查"，覆盖 FR1.3 双模式逻辑分支、FR1.4 诊断分支、字段一致性。已在 Task 3/6 降级说明中标注。

**Commit:** 保存 `test_integration_ac.py` 即视为 Task 7 交付。

---

## Task 8: 文档与自检

**Files:**
- Modify: `skills/test-report/README.md`（补充配置项表与触发示例，Task 1 已写骨架，此处补全 §4.4 配置项与产物路径说明）
- 无新增测试：本 Task 为文档与计划级 self-review

**Why:** FR4.2 配置项须在 README/SKILL.md 中可查；writing-plans 技能要求计划完成后做 self-review（spec 覆盖、占位符扫描、关键风险）。

**Steps:**

- [ ] 1. 补全 `skills/test-report/README.md` 配置项与产物路径段落（在已有内容后追加）：
  ```markdown
  
  ## 配置项（FR4.2）
  
  | 配置项 | 默认值 | 说明 |
  |---|---|---|
  | test_command | auto | 测试执行命令，auto 表示自动检测（Maven） |
  | result_file | auto | 解析模式下的结果文件/目录路径 |
  | output_format | markdown | markdown（P0）；html/json 列为 P1 |
  | output_path | reports/ | 报告输出目录 |
  | coverage | auto | auto/on/off |
  | fail_threshold | 无 | 通过率低于该值时结论标记为不达标 |
  
  ## 产物路径
  
  - 默认：`reports/test-report-<YYYYMMDD-HHmmss>.md`
  - 解析模式不触发 `mvn test`，适合 CI 复用已有 JUnit XML
  ```

- [ ] 2. 计划 self-review（writing-plans 技能 §self-review 要求，本计划执行者自查）：

  **1. Spec 覆盖（需求 §4 FR1-FR4 / §5 NFR / §6 AC）：**

  | 需求项 | 覆盖 Task | 状态 |
  |---|---|---|
  | FR1.1 框架识别优先级 | Task 3 `framework_detect` | ✅ |
  | FR1.2 P0 框架（Java/Maven + JUnit XML 兜底） | Task 3 + Task 4 | ✅ |
  | FR1.3 执行/解析双模式 | Task 6 `runner_cli` 两分支 | ✅ |
  | FR1.4 执行失败诊断不空报告 | Task 3 `test_runner` + Task 6 退出码 2 | ✅ |
  | FR2 报告六章节固定顺序 | Task 5 `generate_report` | ✅ |
  | FR3.1 默认 Markdown | Task 5 | ✅（html/json 接口预留，P1 不实现） |
  | FR3.2 默认路径+可覆盖 | Task 6 `--output` | ✅ |
  | FR3.3 返回路径+摘要+1~3 失败原因 | Task 6 `main` 末尾 print | ✅ |
  | FR4.1 触发意图 | Task 1 `SKILL.md` triggers | ✅ |
  | FR4.2 配置项 | Task 1 frontmatter + Task 8 README | ✅ |
  | NFR1 性能（5s/1000 用例） | 纯标准库 + 惰性计算 | ✅（设计层面，实测在 Task 7） |
  | NFR2 缺失降级不崩溃 | Task 2 `normalize_time` + Task 4 `ParseResult.errors` | ✅ |
  | NFR3 安全过滤 | Task 5 `sanitize_secret` | ✅ |
  | NFR4 幂等 | Task 5 `test_idempotent_except_timestamp` | ✅ |
  | NFR5 插件式 | Task 4 `base.PARSER_REGISTRY` + `register_parser` | ✅ |
  | AC1-AC5 | Task 7 | ✅ |

  **2. 占位符扫描：** 无 "TBD/TODO/实现后补/Similar to Task N" 等占位符；每个 step 含完整代码或完整命令与期望输出。

  **3. 关键风险与缓解：**
  - R1（框架 reporter 差异）：P0 统一走 JUnit XML，差异收敛在 `junit_xml.py`；NFR5 注册表为 M2+ 扩展预留。
  - R2（长任务后台执行）：`test_runner.run` 为同步封装，后台执行与轮询由编排层（Agent 运行时）决定是否包裹；计划未硬编码 sleep 轮询，避免阻塞。
  - 构建环境不可用：执行模式端到端降级为静态审查（已在 Task 3/7 标注降级说明）。

- [ ] 3. 最终全量校验命令（单行，变更范围）：
  ```bash
  cd skills/test-report && python3 -m pytest tests/ -x && bash scripts/run.sh --parse tests/fixtures/junit_ok.xml --output /tmp/tr-final && echo PLAN_VERIFY_OK
  ```
  期望：全部测试通过 + 报告生成 + `PLAN_VERIFY_OK`。

**Commit:** 保存 README 补全即视为 Task 8 交付。

---

## 执行交接（Execution Handoff）

计划已完成并保存至 `docs/plans/test-report-skill-2.0-implementation-plan.md`。两种执行方式：

1. **Subagent-Driven（推荐）** — 每个 Task 派发独立 subagent，Task 间 review，快速迭代。建议 Task 2→4→5（数据/解析/生成核心链）由同一 subagent 连续执行以保持上下文，Task 3（pom 改造）与 Task 6/7（编排与验收）独立派发。
2. **Inline Execution** — 在当前会话按 Task 1→8 顺序执行，使用 `superpowers:executing-plans`，批量勾选 checkbox。

**P0 范围交付即视为 M1 里程碑完成。** M2（pytest 支持、fail_threshold 增强、覆盖率阈值下钻）、M3（HTML/JSON 伴随产物）为后续迭代，本计划仅在接口层预留扩展点（`PARSER_REGISTRY`、`TestReport.to_dict`、`generate_report` 章节 `_coverage` 阈值参数）。
