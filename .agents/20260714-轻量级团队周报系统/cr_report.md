# 代码评审报告

**项目**: 轻量级团队周报系统  
**评审日期**: 2026-07-14  
**评审范围**: Controller/Service/Entity/DTO/Repository  
**评审标识**: testCase004-dj

---

## 一、评审摘要

| 类别 | 数量 |
|------|------|
| 🔴 Blocker | 4 |
| 🟠 Critical | 6 |
| 🟡 Major | 8 |
| 🟢 Minor | 5 |

---

## 二、Blocker 级别问题

### [B-001] 更新接口缺少权限校验 🔴

**文件**: `ReportController.java:35-42`, `ReportService.java:48-65`  
**问题**: 更新周报接口未验证操作者是否为周报作者，任何用户都可以修改他人的周报

**当前代码**:
```java
@PutMapping("/{id}")
public ResponseEntity<ApiResponse<ReportResponse>> updateReport(
        @PathVariable Long id,
        @RequestBody ReportUpdateRequest request) {
    WeeklyReport report = reportService.updateReport(id, request);
    // 缺少用户身份验证
}
```

**风险**: 用户A可以修改用户B的周报内容，严重违反业务规则

**建议修复**:
```java
public WeeklyReport updateReport(Long id, ReportUpdateRequest request, Long userId) {
    WeeklyReport report = reportRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("周报不存在"));
    
    if (!report.getAuthorId().equals(userId)) {
        throw new RuntimeException("无权编辑此周报");
    }
    // ... 其他逻辑
}
```

---

### [B-002] 审核接口缺少角色权限校验 🔴

**文件**: `ReportController.java:63-74`  
**问题**: 审核接口未校验操作者角色，任何用户都可以审核周报

**当前代码**:
```java
@PutMapping("/{id}/audit")
public ResponseEntity<ApiResponse<Void>> auditReport(
        @PathVariable Long id,
        @RequestBody AuditRequest request) {
    // 未校验是否为主管角色
    reportService.auditReport(id, request);
}
```

**风险**: 普通员工可以审核自己的周报或他人周报，违反业务规则

**建议修复**:
1. 添加 `@RequestHeader("X-User-Role") String userRole` 参数
2. 在Service中校验 `if (!"MANAGER".equals(userRole)) throw new RuntimeException("只有主管可以审核")`

---

### [B-003] 团队总人数硬编码 🔴

**文件**: `ReportService.java:187-188`  
**问题**: 统计接口中团队总人数硬编码为20

**当前代码**:
```java
// 假设团队总人数为20人（实际应从用户表查询）
long totalMembers = 20;
```

**风险**: 
- 统计数据不准确
- 代码无法适应团队规模变化
- 不符合生产环境要求

**建议修复**: 从用户服务或用户表查询实际团队人数

---

### [B-004] 周报防重逻辑存在并发问题 🔴

**文件**: `ReportService.java:91-98`  
**问题**: 防重校验与状态更新非原子操作，并发场景下可能重复提交

**当前代码**:
```java
boolean exists = reportRepository.existsByAuthorIdAndWeekAndStatusIn(...);
if (exists) {
    throw new RuntimeException("您本周已提交过周报");
}
report.setStatus(ReportStatus.PENDING);
reportRepository.save(report);  // 无唯一约束保护
```

**风险**: 两个请求同时检查都返回false，导致同一用户本周提交多份周报

**建议修复**:
1. 数据库层面添加唯一索引: `UNIQUE(author_id, week_start_date)` where status IN ('PENDING', 'APPROVED')
2. 或使用数据库悲观锁/乐观锁机制

---

## 三、Critical 级别问题

### [C-001] DTO缺少校验注解

**文件**: `AuditRequest.java`, `ReportUpdateRequest.java`  
**问题**: 请求DTO未使用`@Valid`注解和JSR-303校验，依赖手工校验

**风险**: 
- 参数校验不完整
- 可能注入非法数据

**建议**: 添加 `@NotBlank`, `@Size(min=10)` 等校验注解

---

### [C-002] 异常处理不规范

**文件**: `ReportController.java:51-58, 67-73`  
**问题**: Controller直接捕获`RuntimeException`，应使用全局异常处理器

**当前代码**:
```java
try {
    reportService.submitReport(id, userId);
    return ResponseEntity.ok(ApiResponse.success(null, "提交成功"));
} catch (RuntimeException e) {
    return ResponseEntity.badRequest()
            .body(ApiResponse.error(400, e.getMessage()));
}
```

