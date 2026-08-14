package com.example.myapp.models;

/**
 * Agent 执行配置模型
 * 对应 YAML 配置中的 config.execute 部分
 */
public class AgentExecuteConfig {

    private String humanAgentType;
    private int timeout;
    private int retryCount;

    public AgentExecuteConfig() {
    }

    public AgentExecuteConfig(String humanAgentType, int timeout, int retryCount) {
        this.humanAgentType = humanAgentType;
        this.timeout = timeout;
        this.retryCount = retryCount;
    }

    public String getHumanAgentType() {
        return humanAgentType;
    }

    public void setHumanAgentType(String humanAgentType) {
        this.humanAgentType = humanAgentType;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    @Override
    public String toString() {
        return "AgentExecuteConfig{" +
                "humanAgentType='" + humanAgentType + '\'' +
                ", timeout=" + timeout +
                ", retryCount=" + retryCount +
                '}';
    }
}
