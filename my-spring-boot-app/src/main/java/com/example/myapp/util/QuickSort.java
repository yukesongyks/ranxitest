package com.example.myapp.util;

import java.util.Arrays;
import java.util.Collections;

/**
 * 快速排序算法工具类，支持任意 {@link Comparable} 元素的原地排序。
 *
 * <p>采用 Lomuto 分区方案，时间复杂度平均 O(n log n)、最坏 O(n^2)，
 * 空间复杂度 O(log n)（递归栈）。排序结果为升序。</p>
 *
 * @author DTCoder
 * @date 2026/08/06
 */
public final class QuickSort {

    /**
     * 私有构造器，防止实例化工具类。
     */
    private QuickSort() {
    }

    /**
     * 对传入的数组进行原地快速排序，并返回排序后的数组引用。
     *
     * <p>入参为 {@code null} 或长度小于等于 1 时直接原样返回，不做任何修改。</p>
     *
     * @param array 待排序数组，允许为 {@code null}
     * @param <T>   元素类型，必须实现 {@link Comparable}
     * @return 排序后的数组（与入参同一引用），入参为 {@code null} 时返回 {@code null}
     */
    public static <T extends Comparable<? super T>> T[] sort(T[] array) {
        if (array == null || array.length <= 1) {
            return array;
        }
        quickSort(array, 0, array.length - 1);
        return array;
    }

    /**
     * 递归执行快速排序的核心逻辑。
     *
     * @param array 待排序数组
     * @param low   排序区间下界（含）
     * @param high  排序区间上界（含）
     * @param <T>   元素类型
     */
    private static <T extends Comparable<? super T>> void quickSort(T[] array, int low, int high) {
        if (low >= high) {
            return;
        }
        // 分区后基准元素落在正确位置
        int pivotIndex = partition(array, low, high);
        quickSort(array, low, pivotIndex - 1);
        quickSort(array, pivotIndex + 1, high);
    }

    /**
     * 分区操作，选取区间最右端元素为基准，将小于等于基准的元素移至左侧。
     *
     * @param array 待排序数组
     * @param low   区间下界（含）
     * @param high  区间上界（含）
     * @param <T>   元素类型
     * @return 基准元素最终所在位置
     */
    private static <T extends Comparable<? super T>> int partition(T[] array, int low, int high) {
        T pivot = array[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            // 小于等于基准的元素交换到左侧
            if (array[j].compareTo(pivot) <= 0) {
                i++;
                swap(array, i, j);
            }
        }
        // 基准元素归位
        swap(array, i + 1, high);
        return i + 1;
    }

    /**
     * 交换数组中两个位置的元素。
     *
     * @param array 目标数组
     * @param i     位置一
     * @param j     位置二
     * @param <T>   元素类型
     */
    private static <T> void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
