package com.example.myapp.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello World REST controller — 项目基础入口演示端点.
 */
@RestController
public class HelloWorldController {

    /**
     * 返回 "Hello, World!" 问候语.
     *
     * @return 问候语字符串
     */
    @GetMapping("/api/hello")
    public String sayHello() {
        return "Hello, World!";
    }
}