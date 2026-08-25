package com.example.myapp.utils;

import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * In-place QuickSort implementation using Lomuto partition scheme
 * with random pivot selection.
 */
public final class QuickSort {

    private QuickSort() {
        // utility class — prevent instantiation
    }

    /**
     * Sorts the given array in-place using the elements' natural ordering.
     *
     * @param array the array to sort; must not be null
     * @param <T>   the element type, must implement {@link Comparable}
     * @throws NullPointerException if array is null
     */
    public static <T extends Comparable<T>> void sort(T[] array) {
        Objects.requireNonNull(array, "array must not be null");
        sort(array, Comparator.naturalOrder());
    }

    /**
     * Sorts the given array in-place using the provided comparator.
     *
     * @param array      the array to sort; must not be null
     * @param comparator the comparator to determine element order; must not be null
     * @param <T>        the element type
     * @throws NullPointerException if array or comparator is null
     */
    public static <T> void sort(T[] array, Comparator<? super T> comparator) {
        Objects.requireNonNull(array, "array must not be null");
        Objects.requireNonNull(comparator, "comparator must not be null");
        if (array.length <= 1) {
            return;
        }
        quickSort(array, 0, array.length - 1, comparator);
    }

    private static <T> void quickSort(T[] array, int low, int high, Comparator<? super T> cmp) {
        if (low < high) {
            int pivotIndex = partition(array, low, high, cmp);
            quickSort(array, low, pivotIndex - 1, cmp);
            quickSort(array, pivotIndex + 1, high, cmp);
        }
    }

    private static <T> int partition(T[] array, int low, int high, Comparator<? super T> cmp) {
        // random pivot selection to avoid O(n²) on sorted input
        int randomPivot = low + ThreadLocalRandom.current().nextInt(high - low + 1);
        swap(array, randomPivot, high);

        T pivot = array[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (cmp.compare(array[j], pivot) <= 0) {
                i++;
                swap(array, i, j);
            }
        }

        swap(array, i + 1, high);
        return i + 1;
    }

    private static <T> void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}