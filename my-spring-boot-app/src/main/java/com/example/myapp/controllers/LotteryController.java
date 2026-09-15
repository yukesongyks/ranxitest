package com.example.myapp.controllers;

import com.example.myapp.services.LotteryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/lottery")
public class LotteryController {

    private final LotteryService lotteryService;

    @Autowired
    public LotteryController(LotteryService lotteryService) {
        this.lotteryService = lotteryService;
    }

    @GetMapping
    public String lotteryPage() {
        return "lottery";
    }

    @GetMapping("/draw")
    @ResponseBody
    public Map<String, Object> draw() {
        return Map.of("number", lotteryService.draw());
    }
}
