package com.example.myapp.sort;

import java.util.List;

/**
 * 冒泡排序结果数据。
 *
 * @author AiWork
 * @date 2026-09-15
 */
public class SortResult {

    /** 排序后整数数组。 */
    private List<Integer> sorted;

    /** 实际使用的排序方向。 */
    private String order;

    /** 参与排序的元素数量。 */
    private Integer count;

    /**
     * 构造排序结果。
     *
     * @param sorted 排序后数组
     * @param order  排序方向
     * @param count  元素数量
     */
    public SortResult(List<Integer> sorted, String order, Integer count) {
        this.sorted = sorted;
        this.order = order;
        this.count = count;
    }

    /**
     * 获取排序后数组。
     *
     * @return 排序后数组
     */
    public List<Integer> getSorted() {
        return sorted;
    }

    /**
     * 设置排序后数组。
     *
     * @param sorted 排序后数组
     */
    public void setSorted(List<Integer> sorted) {
        this.sorted = sorted;
    }

    /**
     * 获取排序方向。
     *
     * @return 排序方向
     */
    public String getOrder() {
        return order;
    }

    /**
     * 设置排序方向。
     *
     * @param order 排序方向
     */
    public void setOrder(String order) {
        this.order = order;
    }

    /**
     * 获取元素数量。
     *
     * @return 元素数量
     */
    public Integer getCount() {
        return count;
    }

    /**
     * 设置元素数量。
     *
     * @param count 元素数量
     */
    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "SortResult{sorted=" + sorted + ", order='" + order + "', count=" + count + "}";
    }
}
