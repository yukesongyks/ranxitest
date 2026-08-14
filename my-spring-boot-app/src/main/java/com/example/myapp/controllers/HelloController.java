package com.example.myapp.controllers;

import com.example.myapp.services.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 问候控制器，对外提供 REST 问候接口。
 *
 * @author DTCoder
 */
@RestController
@RequestMapping("/api")
public class HelloController {

    private final HelloService helloService;

    @Autowired
    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    /**
     * 获取问候消息。
     *
     * @param name 可选的名字参数，缺省时返回 "Hello, World!"
     * @return 包含问候消息的 JSON 响应
     */
    @GetMapping("/hello")
    public Map<String, String> hello(@RequestParam(required = false) String name) {
        String message = helloService.greet(name);
        return Map.of("message", message);
    }
}
