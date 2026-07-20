package com.example.myapp.util;

import java.util.Comparator;
import java.util.List;

/**
 * 快速排序算法实现。
 *
 * <p>采用 Lomuto 分区方案，对 {@link List} 进行原地（in-place）升序排序。
 * 支持任意可比较元素类型，也支持通过 {@link Comparator} 指定排序规则。
 *
 * @param <T> 元素类型，需实现 {@link Comparable}
 */
public final class QuickSort<T extends Comparable<? super T>> {

    private final Comparator<? super T> comparator;

    public QuickSort() {
        this(Comparator.naturalOrder());
    }

    public QuickSort(Comparator<? super T> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("comparator must not be null");
        }
        this.comparator = comparator;
    }

    /**
     * 对给定列表进行原地快速排序。允许传入 {@code null}，此时方法空操作返回。
     *
     * @param items 待排序列表
     */
    public void sort(List<T> items) {
        if (items == null || items.size() < 2) {
            return;
        }
        quickSort(items, 0, items.size() - 1);
    }

    private void quickSort(List<T> items, int low, int high) {
        if (low >= high) {
            return;
        }
        int pivotIndex = partition(items, low, high);
        quickSort(items, low, pivotIndex - 1);
        quickSort(items, pivotIndex + 1, high);
    }

    /**
     * Lomuto 分区：以末位为基准，将小于基准的元素交换到左侧。
     */
    private int partition(List<T> items, int low, int high) {
        T pivot = items.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (comparator.compare(items.get(j), pivot) <= 0) {
                i++;
                swap(items, i, j);
            }
        }
        swap(items, i + 1, high);
        return i + 1;
    }

    private void swap(List<T> items, int a, int b) {
        if (a == b) {
            return;
        }
        T tmp = items.get(a);
        items.set(a, items.get(b));
        items.set(b, tmp);
    }
}
