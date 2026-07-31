package com.example.myapp.controllers;

import com.example.myapp.common.ApiResult;
import com.example.myapp.dto.StatisticsVO;
import com.example.myapp.services.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 埋点统计查询 REST 接口。
 */
@RestController
@RequestMapping("/api")
public class TrackController {

    private final TrackService trackService;

    @Autowired
    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    /**
     * W05 埋点统计查询接口：按指定维度查询接口调用统计数据。
     */
    @GetMapping("/statistics")
    public ApiResult<StatisticsVO> statistics(@RequestParam String dimension,
                                              @RequestParam(required = false) String startDate,
                                              @RequestParam(required = false) String endDate,
                                              @RequestParam(required = false) String chartType) {
        StatisticsVO data = trackService.statistics(dimension, startDate, endDate);
        return ApiResult.success(data);
    }
}
