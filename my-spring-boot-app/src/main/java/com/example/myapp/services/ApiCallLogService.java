package com.example.myapp.services;

import com.example.myapp.dto.CallStatsResponse;
import com.example.myapp.models.ApiCallLog;
import com.example.myapp.repositories.ApiCallLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ApiCallLogService {

    private final ApiCallLogRepository apiCallLogRepository;

    @Autowired
    public ApiCallLogService(ApiCallLogRepository apiCallLogRepository) {
        this.apiCallLogRepository = apiCallLogRepository;
    }

    public void log(String apiName, String callerName, String userType, String userLevel, String department) {
        ApiCallLog logEntry = new ApiCallLog(apiName, callerName, userType, userLevel, department);
        apiCallLogRepository.save(logEntry);
    }

    public CallStatsResponse getStats() {
        CallStatsResponse response = new CallStatsResponse();
        response.setTotalCalls(apiCallLogRepository.count());

        response.setByUserType(toDimensionStats("userType", apiCallLogRepository.countByUserType()));
        response.setByUserLevel(toDimensionStats("userLevel", apiCallLogRepository.countByUserLevel()));
        response.setByDepartment(toDimensionStats("department", apiCallLogRepository.countByDepartment()));
        response.setTrendByDay(toTrendPoints(apiCallLogRepository.countByDay()));

        return response;
    }

    private List<CallStatsResponse.DimensionStat> toDimensionStats(String dimension, List<Object[]> rows) {
        List<CallStatsResponse.DimensionStat> stats = new ArrayList<>();
        for (Object[] row : rows) {
            String value = (String) row[0];
            long count = ((Number) row[1]).longValue();
            stats.add(new CallStatsResponse.DimensionStat(dimension, value, count));
        }
        return stats;
    }

    private List<CallStatsResponse.TrendPoint> toTrendPoints(List<Object[]> rows) {
        List<CallStatsResponse.TrendPoint> points = new ArrayList<>();
        for (Object[] row : rows) {
            String date = String.valueOf(row[0]);
            long count = ((Number) row[1]).longValue();
            points.add(new CallStatsResponse.TrendPoint(date, count));
        }
        return points;
    }
}
