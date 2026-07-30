package com.example.myapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 前端展示页面路由 (I05)。
 * 返回 Thymeleaf 视图 algorithm/show。
 */
@Controller
@RequestMapping("/algorithm")
public class AlgorithmPageController {

    @GetMapping
    public String show() {
        return "algorithm/show";
    }
}
