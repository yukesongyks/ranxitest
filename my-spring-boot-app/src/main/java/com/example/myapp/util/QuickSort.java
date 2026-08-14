package com.example.myapp.util;

import java.util.Comparator;

/**
 * 快速排序算法工具类。
 *
 * <p>基于三数取中（median-of-three）优化的快速排序实现，提供原地排序能力。
 * 支持 int[] 基本类型数组、泛型 Comparable 数组以及自定义 Comparator 排序。
 *
 * <p>平均时间复杂度 O(n log n)，空间复杂度 O(log n)（递归栈深度）。
 * 所有公共方法对 null 和长度 ≤ 1 的数组安全处理，不抛异常（除 Comparator 为 null 外）。
 *
 * @author DTCoder
 * @date 2026/08/14
 */
public final class QuickSort {

    /** 私有构造器，防止实例化 */
    private QuickSort() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // ======================== S01: int[] 升序 ========================

    /**
     * 对整数数组进行原地升序快速排序。
     *
     * @param arr 待排序的整数数组，null 或长度 ≤ 1 时直接返回
     */
    public static void sort(int[] arr) {
        sort(arr, true);
    }

    // ======================== S02: int[] 带方向 ========================

    /**
     * 对整数数组进行原地快速排序，支持升序或降序。
     *
     * @param arr       待排序的整数数组，null 或长度 ≤ 1 时直接返回
     * @param ascending true 表示升序，false 表示降序
     */
    public static void sort(int[] arr, boolean ascending) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        quickSort(arr, 0, arr.length - 1, ascending);
    }

    // ======================== S03: 泛型 Comparable 升序 ========================

    /**
     * 对实现了 {@link Comparable} 接口的泛型数组进行原地升序快速排序。
     *
     * @param <T> 元素类型，必须实现 Comparable
     * @param arr 待排序的泛型数组，null 或长度 ≤ 1 时直接返回
     */
    public static <T extends Comparable<T>> void sort(T[] arr) {
        sort(arr, true);
    }

    // ======================== S04: 泛型 Comparable 带方向 ========================

    /**
     * 对实现了 {@link Comparable} 接口的泛型数组进行原地快速排序，支持升序或降序。
     *
     * @param <T>       元素类型，必须实现 Comparable
     * @param arr       待排序的泛型数组，null 或长度 ≤ 1 时直接返回
     * @param ascending true 表示升序，false 表示降序
     */
    public static <T extends Comparable<T>> void sort(T[] arr, boolean ascending) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        quickSortComparable(arr, 0, arr.length - 1, ascending);
    }

    // ======================== S05: 自定义 Comparator ========================

    /**
     * 使用自定义比较器对泛型数组进行原地快速排序。
     *
     * @param <T>        元素类型
     * @param arr        待排序的泛型数组，null 或长度 ≤ 1 时直接返回
     * @param comparator 自定义比较器，不能为 null
     * @throws IllegalArgumentException 当 comparator 为 null 时抛出
     */
    public static <T> void sort(T[] arr, Comparator<T> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("Comparator must not be null");
        }
        if (arr == null || arr.length <= 1) {
            return;
        }
        quickSortWithComparator(arr, 0, arr.length - 1, comparator);
    }

    // ======================== 核心算法：int[] ========================

    /**
     * 递归快速排序核心（int[] 版本）。
     */
    private static void quickSort(int[] arr, int lo, int hi, boolean ascending) {
        if (lo >= hi) {
            return;
        }
        int pivotIndex = partition(arr, lo, hi, ascending);
        quickSort(arr, lo, pivotIndex - 1, ascending);
        quickSort(arr, pivotIndex + 1, hi, ascending);
    }

    /**
     * 三数取中选取 pivot，并执行分区操作。
     *
     * @return pivot 最终位置索引
     */
    private static int partition(int[] arr, int lo, int hi, boolean ascending) {
        // 三数取中：取 arr[lo]、arr[mid]、arr[hi] 的中位数作为 pivot
        int mid = lo + (hi - lo) / 2;
        int pivot = medianOfThree(arr[lo], arr[mid], arr[hi]);

        // 将 pivot 值交换到 hi 位置（简化分区逻辑）
        if (pivot == arr[lo]) {
            swap(arr, lo, hi);
        } else if (pivot == arr[mid]) {
            swap(arr, mid, hi);
        }
        // 如果 pivot == arr[hi]，无需交换

        int pivotValue = arr[hi];
        int i = lo - 1;

        for (int j = lo; j < hi; j++) {
            boolean shouldMove;
            if (ascending) {
                shouldMove = arr[j] < pivotValue;
            } else {
                shouldMove = arr[j] > pivotValue;
            }
            if (shouldMove) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, hi);
        return i + 1;
    }

    /**
     * 三数取中值。
     */
    private static int medianOfThree(int a, int b, int c) {
        if ((a >= b && a <= c) || (a >= c && a <= b)) {
            return a;
        }
        if ((b >= a && b <= c) || (b >= c && b <= a)) {
            return b;
        }
        return c;
    }

    /**
     * 交换数组中两个位置的元素。
     */
    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    // ======================== 核心算法：泛型 Comparable ========================

    /**
     * 递归快速排序核心（泛型 Comparable 版本）。
     */
    private static <T extends Comparable<T>> void quickSortComparable(T[] arr, int lo, int hi, boolean ascending) {
        if (lo >= hi) {
            return;
        }
        int pivotIndex = partitionComparable(arr, lo, hi, ascending);
        quickSortComparable(arr, lo, pivotIndex - 1, ascending);
        quickSortComparable(arr, pivotIndex + 1, hi, ascending);
    }

    /**
     * 分区操作（泛型 Comparable 版本）。
     */
    private static <T extends Comparable<T>> int partitionComparable(T[] arr, int lo, int hi, boolean ascending) {
        int mid = lo + (hi - lo) / 2;
        T pivot = medianOfThreeComparable(arr[lo], arr[mid], arr[hi]);

        if (pivot == arr[lo]) {
            swapComparable(arr, lo, hi);
        } else if (pivot == arr[mid]) {
            swapComparable(arr, mid, hi);
        }

        T pivotValue = arr[hi];
        int i = lo - 1;

        for (int j = lo; j < hi; j++) {
            int cmp = arr[j].compareTo(pivotValue);
            boolean shouldMove;
            if (ascending) {
                shouldMove = cmp < 0;
            } else {
                shouldMove = cmp > 0;
            }
            if (shouldMove) {
                i++;
                swapComparable(arr, i, j);
            }
        }
        swapComparable(arr, i + 1, hi);
        return i + 1;
    }

    /**
     * 三数取中值（泛型版本）。
     */
    private static <T extends Comparable<T>> T medianOfThreeComparable(T a, T b, T c) {
        boolean aBetweenBAndC = (a.compareTo(b) >= 0 && a.compareTo(c) <= 0)
                || (a.compareTo(c) >= 0 && a.compareTo(b) <= 0);
        if (aBetweenBAndC) {
            return a;
        }
        boolean bBetweenAAndC = (b.compareTo(a) >= 0 && b.compareTo(c) <= 0)
                || (b.compareTo(c) >= 0 && b.compareTo(a) <= 0);
        if (bBetweenAAndC) {
            return b;
        }
        return c;
    }

    /**
     * 交换泛型数组中两个位置的元素。
     */
    private static <T> void swapComparable(T[] arr, int i, int j) {
        T tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    // ======================== 核心算法：自定义 Comparator ========================

    /**
     * 递归快速排序核心（Comparator 版本）。
     */
    private static <T> void quickSortWithComparator(T[] arr, int lo, int hi, Comparator<T> comparator) {
        if (lo >= hi) {
            return;
        }
        int pivotIndex = partitionWithComparator(arr, lo, hi, comparator);
        quickSortWithComparator(arr, lo, pivotIndex - 1, comparator);
        quickSortWithComparator(arr, pivotIndex + 1, hi, comparator);
    }

    /**
     * 分区操作（Comparator 版本）。
     */
    private static <T> int partitionWithComparator(T[] arr, int lo, int hi, Comparator<T> comparator) {
        int mid = lo + (hi - lo) / 2;
        T pivot = medianOfThreeComparator(arr[lo], arr[mid], arr[hi], comparator);

        if (pivot == arr[lo]) {
            swapGeneric(arr, lo, hi);
        } else if (pivot == arr[mid]) {
            swapGeneric(arr, mid, hi);
        }

        T pivotValue = arr[hi];
        int i = lo - 1;

        for (int j = lo; j < hi; j++) {
            if (comparator.compare(arr[j], pivotValue) < 0) {
                i++;
                swapGeneric(arr, i, j);
            }
        }
        swapGeneric(arr, i + 1, hi);
        return i + 1;
    }

    /**
     * 三数取中值（Comparator 版本）。
     */
    private static <T> T medianOfThreeComparator(T a, T b, T c, Comparator<T> comparator) {
        boolean aBetweenBAndC = (comparator.compare(a, b) >= 0 && comparator.compare(a, c) <= 0)
                || (comparator.compare(a, c) >= 0 && comparator.compare(a, b) <= 0);
        if (aBetweenBAndC) {
            return a;
        }
        boolean bBetweenAAndC = (comparator.compare(b, a) >= 0 && comparator.compare(b, c) <= 0)
                || (comparator.compare(b, c) >= 0 && comparator.compare(b, a) <= 0);
        if (bBetweenAAndC) {
            return b;
        }
        return c;
    }

    /**
     * 交换泛型数组中两个位置的元素（通用版本）。
     */
    private static <T> void swapGeneric(T[] arr, int i, int j) {
        T tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}