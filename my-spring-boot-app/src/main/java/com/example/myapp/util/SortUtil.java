package com.example.myapp.util;

/**
 * Utility class providing a generic quick sort implementation.
 * <p>
 * Uses the Lomuto partition scheme with the last element as pivot.
 * Sorts arrays in ascending order using the natural ordering of elements.
 * Sorting is performed in-place.
 */
public final class SortUtil {

    private SortUtil() {
        // utility class — prevent instantiation
    }

    /**
     * Sorts the specified array in ascending order using the quick sort algorithm.
     * <p>
     * The sort is performed in-place. The algorithm handles null, empty,
     * and single-element arrays gracefully as no-ops.
     *
     * @param <T>   the type of elements in the array, must implement {@link Comparable}
     * @param array the array to be sorted (may be {@code null})
     */
    public static <T extends Comparable<T>> void quickSort(T[] array) {
        if (array == null || array.length <= 1) {
            return;
        }
        quickSort(array, 0, array.length - 1);
    }

    /**
     * Recursively sorts the sub-array {@code array[low..high]} using quick sort.
     */
    private static <T extends Comparable<T>> void quickSort(T[] array, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(array, low, high);
            quickSort(array, low, pivotIndex - 1);
            quickSort(array, pivotIndex + 1, high);
        }
    }

    /**
     * Lomuto partition scheme: uses the last element ({@code array[high]}) as the pivot,
     * places it at its correct sorted position, and returns its index.
     * <p>
     * Elements smaller than the pivot are moved to the left; larger elements to the right.
     */
    private static <T extends Comparable<T>> int partition(T[] array, int low, int high) {
        T pivot = array[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (array[j].compareTo(pivot) <= 0) {
                i++;
                swap(array, i, j);
            }
        }

        swap(array, i + 1, high);
        return i + 1;
    }

    /**
     * Swaps the elements at positions {@code i} and {@code j} in the array.
     */
    private static <T> void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}