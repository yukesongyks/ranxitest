package com.example.myapp.utils;

import java.util.Comparator;

/**
 * 通用快速排序（QuickSort）工具类。
 * 基于 Lomuto 分区方案 + 三数取中（median-of-three）优化，原地排序。
 */
public final class QuickSort {

    private QuickSort() {
        // 工具类，禁止实例化
    }

    /**
     * 对实现了 {@link Comparable} 的数组进行原地快速排序。
     */
    public static <T extends Comparable<? super T>> void sort(T[] array) {
        if (array == null || array.length <= 1) {
            return;
        }
        sort(array, Comparator.naturalOrder());
    }

    /**
     * 使用自定义比较器对数组进行原地快速排序。
     */
    public static <T> void sort(T[] array, Comparator<? super T> comparator) {
        if (array == null || array.length <= 1) {
            return;
        }
        if (comparator == null) {
            throw new IllegalArgumentException("comparator must not be null");
        }
        quicksort(array, 0, array.length - 1, comparator);
    }

    private static <T> void quicksort(T[] array, int low, int high, Comparator<? super T> cmp) {
        if (low >= high) {
            return;
        }
        int pivotIndex = partition(array, low, high, cmp);
        quicksort(array, low, pivotIndex - 1, cmp);
        quicksort(array, pivotIndex + 1, high, cmp);
    }

    private static <T> int partition(T[] array, int low, int high, Comparator<? super T> cmp) {
        // 三数取中：选择 low / mid / high 的中位数作为 pivot，移至末尾
        int mid = low + (high - low) / 2;
        if (cmp.compare(array[low], array[mid]) > 0) {
            swap(array, low, mid);
        }
        if (cmp.compare(array[low], array[high]) > 0) {
            swap(array, low, high);
        }
        if (cmp.compare(array[mid], array[high]) > 0) {
            swap(array, mid, high);
        }
        swap(array, mid, high);

        T pivot = array[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (cmp.compare(array[j], pivot) <= 0) {
                i++;
                swap(array, i, j);
            }
        }
        swap(array, i + 1, high);
        return i + 1;
    }

    private static <T> void swap(T[] array, int i, int j) {
        T tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
    }
}