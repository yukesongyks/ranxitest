package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.WeeklyStatisticsResponse;
import com.example.demo.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private ReportService reportService;

    /**
     * 获取周统计数据
     */
    @GetMapping("/weekly")
    public ResponseEntity<ApiResponse<WeeklyStatisticsResponse>> getWeeklyStatistics(
            @RequestParam(required = false) String weekDate) {
        WeeklyStatisticsResponse response = reportService.getWeeklyStatistics(weekDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}