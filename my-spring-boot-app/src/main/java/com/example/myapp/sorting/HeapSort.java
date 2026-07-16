package com.example.myapp.sorting;

/**
 * Generic heap sort implementation with O(n log n) time complexity
 * and O(1) auxiliary space.
 *
 * @param <T> the type of elements to sort, must be {@link Comparable}
 */
public final class HeapSort {

    private HeapSort() {
        // utility class - no instantiation
    }

    /**
     * Sorts the given array in ascending order.
     *
     * @param <T>   element type
     * @param array the array to sort
     * @throws IllegalArgumentException if array is null
     */
    public static <T extends Comparable<T>> void sort(T[] array) {
        sort(array, true);
    }

    /**
     * Sorts the given array in the specified order.
     *
     * @param <T>       element type
     * @param array     the array to sort
     * @param ascending true for ascending, false for descending
     * @throws IllegalArgumentException if array is null
     */
    public static <T extends Comparable<T>> void sort(T[] array, boolean ascending) {
        if (array == null) {
            throw new IllegalArgumentException("array must not be null");
        }
        int n = array.length;
        if (n <= 1) {
            return;
        }

        // Build max-heap (or min-heap for descending)
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(array, n, i, ascending);
        }

        // Extract elements from heap one by one
        for (int i = n - 1; i > 0; i--) {
            swap(array, 0, i);
            heapify(array, i, 0, ascending);
        }
    }

    /**
     * Maintains the heap property for the subtree rooted at index i.
     */
    private static <T extends Comparable<T>> void heapify(T[] array, int heapSize, int i, boolean ascending) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < heapSize && compare(array[left], array[largest], ascending) > 0) {
            largest = left;
        }
        if (right < heapSize && compare(array[right], array[largest], ascending) > 0) {
            largest = right;
        }

        if (largest != i) {
            swap(array, i, largest);
            heapify(array, heapSize, largest, ascending);
        }
    }

    /**
     * Compares two elements. Returns a positive value if a should come before b
     * under the current ordering.
     */
    private static <T extends Comparable<T>> int compare(T a, T b, boolean ascending) {
        if (ascending) {
            return a.compareTo(b);
        }
        return b.compareTo(a);
    }

    private static <T> void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}