package com.example.myapp.services;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class LotteryService {

    private static final int COUNT = 5;
    private static final int MIN = 1;
    private static final int MAX_INCLUSIVE = 999;

    /**
     * 摇号：返回 5 个互不重复、升序排列的随机整数，范围 [1, 999]。
     */
    public List<Integer> draw() {
        return ThreadLocalRandom.current()
                .ints(MIN, MAX_INCLUSIVE + 1)
                .distinct()
                .limit(COUNT)
                .boxed()
                .sorted()
                .collect(Collectors.toList());
    }
}