**建议**: 使用 `@ControllerAdvice` + `@ExceptionHandler` 统一处理异常

---

### [C-003] 提交时未校验请求体

**文件**: `ReportController.java:47-58`  
**问题**: 提交接口使用空请求体 `PUT /api/reports/{id}/submit` 但未验证用户是否有草稿

**风险**: 用户可能绕过前端直接调用接口

---

### [C-004] 时间格式不一致

**文件**: `ReportService.java:239, 242`, `ReportController.java:124, 127`  
**问题**: LocalDateTime直接toString()输出格式为ISO-8601，与设计文档要求的格式可能不一致

**建议**: 使用 `DateTimeFormatter` 统一格式化

---

### [C-005] Repository查询方法返回类型可能导致空指针

**文件**: `ReportService.java:50, 72, 110, 168`  
**问题**: 多处使用 `orElseThrow` 但异常消息不一致，应定义业务异常类

---

### [C-006] 缺少删除功能

**问题**: 系统未提供删除周报功能，可能导致草稿数据无限增长

---

## 四、Major 级别问题

### [M-001] 分页参数未校验

**文件**: `ReportController.java:81-82`  
**问题**: page和size参数未限制最大值，可能导致内存溢出

**建议**: 添加 `@Max(100)` 限制size最大值

---

### [M-002] 状态枚举转换可能抛出异常

**文件**: `ReportController.java:84`  
**问题**: `ReportStatus.valueOf(status)` 若传入非法值会抛出 `IllegalArgumentException`

**建议**: 添加try-catch或自定义转换器

---

### [M-003] toResponse方法重复实现

**文件**: `ReportController.java:114-130`, `ReportService.java:229-245`  
**问题**: Controller和Service中都实现了实体转DTO逻辑，违反DRY原则

**建议**: 提取到独立的Converter或使用MapStruct

---

### [M-004] 提交时间字段命名不一致

**文件**: Entity可能的字段命名  
**问题**: 有 `submittedAt` 和 `auditedAt`，但没有统一的审计字段设计

---

### [M-005] 缺少日志记录

**问题**: 关键操作（创建、提交、审核）缺少日志记录，难以追溯问题

---

### [M-006] 统计接口weekDate参数格式未校验

**文件**: `ReportService.java:219-224`  
**问题**: weekDate参数直接拼接解析，可能抛出 `DateTimeParseException`

---

### [M-007] 审核通过时未清空打回原因

**文件**: `ReportService.java:117-118`  
**问题**: 审核通过后应清空 rejectReason 字段

---

### [M-008] 缺少单元测试

**问题**: 未发现测试代码，核心业务逻辑缺少测试覆盖

---

## 五、Minor 级别问题

### [m-001] ApiResponse缺少无参构造器

**问题**: 可能影响JSON反序列化框架

---

### [m-002] 建议使用构造器注入替代字段注入

**文件**: 多个Controller和Service  
**问题**: 使用 `@Autowired` 字段注入，建议改用构造器注入

---

### [m-003] 魔法值应定义为常量

**文件**: `ReportService.java:188`  
**问题**: 团队总人数20应定义为配置项

---

### [m-004] 缺少接口文档

**问题**: 缺少Swagger/OpenAPI文档注解

---

### [m-005] 状态字段建议使用枚举类型

**文件**: `ReportUpdateRequest.java:6`  
**问题**: status字段使用String类型，建议使用枚举

---

## 六、评审总结

### 主要风险点
1. **安全性风险高**: 权限校验缺失，任何人可修改/审核他人周报
2. **并发安全性差**: 防重逻辑存在竞态条件
3. **生产就绪度低**: 硬编码团队人数、缺少异常处理、缺少测试

### 建议优先修复顺序
1. B-001, B-002: 添加权限校验（必须）
2. B-003: 从用户表查询团队人数（必须）
3. B-004: 添加数据库唯一约束（必须）
4. C-001, C-002: 完善参数校验和异常处理（推荐）

### 整体代码质量评估
- **功能完整性**: 85%（核心功能已实现，缺少删除和权限控制）
- **代码规范**: 70%（存在重复代码、硬编码问题）
- **安全性**: 40%（权限校验严重缺失）
- **可维护性**: 75%（代码结构清晰，但缺少测试）

---

**评审结论**: ⚠️ **不建议直接上线**，需优先修复Blocker级别问题后重新评审。

**Blocker数量**: 4