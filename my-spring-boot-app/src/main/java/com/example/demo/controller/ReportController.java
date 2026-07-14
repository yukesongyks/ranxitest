package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.ReportStatus;
import com.example.demo.entity.WeeklyReport;
import com.example.demo.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 创建周报
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ReportResponse>> createReport(
            @Valid @RequestBody ReportCreateRequest request,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Name") String userName) {
        WeeklyReport report = reportService.createReport(request, userId, userName);
        ReportResponse response = toResponse(report);
        return ResponseEntity.ok(ApiResponse.success(response, "保存成功"));
    }

    /**
     * 更新周报
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReportResponse>> updateReport(
            @PathVariable Long id,
            @RequestBody ReportUpdateRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        WeeklyReport report = reportService.updateReport(id, request, userId);
        ReportResponse response = toResponse(report);
        return ResponseEntity.ok(ApiResponse.success(response, "保存成功"));
    }

    /**
     * 提交周报
     */
    @PutMapping("/{id}/submit")
    public ResponseEntity<ApiResponse<Void>> submitReport(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        reportService.submitReport(id, userId);
        return ResponseEntity.ok(ApiResponse.success(null, "提交成功"));
    }

    /**
     * 审核周报
     */
    @PutMapping("/{id}/audit")
    public ResponseEntity<ApiResponse<Void>> auditReport(
            @PathVariable Long id,
            @RequestBody AuditRequest request,
            @RequestHeader("X-User-Role") String userRole) {
        reportService.auditReport(id, request, userRole);
        return ResponseEntity.ok(ApiResponse.success(null, "审核成功"));
    }

    /**
     * 查询周报列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ReportResponse>>> getReports(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        ReportStatus statusEnum = null;
        if (status != null) {
            try {
                statusEnum = ReportStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                throw new com.example.demo.exception.ReportBusinessException("无效的状态参数: " + status);
            }
        }
        PageResponse<ReportResponse> response = reportService.getReports(page, size, statusEnum);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 获取周报详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReportResponse>> getReport(@PathVariable Long id) {
        WeeklyReport report = reportService.getReportById(id);
        ReportResponse response = toResponse(report);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 查询我的周报
     */
    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<PageResponse<ReportResponse>>> getMyReports(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ReportResponse> response = reportService.getMyReports(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 实体转响应DTO
     */
    private ReportResponse toResponse(WeeklyReport report) {
        ReportResponse response = new ReportResponse();
        response.setId(report.getId());
        response.setAuthorId(report.getAuthorId());
        response.setAuthorName(report.getAuthorName());
        response.setThisWeekWork(report.getThisWeekWork());
        response.setNextWeekPlan(report.getNextWeekPlan());
        response.setStatus(report.getStatus());
        response.setRejectReason(report.getRejectReason());
        if (report.getCreatedAt() != null) {
            response.setCreatedAt(report.getCreatedAt().toString());
        }
        if (report.getUpdatedAt() != null) {
            response.setUpdatedAt(report.getUpdatedAt().toString());
        }
        return response;
    }
}