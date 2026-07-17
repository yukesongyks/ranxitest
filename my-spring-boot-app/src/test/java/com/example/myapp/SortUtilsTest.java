package com.example.myapp;

import com.example.myapp.services.SortUtils;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class SortUtilsTest {

    @Test
    void testQuickSortWithIntegers() {
        Integer[] arr = {5, 3, 8, 1, 9, 2};
        SortUtils.quickSort(arr);
        assertArrayEquals(new Integer[]{1, 2, 3, 5, 8, 9}, arr);
    }

    @Test
    void testQuickSortWithStrings() {
        String[] arr = {"banana", "apple", "cherry", "date"};
        SortUtils.quickSort(arr);
        assertArrayEquals(new String[]{"apple", "banana", "cherry", "date"}, arr);
    }

    @Test
    void testQuickSortWithComparatorDescending() {
        Integer[] arr = {5, 3, 8, 1, 9, 2};
        SortUtils.quickSort(arr, Comparator.reverseOrder());
        assertArrayEquals(new Integer[]{9, 8, 5, 3, 2, 1}, arr);
    }

    @Test
    void testQuickSortEmptyArray() {
        Integer[] arr = {};
        SortUtils.quickSort(arr);
        assertEquals(0, arr.length);
    }

    @Test
    void testQuickSortNullArray() {
        SortUtils.quickSort((Integer[]) null);
        // 不应抛出异常
    }

    @Test
    void testQuickSortSingleElement() {
        Integer[] arr = {42};
        SortUtils.quickSort(arr);
        assertArrayEquals(new Integer[]{42}, arr);
    }

    @Test
    void testQuickSortAlreadySorted() {
        Integer[] arr = {1, 2, 3, 4, 5};
        SortUtils.quickSort(arr);
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    void testQuickSortDuplicates() {
        Integer[] arr = {3, 1, 3, 2, 1, 2};
        SortUtils.quickSort(arr);
        assertArrayEquals(new Integer[]{1, 1, 2, 2, 3, 3}, arr);
    }

    @Test
    void testQuickSortLargeArray() {
        int n = 1000;
        Integer[] arr = new Integer[n];
        for (int i = 0; i < n; i++) {
            arr[i] = n - i;
        }
        SortUtils.quickSort(arr);
        for (int i = 0; i < n; i++) {
            assertEquals(Integer.valueOf(i + 1), arr[i]);
        }
    }
}