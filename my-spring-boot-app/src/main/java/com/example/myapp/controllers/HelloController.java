package com.example.myapp.controllers;

import com.example.myapp.services.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello 控制器，提供 Hello World REST API。
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
     * 返回问候语。
     *
     * @return 问候语字符串
     */
    @GetMapping("/hello")
    public String hello() {
        return helloService.greet();
    }
}