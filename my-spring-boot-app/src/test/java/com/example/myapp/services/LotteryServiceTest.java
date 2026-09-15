package com.example.myapp.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LotteryServiceTest {

    private final LotteryService lotteryService = new LotteryService();

    @Test
    void draw_returnsNumberBetween1And99() {
        for (int i = 0; i < 10_000; i++) {
            int number = lotteryService.draw();
            assertTrue(number >= 1, "摇号结果不应小于 1，实际: " + number);
            assertTrue(number <= 99, "摇号结果不应大于 99，实际: " + number);
        }
    }
}
