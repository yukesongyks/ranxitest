package com.example.myapp.controllers;

import com.example.myapp.services.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 问候控制器
 * <p>提供 Hello World 相关的 REST API 接口</p>
 *
 * @author DTCoder
 */
@RestController
@RequestMapping("/api/hello")
public class HelloController {

    private final HelloService helloService;

    /**
     * 构造方法注入 HelloService
     *
     * @param helloService 问候服务
     */
    @Autowired
    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    /**
     * 获取默认问候消息
     *
     * @return 包含问候消息的响应
     */
    @GetMapping
    public ResponseEntity<Map<String, String>> sayHello() {
        String message = helloService.sayHello();
        return ResponseEntity.ok(buildResponse(message));
    }

    /**
     * 获取带姓名的问候消息
     *
     * @param name 用户姓名
     * @return 包含问候消息的响应
     */
    @GetMapping("/to")
    public ResponseEntity<Map<String, String>> sayHelloTo(@RequestParam String name) {
        String message = helloService.sayHelloTo(name);
        return ResponseEntity.ok(buildResponse(message));
    }

    /**
     * 构建响应体
     *
     * @param message 问候消息
     * @return 响应体 Map
     */
    private Map<String, String> buildResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return response;
    }
}
