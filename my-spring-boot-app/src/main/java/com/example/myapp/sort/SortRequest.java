package com.example.myapp.sort;

import java.util.List;

/**
 * 冒泡排序请求 DTO。
 */
public class SortRequest {

    private List<Integer> numbers;
    private String order;

    public List<Integer> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<Integer> numbers) {
        this.numbers = numbers;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
