package com.example.myapp.docgen;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * docgen 导出监控埋点（进程内累加计数）。
 *
 * <p>对应设计 §6.5/§7.1：记录导出请求次数、成功数、失败数（按错误码）、
 * 累计耗时、累计行数、累计体积，供监控系统聚合平均耗时/平均行数
 * 并触发失败率/超时告警；无第三方监控组件，采用内存计数 + 请求摘要日志。</p>
 */
@Component
public class DocgenExportMetrics {

    /** 导出请求次数。 */
    private final AtomicLong requestCount = new AtomicLong();

    /** 导出成功次数。 */
    private final AtomicLong successCount = new AtomicLong();

    /** 失败次数（按错误码）。 */
    private final Map<String, AtomicLong> failureCountByCode = new ConcurrentHashMap<>();

    /** 导出累计耗时（毫秒）。 */
    private final AtomicLong totalElapsedMs = new AtomicLong();

    /** 导出累计行数。 */
    private final AtomicLong totalRows = new AtomicLong();

    /** 导出累计体积（字节）。 */
    private final AtomicLong totalBytes = new AtomicLong();

    /**
     * 记录一次导出请求。
     */
    public void recordRequest() {
        requestCount.incrementAndGet();
    }

    /**
     * 记录一次导出成功。
     *
     * @param elapsedMs 本次生成耗时（毫秒）
     * @param rows      本次导出行数（不含表头）
     * @param bytes     本次导出体积（字节）
     */
    public void recordSuccess(long elapsedMs, int rows, long bytes) {
        successCount.incrementAndGet();
        totalElapsedMs.addAndGet(elapsedMs);
        totalRows.addAndGet(rows);
        totalBytes.addAndGet(bytes);
    }

    /**
     * 记录一次导出失败。
     *
     * @param errorCode 错误码（DOCGEN_001/002/003）
     */
    public void recordFailure(String errorCode) {
        failureCountByCode.computeIfAbsent(errorCode, k -> new AtomicLong()).incrementAndGet();
    }

    public long getRequestCount() {
        return requestCount.get();
    }

    public long getSuccessCount() {
        return successCount.get();
    }

    /**
     * 获取指定错误码的失败次数。
     *
     * @param errorCode 错误码
     * @return 失败次数（无记录时为 0）
     */
    public long getFailureCount(String errorCode) {
        AtomicLong count = failureCountByCode.get(errorCode);
        return count == null ? 0L : count.get();
    }

    public long getTotalElapsedMs() {
        return totalElapsedMs.get();
    }

    public long getTotalRows() {
        return totalRows.get();
    }

    public long getTotalBytes() {
        return totalBytes.get();
    }
}