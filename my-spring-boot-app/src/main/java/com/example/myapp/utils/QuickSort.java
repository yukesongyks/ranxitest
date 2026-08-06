package com.example.myapp.utils;

import java.util.Objects;

/**
 * 快速排序工具类。
 *
 * <p>提供对整型数组的原地升序排序，平均时间复杂度 O(n log n)，空间复杂度 O(log n)。
 * 采用 Lomuto 分区方案，选取分区区间末位元素作为基准（pivot）。
 *
 * @author DTCoder
 */
public final class QuickSort {

    /**
     * 私有构造函数，工具类禁止实例化。
     */
    private QuickSort() {
    }

    /**
     * 对整型数组进行原地升序排序。
     *
     * @param array 待排序数组，不可为 null
     * @throws NullPointerException 当 array 为 null 时抛出
     */
    public static void sort(int[] array) {
        Objects.requireNonNull(array, "array must not be null");
        if (array.length <= 1) {
            return;
        }
        quickSort(array, 0, array.length - 1);
    }

    /**
     * 递归地对数组的指定区间执行快速排序。
     *
     * @param array 待排序数组
     * @param low  区间下界（含）
     * @param high 区间上界（含）
     */
    private static void quickSort(int[] array, int low, int high) {
        if (low >= high) {
            return;
        }
        int pivotIndex = partition(array, low, high);
        quickSort(array, low, pivotIndex - 1);
        quickSort(array, pivotIndex + 1, high);
    }

    /**
     * 对数组的指定区间进行分区，返回基准元素的最终位置。
     *
     * @param array 待分区数组
     * @param low  区间下界（含）
     * @param high 区间上界（含）
     * @return 基准元素排序后的下标
     */
    private static int partition(int[] array, int low, int high) {
        int pivot = array[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (array[j] <= pivot) {
                i++;
                swap(array, i, j);
            }
        }
        swap(array, i + 1, high);
        return i + 1;
    }

    /**
     * 交换数组中两个位置的元素。
     *
     * @param array 目标数组
     * @param i     位置一
     * @param j     位置二
     */
    private static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
