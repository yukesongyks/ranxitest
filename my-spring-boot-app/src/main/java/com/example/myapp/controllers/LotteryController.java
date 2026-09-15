package com.example.myapp.controllers;

import com.example.myapp.services.LotteryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class LotteryController {

    private final LotteryService lotteryService;

    public LotteryController(LotteryService lotteryService) {
        this.lotteryService = lotteryService;
    }

    @GetMapping("/lottery")
    public String lotteryPage() {
        return "lottery/index";
    }

    @GetMapping("/api/lottery/draw")
    @ResponseBody
    public List<Integer> draw() {
        return lotteryService.draw();
    }
}
