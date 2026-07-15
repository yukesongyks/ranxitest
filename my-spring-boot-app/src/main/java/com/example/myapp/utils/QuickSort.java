package com.example.myapp.utils;

/**
 * Generic in-place quicksort utility using Hoare partition scheme.
 *
 * <p>Time complexity: O(n log n) average, O(n²) worst case.
 * Space complexity: O(log n) for recursion stack.
 *
 * @param <T> any Comparable type
 */
public final class QuickSort {

    private QuickSort() {
        // utility class
    }

    /**
     * Sort array in ascending order (in-place).
     *
     * @param arr the array to sort; must not be null
     * @param <T> any Comparable type
     * @throws IllegalArgumentException if arr is null
     */
    public static <T extends Comparable<T>> void sort(T[] arr) {
        sort(arr, true);
    }

    /**
     * Sort array in specified order (in-place).
     *
     * @param arr       the array to sort; must not be null
     * @param ascending true for ascending, false for descending
     * @param <T>       any Comparable type
     * @throws IllegalArgumentException if arr is null
     */
    public static <T extends Comparable<T>> void sort(T[] arr, boolean ascending) {
        if (arr == null) {
            throw new IllegalArgumentException("array must not be null");
        }
        if (arr.length <= 1) {
            return;
        }
        quicksort(arr, 0, arr.length - 1, ascending);
    }

    private static <T extends Comparable<T>> void quicksort(T[] arr, int lo, int hi, boolean ascending) {
        if (lo >= hi) {
            return;
        }
        int pivot = partition(arr, lo, hi, ascending);
        quicksort(arr, lo, pivot, ascending);
        quicksort(arr, pivot + 1, hi, ascending);
    }

    private static <T extends Comparable<T>> int partition(T[] arr, int lo, int hi, boolean ascending) {
        T pivot = arr[lo];
        int i = lo - 1;
        int j = hi + 1;

        while (true) {
            if (ascending) {
                do { i++; } while (arr[i].compareTo(pivot) < 0);
                do { j--; } while (arr[j].compareTo(pivot) > 0);
            } else {
                do { i++; } while (arr[i].compareTo(pivot) > 0);
                do { j--; } while (arr[j].compareTo(pivot) < 0);
            }

            if (i >= j) {
                return j;
            }
            swap(arr, i, j);
        }
    }

    private static <T> void swap(T[] arr, int i, int j) {
        T tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}