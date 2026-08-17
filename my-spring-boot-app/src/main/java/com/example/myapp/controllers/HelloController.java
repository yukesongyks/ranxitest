package com.example.myapp.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello World 控制器。
 * 提供基础问候接口。
 */
@RestController
public class HelloController {

    /**
     * 返回 Hello World 问候语。
     *
     * @return "Hello, World!"
     */
    @GetMapping("/api/hello")
    public String hello() {
        return "Hello, World!";
    }
}