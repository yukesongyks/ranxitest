package com.example.myapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/")
    public String getHome() {
        return "redirect:/items";
    }

    @GetMapping("/hello")
    @ResponseBody
    public String helloWorld() {
        return "Hello World!";
    }
}