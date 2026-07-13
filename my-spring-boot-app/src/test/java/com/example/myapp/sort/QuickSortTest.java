package com.example.myapp.sort;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class QuickSortTest {

    private QuickSort quickSort;

    @BeforeEach
    void setUp() {
        quickSort = new QuickSort();
    }

    @Test
    void testSortEmptyArray() {
        int[] arr = {};
        quickSort.sort(arr);
        assertArrayEquals(new int[]{}, arr);
    }

    @Test
    void testSortSingleElement() {
        int[] arr = {5};
        quickSort.sort(arr);
        assertArrayEquals(new int[]{5}, arr);
    }

    @Test
    void testSortAlreadySorted() {
        int[] arr = {1, 2, 3, 4, 5};
        quickSort.sort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    void testSortReverseSorted() {
        int[] arr = {5, 4, 3, 2, 1};
        quickSort.sort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    void testSortRandomOrder() {
        int[] arr = {3, 1, 4, 1, 5, 9, 2, 6};
        quickSort.sort(arr);
        assertArrayEquals(new int[]{1, 1, 2, 3, 4, 5, 6, 9}, arr);
    }

    @Test
    void testSortWithDuplicates() {
        int[] arr = {5, 2, 5, 1, 2, 3};
        quickSort.sort(arr);
        assertArrayEquals(new int[]{1, 2, 2, 3, 5, 5}, arr);
    }

    @Test
    void testSortNegativeNumbers() {
        int[] arr = {-3, -1, -5, 2, 0, -2};
        quickSort.sort(arr);
        assertArrayEquals(new int[]{-5, -3, -2, -1, 0, 2}, arr);
    }

    @Test
    void testSortLargeArray() {
        int[] arr = {100, 50, 75, 25, 125, 10, 200, 5, 150, 175};
        quickSort.sort(arr);
        assertArrayEquals(new int[]{5, 10, 25, 50, 75, 100, 125, 150, 175, 200}, arr);
    }

    @Test
    void testSortNullArray() {
        int[] arr = null;
        assertThrows(IllegalArgumentException.class, () -> quickSort.sort(arr));
    }
}