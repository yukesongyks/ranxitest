# Hello World 1.0T2 重跑 — 系分设计文档

> 阶段：系分设计（clarify） | 日期：2026-07-31 | 技能：brainstorming

---

## 1. 需求与功能模块

| 模块 | 职责 | 落点 |
|------|------|------|
| M1 三个演示接口 | helloworld / 哈希算法 / 冒泡排序 | ranxitest 后端 |
| M2 前端三 Tab 页面 | 展示各接口执行结果 | ranxitest 前端（Thymeleaf + 静态 JS） |
| M3 导出功能 | 导出按钮 + 后台导出接口 | ranxitest 前后端一体 |
| M4 埋点采集 | 调用次数 + 调用人 + 维度 | ranxitest 后端 |
| M5 报表可视化 | 折线图/饼图/柱状图按维度展示 | ranxitest 前端（CDN 图表库） |

**前端库指定**：yukesong/ranxitest（ranxitest-0314-test worktree）。

---

## 2. 仓库现状证据

### 2.1 ranxitest-0314-test（后端 + 前端一体）

- **技术栈**：Spring Boot 2.6.6 / Java 17 / Spring Data JPA / Thymeleaf / H2
- **配置**（`application.properties`）：`server.port=8080`，H2 内存库 `jdbc:h2:mem:itemdb`，`ddl-auto=update`（自动建表），`h2.console.enabled=true`
- **模板体系**：`src/main/resources/templates/` 下已有 10 个 Thymeleaf 模板（items/profile/error/index）
- **静态资源**：**无 `static/` 目录**，需新建 `src/main/resources/static/` 承载前端 JS/CSS
- **现有分层**：`controllers`(HomeController/ItemController/ProfileController) → `services` → `repositories` → `models`(Item/User)
- **异常处理现状**（关键矛盾）：`GlobalExceptionHandler` 是 `@ControllerAdvice`，异常时返回 `redirect:` 视图名或 `"error"` 模板——**对 REST API 会返回 HTML 而非 JSON**

### 2.2 User 模型字段

`User.java` 现有：`username, email, phone, bio, location, avatarUrl, createdAt, updatedAt`。**无** userType/userLevel/department 维度字段。`pom.xml` 无 spring-security。

---

## 3. 设计决策（确认项已按默认值填写）

| # | 决策项 | 取值 | 依据 |
|---|--------|------|------|
| D1 | 前端页面落点 | ranxitest 内 Thymeleaf 模板 + `static/` 静态 JS | 用户指定前端库为 ranxitest；前后端同库无跨域 |
| D2 | 图表库选型 | **Chart.js 4.x**（CDN 引入） | 无构建链依赖；Thymeleaf 模板内 `<script src>` 即用；支持折线/饼/柱 |
| D3 | 接口暴露方式 | 新增 `@RestController`，不改现有 `@Controller` | 向后兼容，功能隔离 |
| D4 | 埋点维度数据源 | 扩展 User（+3 字段）+ 新建 CallLog 表 | 维度持久化 + 调用记录独立 |
| D5 | 调用人识别 | 请求头 `X-User-Id` + `HandlerInterceptor` | 无 Security 依赖，轻量方案 |
| D6 | 前端 Tab 组件 | 原生 HTML + CSS（无前端框架） | ranxitest 无 React/Vue，Thymeleaf 纯服务端渲染 |
| D7 | 导出格式 | CSV + JSON 双格式 | 通用需求，JDK 原生拼接 CSV |

---

## 4. 详细设计

### 4.1 演示接口契约（M1）

新增 `DemoApiController`（`@RestController @RequestMapping("/api/demo")`）。

**GET /api/demo/helloworld** → 无入参
```json
{ "message": "Hello, World!", "timestamp": "2026-07-31T10:00:00", "durationMs": 0 }
```

**POST /api/demo/hash**
```json
// 请求
{ "input": "hello", "algorithm": "SHA-256" }
// 响应
{ "input": "hello", "algorithm": "SHA-256", "hashValue": "2cf24dba...", "durationMs": 1 }
```
algorithm 默认 `SHA-256`，可选 SHA-256/SHA-512/MD5。

