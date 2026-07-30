package com.example.myapp.utils;

import java.util.Arrays;
import java.util.Objects;

/**
 * 冒泡排序工具类。
 *
 * <p>提供静态工具方法，对 {@code int[]} 执行冒泡排序。方法返回新数组，
 * 不修改入参，保证无副作用。
 *
 * <p>实现带有提前终止优化：当某轮未发生任何交换时，说明数组已有序，
 * 立即返回，最优时间复杂度为 O(n)。
 *
 * @author example
 * @since 1.0
 */
public final class BubbleSort {

    /**
     * 私有构造方法，防止工具类被实例化。
     *
     * @throws UnsupportedOperationException 总是抛出，表明工具类不应被实例化
     */
    private BubbleSort() {
        throw new UnsupportedOperationException("工具类不支持实例化");
    }

    /**
     * 对整数数组进行冒泡排序，返回排序后的新数组。
     *
     * <p>方法不修改入参数组，内部基于其副本排序后返回。
     *
     * @param array 待排序数组，允许为 {@code null}
     * @return 排序后的新数组；入参为 {@code null} 时返回空数组
     */
    public static int[] sort(int[] array) {
        if (array == null || array.length == 0) {
            return new int[0];
        }

        int[] result = Arrays.copyOf(array, array.length);
        int n = result.length;
        // 外层控制轮次，内层比较相邻元素并交换
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (result[j] > result[j + 1]) {
                    swap(result, j, j + 1);
                    swapped = true;
                }
            }
            // 本轮无交换，数组已有序，提前终止
            if (!swapped) {
                break;
            }
        }
        return result;
    }

    /**
     * 交换数组中指定下标的两个元素。
     *
     * @param array 目标数组，非 {@code null}
     * @param i     下标 i
     * @param j     下标 j
     * @throws NullPointerException 若 array 为 {@code null}
     */
    private static void swap(int[] array, int i, int j) {
        Objects.requireNonNull(array, "array 不能为 null");
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
