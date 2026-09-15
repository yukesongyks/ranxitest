package com.example.myapp.sort;

/**
 * 排序通用工具类。
 *
 * <p>提供排序算法中常用的辅助方法，如元素交换。</p>
 */
public final class SortUtils {

    private SortUtils() {
        // 工具类禁止实例化
    }

    /**
     * 交换数组中指定下标的两个元素。
     *
     * @param array 目标数组
     * @param i     第一个元素下标
     * @param j     第二个元素下标
     */
    public static <T> void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