**POST /api/demo/bubble-sort**
```json
// 请求
{ "input": [64, 34, 25, 12, 22, 11, 90] }
// 响应
{ "input": [64,34,25,12,22,11,90], "sorted": [11,12,22,25,34,64,90], "swapCount": 11, "durationMs": 0 }
```

### 4.2 前端页面（M2 + M5）

**新增文件**：
- 模板：`templates/demo.html`（Thymeleaf，含三 Tab 结构 + 报表容器 + 导出按钮）
- 静态 JS：`static/js/demo.js`（Tab 切换、接口调用、图表渲染）
- 静态 CSS：`static/css/demo.css`（Tab 样式）
- 图表库：CDN 引入 `https://cdn.jsdelivr.net/npm/chart.js@4`

**页面布局**：
```
┌──────────────────────────────────────────┐
│  Demo 演示页面          [导出 CSV▼|JSON]  │
├──────────────────────────────────────────┤
│  [HelloWorld] [哈希算法] [冒泡排序]        │  ← 原生 Tab
├──────────────────────────────────────────┤
│        <当前 Tab 执行结果展示区>            │
├──────────────────────────────────────────┤
│  📊 调用报表  维度:[人员类型▼]              │
│  折线图(趋势) │ 饼图(占比) │ 柱状图(对比)   │  ← Chart.js
└──────────────────────────────────────────┘
```

### 4.3 导出功能（M3）

**GET /api/demo/export?type={helloworld|hash|bubble-sort}&format={csv|json}**
- CSV：`Content-Type: text/csv`，`Content-Disposition: attachment; filename="xxx-export.csv"`
- JSON：`Content-Type: application/json`，`Content-Disposition: attachment`
- 实现：查 CallLog 对应 apiName 的历史记录 + 结果，格式化返回

### 4.4 埋点采集（M4）

**扩展 User.java**（+3 字段，向后兼容）：
```java
@Column(name = "user_type", length = 50)      private String userType;   // 默认 "内部"
@Column(name = "user_level", length = 50)    private String userLevel;  // 默认 "P6"
@Column(name = "department", length = 100)   private String department; // 默认 "技术部"
```

**新建 CallLog 实体**（`@Entity @Table(name="call_logs")`）：
- `id, apiName, userId, username, userType, userLevel, department, callTime, durationMs, success, resultSummary`

**采集流程**：`UserContextInterceptor`（`HandlerInterceptor`）从请求头 `X-User-Id` 提取用户 → ThreadLocal → `DemoApiController` 返回前调用 `MetricsService.recordCall()` → 查 User 获取维度快照 → 写 CallLog。

### 4.5 报表统计接口

**GET /api/demo/metrics/stats?dimension={userType|userLevel|department}&apiName={可选}**
```json
{
  "dimension": "userType",
  "totalCalls": 156,
  "series": [{ "label": "内部", "value": 80 }, { "label": "外部", "value": 76 }],
  "timeSeries": [{ "date": "2026-07-25", "内部": 12, "外部": 8 }]
}
```

---

## 5. 异常兜底方案

### 5.1 现状矛盾

现有 `GlobalExceptionHandler`（`@ControllerAdvice`）处理异常时返回 Thymeleaf 视图（`redirect:` / `"error"` 模板）。对 REST API（`/api/demo/**`）来说，异常响应会是 HTML 而非 JSON，前端 `fetch` 解析失败。

### 5.2 兜底方案

**策略**：新增 `ApiExceptionHandler`（`@RestControllerAdvice`）专管 `/api/demo/**` 路径，返回统一 JSON 错误体；现有 `GlobalExceptionHandler` 保持不变（继续管 Thymeleaf 页面异常）。

```java
@RestControllerAdvice(basePackages = "com.example.myapp.controllers")
@Order(1)  // 优先于 GlobalExceptionHandler
public class ApiExceptionHandler {

    // 统一错误响应体
    // { "code": 400, "error": "参数错误", "message": "...", "timestamp": "...", "path": "..." }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,Object>> handleIllegalArg(...) { /* → 400 */ }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleValidation(...) { /* → 400, 字段详情 */ }

    @ExceptionHandler(NoSuchAlgorithmException.class)
    public ResponseEntity<Map<String,Object>> handleNoHashAlgo(...) { /* → 400, "不支持的哈希算法" */ }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleGeneral(...) { /* → 500, "服务器内部错误" */ }
}
```

