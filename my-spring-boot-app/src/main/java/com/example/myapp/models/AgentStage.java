package com.example.myapp.models;

/**
 * Agent 阶段配置模型
 * 对应 YAML 配置中的 stage 定义
 */
public class AgentStage {

    private String stage;
    private String type;
    private String prompt;
    private String agent;
    private AgentExecuteConfig config;

    public AgentStage() {
    }

    public AgentStage(String stage, String type, String prompt, String agent, AgentExecuteConfig config) {
        this.stage = stage;
        this.type = type;
        this.prompt = prompt;
        this.agent = agent;
        this.config = config;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public AgentExecuteConfig getConfig() {
        return config;
    }

    public void setConfig(AgentExecuteConfig config) {
        this.config = config;
    }

    /**
     * 创建默认的 "编码实现" Agent 配置
     */
    public static AgentStage createDefaultCodingStage() {
        AgentExecuteConfig executeConfig = new AgentExecuteConfig("auto", 30, 2);
        return new AgentStage("编码实现", "agent", "帮我写个hello world", "dtcoder", executeConfig);
    }

    @Override
    public String toString() {
        return "AgentStage{" +
                "stage='" + stage + '\'' +
                ", type='" + type + '\'' +
                ", prompt='" + prompt + '\'' +
                ", agent='" + agent + '\'' +
                ", config=" + config +
                '}';
    }
}
