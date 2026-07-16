package com.example.myapp.util;

/**
 * Generic QuickSort utility class.
 * <p>
 * Provides in-place and copy-based sorting for arrays of {@link Comparable} elements.
 * Uses the Hoare partition scheme with median-of-three pivot selection.
 */
public final class QuickSort {

    private QuickSort() {
        // utility class — prevent instantiation
    }

    /**
     * Sorts the given array in-place in ascending order using quicksort.
     *
     * @param <T>   the element type (must implement {@link Comparable})
     * @param array the array to sort; must not be {@code null}
     * @throws IllegalArgumentException if {@code array} is {@code null}
     */
    public static <T extends Comparable<T>> void sort(T[] array) {
        if (array == null) {
            throw new IllegalArgumentException("array must not be null");
        }
        if (array.length <= 1) {
            return;
        }
        quicksort(array, 0, array.length - 1);
    }

    /**
     * Returns a new sorted copy of the given array. The original array is unchanged.
     *
     * @param <T>   the element type (must implement {@link Comparable})
     * @param array the array to sort; may be {@code null} (returns empty array)
     * @return a new sorted copy of the array, or an empty array if input is {@code null}
     */
    @SuppressWarnings("unchecked")
    public static <T extends Comparable<T>> T[] sortCopy(T[] array) {
        if (array == null) {
            return (T[]) new Comparable[0];
        }
        T[] copy = array.clone();
        sort(copy);
        return copy;
    }

    // --- internal quicksort implementation ---

    private static <T extends Comparable<T>> void quicksort(T[] array, int low, int high) {
        if (low >= high) {
            return;
        }
        int p = partition(array, low, high);
        quicksort(array, low, p);
        quicksort(array, p + 1, high);
    }

    /**
     * Hoare partition scheme with median-of-three pivot selection.
     *
     * @return the index of the pivot after partitioning
     */
    private static <T extends Comparable<T>> int partition(T[] array, int low, int high) {
        T pivot = medianOfThree(array, low, high);
        int i = low - 1;
        int j = high + 1;

        while (true) {
            do {
                i++;
            } while (array[i].compareTo(pivot) < 0);

            do {
                j--;
            } while (array[j].compareTo(pivot) > 0);

            if (i >= j) {
                return j;
            }
            swap(array, i, j);
        }
    }

    private static <T extends Comparable<T>> T medianOfThree(T[] array, int low, int high) {
        int mid = low + (high - low) / 2;
        if (array[low].compareTo(array[mid]) > 0) {
            swap(array, low, mid);
        }
        if (array[low].compareTo(array[high]) > 0) {
            swap(array, low, high);
        }
        if (array[mid].compareTo(array[high]) > 0) {
            swap(array, mid, high);
        }
        return array[mid];
    }

    private static <T> void swap(T[] array, int i, int j) {
        T tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
    }
}