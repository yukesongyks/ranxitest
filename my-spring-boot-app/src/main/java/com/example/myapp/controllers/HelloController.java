package com.example.myapp.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello World 示例控制器
 * <p>
 * 提供简单的 REST 接口，用于验证应用是否正常运行。
 * </p>
 *
 * @author DTCoder
 * @date 2026/07/13
 */
@RestController
public class HelloController {

    /**
     * 返回 Hello World 字符串
     *
     * @return "Hello World"
     */
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello World";
    }
}