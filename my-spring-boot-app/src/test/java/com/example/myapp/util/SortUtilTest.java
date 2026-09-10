package com.example.myapp.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

class SortUtilTest {

    // --- Edge cases ---

    @Test
    void nullArray_shouldThrowNoException() {
        SortUtil.quickSort(null);
        // no assertion needed — we verify no exception is thrown
    }

    @Test
    void emptyArray_shouldRemainEmpty() {
        Integer[] array = {};
        SortUtil.quickSort(array);
        assertEquals(0, array.length);
    }

    @Test
    void singleElementArray_shouldRemainUnchanged() {
        Integer[] array = {42};
        SortUtil.quickSort(array);
        assertEquals(42, array[0]);
    }

    // --- Sorting correctness ---

    @Test
    void alreadySortedArray_shouldRemainSorted() {
        Integer[] array = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Integer[] expected = Arrays.copyOf(array, array.length);
        SortUtil.quickSort(array);
        assertArrayEquals(expected, array);
    }

    @Test
    void reverseSortedArray_shouldBeSortedAscending() {
        Integer[] array = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
        Integer[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        SortUtil.quickSort(array);
        assertArrayEquals(expected, array);
    }

    @Test
    void arrayWithDuplicates_shouldPreserveAllElements() {
        Integer[] array = {5, 3, 8, 3, 9, 1, 5, 3};
        Integer[] expected = {1, 3, 3, 3, 5, 5, 8, 9};
        SortUtil.quickSort(array);
        assertArrayEquals(expected, array);
    }

    @Test
    void arrayWithNegativeNumbers_shouldBeSortedCorrectly() {
        Integer[] array = {-5, -1, -10, -3, -7, -2};
        Integer[] expected = {-10, -7, -5, -3, -2, -1};
        SortUtil.quickSort(array);
        assertArrayEquals(expected, array);
    }

    @Test
    void arrayWithMixedPositiveNegativeZero_shouldBeSortedCorrectly() {
        Integer[] array = {0, -5, 3, -1, 7, 0, -2, 4};
        Integer[] expected = {-5, -2, -1, 0, 0, 3, 4, 7};
        SortUtil.quickSort(array);
        assertArrayEquals(expected, array);
    }

    @Test
    void arrayWithAllIdenticalElements_shouldRemainUnchanged() {
        Integer[] array = {7, 7, 7, 7, 7, 7, 7};
        Integer[] expected = Arrays.copyOf(array, array.length);
        SortUtil.quickSort(array);
        assertArrayEquals(expected, array);
    }

    // --- Large array test ---

    @Test
    void largeArray_shouldBeSortedCorrectlyAndPerformReasonably() {
        int size = 1000;
        Integer[] array = new Integer[size];
        Random random = new Random(42);
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(10_000);
        }
        Integer[] expected = Arrays.copyOf(array, array.length);
        Arrays.sort(expected);

        long start = System.nanoTime();
        SortUtil.quickSort(array);
        long duration = System.nanoTime() - start;

        assertArrayEquals(expected, array);
        // reasonable performance: 1000 elements should sort in well under 1 second
        assertTrue(duration < 1_000_000_000,
                "Quick sort took too long: " + (duration / 1_000_000) + " ms");
    }

    // --- In-place verification ---

    @Test
    void sortShouldBeInPlace() {
        Integer[] array = {9, 5, 7, 1, 3};
        Integer[] snapshot = Arrays.copyOf(array, array.length);
        SortUtil.quickSort(array);
        // The same reference should hold the sorted result
        assertArrayEquals(new Integer[]{1, 3, 5, 7, 9}, array);
        // Verify it's the same array object (in-place)
        assertNotNull(array);
    }

    // --- Generic type: String ---

    @Test
    void stringArray_shouldBeSortedCorrectly() {
        String[] array = {"banana", "apple", "cherry", "date"};
        String[] expected = {"apple", "banana", "cherry", "date"};
        SortUtil.quickSort(array);
        assertArrayEquals(expected, array);
    }

    // --- Double values ---

    @Test
    void doubleArray_shouldBeSortedCorrectly() {
        Double[] array = {3.3, 1.1, 2.2, 5.5, 4.4};
        Double[] expected = {1.1, 2.2, 3.3, 4.4, 5.5};
        SortUtil.quickSort(array);
        assertArrayEquals(expected, array);
    }
}