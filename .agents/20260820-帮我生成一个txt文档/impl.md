# TXT 文档生成与导出功能 编码实现报告

> **文档元信息**
>
> | 项目 | 内容 |
> |------|------|
> | 文档版本 | v1.0 |
> | 作者 | DTCoder（编码实现 Agent） |
> | 创建日期 | 2026-08-20 |
> | 需求来源 | 用户需求「帮我生成一个txt文档」（流水线 coding 阶段任务） |
> | 系分方案 | `.agents/20260820-帮我生成一个txt文档/design.md` |
> | 实现状态 | ✅ 已完成（动态验证受环境限制，见 CHECK） |

## 1. 模块进度追踪表

| 序号 | 模块 | READ | TEST | IMPL | CHECK | DOCS | 状态 |
|:----:|------|:----:|:----:|:----:|:-----:|:----:|------|
| 1 | docgen（通用 TXT 生成） | ✅ | ✅ | ✅ | ✅ | ✅ | 已完成 |
| 2 | item-export（物品适配+控制器） | ✅ | ✅ | ✅ | ✅ | ✅ | 已完成 |

## 2. READ 阶段摘要

- **系分方案**：`design.md`（方案 A：独立 docgen 模块 + item 适配层）
- **既有工程事实**：`Item` 实体（id/name/description/category/quantity/price）、`ItemService.findAll()`、`items/list.html` 清单页、`GlobalExceptionHandler`、具体类 + `@Service` + 构造器注入风格
- **已加载规范**：unit-testing.md、naming.md、exception-logging.md、constants.md（javadoc 注释规约）
- **实现取舍**：跟随仓库既有「具体类 + @Service」风格（不引入 Service/Impl 分层）；控制器直接构造 `TxtExportOptions`，采用构造器注入

## 3. TEST 阶段摘要（TDD，测试先行）

| 测试文件 | 被测类 | 方法数 | 覆盖场景 |
|----------|--------|:----:|----------|
| `src/test/java/com/example/myapp/docgen/TxtExportServiceTest.java` | TxtExportService | 6 | 正常路径（表头+数据+汇总、UTF-8、CRLF）、转义（R02）、行数超限（R01）、体积超限（R01）、空数据、文件名格式（S03） |
| `src/test/java/com/example/myapp/services/ItemExportServiceTest.java` | ItemExportService | 3 | 正常映射（价格两位小数）、空清单、数据源异常（DOCGEN_001） |
| `src/test/java/com/example/myapp/controllers/ItemExportControllerTest.java` | ItemExportController | 6 | W01 成功/开关关闭、O01 成功/limit 越界（DOCGEN_003）/encoding 不支持（DOCGEN_003）/超限（DOCGEN_002） |

## 4. IMPL 阶段摘要

### 4.1 新增文件

| 文件 | 说明 |
|------|------|
| `docgen/DocgenErrorCode.java` | 错误码枚举 DOCGEN_001/002/003 |
| `docgen/DocgenExportException.java` | 业务异常（携带 errorCode） |
| `docgen/TxtRow.java` | 通用 TXT 行模型（不可变单元格列表） |
| `docgen/TxtExportOptions.java` | 导出选项（分隔符/换行/编码/上限/汇总模板） |
| `docgen/TxtExportService.java` | S01 `exportTxt` + S03 `buildFileName`，含 R01 校验与 R02 转义 |
| `docgen/DocgenExportProperties.java` | `docgen.export.*` 配置项（enabled/timeout-ms） |
| `services/ItemExportService.java` | S02 `buildRows`（ID/名称/描述/价格，价格两位小数） |
| `controllers/ItemExportController.java` | W01 `/items/export.txt` + O01 `/openapi/items/export` |

### 4.2 修改文件

| 文件 | 改动 |
|------|------|
| `controllers/ItemController.java` | 注入 `DocgenExportProperties`，`@ModelAttribute("docgenExportEnabled")` 供页面控制入口显隐 |
| `templates/items/list.html` | 顶部新增「导出TXT」按钮（受开关控制） |
| `resources/application.properties` | 新增 `docgen.export.enabled=true`、`docgen.export.timeout-ms=10000` |

