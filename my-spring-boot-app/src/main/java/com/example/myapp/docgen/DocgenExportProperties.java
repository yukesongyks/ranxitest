package com.example.myapp.docgen;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * docgen 导出功能配置项。
 *
 * <p>对应配置前缀 docgen.export：功能开关（可应急秒级关闭）与生成超时阈值。</p>
 */
@Component
@ConfigurationProperties(prefix = "docgen.export")
public class DocgenExportProperties {

    /** 功能是否开启，默认开启。 */
    private boolean enabled = true;

    /** 单次生成超时阈值（毫秒），默认 10000。 */
    private long timeoutMs = 10000L;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }
}