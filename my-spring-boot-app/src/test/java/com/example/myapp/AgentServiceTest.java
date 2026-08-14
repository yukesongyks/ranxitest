package com.example.myapp;

import com.example.myapp.models.AgentExecuteConfig;
import com.example.myapp.models.AgentStage;
import com.example.myapp.services.AgentService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AgentServiceTest {

    @Test
    void testExecuteDefaultCodingStage() {
        AgentService agentService = new AgentService();
        String result = agentService.executeDefaultCodingStage();

        assertNotNull(result);
        assertTrue(result.contains("Hello World"));
    }

    @Test
    void testExecuteStageWithRetry() {
        AgentService agentService = new AgentService();
        AgentExecuteConfig config = new AgentExecuteConfig("auto", 30, 2);
        AgentStage stage = new AgentStage("编码实现", "agent", "帮我写个hello world", "dtcoder", config);

        String result = agentService.executeStage(stage);

        assertNotNull(result);
        assertTrue(result.contains("Hello World"));
    }
}
