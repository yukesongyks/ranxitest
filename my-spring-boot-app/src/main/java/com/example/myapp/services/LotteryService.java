package com.example.myapp.services;

import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 摇号业务逻辑：返回 1-99（含）之间的随机整数。
 */
@Service
public class LotteryService {

    public int draw() {
        return ThreadLocalRandom.current().nextInt(1, 100);
    }
}
