package com.example.myapp.aspect;

import com.example.myapp.services.TrackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 异步埋点记录助手，独立 bean 以确保 @Async 代理生效。
 * TrackAspect 通过注入此 bean 调用，避免 self-invocation 导致 @Async 失效。
 */
@Component
public class TrackAsyncHelper {

    private static final Logger log = LoggerFactory.getLogger(TrackAsyncHelper.class);

    private final TrackService trackService;

    @Autowired
    public TrackAsyncHelper(TrackService trackService) {
        this.trackService = trackService;
    }

    /**
     * 异步记录调用日志，异常仅记日志不传播。
     */
    @Async
    public void recordCallAsync(String apiName, Long userId, Long duration, String result) {
        if (userId == null) {
            log.warn("userId 为空，跳过埋点记录, apiName={}", apiName);
            return;
        }
        try {
            trackService.recordCall(apiName, userId, duration, result);
        } catch (Exception e) {
            log.error("埋点异步写入失败, apiName={}, userId={}", apiName, userId, e);
        }
    }
}
