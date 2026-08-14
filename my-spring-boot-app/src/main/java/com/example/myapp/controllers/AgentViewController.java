package com.example.myapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Agent 视图控制器
 * 提供 Agent 执行器的页面视图
 */
@Controller
public class AgentViewController {

    /**
     * Agent 执行器页面
     *
     * @return Agent 页面视图
     */
    @GetMapping("/agent")
    public String agentPage() {
        return "agent";
    }
}
