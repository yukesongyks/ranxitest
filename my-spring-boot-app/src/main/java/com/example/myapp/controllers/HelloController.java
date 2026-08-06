package com.example.myapp.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello 接口，返回简单的问候语，用于验证服务可用性。
 */
@RestController
public class HelloController {

    /**
     * GET /hello
     * 返回固定的 "Hello, World!" 字符串。
     *
     * @return 问候语
     */
    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }
}
