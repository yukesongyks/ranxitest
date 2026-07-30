package com.example.myapp.utils;

/**
 * 快速排序工具类。
 *
 * <p>提供基于数组的快速排序算法实现，采用三数取中（median-of-three）法选取基准元素，
 * 以降低在近似有序数组上的时间复杂度退化风险。所有方法均为静态工具方法，不可实例化。
 *
 * <p>时间复杂度：平均 O(n log n)，最坏 O(n²)（三数取中优化后概率极低）。
 * 空间复杂度：O(log n)（递归栈）。
 *
 * @author example
 * @since 1.0
 */
public final class QuickSort {

    /** 长度小于该阈值的数组无需排序。 */
    private static final int MIN_SORTABLE_LENGTH = 2;

    private QuickSort() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 对整型数组进行升序快速排序（原地排序）。
     *
     * @param arr 待排序数组，允许为空或长度小于 2
     * @throws IllegalArgumentException 当 arr 为 null 时抛出
     */
    public static void sort(int[] arr) {
        if (arr == null) {
            throw new IllegalArgumentException("Input array must not be null");
        }
        if (arr.length < MIN_SORTABLE_LENGTH) {
            return;
        }
        quickSort(arr, 0, arr.length - 1);
    }

    /**
     * 递归执行快速排序。
     *
     * @param arr  目标数组
     * @param low  当前分区下界（含）
     * @param high 当前分区上界（含）
     */
    private static void quickSort(int[] arr, int low, int high) {
        if (low >= high) {
            return;
        }
        int pivotIndex = partition(arr, low, high);
        quickSort(arr, low, pivotIndex - 1);
        quickSort(arr, pivotIndex + 1, high);
    }

    /**
     * 以三数取中后的基准元素对 [low, high] 区间进行 Lomuto 分区。
     *
     * @param arr  目标数组
     * @param low  分区下界（含）
     * @param high 分区上界（含），分区前基准已放置于此
     * @return 基准元素最终所在的下标
     */
    private static int partition(int[] arr, int low, int high) {
        int pivot = medianOfThree(arr, low, high);
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
     * 三数取中法：选取 low、mid、high 三个位置的中位数作为基准，
     * 并将其交换到 high 位置以便后续 Lomuto 分区统一处理。
     *
     * @param arr  目标数组
     * @param low  分区下界（含）
     * @param high 分区上界（含）
     * @return 基准元素的值
     */
    private static int medianOfThree(int[] arr, int low, int high) {
        int mid = low + (high - low) / 2;
        if (arr[low] > arr[mid]) {
            swap(arr, low, mid);
        }
        if (arr[low] > arr[high]) {
            swap(arr, low, high);
        }
        if (arr[mid] > arr[high]) {
            swap(arr, mid, high);
        }
        // 此时 arr[low] <= arr[mid] <= arr[high]，中位数为 arr[mid]
        swap(arr, mid, high);
        return arr[high];
    }

    /**
     * 交换数组中两个位置的元素。
     *
     * @param arr 目标数组
     * @param i   位置 i
     * @param j   位置 j
     */
    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}
