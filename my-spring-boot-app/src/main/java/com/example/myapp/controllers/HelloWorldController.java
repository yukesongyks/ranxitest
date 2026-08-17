package com.example.myapp.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HelloWorld 控制器，提供示例问候接口。
 */
@RestController
public class HelloWorldController {

    /**
     * 返回 Hello World 问候语。
     *
     * @return Hello World 字符串
     */
    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }
}
