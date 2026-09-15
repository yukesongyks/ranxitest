package com.example.myapp.sort;

import java.util.List;

/**
 * 冒泡排序请求 DTO。
 *
 * @author AiWork
 * @date 2026-09-15
 */
public class SortRequest {

    /** 待排序整数数组。 */
    private List<Integer> numbers;

    /** 排序方向，ASC=升序（默认），DESC=降序。 */
    private String order;

    /**
     * 获取待排序整数数组。
     *
     * @return 整数数组
     */
    public List<Integer> getNumbers() {
        return numbers;
    }

    /**
     * 设置待排序整数数组。
     *
     * @param numbers 整数数组
     */
    public void setNumbers(List<Integer> numbers) {
        this.numbers = numbers;
    }

    /**
     * 获取排序方向。
     *
     * @return 排序方向字符串
     */
    public String getOrder() {
        return order;
    }

    /**
     * 设置排序方向。
     *
     * @param order 排序方向字符串
     */
    public void setOrder(String order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "SortRequest{numbers=" + numbers + ", order='" + order + "'}";
    }
}
