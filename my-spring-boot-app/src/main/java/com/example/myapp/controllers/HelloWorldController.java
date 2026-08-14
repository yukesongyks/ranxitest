package com.example.myapp.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HelloWorld 示例控制器。
 */
@RestController
public class HelloWorldController {

    /**
     * 返回 Hello, World! 问候语。
     *
     * @return 问候字符串
     */
    @GetMapping("/api/hello")
    public String hello() {
        return "Hello, World!";
    }
}