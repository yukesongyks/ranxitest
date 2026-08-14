package com.example.myapp.util;

/**
 * 快速排序工具类，提供基于比较的原地快速排序算法。
 *
 * <p>支持泛型 {@link Comparable} 类型数组及 {@code int} 基本类型数组的排序。
 * 该工具类不可实例化。
 *
 * @author DTCoder
 * @date 2026/01/28
 */
public final class QuickSort {

    private QuickSort() {
        // 工具类，禁止实例化
    }

    /**
     * 对实现 {@link Comparable} 接口的对象数组进行原地快速排序。
     *
     * @param <T>  数组元素类型，必须实现 {@link Comparable}
     * @param array 待排序数组，不能为 {@code null}
     * @throws IllegalArgumentException 如果 array 为 {@code null}
     */
    public static <T extends Comparable<T>> void sort(T[] array) {
        if (array == null) {
            throw new IllegalArgumentException("array must not be null");
        }
        quickSort(array, 0, array.length - 1);
    }

    /**
     * 对 {@code int} 基本类型数组进行原地快速排序。
     *
     * @param array 待排序数组，不能为 {@code null}
     * @throws IllegalArgumentException 如果 array 为 {@code null}
     */
    public static void sort(int[] array) {
        if (array == null) {
            throw new IllegalArgumentException("array must not be null");
        }
        quickSort(array, 0, array.length - 1);
    }

    /**
     * 递归快速排序——泛型版本。
     *
     * @param array 待排序数组
     * @param left  左边界索引（包含）
     * @param right 右边界索引（包含）
     */
    private static <T extends Comparable<T>> void quickSort(T[] array, int left, int right) {
        if (left >= right) {
            return;
        }

        int pivotIndex = partition(array, left, right);
        quickSort(array, left, pivotIndex - 1);
        quickSort(array, pivotIndex + 1, right);
    }

    /**
     * 递归快速排序——int 基本类型版本。
     *
     * @param array 待排序数组
     * @param left  左边界索引（包含）
     * @param right 右边界索引（包含）
     */
    private static void quickSort(int[] array, int left, int right) {
        if (left >= right) {
            return;
        }

        int pivotIndex = partition(array, left, right);
        quickSort(array, left, pivotIndex - 1);
        quickSort(array, pivotIndex + 1, right);
    }

    /**
     * 分区操作——泛型版本。
     *
     * <p>选取最右侧元素为基准值（pivot），将数组划分为小于等于基准值
     * 和大于基准值的两个部分，返回基准值最终所在位置。
     *
     * @param array 待分区数组
     * @param left  左边界索引
     * @param right 右边界索引（基准值所在位置）
     * @return 基准值最终索引
     */
    private static <T extends Comparable<T>> int partition(T[] array, int left, int right) {
        T pivot = array[right];
        int i = left - 1;

        for (int j = left; j < right; j++) {
            // array[j] <= pivot 时将其交换到左侧区域
            if (array[j].compareTo(pivot) <= 0) {
                i++;
                swap(array, i, j);
            }
        }
        swap(array, i + 1, right);
        return i + 1;
    }

    /**
     * 分区操作——int 基本类型版本。
     *
     * @param array 待分区数组
     * @param left  左边界索引
     * @param right 右边界索引（基准值所在位置）
     * @return 基准值最终索引
     */
    private static int partition(int[] array, int left, int right) {
        int pivot = array[right];
        int i = left - 1;

        for (int j = left; j < right; j++) {
            if (array[j] <= pivot) {
                i++;
                swap(array, i, j);
            }
        }
        swap(array, i + 1, right);
        return i + 1;
    }

    /**
     * 交换数组中两个位置的元素——泛型版本。
     *
     * @param array 数组
     * @param i     第一个索引
     * @param j     第二个索引
     */
    private static <T> void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    /**
     * 交换数组中两个位置的元素——int 基本类型版本。
     *
     * @param array 数组
     * @param i     第一个索引
     * @param j     第二个索引
     */
    private static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}