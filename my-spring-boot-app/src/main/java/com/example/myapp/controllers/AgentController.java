package com.example.myapp.controllers;

import com.example.myapp.models.AgentStage;
import com.example.myapp.services.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Agent 执行控制器
 * 提供 REST API 接口执行 Agent 任务
 */
@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final AgentService agentService;

    @Autowired
    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    /**
     * 执行 Agent 阶段任务
     *
     * @param stage Agent 阶段配置
     * @return 执行结果
     */
    @PostMapping("/execute")
    public ResponseEntity<Map<String, Object>> executeStage(@RequestBody AgentStage stage) {
        String result = agentService.executeStage(stage);

        Map<String, Object> response = new HashMap<>();
        response.put("stage", stage.getStage());
        response.put("agent", stage.getAgent());
        response.put("result", result);
        response.put("success", !result.contains("失败"));

        return ResponseEntity.ok(response);
    }

    /**
     * 执行默认的 "编码实现" 阶段
     *
     * @return 执行结果
     */
    @GetMapping("/execute/default")
    public ResponseEntity<Map<String, Object>> executeDefaultStage() {
        String result = agentService.executeDefaultCodingStage();

        Map<String, Object> response = new HashMap<>();
        response.put("stage", "编码实现");
        response.put("agent", "dtcoder");
        response.put("prompt", "帮我写个hello world");
        response.put("result", result);
        response.put("success", !result.contains("失败"));

        return ResponseEntity.ok(response);
    }
}
