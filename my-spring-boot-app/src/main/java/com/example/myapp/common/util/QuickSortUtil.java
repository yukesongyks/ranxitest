package com.example.myapp.common.util;

import java.util.List;

/**
 * 快速排序工具类。
 *
 * <p>提供对基本类型数组 {@code int[]} 与泛型列表 {@code List} 的原地快速排序实现。
 * 采用 Lomuto 分区策略，并结合三数取中选取基准值，以降低在接近有序输入下
 * 退化为 O(n^2) 的概率。排序结果为升序。</p>
 *
 * @author dtcoder
 * @date 2026/07/31
 */
public final class QuickSortUtil {

    /**
     * 对整型数组进行原地升序快速排序。
     *
     * @param array 待排序数组，允许为 {@code null} 或空数组，此时直接返回不做处理
     */
    public static void sort(int[] array) {
        if (array == null || array.length <= 1) {
            return;
        }
        quickSort(array, 0, array.length - 1);
    }

    /**
     * 对泛型列表进行升序快速排序，依据元素自身的自然顺序比较。
     *
     * @param list  待排序列表，允许为 {@code null} 或空列表，此时直接返回不做处理
     * @param <T>   列表元素类型，必须实现 {@link Comparable}
     */
    public static <T extends Comparable<? super T>> void sort(List<T> list) {
        if (list == null || list.size() <= 1) {
            return;
        }
        @SuppressWarnings("unchecked")
        T[] array = (T[]) new Comparable[list.size()];
        list.toArray(array);
        quickSort(array, 0, array.length - 1);
        for (int i = 0; i < array.length; i++) {
            list.set(i, array[i]);
        }
    }

    /**
     * 数组形式的递归快速排序主流程。
     *
     * @param array 待排序数组
     * @param low   当前分区下界（含）
     * @param high  当前分区上界（含）
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
     * Lomuto 分区：选取基准值并将小于基准的元素交换至左侧。
     *
     * @param array 待分区数组
     * @param low   分区下界（含）
     * @param high  分区上界（含）
     * @return 排序后基准值的最终下标
     */
    private static int partition(int[] array, int low, int high) {
        int pivot = medianOfThree(array, low, high);
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
     * 三数取中：以首、中、尾三元素的中位数作为基准，并将基准交换至末尾。
     *
     * @param array 待处理数组
     * @param low   分区下界（含）
     * @param high   分区上界（含）
     * @return 选定的基准值
     */
    private static int medianOfThree(int[] array, int low, int high) {
        int mid = low + (high - low) / 2;
        if (array[low] > array[mid]) {
            swap(array, low, mid);
        }
        if (array[low] > array[high]) {
            swap(array, low, high);
        }
        if (array[mid] > array[high]) {
            swap(array, mid, high);
        }
        swap(array, mid, high);
        return array[high];
    }

    /**
     * 交换数组中两个指定下标的元素。
     *
     * @param array 目标数组
     * @param i     下标 i
     * @param j     下标 j
     */
    private static void swap(int[] array, int i, int j) {
        if (i == j) {
            return;
        }
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    /**
     * 泛型数组形式的递归快速排序主流程。
     *
     * @param array 待排序数组
     * @param low   当前分区下界（含）
     * @param high  当前分区上界（含）
     * @param <T>   元素类型
     */
    private static <T extends Comparable<? super T>> void quickSort(T[] array, int low, int high) {
        if (low >= high) {
            return;
        }
        int pivotIndex = partition(array, low, high);
        quickSort(array, low, pivotIndex - 1);
        quickSort(array, pivotIndex + 1, high);
    }

    /**
     * 泛型数组的 Lomuto 分区。
     *
     * @param array 待分区数组
     * @param low   分区下界（含）
     * @param high  分区上界（含）
     * @param <T>   元素类型
     * @return 排序后基准值的最终下标
     */
    private static <T extends Comparable<? super T>> int partition(T[] array, int low, int high) {
        T pivot = array[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (array[j].compareTo(pivot) <= 0) {
                i++;
                swap(array, i, j);
            }
        }
        swap(array, i + 1, high);
        return i + 1;
    }

    /**
     * 泛型数组的元素交换。
     *
     * @param array 目标数组
     * @param i     下标 i
     * @param j     下标 j
     * @param <T>   元素类型
     */
    private static <T> void swap(T[] array, int i, int j) {
        if (i == j) {
            return;
        }
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    private QuickSortUtil() {
    }
}
