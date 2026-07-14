# 轻量级团队周报系统 - 设计文档

**版本**: v1.0 | **日期**: 2026-07-14 | **标识**: testCase004-dj

---

## 1. 系统概述

**背景**: 团队周报数据分散，需开发轻量级管理系统，实现员工提交、主管审核、统计分析。

**核心功能**: 周报草稿管理 | 提交审核 | 审核流程 | 团队统计

**系统边界**: 仅周报管理，不涉及用户权限体系（假设已有统一认证）

---

## 2. 角色与权限

| 角色 | 权限 |
|------|------|
| **员工** | 创建/编辑/提交自己的周报，仅草稿状态可编辑 |
| **主管** | 查看团队所有周报、审核（通过/打回）、查看统计 |

---

## 3. 数据模型

### 3.1 周报表 (weekly_report)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| author_id | BIGINT | 作者ID |
| this_week_work | TEXT | 本周工作 |
| next_week_plan | TEXT | 下周计划 |
| status | VARCHAR(20) | 状态：DRAFT/PENDING/APPROVED/REJECTED |
| reject_reason | VARCHAR(500) | 打回原因 |
| week_start_date | DATE | 所属周（周一） |
| submitted_at | DATETIME | 提交时间 |
| audited_at | DATETIME | 审核时间 |
| auditor_id | BIGINT | 审核人 |

**约束**: `UNIQUE(author_id, week_start_date)` 同一员工每周仅一条周报

### 3.2 状态机

```
DRAFT(草稿) → 提交 → PENDING(待审核) → 通过 → APPROVED(已通过)
                           ↓
                         打回 → DRAFT(重新编辑)
```

---

## 4. API设计

### 4.1 周报管理

```
POST /api/reports           创建草稿
PUT  /api/reports/{id}      更新草稿（自动保存）
PUT  /api/reports/{id}/submit  提交审核

请求: { "thisWeekWork": "...", "nextWeekPlan": "...", "status": "DRAFT" }
响应: { "code": 200, "data": { "id": 101, "updatedAt": "..." } }
```

**校验**: 前端（内容非空，字数>10，二次确认）+ 后端（同一周唯一性）

### 4.2 审核管理

```
GET  /api/reports?page=1&size=10&status=PENDING  查询周报列表
PUT  /api/reports/{id}/audit  审核操作

通过: { "action": "APPROVE" }
打回: { "action": "REJECT", "rejectReason": "内容太简略" }
```

### 4.3 统计分析

```
GET /api/statistics/weekly?weekDate=2023-10-23

响应: {
  "submitRate": 0.85,
  "approvalRate": 0.95,
  "totalMembers": 20,
  "submittedMembers": 17
}
```

---

## 5. 核心流程

### 5.1 自动保存
- 前端：失焦触发 + 30秒定时器，状态提示"保存中..." → "已保存"
- 防抖处理避免频繁请求

### 5.2 提交审核
1. 前端校验（必填、字数>10）
2. 弹出二次确认框
3. 后端校验（唯一性、状态必须为DRAFT）
4. 更新状态为PENDING

### 5.3 审核操作
1. 主管进入审核列表（默认PENDING状态）
2. 筛选不同状态查看
3. 点击通过/打回（打回需填写原因）
4. 更新状态，记录审核人和时间

---

## 6. 超长文本处理

### 6.1 场景说明
员工粘贴上万字周报内容，可能导致：
- 数据库字段溢出（MySQL TEXT类型最大64KB）
- 前端渲染性能下降
- 网络传输超时
- 统计列表页加载缓慢

### 6.2 解决方案

**字段设计**:
- 使用`TEXT`类型（最大64KB，约2万汉字），超出时自动截断并提示
- 或使用`MEDIUMTEXT`类型（最大16MB），但需配合业务限制

**前端限制**:
```javascript
// 字数统计与提示
const MAX_LENGTH = 10000; // 单字段最大字数

function validateContent(content) {
  if (content.length > MAX_LENGTH) {
    return {
      valid: false,
      message: `内容过长，当前${content.length}字，最多${MAX_LENGTH}字`
    };
  }
  return { valid: true };
}
```

**后端校验**:
```java
// 提交前校验字段长度
if (thisWeekWork.length() > 10000 || nextWeekPlan.length() > 10000) {
    return Result.fail("周报内容超过字数限制，请精简后提交");
}
```

### 6.3 具体限制

| 限制项 | 阈值 | 处理方式 |
|--------|------|----------|
| 单字段最大字数 | 10000字 | 前端实时统计，超限禁用提交 |
| 周报总字数 | 20000字 | 后端二次校验，超限返回错误 |
| 列表页内容截断 | 200字 | 仅显示前200字 + "..."，点击查看全文 |

### 6.4 用户体验优化

- 编辑页显示实时字数统计："已输入 8500/10000 字"
- 接近上限时黄色警告，超限时红色提示并禁用提交
- 列表页仅显示摘要，避免加载全文影响性能

---

## 7. 并发冲突处理

### 6.1 场景说明
员工编辑周报时，主管同时尝试审核该周报。

### 6.2 解决方案

**后端保障**:
- 审核接口仅在`PENDING`状态可执行
- 使用行锁或乐观锁：`SELECT ... FOR UPDATE` 或 `UPDATE ... WHERE status='PENDING'`
- 状态不匹配返回400错误："周报状态已变更"

**前端保护**:
- 员工提交成功后立即跳转列表页
- 主管审核失败时提示刷新，不自动重试

### 6.3 异常处理

| 场景 | 处理 |
|------|------|
| 审核时状态非PENDING | 返回400错误，前端提示"周报状态已变更，请刷新" |
| 提交时状态非DRAFT | 返回400错误，前端跳转列表页 |
| 提交成功后后退编辑页 | 前端检测状态自动跳转 |

---

## 7. 技术选型

**前端**: Vue 3 / React 18 | Element Plus / Ant Design | ECharts（统计看板）

**后端**: Java 17 / Node.js 18 | Spring Boot 3 / NestJS | MySQL 8.0 | Redis（可选）

**数据库索引**:
```sql
CREATE INDEX idx_author_week ON weekly_report(author_id, week_start_date);
CREATE INDEX idx_status ON weekly_report(status);
```

---

## 8. 非功能性需求

| 指标 | 目标 |
|------|------|
| 响应时间 | < 500ms |
| 并发支持 | 100 QPS |
| 自动保存延迟 | < 1s |

---

## 9. 页面清单

| 页面 | 路由 | 角色 |
|------|------|------|
| 周报编辑页 | /report/edit | 员工 |
| 我的周报 | /report/mine | 员工 |
| 审核列表 | /report/audit | 主管 |
| 统计看板 | /statistics | 主管 |

---

**文档结束**