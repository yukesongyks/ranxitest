package com.example.myapp.util;

import java.util.Arrays;
import java.util.Objects;

/**
 * 快速排序算法工具类。
 *
 * <p>基于 Lomuto 分区策略实现的原地快速排序，时间复杂度平均 O(n log n)，最坏 O(n^2)，
 * 空间复杂度 O(log n)（递归栈）。排序结果为升序。</p>
 *
 * <p>本工具类为纯静态方法工具类，不持有状态，不可实例化。</p>
 *
 * @author myapp
 * @since 1.0
 */
public final class QuickSort {

    private QuickSort() {
        // 工具类禁止实例化
    }

    /**
     * 对传入的整型数组进行原地升序排序。
     *
     * <p>调用本方法后，入参数组本身会被修改为有序状态。</p>
     *
     * @param array 待排序数组，可为空数组，不可为 null
     * @throws IllegalArgumentException 当 array 为 null 时抛出
     */
    public static void sort(int[] array) {
        if (Objects.isNull(array)) {
            throw new IllegalArgumentException("待排序数组不能为 null");
        }
        quickSort(array, 0, array.length - 1);
    }

    /**
     * 对传入的整型数组进行排序，返回一个新的有序数组副本，不修改原数组。
     *
     * @param array 待排序数组，可为空数组，不可为 null
     * @return 升序排列的新数组
     * @throws IllegalArgumentException 当 array 为 null 时抛出
     */
    public static int[] sortedCopy(int[] array) {
        if (Objects.isNull(array)) {
            throw new IllegalArgumentException("待排序数组不能为 null");
        }
        int[] copy = Arrays.copyOf(array, array.length);
        quickSort(copy, 0, copy.length - 1);
        return copy;
    }

    /**
     * 递归执行快速排序。
     *
     * @param array 待排序数组
     * @param low   当前分区起始下标（含）
     * @param high  当前分区结束下标（含）
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
     * Lomuto 分区：以末尾元素为基准，将小于基准的元素交换到左侧。
     *
     * @param array 待分区数组
     * @param low   起始下标（含）
     * @param high  结束下标（含），其元素作为基准
     * @return 基准元素最终所在下标
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
     * 交换数组中两个下标位置的元素。
     *
     * @param array 目标数组
     * @param i     下标 i
     * @param j     下标 j
     */
    private static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
