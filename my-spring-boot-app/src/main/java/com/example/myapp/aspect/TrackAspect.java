package com.example.myapp.aspect;

import com.example.myapp.annotation.TrackCall;
import com.example.myapp.enums.CallResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 埋点 AOP 切面，拦截 @TrackCall 注解的方法。
 * 方法成功返回后通过 TrackAsyncHelper 异步记录调用日志；方法异常时记录 FAIL 结果。
 * 埋点异常不传播，不影响主业务流程。
 */
@Aspect
@Component
public class TrackAspect {

    private static final Logger log = LoggerFactory.getLogger(TrackAspect.class);

    private final TrackAsyncHelper trackAsyncHelper;

    @Autowired
    public TrackAspect(TrackAsyncHelper trackAsyncHelper) {
        this.trackAsyncHelper = trackAsyncHelper;
    }

    @Around("@annotation(trackCall)")
    public Object track(ProceedingJoinPoint joinPoint, TrackCall trackCall) throws Throwable {
        long startTime = System.currentTimeMillis();
        String apiName = trackCall.value().name();
        Long userId = null;

        try {
            userId = extractUserId(joinPoint);
        } catch (Exception e) {
            log.warn("埋点切面提取 userId 失败, apiName={}", apiName, e);
        }

        Object result;
        String callResult = CallResult.SUCCESS.name();
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            callResult = CallResult.FAIL.name();
            trackAsyncHelper.recordCallAsync(apiName, userId,
                    System.currentTimeMillis() - startTime, callResult);
            throw throwable;
        }

        long duration = System.currentTimeMillis() - startTime;
        trackAsyncHelper.recordCallAsync(apiName, userId, duration, callResult);
        return result;
    }

    /**
     * 从方法参数中提取 userId。
     * 支持直接参数、@RequestParam 注解参数、以及 Request DTO 中的 userId 字段。
     */
    private Long extractUserId(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg == null) {
                continue;
            }
            if (arg instanceof Long) {
                return (Long) arg;
            }
            if (arg instanceof Number) {
                return ((Number) arg).longValue();
            }
            Long extracted = tryExtractUserIdFromObject(arg);
            if (extracted != null) {
                return extracted;
            }
        }
        return null;
    }

    private Long tryExtractUserIdFromObject(Object obj) {
        try {
            Field field = obj.getClass().getDeclaredField("userId");
            field.setAccessible(true);
            Object value = field.get(obj);
            if (value instanceof Long) {
                return (Long) value;
            }
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            // 不是 DTO 类，忽略
        }
        return null;
    }
}
