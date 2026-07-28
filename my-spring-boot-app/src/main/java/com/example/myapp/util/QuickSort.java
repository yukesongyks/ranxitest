package com.example.myapp.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 快速排序（QuickSort）工具类。
 *
 * <p>提供对 {@code int[]} 原始数组与 {@code List<Integer>} 列表的快速排序实现。
 * 采用 Lomuto 分区方案，以序列末元素为基准（pivot）进行分区。</p>
 *
 * <h3>算法特性</h3>
 * <ul>
 *   <li>时间复杂度：平均 O(n log n)，最坏 O(n²)（当序列已有序时）。</li>
 *   <li>空间复杂度：O(log n)（递归栈深度）。</li>
 *   <li>稳定性：快速排序为<strong>不稳定</strong>排序，相等的元素相对次序可能改变。</li>
 * </ul>
 *
 * <h3>线程安全</h3>
 * <p>本类为无状态工具类，所有方法均为静态方法，可安全并发调用。
 * 但传入的集合对象本身不应在排序期间被其它线程并发修改。</p>
 *
 * @author dtcoder
 * @version 1.0
 * @since 1.0
 */
public final class QuickSort {

    /**
     * 私有构造器，防止实例化工具类。
     */
    private QuickSort() {
        throw new AssertionError("工具类不允许实例化");
    }

    /**
     * 对整型数组进行升序快速排序，返回排序后的新数组。
     *
     * <p>本方法不会修改传入的原数组内容，而是基于其副本排序后返回。</p>
     *
     * @param array 待排序数组，不可为 {@code null}
     * @return 升序排列的新数组；当入参为空数组时返回空数组
     * @throws IllegalArgumentException 当 {@code array} 为 {@code null} 时
     */
    public static int[] sort(int[] array) {
        if (array == null) {
            throw new IllegalArgumentException("待排序数组不能为 null");
        }
        int[] copy = Arrays.copyOf(array, array.length);
        quickSort(copy, 0, copy.length - 1);
        return copy;
    }

    /**
     * 对整型列表进行升序快速排序，返回排序后的新列表。
     *
     * <p>本方法不会修改传入的原列表，而是基于其副本排序后返回。
     * 列表中若存在 {@code null} 元素将抛出 {@link NullPointerException}。</p>
     *
     * @param list 待排序列表，不可为 {@code null}
     * @param <E>  元素类型，需实现 {@link Comparable}
     * @return 升序排列的新列表；当入参为空列表时返回空列表
     * @throws IllegalArgumentException 当 {@code list} 为 {@code null} 时
     * @throws NullPointerException    当列表元素为 {@code null} 时
     */
    public static <E extends Comparable<E>> List<E> sort(List<E> list) {
        if (list == null) {
            throw new IllegalArgumentException("待排序列表不能为 null");
        }
        List<E> copy = new ArrayList<>(list);
        quickSort(copy, 0, copy.size() - 1);
        return copy;
    }

    /**
     * 对数组指定区间 [low, high] 执行原地快速排序。
     *
     * @param arr  目标数组
     * @param low  起始下标（含）
     * @param high 结束下标（含）
     */
    private static void quickSort(int[] arr, int low, int high) {
        if (low >= high) {
            return;
        }
        int pivotIndex = partition(arr, low, high);
        quickSort(arr, low, pivotIndex - 1);
        quickSort(arr, pivotIndex + 1, high);
    }

    /**
     * Lomuto 分区：以末元素为 pivot，将小于 pivot 的元素移至左侧。
     *
     * @param arr  目标数组
     * @param low  起始下标（含）
     * @param high 结束下标（含）
     * @return pivot 最终落位的下标
     */
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

    /**
     * 对列表指定区间 [low, high] 执行原地快速排序。
     *
     * @param list 目标列表
     * @param low  起始下标（含）
     * @param high 结束下标（含）
     */
    private static <E extends Comparable<E>> void quickSort(List<E> list, int low, int high) {
        if (low >= high) {
            return;
        }
        int pivotIndex = partition(list, low, high);
        quickSort(list, low, pivotIndex - 1);
        quickSort(list, pivotIndex + 1, high);
    }

    /**
     * Lomuto 分区（列表版）：以末元素为 pivot。
     *
     * @param list 目标列表
     * @param low  起始下标（含）
     * @param high 结束下标（含）
     * @return pivot 最终落位的下标
     */
    private static <E extends Comparable<E>> int partition(List<E> list, int low, int high) {
        E pivot = list.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            // list.get(j) 为 null 时自然抛出 NullPointerException
            if (list.get(j).compareTo(pivot) <= 0) {
                i++;
                swap(list, i, j);
            }
        }
        swap(list, i + 1, high);
        return i + 1;
    }

    /**
     * 交换数组中两个下标位置的元素。
     *
     * @param arr 目标数组
     * @param i   下标一
     * @param j   下标二
     */
    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    /**
     * 交换列表中两个下标位置的元素。
     *
     * @param list 目标列表
     * @param i    下标一
     * @param j    下标二
     */
    private static <E> void swap(List<E> list, int i, int j) {
        E temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }
}
