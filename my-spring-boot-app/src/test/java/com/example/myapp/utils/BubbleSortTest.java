package com.example.myapp.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * BubbleSort 单元测试。
 */
class BubbleSortTest {

    // ==================== int[] 测试 ====================

    @Test
    void sortInt_shouldSortUnsortedArray() {
        int[] arr = {5, 3, 8, 1, 2};
        BubbleSort.sort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 5, 8}, arr);
    }

    @Test
    void sortInt_shouldHandleAlreadySortedArray() {
        int[] arr = {1, 2, 3, 4, 5};
        BubbleSort.sort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    void sortInt_shouldHandleReverseSortedArray() {
        int[] arr = {9, 7, 5, 3, 1};
        BubbleSort.sort(arr);
        assertArrayEquals(new int[]{1, 3, 5, 7, 9}, arr);
    }

    @Test
    void sortInt_shouldHandleSingleElement() {
        int[] arr = {42};
        BubbleSort.sort(arr);
        assertArrayEquals(new int[]{42}, arr);
    }

    @Test
    void sortInt_shouldHandleEmptyArray() {
        int[] arr = {};
        BubbleSort.sort(arr);
        assertArrayEquals(new int[]{}, arr);
    }

    @Test
    void sortInt_shouldHandleDuplicateElements() {
        int[] arr = {3, 1, 3, 2, 1};
        BubbleSort.sort(arr);
        assertArrayEquals(new int[]{1, 1, 2, 3, 3}, arr);
    }

    @Test
    void sortInt_shouldThrowOnNull() {
        assertThrows(IllegalArgumentException.class, () -> BubbleSort.sort((int[]) null));
    }

    // ==================== Comparable 泛型测试 ====================

    @Test
    void sortComparable_shouldSortStrings() {
        String[] arr = {"banana", "apple", "cherry", "date"};
        BubbleSort.sort(arr);
        assertArrayEquals(new String[]{"apple", "banana", "cherry", "date"}, arr);
    }

    @Test
    void sortComparable_shouldSortIntegers() {
        Integer[] arr = {5, 3, 8, 1, 2};
        BubbleSort.sort(arr);
        assertArrayEquals(new Integer[]{1, 2, 3, 5, 8}, arr);
    }

    @Test
    void sortComparable_shouldThrowOnNull() {
        assertThrows(IllegalArgumentException.class, () -> BubbleSort.sort((Comparable[]) null));
    }

    // ==================== long[] 测试 ====================

    @Test
    void sortLong_shouldSortUnsortedArray() {
        long[] arr = {100L, 50L, 200L, 25L};
        BubbleSort.sort(arr);
        assertArrayEquals(new long[]{25L, 50L, 100L, 200L}, arr);
    }

    // ==================== double[] 测试 ====================

    @Test
    void sortDouble_shouldSortUnsortedArray() {
        double[] arr = {3.5, 1.2, 4.8, 2.0};
        BubbleSort.sort(arr);
        assertArrayEquals(new double[]{1.2, 2.0, 3.5, 4.8}, arr, 0.0001);
    }
}