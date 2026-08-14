package com.example.myapp.util;

import java.util.Comparator;

/**
 * 快速排序算法工具类。
 *
 * <p>基于三数取中（median-of-three）优化的三路快速排序实现，提供原地排序能力。
 * 支持 int[] 基本类型数组、泛型 Comparable 数组以及自定义 Comparator 排序。
 * 三路分区（Dutch National Flag）确保所有相等元素场景下仍保持 O(n log n) 复杂度。
 *
 * <p>平均时间复杂度 O(n log n)，空间复杂度 O(log n)（递归栈深度）。
 * 所有公共方法对 null 和长度 ≤ 1 的数组安全处理，不抛异常（除 Comparator 为 null 外）。
 *
 * @author DTCoder
 * @date 2026/08/14
 */
public final class QuickSort {

    /** 小数组阈值，子数组长度小于此值时使用插入排序优化 */
    private static final int INSERTION_SORT_THRESHOLD = 10;

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
     * 递归快速排序核心（int[] 版本），小数组切换插入排序。
     */
    private static void quickSort(int[] arr, int lo, int hi, boolean ascending) {
        if (lo >= hi) {
            return;
        }
        if (hi - lo + 1 < INSERTION_SORT_THRESHOLD) {
            insertionSort(arr, lo, hi, ascending);
            return;
        }
        int[] range = threeWayPartition(arr, lo, hi, ascending);
        quickSort(arr, lo, range[0] - 1, ascending);
        quickSort(arr, range[1] + 1, hi, ascending);
    }

    /**
     * 三路分区（Dutch National Flag）：将数组划分为 &lt; pivot、= pivot、&gt; pivot 三部分。
     * 三数取中选取 pivot 值，相等元素集中在中间区域，确保全部相等元素场景下 O(n log n)。
     *
     * @return int[2] = {lt, gt}，其中 arr[lo..lt-1] &lt; pivot, arr[lt..gt] == pivot, arr[gt+1..hi] &gt; pivot
     */
    private static int[] threeWayPartition(int[] arr, int lo, int hi, boolean ascending) {
        int mid = lo + (hi - lo) / 2;
        int pivotValue = medianOfThree(arr[lo], arr[mid], arr[hi]);

        int lt = lo;
        int gt = hi;
        int i = lo;

        while (i <= gt) {
            if (ascending) {
                if (arr[i] < pivotValue) {
                    swap(arr, lt++, i++);
                } else if (arr[i] > pivotValue) {
                    swap(arr, i, gt--);
                } else {
                    i++;
                }
            } else {
                if (arr[i] > pivotValue) {
                    swap(arr, lt++, i++);
                } else if (arr[i] < pivotValue) {
                    swap(arr, i, gt--);
                } else {
                    i++;
                }
            }
        }
        return new int[]{lt, gt};
    }

    /**
     * 插入排序（int[] 版本），用于小数组优化。
     */
    private static void insertionSort(int[] arr, int lo, int hi, boolean ascending) {
        for (int i = lo + 1; i <= hi; i++) {
            int key = arr[i];
            int j = i - 1;
            if (ascending) {
                while (j >= lo && arr[j] > key) {
                    arr[j + 1] = arr[j];
                    j--;
                }
            } else {
                while (j >= lo && arr[j] < key) {
                    arr[j + 1] = arr[j];
                    j--;
                }
            }
            arr[j + 1] = key;
        }
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
     * 递归快速排序核心（泛型 Comparable 版本），小数组切换插入排序。
     */
    private static <T extends Comparable<T>> void quickSortComparable(T[] arr, int lo, int hi, boolean ascending) {
        if (lo >= hi) {
            return;
        }
        if (hi - lo + 1 < INSERTION_SORT_THRESHOLD) {
            insertionSortComparable(arr, lo, hi, ascending);
            return;
        }
        int[] range = threeWayPartitionComparable(arr, lo, hi, ascending);
        quickSortComparable(arr, lo, range[0] - 1, ascending);
        quickSortComparable(arr, range[1] + 1, hi, ascending);
    }

    /**
     * 三路分区（泛型 Comparable 版本）。
     *
     * @return int[2] = {lt, gt}
     */
    private static <T extends Comparable<T>> int[] threeWayPartitionComparable(T[] arr, int lo, int hi, boolean ascending) {
        int mid = lo + (hi - lo) / 2;
        T pivotValue = medianOfThreeComparable(arr[lo], arr[mid], arr[hi]);

        int lt = lo;
        int gt = hi;
        int i = lo;

        while (i <= gt) {
            int cmp = arr[i].compareTo(pivotValue);
            if (ascending) {
                if (cmp < 0) {
                    swapComparable(arr, lt++, i++);
                } else if (cmp > 0) {
                    swapComparable(arr, i, gt--);
                } else {
                    i++;
                }
            } else {
                if (cmp > 0) {
                    swapComparable(arr, lt++, i++);
                } else if (cmp < 0) {
                    swapComparable(arr, i, gt--);
                } else {
                    i++;
                }
            }
        }
        return new int[]{lt, gt};
    }

    /**
     * 插入排序（泛型 Comparable 版本），用于小数组优化。
     */
    private static <T extends Comparable<T>> void insertionSortComparable(T[] arr, int lo, int hi, boolean ascending) {
        for (int i = lo + 1; i <= hi; i++) {
            T key = arr[i];
            int j = i - 1;
            if (ascending) {
                while (j >= lo && arr[j].compareTo(key) > 0) {
                    arr[j + 1] = arr[j];
                    j--;
                }
            } else {
                while (j >= lo && arr[j].compareTo(key) < 0) {
                    arr[j + 1] = arr[j];
                    j--;
                }
            }
            arr[j + 1] = key;
        }
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
     * 递归快速排序核心（Comparator 版本），小数组切换插入排序。
     */
    private static <T> void quickSortWithComparator(T[] arr, int lo, int hi, Comparator<T> comparator) {
        if (lo >= hi) {
            return;
        }
        if (hi - lo + 1 < INSERTION_SORT_THRESHOLD) {
            insertionSortWithComparator(arr, lo, hi, comparator);
            return;
        }
        int[] range = threeWayPartitionWithComparator(arr, lo, hi, comparator);
        quickSortWithComparator(arr, lo, range[0] - 1, comparator);
        quickSortWithComparator(arr, range[1] + 1, hi, comparator);
    }

    /**
     * 三路分区（Comparator 版本）。
     *
     * @return int[2] = {lt, gt}
     */
    private static <T> int[] threeWayPartitionWithComparator(T[] arr, int lo, int hi, Comparator<T> comparator) {
        int mid = lo + (hi - lo) / 2;
        T pivotValue = medianOfThreeComparator(arr[lo], arr[mid], arr[hi], comparator);

        int lt = lo;
        int gt = hi;
        int i = lo;

        while (i <= gt) {
            int cmp = comparator.compare(arr[i], pivotValue);
            if (cmp < 0) {
                swapGeneric(arr, lt++, i++);
            } else if (cmp > 0) {
                swapGeneric(arr, i, gt--);
            } else {
                i++;
            }
        }
        return new int[]{lt, gt};
    }

    /**
     * 插入排序（Comparator 版本），用于小数组优化。
     */
    private static <T> void insertionSortWithComparator(T[] arr, int lo, int hi, Comparator<T> comparator) {
        for (int i = lo + 1; i <= hi; i++) {
            T key = arr[i];
            int j = i - 1;
            while (j >= lo && comparator.compare(arr[j], key) > 0) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
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