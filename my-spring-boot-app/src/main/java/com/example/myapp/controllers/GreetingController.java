package com.example.myapp.controllers;

import com.example.myapp.services.GreetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 问候模块 Controller。
 *
 * W01: GET /api/hello      — 纯文本 "你好"（冒烟/健康检查）
 * O01: GET /openapi/hello  — JSON 包装 {code, msg, data:{greeting}}（对外接口）
 */
@RestController
public class GreetingController {

    private final GreetingService greetingService;

    @Autowired
    public GreetingController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    /**
     * W01 输出你好（页面版）。
     * 返回纯文本 "你好"，Content-Type: text/plain; charset=UTF-8。
     */
    @GetMapping(value = "/api/hello", produces = MediaType.TEXT_PLAIN_VALUE + "; charset=UTF-8")
    public String helloPlain() {
        return greetingService.getGreeting();
    }

    /**
     * O01 输出你好（对外版）。
     * 返回 JSON 通用出参 {code, msg, data:{greeting}}。
     */
    @GetMapping(value = "/openapi/hello", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=UTF-8")
    public Map<String, Object> helloJson() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("greeting", greetingService.getGreeting());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("code", "OK");
        response.put("msg", "SUCCESS");
        response.put("data", data);
        return response;
    }
}
