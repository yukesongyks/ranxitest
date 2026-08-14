package com.example.myapp.services;

import com.example.myapp.models.AgentExecuteConfig;
import com.example.myapp.models.AgentStage;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Agent 执行服务
 * 根据 YAML 配置执行 Agent 任务
 */
@Service
public class AgentService {

    /**
     * 执行 Agent 阶段任务
     *
     * @param stage Agent 阶段配置
     * @return 执行结果
     */
    public String executeStage(AgentStage stage) {
        if (stage == null || stage.getConfig() == null) {
            throw new IllegalArgumentException("AgentStage 配置不能为空");
        }

        AgentExecuteConfig config = stage.getConfig();
        int retryCount = config.getRetryCount();
        int timeout = config.getTimeout();

        String result = null;
        int attempts = 0;

        while (attempts <= retryCount) {
            try {
                result = executeWithTimeout(stage, timeout);
                break;
            } catch (Exception e) {
                attempts++;
                if (attempts > retryCount) {
                    return String.format("Agent 执行失败，已重试 %d 次: %s", retryCount, e.getMessage());
                }
            }
        }

        return result;
    }

    /**
     * 执行默认的 "编码实现" 阶段
     *
     * @return 执行结果
     */
    public String executeDefaultCodingStage() {
        AgentStage stage = AgentStage.createDefaultCodingStage();
        return executeStage(stage);
    }

    /**
     * 带超时控制的执行
     */
    private String executeWithTimeout(AgentStage stage, int timeoutSeconds) throws Exception {
        return CompletableFuture.supplyAsync(() -> executeAgentLogic(stage))
                .get(timeoutSeconds, TimeUnit.SECONDS);
    }

    /**
     * Agent 业务逻辑
     */
    private String executeAgentLogic(AgentStage stage) {
        String agent = stage.getAgent();
        String prompt = stage.getPrompt();
        String stageName = stage.getStage();

        System.out.printf("[%s] Agent '%s' 正在处理任务: %s%n", stageName, agent, prompt);

        if ("dtcoder".equals(agent)) {
            return handleDtcoderAgent(prompt);
        }

        return String.format("Agent '%s' 执行完成，提示: %s", agent, prompt);
    }

    /**
     * 处理 dtcoder Agent 的逻辑
     */
    private String handleDtcoderAgent(String prompt) {
        if (prompt.contains("hello world") || prompt.contains("Hello World")) {
            return "public class HelloWorld {\n" +
                    "    public static void main(String[] args) {\n" +
                    "        System.out.println(\"Hello World\");\n" +
                    "    }\n" +
                    "}";
        }

        return "DTCoder 已收到请求，正在处理: " + prompt;
    }
}
