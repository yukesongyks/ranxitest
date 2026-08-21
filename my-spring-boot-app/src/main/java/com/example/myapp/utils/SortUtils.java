package com.example.myapp.utils;

import java.util.Objects;

/**
 * Utility class providing sorting algorithms.
 */
public final class SortUtils {

    private SortUtils() {
        // utility class, prevent instantiation
    }

    /**
     * Sorts the specified array in ascending order using the quick sort algorithm.
     * <p>
     * This is an in-place sorting algorithm with average time complexity O(n log n).
     * The implementation uses the median-of-three pivot selection to avoid
     * worst-case performance on nearly sorted arrays.
     *
     * @param array the array to be sorted (non-null)
     * @throws NullPointerException if the specified array is null
     */
    public static void quickSort(int[] array) {
        Objects.requireNonNull(array, "array must not be null");
        if (array.length <= 1) {
            return;
        }
        quickSort(array, 0, array.length - 1);
    }

    private static void quickSort(int[] array, int left, int right) {
        if (left < right) {
            int pivotIndex = partition(array, left, right);
            quickSort(array, left, pivotIndex - 1);
            quickSort(array, pivotIndex + 1, right);
        }
    }

    private static int partition(int[] array, int left, int right) {
        // median-of-three pivot selection
        int mid = left + (right - left) / 2;
        int pivotIndex = medianOfThree(array, left, mid, right);
        swap(array, pivotIndex, right);

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

    private static int medianOfThree(int[] array, int a, int b, int c) {
        // Return the index of the median value among three positions
        if (array[a] > array[b]) {
            if (array[b] > array[c]) {
                return b;
            } else if (array[a] > array[c]) {
                return c;
            } else {
                return a;
            }
        } else {
            if (array[a] > array[c]) {
                return a;
            } else if (array[b] > array[c]) {
                return c;
            } else {
                return b;
            }
        }
    }

    private static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}