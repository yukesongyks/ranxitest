package com.example.myapp.sort;

import java.util.List;

/**
 * 冒泡排序结果数据。
 */
public class SortResult {

    private List<Integer> sorted;
    private String order;
    private int count;

    public SortResult(List<Integer> sorted, String order, int count) {
        this.sorted = sorted;
        this.order = order;
        this.count = count;
    }

    public List<Integer> getSorted() {
        return sorted;
    }

    public void setSorted(List<Integer> sorted) {
        this.sorted = sorted;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
