# 代码评审报告 - 轻量级团队周报系统

**评审日期**: 2026-07-14  
**评审范围**: ReportController, ReportService, WeeklyReport, ReportStatus, ReportRepository  
**评审人**: AI Code Reviewer

---

## 一、评审概述

本次评审针对轻量级团队周报系统的核心业务代码，重点关注业务逻辑正确性、安全性、并发安全性及代码质量。

**评审结论**: 发现 **4 个阻塞级问题**，需在合并前修复。

---

## 二、阻塞级问题 (Blocker)

### B-001: 提交周报存在并发竞态条件 ⚠️ 高危

**位置**: `ReportService.java:104-111`

**问题描述**:
```java
// 防重：检查本周是否已提交过
LocalDateTime weekStart = getCurrentWeekStart();
LocalDateTime weekEnd = weekStart.plusWeeks(1);
List<ReportStatus> submittedStatuses = Arrays.asList(ReportStatus.PENDING, ReportStatus.APPROVED);
boolean exists = reportRepository.existsByAuthorIdAndWeekAndStatusIn(authorId, weekStart, weekEnd, submittedStatuses);
if (exists) {
    throw new ReportBusinessException("您本周已提交过周报");
}
```

防重检查与状态更新之间存在时间窗口，两个并发请求可能同时通过检查，导致同一用户本周重复提交多份周报。

**修复建议**:
1. 使用数据库唯一约束: `(author_id, week_start_date)` 唯一索引
2. 或在 `@Transactional` 中使用悲观锁 `LockModeType.PESSIMISTIC_WRITE`

---

### B-002: Controller 层异常处理不规范

**位置**: `ReportController.java:52-58, 69-75`

**问题描述**:
```java
try {
    reportService.submitReport(id, userId);
    return ResponseEntity.ok(ApiResponse.success(null, "提交成功"));
} catch (RuntimeException e) {
    return ResponseEntity.badRequest()
            .body(ApiResponse.error(400, e.getMessage()));
}
```

使用 `try-catch RuntimeException` 捕获所有异常返回 400，违反 Spring 最佳实践，应使用 `@ExceptionHandler` 或 `@ControllerAdvice` 统一处理。

**影响**:
- 隐藏了系统级错误（如数据库连接失败）
- 无法区分业务异常和系统异常
- 响应码不统一

**修复建议**:
使用全局异常处理器 `GlobalExceptionHandler` 统一处理 `ReportBusinessException`

---

### B-003: 状态参数校验不完整

**位置**: `ReportController.java:86`

**问题描述**:
```java
ReportStatus statusEnum = status != null ? ReportStatus.valueOf(status) : null;
```

当 `status` 参数为无效字符串（如 "INVALID"）时，`ReportStatus.valueOf()` 会抛出 `IllegalArgumentException`，未进行友好提示。

**修复建议**:
```java
ReportStatus statusEnum = null;
if (status != null) {
    try {
        statusEnum = ReportStatus.valueOf(status);
    } catch (IllegalArgumentException e) {
        throw new ReportBusinessException("无效的状态参数: " + status);
    }
}
```

---

### B-004: 未使用的导入语句

**位置**: `ReportService.java:16`

**问题描述**:
```java
import javax.persistence.LockModeType;
```

导入但从未使用，属于代码冗余。

**修复建议**: 删除该导入语句

---

## 三、重要问题 (Major)

### M-001: 实体校验与业务校验重复

**位置**: `WeeklyReport.java:22-30` 与 `ReportService.java:97-102`

**问题**: 
实体类使用 `@NotBlank` 和 `@Size(min=10)` 注解，Service 层又进行手动校验，存在重复逻辑。

**建议**: 统一使用 Bean Validation，或仅在 Service 层校验以保证业务一致性。

---

### M-002: 审核通过未清空打回原因（已修复）

**位置**: `ReportService.java:137-138`

已包含清空逻辑，无需修改。

---

### M-003: 团队总人数硬编码问题（已修复）

**位置**: `ReportService.java:35-36`

已使用 `@Value` 配置化，无需修改。

---

## 四、一般问题 (Minor)

### m-001: Controller 缺少参数校验注解

**位置**: `ReportController.java:38`

`updateReport` 方法的 `@RequestBody ReportUpdateRequest request` 未添加 `@Valid` 注解。

**建议**: 添加 `@Valid` 确保请求参数校验生效。

---

### m-002: 分页参数未限制上限

**位置**: `ReportController.java:83-84`

```java
@RequestParam(defaultValue = "10") int size
```

未限制 `size` 最大值，可能导致大量数据查询影响性能。

**建议**: 添加 `@Max(100)` 注解或业务层限制。

---

## 五、代码质量评价

| 维度 | 评分 | 说明 |
|------|------|------|
| 业务逻辑完整性 | ★★★★☆ | 核心流程完整，状态流转清晰 |
| 安全性 | ★★★☆☆ | 权限校验到位，但并发安全不足 |
| 代码规范性 | ★★★☆☆ | 存在冗余导入、异常处理不规范 |
| 可维护性 | ★★★★☆ | 结构清晰，命名规范 |
| 测试覆盖度 | - | 未评审 |

---

## 六、修复优先级建议

| 优先级 | 问题编号 | 修复内容 |
|--------|----------|----------|
| P0 | B-001 | 添加数据库唯一约束或悲观锁 |
| P0 | B-002 | 使用全局异常处理器 |
| P1 | B-003 | 状态参数校验优化 |
| P1 | B-004 | 删除未使用导入 |
| P2 | M-001 | 统一校验策略 |
| P3 | m-001, m-002 | 参数校验优化 |

---

## 七、总结

本次评审发现 4 个阻塞级问题，主要集中在并发安全性和异常处理规范性。建议优先修复 B-001 并发竞态条件问题，该问题可能导致业务数据不一致。

**评审状态**: ❌ 不建议合并，需修复阻塞级问题后重新评审。

---

**blocker_count**: 4