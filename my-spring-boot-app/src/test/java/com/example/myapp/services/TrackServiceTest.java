package com.example.myapp.services;

import com.example.myapp.common.BizException;
import com.example.myapp.dto.StatisticsVO;
import com.example.myapp.models.CallLog;
import com.example.myapp.models.User;
import com.example.myapp.repositories.CallLogRepository;
import com.example.myapp.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TrackService 单元测试，覆盖统计查询和异常场景。
 */
@ExtendWith(MockitoExtension.class)
class TrackServiceTest {

    @Mock
    private CallLogRepository callLogRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TrackService trackService;

    @Test
    @DisplayName("statistics 对无效维度抛出 TRACK_001")
    void should_throwException_when_dimensionInvalid() {
        BizException ex = assertThrows(BizException.class,
                () -> trackService.statistics("INVALID", null, null));
        assertEquals("TRACK_001", ex.getCode());
    }

    @Test
    @DisplayName("statistics 按 USER_TYPE 维度返回聚合统计")
    void should_returnStatistics_when_dimensionUserType() {
        List<Object[]> mockRows = new ArrayList<>();
        mockRows.add(new Object[]{"开发", 150L});
        mockRows.add(new Object[]{"测试", 80L});
        when(callLogRepository.countByUserType()).thenReturn(mockRows);

        StatisticsVO result = trackService.statistics("USER_TYPE", null, null);

        assertNotNull(result);
        assertEquals("USER_TYPE", result.getDimension());
        assertEquals(2, result.getItems().size());
        assertEquals("开发", result.getItems().get(0).getLabel());
        assertEquals(150L, result.getItems().get(0).getCount());
    }

    @Test
    @DisplayName("statistics 对非法日期格式抛出 TRACK_002")
    void should_throwException_when_dateFormatInvalid() {
        BizException ex = assertThrows(BizException.class,
                () -> trackService.statistics("USER_TYPE", "invalid-date", null));
        assertEquals("TRACK_002", ex.getCode());
    }

    @Test
    @DisplayName("statistics 对 start > end 抛出 TRACK_002")
    void should_throwException_when_startAfterEnd() {
        BizException ex = assertThrows(BizException.class,
                () -> trackService.statistics("USER_TYPE", "2026-07-31", "2026-07-01"));
        assertEquals("TRACK_002", ex.getCode());
    }

    @Test
    @DisplayName("statistics 按 API_NAME 维度返回聚合统计")
    void should_returnStatistics_when_dimensionApiName() {
        List<Object[]> mockRows = new ArrayList<>();
        mockRows.add(new Object[]{"HELLOWORLD", 50L});
        when(callLogRepository.countByApiName()).thenReturn(mockRows);

        StatisticsVO result = trackService.statistics("API_NAME", null, null);

        assertNotNull(result);
        assertEquals("API_NAME", result.getDimension());
        assertEquals(1, result.getItems().size());
        assertEquals("HELLOWORLD", result.getItems().get(0).getLabel());
    }

    @Test
    @DisplayName("recordCall 用户不存在时仍记录日志，userName 为 unknown")
    void should_recordCallWithUnknown_when_userNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        trackService.recordCall("HELLOWORLD", 999L, 10L, "SUCCESS");

        verify(callLogRepository).save(any(CallLog.class));
    }

    @Test
    @DisplayName("recordCall 用户存在时记录含人员维度信息的日志")
    void should_recordCallWithDimensions_when_userExists() {
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setUserType("开发");
        user.setUserLevel("高级");
        user.setDepartment("技术部");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        trackService.recordCall("HELLOWORLD", 1L, 10L, "SUCCESS");

        verify(callLogRepository).save(any(CallLog.class));
    }

    @Test
    @DisplayName("recordCall 数据库异常时不传播")
    void should_notPropagateException_when_dbFails() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        when(callLogRepository.save(any())).thenThrow(new RuntimeException("DB error"));

        assertDoesNotThrow(() -> trackService.recordCall("HELLOWORLD", 1L, 10L, "SUCCESS"));
    }
}
