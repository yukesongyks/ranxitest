package com.example.myapp.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class QuickSortTest {

    @Test
    @DisplayName("正常数组排序")
    void testSortNormalArray() {
        int[] arr = {5, 2, 9, 1, 5, 6};
        QuickSort.sort(arr);
        assertArrayEquals(new int[]{1, 2, 5, 5, 6, 9}, arr);
    }

    @Test
    @DisplayName("空数组")
    void testSortEmptyArray() {
        int[] arr = {};
        QuickSort.sort(arr);
        assertArrayEquals(new int[]{}, arr);
    }

    @Test
    @DisplayName("单元素数组")
    void testSortSingleElement() {
        int[] arr = {42};
        QuickSort.sort(arr);
        assertArrayEquals(new int[]{42}, arr);
    }

    @Test
    @DisplayName("已排序数组")
    void testSortAlreadySorted() {
        int[] arr = {1, 2, 3, 4, 5};
        QuickSort.sort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    @DisplayName("逆序数组")
    void testSortReverseOrder() {
        int[] arr = {5, 4, 3, 2, 1};
        QuickSort.sort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    @DisplayName("包含重复元素")
    void testSortWithDuplicates() {
        int[] arr = {3, 1, 4, 1, 5, 9, 2, 6, 5, 3};
        QuickSort.sort(arr);
        assertArrayEquals(new int[]{1, 1, 2, 3, 3, 4, 5, 5, 6, 9}, arr);
    }

    @Test
    @DisplayName("null数组抛出异常")
    void testSortNullArray() {
        assertThrows(IllegalArgumentException.class, () -> QuickSort.sort(null));
    }
}