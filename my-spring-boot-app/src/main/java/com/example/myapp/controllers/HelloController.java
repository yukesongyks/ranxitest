package com.example.myapp.controllers;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

    @GetMapping("/api/hello")
    @ResponseBody
    public Map<String, String> getApiHello() {
        return Map.of("message", "Hello, World!");
    }

    @GetMapping("/hello")
    public String getHello() {
        return "hello";
    }
}
