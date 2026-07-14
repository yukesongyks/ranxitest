package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.ReportStatus;
import com.example.demo.entity.WeeklyReport;
import com.example.demo.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    /**
     * 创建周报草稿
     */
    @Transactional
    public WeeklyReport createReport(ReportCreateRequest request, Long authorId, String authorName) {
        WeeklyReport report = new WeeklyReport();
        report.setAuthorId(authorId);
        report.setAuthorName(authorName);
        report.setThisWeekWork(request.getThisWeekWork());
        report.setNextWeekPlan(request.getNextWeekPlan());
        report.setStatus(ReportStatus.DRAFT);
        report.setWeekStartDate(getCurrentWeekStart());
        return reportRepository.save(report);
    }

    /**
     * 更新周报草稿
     */
    @Transactional
    public WeeklyReport updateReport(Long id, ReportUpdateRequest request) {
        WeeklyReport report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("周报不存在"));
        
        if (report.getStatus() != ReportStatus.DRAFT && report.getStatus() != ReportStatus.REJECTED) {
            throw new RuntimeException("只有草稿或已打回的周报可以编辑");
        }
        
        if (request.getThisWeekWork() != null) {
            report.setThisWeekWork(request.getThisWeekWork());
        }
        if (request.getNextWeekPlan() != null) {
            report.setNextWeekPlan(request.getNextWeekPlan());
        }
        
        return reportRepository.save(report);
    }

    /**
     * 提交周报
     */
    @Transactional
    public void submitReport(Long id, Long authorId) {
        WeeklyReport report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("周报不存在"));
        
        if (!report.getAuthorId().equals(authorId)) {
            throw new RuntimeException("无权提交此周报");
        }
        
        if (report.getStatus() != ReportStatus.DRAFT && report.getStatus() != ReportStatus.REJECTED) {
            throw new RuntimeException("只有草稿或已打回的周报可以提交");
        }
        
        // 校验字数
        if (report.getThisWeekWork() == null || report.getThisWeekWork().length() < 10) {
            throw new RuntimeException("本周工作内容字数需大于10字");
        }
        if (report.getNextWeekPlan() == null || report.getNextWeekPlan().length() < 10) {
            throw new RuntimeException("下周计划字数需大于10字");
        }
        
        // 防重：检查本周是否已提交过
        LocalDateTime weekStart = getCurrentWeekStart();
        LocalDateTime weekEnd = weekStart.plusWeeks(1);
        List<ReportStatus> submittedStatuses = Arrays.asList(ReportStatus.PENDING, ReportStatus.APPROVED);
        boolean exists = reportRepository.existsByAuthorIdAndWeekAndStatusIn(authorId, weekStart, weekEnd, submittedStatuses);
        if (exists) {
            throw new RuntimeException("您本周已提交过周报");
        }
        
        report.setStatus(ReportStatus.PENDING);
        report.setSubmittedAt(LocalDateTime.now());
        reportRepository.save(report);
    }

    /**
     * 审核周报
     */
    @Transactional
    public void auditReport(Long id, AuditRequest request) {
        WeeklyReport report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("周报不存在"));
        
        if (report.getStatus() != ReportStatus.PENDING) {
            throw new RuntimeException("只有待审核的周报可以审核");
        }
        
        if ("APPROVE".equals(request.getAction())) {
            report.setStatus(ReportStatus.APPROVED);
        } else if ("REJECT".equals(request.getAction())) {
            report.setStatus(ReportStatus.DRAFT);
            report.setRejectReason(request.getRejectReason());
        } else {
            throw new RuntimeException("无效的审核操作");
        }
        
        report.setAuditedAt(LocalDateTime.now());
        reportRepository.save(report);
    }

    /**
     * 查询周报列表（分页）
     */
    public PageResponse<ReportResponse> getReports(int page, int size, ReportStatus status) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<WeeklyReport> reportPage;
        
        if (status != null) {
            reportPage = reportRepository.findByStatus(status, pageable);
        } else {
            reportPage = reportRepository.findAll(pageable);
        }
        
        List<ReportResponse> list = reportPage.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        
        return new PageResponse<>(reportPage.getTotalElements(), list);
    }

    /**
     * 查询用户的周报列表
     */
    public PageResponse<ReportResponse> getMyReports(Long authorId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<WeeklyReport> reportPage = reportRepository.findByAuthorId(authorId, pageable);
        
        List<ReportResponse> list = reportPage.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        
        return new PageResponse<>(reportPage.getTotalElements(), list);
    }

    /**
     * 获取周报详情
     */
    public WeeklyReport getReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("周报不存在"));
    }

    /**
     * 获取周统计数据
     */
    public WeeklyStatisticsResponse getWeeklyStatistics(String weekDate) {
        LocalDateTime weekStart = parseWeekStart(weekDate);
        LocalDateTime weekEnd = weekStart.plusWeeks(1);
        
        List<ReportStatus> submittedStatuses = Arrays.asList(
            ReportStatus.PENDING, ReportStatus.APPROVED
        );
        
        long submittedMembers = reportRepository.countSubmittedMembers(weekStart, weekEnd, submittedStatuses);
        long approvedReports = reportRepository.countApprovedReports(weekStart, weekEnd);
        long submittedReports = reportRepository.countSubmittedReports(weekStart, weekEnd, submittedStatuses);
        
        // 假设团队总人数为20人（实际应从用户表查询）
        long totalMembers = 20;
        
        BigDecimal submitRate = totalMembers > 0 
            ? BigDecimal.valueOf(submittedMembers).divide(BigDecimal.valueOf(totalMembers), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
        
        BigDecimal approvalRate = submittedReports > 0
            ? BigDecimal.valueOf(approvedReports).divide(BigDecimal.valueOf(submittedReports), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;
        
        WeeklyStatisticsResponse response = new WeeklyStatisticsResponse();
        response.setSubmitRate(submitRate);
        response.setApprovalRate(approvalRate);
        response.setTotalMembers(totalMembers);
        response.setSubmittedMembers(submittedMembers);
        
        return response;
    }

    /**
     * 获取当前周的起始时间（周一 00:00:00）
     */
    private LocalDateTime getCurrentWeekStart() {
        return LocalDateTime.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .with(LocalTime.MIN);
    }

    /**
     * 解析周起始时间
     */
    private LocalDateTime parseWeekStart(String weekDate) {
        if (weekDate == null || weekDate.isEmpty()) {
            return getCurrentWeekStart();
        }
        return LocalDateTime.parse(weekDate + "T00:00:00");
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