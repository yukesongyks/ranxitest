package com.example.myapp.sort;

/**
 * 排序算法统一接口。
 *
 * <p>定义统一的排序行为契约，所有具体排序算法均需实现此接口。</p>
 *
 * @param <T> 数组元素类型，需实现 Comparable 接口
 */
public interface Sorter<T extends Comparable<T>> {

    /**
     * 对数组进行原地排序（非递减序）。
     *
     * @param array 待排序数组，元素需实现 Comparable 接口
     * @throws IllegalArgumentException 当 array 为 null 时抛出
     */
    void sort(T[] array);
}
