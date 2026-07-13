package com.example.myapp.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello World 控制器
 * 提供基本的 Hello World REST 端点
 */
@RestController
@RequestMapping("/api")
public class HelloController {

    /**
     * 返回 Hello World 消息
     *
     * @return Hello World 字符串
     */
    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }
}