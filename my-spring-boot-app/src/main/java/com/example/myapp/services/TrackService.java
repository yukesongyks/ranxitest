package com.example.myapp.services;

import com.example.myapp.common.BizException;
import com.example.myapp.common.ErrorCode;
import com.example.myapp.dto.StatisticsVO;
import com.example.myapp.enums.CallResult;
import com.example.myapp.enums.Dimension;
import com.example.myapp.models.CallLog;
import com.example.myapp.models.User;
import com.example.myapp.repositories.CallLogRepository;
import com.example.myapp.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 埋点服务，提供异步调用日志记录和多维度统计查询。
 */
@Service
public class TrackService {

    private static final Logger log = LoggerFactory.getLogger(TrackService.class);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final CallLogRepository callLogRepository;
    private final UserRepository userRepository;

    @Autowired
    public TrackService(CallLogRepository callLogRepository, UserRepository userRepository) {
        this.callLogRepository = callLogRepository;
        this.userRepository = userRepository;
    }

    /**
     * 记录接口调用日志（异步调用，异常不传播）。
     *
     * @param apiName 接口名称
     * @param userId  调用人ID
     * @param duration 调用耗时（毫秒）
     * @param result  调用结果
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void recordCall(String apiName, Long userId, Long duration, String result) {
        try {
            CallLog callLog = new CallLog();
            callLog.setApiName(apiName);
            callLog.setUserId(userId);
            callLog.setCallTime(LocalDateTime.now());
            callLog.setDuration(duration);
            callLog.setResult(result);

            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                callLog.setUserName(user.getUsername());
                callLog.setUserType(user.getUserType());
                callLog.setUserLevel(user.getUserLevel());
                callLog.setDepartment(user.getDepartment());
            } else {
                log.warn("埋点时用户不存在, userId={}", userId);
                callLog.setUserName("unknown");
            }
            callLogRepository.save(callLog);
        } catch (Exception e) {
            log.error("埋点写入失败, apiName={}, userId={}", apiName, userId, e);
        }
    }

    /**
     * 多维度统计查询。
     *
     * @param dimension 统计维度
     * @param startDate  开始日期（yyyy-MM-dd，可选）
     * @param endDate    结束日期（yyyy-MM-dd，可选）
     * @return 统计结果 VO
     */
    @Transactional(readOnly = true)
    public StatisticsVO statistics(String dimension, String startDate, String endDate) {
        Dimension dim = Dimension.fromString(dimension);
        if (dim == null) {
            throw new BizException(ErrorCode.TRACK_001, ErrorCode.MSG_DIMENSION_INVALID);
        }

        LocalDateTime start = parseStart(startDate);
        LocalDateTime end = parseEnd(endDate);
        if (start != null && end != null && start.isAfter(end)) {
            throw new BizException(ErrorCode.TRACK_002, ErrorCode.MSG_DATE_FORMAT_ERROR);
        }

        List<StatisticsVO.StatisticsItem> items = aggregateByDimension(dim, start, end);
        return new StatisticsVO(dim.name(), items);
    }

    private List<StatisticsVO.StatisticsItem> aggregateByDimension(Dimension dim,
                                                                     LocalDateTime start,
                                                                     LocalDateTime end) {
        List<Object[]> rows;
        switch (dim) {
            case USER_TYPE:
                rows = callLogRepository.countByUserType();
                break;
            case USER_LEVEL:
                rows = callLogRepository.countByUserLevel();
                break;
            case DEPARTMENT:
                rows = callLogRepository.countByDepartment();
                break;
            case API_NAME:
                rows = callLogRepository.countByApiName();
                break;
            default:
                rows = Collections.emptyList();
        }
        List<StatisticsVO.StatisticsItem> items = new ArrayList<>();
        for (Object[] row : rows) {
            String label = String.valueOf(row[0]);
            Long count = (Long) row[1];
            items.add(new StatisticsVO.StatisticsItem(label, count));
        }

        if (start != null && end != null) {
            attachTrendData(dim, items, start, end);
        }
        return items;
    }

    private void attachTrendData(Dimension dim, List<StatisticsVO.StatisticsItem> items,
                                LocalDateTime start, LocalDateTime end) {
        List<Object[]> trendRows;
        switch (dim) {
            case USER_TYPE:
                trendRows = callLogRepository.trendByUserType(start, end);
                break;
            case USER_LEVEL:
                trendRows = callLogRepository.trendByUserLevel(start, end);
                break;
            case DEPARTMENT:
                trendRows = callLogRepository.trendByDepartment(start, end);
                break;
            case API_NAME:
                trendRows = callLogRepository.trendByApiName(start, end);
                break;
            default:
                return;
        }
        Map<String, List<StatisticsVO.TrendPoint>> trendMap = new LinkedHashMap<>();
        for (Object[] row : trendRows) {
            String date = String.valueOf(row[0]);
            String label = String.valueOf(row[1]);
            Long count = (Long) row[2];
            trendMap.computeIfAbsent(label, k -> new ArrayList<>())
                    .add(new StatisticsVO.TrendPoint(date, count));
        }
        for (StatisticsVO.StatisticsItem item : items) {
            item.setTrend(trendMap.getOrDefault(item.getLabel(), Collections.emptyList()));
        }
    }

    private LocalDateTime parseStart(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr.trim(), DATE_FMT).atStartOfDay();
        } catch (DateTimeParseException e) {
            throw new BizException(ErrorCode.TRACK_002, ErrorCode.MSG_DATE_FORMAT_ERROR);
        }
    }

    private LocalDateTime parseEnd(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr.trim(), DATE_FMT).atTime(23, 59, 59);
        } catch (DateTimeParseException e) {
            throw new BizException(ErrorCode.TRACK_002, ErrorCode.MSG_DATE_FORMAT_ERROR);
        }
    }
}
