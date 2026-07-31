package com.example.myapp.common.util;

/**
 * 快速排序工具类，提供原地升序排序能力。
 *
 * <p>采用 Lomuto 分区方案，以子区间末元素作为基准值（pivot），
 * 平均时间复杂度 O(n log n)，最坏情况 O(n^2)，空间复杂度 O(log n)（递归栈）。</p>
 *
 * @author DTCoder
 * @date 2026-07-31
 */
public final class QuickSort {

    /**
     * 私有构造函数，禁止实例化工具类。
     */
    private QuickSort() {
    }

    /**
     * 对整型数组进行原地升序排序。
     *
     * <p>排序结果直接反映在传入的数组上，方法无返回值以避免不必要的数组拷贝。
     * 传入 {@code null} 将抛出 {@link IllegalArgumentException}，调用方应在调用前做空值校验。</p>
     *
     * @param arr 待排序数组，排序结果原地生效
     * @throws IllegalArgumentException 当 arr 为 null 时抛出
     */
    public static void sort(int[] arr) {
        if (arr == null) {
            throw new IllegalArgumentException("待排序数组不能为 null");
        }
        if (arr.length <= 1) {
            return;
        }
        sort(arr, 0, arr.length - 1);
    }

    /**
     * 对数组的指定子区间 [low, high] 执行递归快速排序。
     *
     * @param arr  目标数组
     * @param low  子区间起始下标（含）
     * @param high 子区间结束下标（含）
     */
    private static void sort(int[] arr, int low, int high) {
        if (low >= high) {
            return;
        }
        int pivotIndex = partition(arr, low, high);
        sort(arr, low, pivotIndex - 1);
        sort(arr, pivotIndex + 1, high);
    }

    /**
     * 以末元素为基准值对子区间进行分区，返回基准值最终下标。
     *
     * <p>分区后，基准值左侧元素均不大于基准值，右侧元素均不小于基准值。</p>
     *
     * @param arr  目标数组
     * @param low  子区间起始下标（含）
     * @param high 子区间结束下标（含），对应基准值初始位置
     * @return 基准值分区后的最终下标
     */
    private static int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    /**
     * 交换数组中两个指定下标的元素。
     *
     * @param arr 目标数组
     * @param i   第一个下标
     * @param j   第二个下标
     */
    private static void swap(int[] arr, int i, int j) {
        if (i == j) {
            return;
        }
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
