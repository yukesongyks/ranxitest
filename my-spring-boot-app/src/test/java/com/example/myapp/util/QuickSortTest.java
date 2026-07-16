package com.example.myapp.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class QuickSortTest {

    // --- sort(T[] array) tests ---

    @Test
    @DisplayName("sort: null array throws IllegalArgumentException")
    void sortNullArrayThrows() {
        assertThrows(IllegalArgumentException.class, () -> QuickSort.sort(null));
    }

    @Test
    @DisplayName("sort: empty array is no-op")
    void sortEmptyArray() {
        Integer[] arr = {};
        QuickSort.sort(arr);
        assertArrayEquals(new Integer[0], arr);
    }

    @Test
    @DisplayName("sort: single element array is unchanged")
    void sortSingleElement() {
        Integer[] arr = {42};
        QuickSort.sort(arr);
        assertArrayEquals(new Integer[]{42}, arr);
    }

    @Test
    @DisplayName("sort: already sorted array")
    void sortAlreadySorted() {
        Integer[] arr = {1, 2, 3, 4, 5};
        QuickSort.sort(arr);
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    @DisplayName("sort: reverse sorted array")
    void sortReverseSorted() {
        Integer[] arr = {5, 4, 3, 2, 1};
        QuickSort.sort(arr);
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    @DisplayName("sort: array with duplicates")
    void sortWithDuplicates() {
        Integer[] arr = {3, 1, 2, 3, 1, 2};
        QuickSort.sort(arr);
        assertArrayEquals(new Integer[]{1, 1, 2, 2, 3, 3}, arr);
    }

    @Test
    @DisplayName("sort: all elements equal")
    void sortAllEqual() {
        Integer[] arr = {7, 7, 7, 7, 7};
        QuickSort.sort(arr);
        assertArrayEquals(new Integer[]{7, 7, 7, 7, 7}, arr);
    }

    @Test
    @DisplayName("sort: unsorted array")
    void sortUnsorted() {
        Integer[] arr = {9, 3, 7, 1, 8, 2, 5, 4, 6};
        QuickSort.sort(arr);
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, arr);
    }

    @Test
    @DisplayName("sort: string array")
    void sortStrings() {
        String[] arr = {"banana", "apple", "cherry", "date"};
        QuickSort.sort(arr);
        assertArrayEquals(new String[]{"apple", "banana", "cherry", "date"}, arr);
    }

    @Test
    @DisplayName("sort: large random array")
    void sortLargeRandomArray() {
        int size = 1000;
        Integer[] arr = new Integer[size];
        Random rng = new Random(42); // fixed seed for reproducibility
        for (int i = 0; i < size; i++) {
            arr[i] = rng.nextInt(10000);
        }
        Integer[] expected = arr.clone();
        Arrays.sort(expected);

        QuickSort.sort(arr);
        assertArrayEquals(expected, arr);
    }

    // --- sortCopy(T[] array) tests ---

    @Test
    @DisplayName("sortCopy: null returns empty array")
    void sortCopyNullReturnsEmpty() {
        Integer[] result = QuickSort.sortCopy(null);
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    @DisplayName("sortCopy: original array is unchanged")
    void sortCopyOriginalUnchanged() {
        Integer[] original = {3, 1, 2};
        Integer[] result = QuickSort.sortCopy(original);
        assertArrayEquals(new Integer[]{3, 1, 2}, original);
        assertArrayEquals(new Integer[]{1, 2, 3}, result);
    }

    @Test
    @DisplayName("sortCopy: empty array copy")
    void sortCopyEmpty() {
        Integer[] arr = {};
        Integer[] result = QuickSort.sortCopy(arr);
        assertArrayEquals(new Integer[0], result);
    }

    @Test
    @DisplayName("sortCopy: result is a different array instance")
    void sortCopyDifferentInstance() {
        Integer[] original = {5, 2, 8};
        Integer[] result = QuickSort.sortCopy(original);
        assertNotSame(original, result);
    }
}