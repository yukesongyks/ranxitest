package com.example.myapp.sort;

import java.util.List;

/**
 * 冒泡排序服务接口。
 * <p>
 * 定义冒泡排序算法的对外契约，由 {@link SortServiceImpl} 提供具体实现。
 *
 * @author AiWork
 * @date 2026-09-15
 */
public interface SortService {

    /**
     * 对整数列表执行冒泡排序。
     *
     * @param numbers 待排序整数列表（不会修改原始列表）
     * @param order   排序方向，ASC=升序（非递减），DESC=降序（非递增）
     * @return 排序后的新列表
     */
    List<Integer> bubbleSort(List<Integer> numbers, SortOrder order);
}
