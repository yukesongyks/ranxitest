package com.example.myapp.util;

/**
 * 快速排序工具类
 * 提供对整型数组的快速排序实现
 */
public class QuickSort {

    private QuickSort() {
        // 工具类禁止实例化
    }

    /**
     * 对数组进行升序排序
     *
     * @param arr 待排序数组
     * @throws IllegalArgumentException 如果数组为null
     */
    public static void sort(int[] arr) {
        if (arr == null) {
            throw new IllegalArgumentException("数组不能为null");
        }
        if (arr.length <= 1) {
            return;
        }
        quickSort(arr, 0, arr.length - 1);
    }

    private static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(arr, low, high);
            quickSort(arr, low, pivotIndex - 1);
            quickSort(arr, pivotIndex + 1, high);
        }
    }

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

    private static void swap(int[] arr, int i, int j) {
        if (i != j) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }
}