### 5.3 兜底覆盖矩阵

| 异常场景 | HTTP 状态 | 处理器 | 响应 |
|----------|-----------|--------|------|
| 接口入参非法（IllegalArgumentException） | 400 | ApiExceptionHandler | JSON `{code:400,error:"参数错误"}` |
| 请求体校验失败（@Valid） | 400 | ApiExceptionHandler | JSON `{code:400,error:"参数错误",message:字段详情}` |
| 不支持的哈希算法 | 400 | ApiExceptionHandler | JSON `{code:400,error:"参数错误"}` |
| 埋点写入失败 | — | 不阻断主流程 | try-catch 吞异常 + log.warn，返回正常业务结果 |
| 其他未知异常 | 500 | ApiExceptionHandler | JSON `{code:500,error:"服务器内部错误"}` |
| 现有 Thymeleaf 页面异常 | — | GlobalExceptionHandler（原样保留） | redirect:/items 或 error 模板 |

### 5.4 埋点兜底原则

埋点逻辑（`MetricsService.recordCall`）**绝不阻断业务主流程**：
- 埋点写入失败时 `try-catch` 捕获 + `log.warn` 记录 + 返回正常业务结果
- 确保即使埋点模块故障，三个演示接口仍可正常响应

---

## 6. 数据模型变更

| 实体 | 变更 | 说明 |
|------|------|------|
| `User` | 新增 3 字段 | userType/userLevel/department，H2 ddl-auto=update 自动加列 |
| `CallLog` | 新建 | `call_logs` 表，自动建表 |

**无新增 Maven 依赖**（`spring-boot-starter-web` 已含 `@RestController`/`@RestControllerAdvice`）。

---

## 7. 改动文件清单（规划，本阶段不执行）

### 后端 Java
| 文件 | 操作 |
|------|------|
| `controllers/DemoApiController.java` | 新增 |
| `services/DemoService.java` | 新增 |
| `services/MetricsService.java` | 新增 |
| `models/CallLog.java` | 新增 |
| `models/User.java` | 修改（+3 字段） |
| `repositories/CallLogRepository.java` | 新增 |
| `interceptors/UserContextInterceptor.java` | 新增 |
| `exception/ApiExceptionHandler.java` | 新增（异常兜底） |
| `config/WebConfig.java` | 新增（注册拦截器） |

### 前端资源
| 文件 | 操作 |
|------|------|
| `resources/templates/demo.html` | 新增（Thymeleaf 模板） |
| `resources/static/js/demo.js` | 新增（Tab/调用/图表） |
| `resources/static/css/demo.css` | 新增（Tab 样式） |

---

## 8. 风险与约束

| 风险 | 等级 | 缓解 |
|------|------|------|
| H2 内存库重启丢数据 | 低 | 演示场景可接受 |
| 请求头传用户标识可伪造 | 中 | 演示可接受；生产需升级 Security |
| User 维度字段无初始数据 | 低 | `data.sql` 预置 Mock 用户 |
| CDN 不可用致图表不渲染 | 低 | `demo.js` 内 fallback 提示文字 |
| 现有 GlobalExceptionHandler 与 ApiExceptionHandler 冲突 | 低 | `@Order(1)` + basePackages 路径隔离 |

---

## 9. 验收标准

- [ ] 三接口可独立调用返回 JSON
- [ ] 前端三 Tab 切换展示对应结果
- [ ] 导出按钮触发 CSV/JSON 下载
- [ ] 每次调用产生 CallLog 记录
- [ ] 报表按维度切换，三图表正确渲染
- [ ] **异常场景返回 JSON 而非 HTML（兜底生效）**
- [ ] 埋点故障不阻断主流程
- [ ] 现有 ItemController/ProfileController 功能不受影响

---

*本文档由 brainstorming 技能驱动，基于跨仓代码证据自主决策。确认项已按默认值填写。*
