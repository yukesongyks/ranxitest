package com.example.myapp.utils;

import java.util.Arrays;

/**
 * 冒泡排序工具类。
 * <p>
 * 冒泡排序通过重复遍历待排序数组，依次比较相邻元素并交换顺序错误的元素对，
 * 每一轮遍历将当前未排序部分的最大（或最小）元素"浮"到末尾。
 * 时间复杂度 O(n²)，空间复杂度 O(1)，是稳定的原地排序算法。
 * </p>
 */
public final class BubbleSort {

    private BubbleSort() {
        // 工具类，禁止实例化
    }

    /**
     * 对 Comparable 类型数组进行升序冒泡排序（原地排序）。
     *
     * @param <T> 可比较的类型
     * @param arr 待排序数组，不能为 null
     * @return 排序后的数组（即入参数组本身）
     * @throws IllegalArgumentException 如果 arr 为 null
     */
    public static <T extends Comparable<? super T>> T[] sort(T[] arr) {
        if (arr == null) {
            throw new IllegalArgumentException("数组不能为 null");
        }
        int n = arr.length;
        // 外层循环：每次把最大的元素冒泡到末尾
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            // 内层循环：比较相邻元素
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr[j].compareTo(arr[j + 1]) > 0) {
                    swap(arr, j, j + 1);
                    swapped = true;
                }
            }
            // 如果本轮没有发生交换，说明数组已经有序，提前退出
            if (!swapped) {
                break;
            }
        }
        return arr;
    }

    /**
     * 对 int 数组进行升序冒泡排序（原地排序）。
     *
     * @param arr 待排序数组，不能为 null
     * @return 排序后的数组（即入参数组本身）
     * @throws IllegalArgumentException 如果 arr 为 null
     */
    public static int[] sort(int[] arr) {
        if (arr == null) {
            throw new IllegalArgumentException("数组不能为 null");
        }
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
        return arr;
    }

    /**
     * 对 long 数组进行升序冒泡排序（原地排序）。
     *
     * @param arr 待排序数组，不能为 null
     * @return 排序后的数组（即入参数组本身）
     * @throws IllegalArgumentException 如果 arr 为 null
     */
    public static long[] sort(long[] arr) {
        if (arr == null) {
            throw new IllegalArgumentException("数组不能为 null");
        }
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    long temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
        return arr;
    }

    /**
     * 对 double 数组进行升序冒泡排序（原地排序）。
     *
     * @param arr 待排序数组，不能为 null
     * @return 排序后的数组（即入参数组本身）
     * @throws IllegalArgumentException 如果 arr 为 null
     */
    public static double[] sort(double[] arr) {
        if (arr == null) {
            throw new IllegalArgumentException("数组不能为 null");
        }
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    double temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
        return arr;
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