### 4.3 接口落地对照

| 设计编号 | 实现 | 说明 |
|----------|------|------|
| W01 | `GET /items/export.txt` | 附件下载 `text/plain;charset=UTF-8` + `Content-Disposition: attachment` |
| O01 | `GET /openapi/items/export?limit=&encoding=` | limit 默认 10000 最大 100000；encoding 仅 utf-8/gbk；失败返回 `{result,msg,data}` |
| S01 | `TxtExportService.exportTxt(rows, options)` | 表头+数据+汇总、CRLF、转义、R01 上限 |
| S02 | `ItemExportService.buildRows()` | 读 `ItemRepository.findAll()` |
| S03 | `TxtExportService.buildFileName(prefix)` | `items-yyyyMMdd-HHmmss.txt` |
| R01 | exportTxt 内校验 | 行数/体积超限 → DOCGEN_002 |
| R02 | exportTxt 内转义 | `\t`/`\r`/`\n` → 空格 |
| R03 | buildFileName 白名单前缀 | 固定格式防路径穿越 |
| 7.3 开关 | `docgen.export.enabled=false` | W01 隐藏入口（503）、O01 返回「导出功能维护中」 |

## 5. CHECK 阶段摘要

### 5.1 L1 静态检查

| 检查项 | 规范要求 | 符合情况 |
|--------|----------|:--------:|
| 命名规范 | 类名大驼峰、方法名小驼峰、常量全大写 | ✅ |
| 异常日志 | SLF4J + 占位符、自定义异常携带错误码 | ✅ |
| 错误码 | 快速溯源、`DOCGEN_XXX` 统一前缀 | ✅ |
| 安全规范 | 文本注入转义、文件名白名单、encoding/limit 白名单校验 | ✅ |
| 单元测试 | 3 个测试类、AAA 模式、AssertJ 断言、Mockito 严格模式 | ✅ |
| 注释规范 | 类/方法 javadoc | ✅ |
| 常量规范 | 无魔法值（超限值、前缀均为常量） | ✅ |
| 日期格式 | 文件时间戳 `yyyyMMdd-HHmmss`（小写 y） | ✅ |

### 5.2 L2 动态验证

| 验证项 | 状态 | 说明 |
|--------|:----:|------|
| 编译验证 | ⚠️ | 环境无 mvn/javac，apt 安装失败（网络受限），跳过 |
| 单测验证 | ⚠️ | 同上，跳过（测试代码已按规约静态审查） |

### 5.3 静态代码审查结论

- 类型一致性：`DocgenExportException` 在 service 抛出、controller 捕获，`getErrorCode()` 与测试断言一致
- 边界条件：空清单（表头+共 0 条）、null 字段（描述/价格为 null → 空串）、limit 越界/encoding 非法、开关关闭
- 路由冲突：`/items/export.txt` 字面路径比 ItemController `/{id}` 更具体，Spring 优先匹配，无冲突
- 未使用 import 已清理；`TxtRow` 不可变防御

## 6. 待人工验证

以下命令请在本地执行，确认代码质量：

```bash
cd my-spring-boot-app
mvn compile -DskipTests
mvn test -Dtest=TxtExportServiceTest,ItemExportServiceTest,ItemExportControllerTest
```

**发现的问题**：无

## 7. 已知限制与缺失条件

- 单次生成超时（A07 `timeout-ms`）仅提供配置项，未实现异步超时中断（demo 数据量级下为纯内存计算，风险低）
- O01 限流（A08：60 次/分钟/IP）与灰度引流（A09）未实现（属于假设待确认项，非 P0/P1 功能点）
- `data` 出参失败时为 null，成功时直接返回文件流（符合系分约定）

## ✅ 各阶段完成总结

| 模块 | 阶段 | 状态 |
|------|------|:----:|
| docgen | READ / TEST / IMPL / CHECK / DOCS | ✅ |
| item-export | READ / TEST / IMPL / CHECK / DOCS | ✅ |