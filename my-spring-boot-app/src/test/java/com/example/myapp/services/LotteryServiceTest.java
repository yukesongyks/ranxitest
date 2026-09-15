package com.example.myapp.services;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LotteryServiceTest {

    private final LotteryService lotteryService = new LotteryService();

    @Test
    void drawReturnsFiveNumbers() {
        List<Integer> result = lotteryService.draw();
        assertEquals(5, result.size());
    }

    @Test
    void drawNumbersAreWithinRange() {
        List<Integer> result = lotteryService.draw();
        for (Integer n : result) {
            assertTrue(n >= 1 && n <= 999, "数字越界: " + n);
        }
    }

    @Test
    void drawNumbersAreDistinct() {
        List<Integer> result = lotteryService.draw();
        assertEquals(5, result.stream().distinct().count(), "存在重复数字");
    }

    @Test
    void drawNumbersAreSortedAscending() {
        List<Integer> result = lotteryService.draw();
        for (int i = 1; i < result.size(); i++) {
            assertTrue(result.get(i - 1) < result.get(i), "结果未升序排列");
        }
    }

    @Test
    void drawIsRandomAcrossInvocations() {
        // 多次抽取，至少出现两组不同结果，验证随机性（理论上 1-999 取 5 个组合极多，碰撞概率可忽略）
        boolean differ = false;
        List<Integer> first = lotteryService.draw();
        for (int i = 0; i < 10 && !differ; i++) {
            differ = !first.equals(lotteryService.draw());
        }
        assertTrue(differ, "多次摇号结果完全相同，疑似未随机");
    }
}
