package com.example.myapp.models.dto;

import java.util.List;

/**
 * 冒泡排序接口出参 (I03)。
 */
public class BubbleSortResult {

    private final List<Integer> sorted;
    private final int swaps;
    private final long costMs;
    private final List<Integer> original;

    public BubbleSortResult(List<Integer> sorted, int swaps, long costMs, List<Integer> original) {
        this.sorted = sorted;
        this.swaps = swaps;
        this.costMs = costMs;
        this.original = original;
    }

    public List<Integer> getSorted() {
        return sorted;
    }

    public int getSwaps() {
        return swaps;
    }

    public long getCostMs() {
        return costMs;
    }

    public List<Integer> getOriginal() {
        return original;
    }
}
