package com.example.myapp.utils;

import java.util.Arrays;
import java.util.Comparator;

/**
 * 快速排序工具类。
 * 提供对基本类型 int 数组以及任意对象数组（基于 {@link Comparator}）的快速排序实现。
 */
public final class QuickSort {

    private QuickSort() {
    }

    /**
     * 对 int 数组进行原地快速排序（升序）。
     *
     * @param arr 待排序数组，不能为 null
     */
    public static void sort(int[] arr) {
        if (arr == null) {
            throw new IllegalArgumentException("待排序数组不能为 null");
        }
        quickSort(arr, 0, arr.length - 1);
    }

    /**
     * 对对象数组进行原地快速排序。
     *
     * @param arr        待排序数组，不能为 null
     * @param comparator 比较器，不能为 null
     * @param <T>        数组元素类型
     */
    public static <T> void sort(T[] arr, Comparator<T> comparator) {
        if (arr == null) {
            throw new IllegalArgumentException("待排序数组不能为 null");
        }
        if (comparator == null) {
            throw new IllegalArgumentException("比较器不能为 null");
        }
        quickSort(arr, comparator, 0, arr.length - 1);
    }

    /**
     * 返回排序后的新数组，不修改原数组。
     */
    public static int[] sortedCopy(int[] arr) {
        if (arr == null) {
            throw new IllegalArgumentException("待排序数组不能为 null");
        }
        int[] copy = Arrays.copyOf(arr, arr.length);
        quickSort(copy, 0, copy.length - 1);
        return copy;
    }

    private static void quickSort(int[] arr, int low, int high) {
        while (low < high) {
            int pivotIndex = partition(arr, low, high);
            // 尾递归优化：对较小一侧递归，较大一侧循环，将栈深度控制在 O(log n)
            if (pivotIndex - low < high - pivotIndex) {
                quickSort(arr, low, pivotIndex - 1);
                low = pivotIndex + 1;
            } else {
                quickSort(arr, pivotIndex + 1, high);
                high = pivotIndex - 1;
            }
        }
    }

    private static int partition(int[] arr, int low, int high) {
        // 三数取中选基准，降低在已排序/近似有序输入下退化为 O(n^2) 的风险
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
        // 将中位数换到 high 位置作为基准
        swap(arr, mid, high);
        int pivot = arr[high];

        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                if (i != j) {
                    swap(arr, i, j);
                }
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    private static <T> void quickSort(T[] arr, Comparator<T> comparator, int low, int high) {
        while (low < high) {
            int pivotIndex = partition(arr, comparator, low, high);
            if (pivotIndex - low < high - pivotIndex) {
                quickSort(arr, comparator, low, pivotIndex - 1);
                high = pivotIndex - 1;
            } else {
                quickSort(arr, comparator, pivotIndex + 1, high);
                low = pivotIndex + 1;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> int partition(T[] arr, Comparator<T> comparator, int low, int high) {
        int mid = low + (high - low) / 2;
        if (comparator.compare(arr[low], arr[mid]) > 0) {
            swap(arr, low, mid);
        }
        if (comparator.compare(arr[low], arr[high]) > 0) {
            swap(arr, low, high);
        }
        if (comparator.compare(arr[mid], arr[high]) > 0) {
            swap(arr, mid, high);
        }
        swap(arr, mid, high);
        T pivot = arr[high];

        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (comparator.compare(arr[j], pivot) <= 0) {
                i++;
                if (i != j) {
                    swap(arr, i, j);
                }
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    @SuppressWarnings("unchecked")
    private static <T> void swap(T[] arr, int i, int j) {
        T tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}
