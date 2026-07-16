package com.example.myapp.utils;

/**
 * 通用快速排序（QuickSort）工具类。
 * 提供原地（in-place）快速排序，选取中间元素作为 pivot。
 *
 * @param <T> 可比较的类型
 */
public final class QuickSort {

    private QuickSort() {
        // 工具类禁止实例化
    }

    /**
     * 对数组进行快速排序（原地排序）。
     *
     * @param arr 待排序数组，若为 null 或长度 <= 1 则直接返回
     * @param <T> 可比较的类型
     */
    public static <T extends Comparable<T>> void sort(T[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        quickSort(arr, 0, arr.length - 1);
    }

    /**
     * 递归快速排序核心。
     *
     * @param arr  数组
     * @param low  起始索引（包含）
     * @param high 结束索引（包含）
     */
    private static <T extends Comparable<T>> void quickSort(T[] arr, int low, int high) {
        if (low >= high) {
            return;
        }
        int pivotIndex = partition(arr, low, high);
        quickSort(arr, low, pivotIndex - 1);
        quickSort(arr, pivotIndex + 1, high);
    }

    /**
     * 分区操作：选取中间元素作为 pivot，将小于 pivot 的元素移到左侧，
     * 大于 pivot 的元素移到右侧。
     *
     * @param arr  数组
     * @param low  起始索引
     * @param high 结束索引
     * @return pivot 最终位置索引
     */
    private static <T extends Comparable<T>> int partition(T[] arr, int low, int high) {
        // 选取中间元素作为 pivot，减少最坏情况概率
        int mid = low + (high - low) / 2;
        T pivot = arr[mid];
        // 将 pivot 移到末尾
        swap(arr, mid, high);

        int i = low;
        for (int j = low; j < high; j++) {
            if (arr[j].compareTo(pivot) <= 0) {
                swap(arr, i, j);
                i++;
            }
        }
        // 将 pivot 放回正确位置
        swap(arr, i, high);
        return i;
    }

    /**
     * 交换数组中两个位置的元素。
     */
    private static <T> void swap(T[] arr, int i, int j) {
        T temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}