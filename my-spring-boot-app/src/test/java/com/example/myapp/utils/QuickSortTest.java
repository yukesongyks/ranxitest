package com.example.myapp.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class QuickSortTest {

    @Test
    @DisplayName("sort ascending：整数数组")
    void sortAscendingIntegers() {
        Integer[] arr = {5, 3, 8, 1, 2, 7, 4, 6};
        QuickSort.sort(arr);
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8}, arr);
    }

    @Test
    @DisplayName("sort ascending：字符串数组")
    void sortAscendingStrings() {
        String[] arr = {"banana", "apple", "cherry", "date"};
        QuickSort.sort(arr);
        assertArrayEquals(new String[]{"apple", "banana", "cherry", "date"}, arr);
    }

    @Test
    @DisplayName("sort descending：整数数组")
    void sortDescendingIntegers() {
        Integer[] arr = {5, 3, 8, 1, 2, 7, 4, 6};
        QuickSort.sort(arr, false);
        assertArrayEquals(new Integer[]{8, 7, 6, 5, 4, 3, 2, 1}, arr);
    }

    @Test
    @DisplayName("空数组不抛异常")
    void emptyArray() {
        Integer[] arr = {};
        QuickSort.sort(arr);
        assertEquals(0, arr.length);
    }

    @Test
    @DisplayName("单元素数组不变")
    void singleElement() {
        Integer[] arr = {42};
        QuickSort.sort(arr);
        assertArrayEquals(new Integer[]{42}, arr);
    }

    @Test
    @DisplayName("null 输入抛出 IllegalArgumentException")
    void nullArrayThrows() {
        assertThrows(IllegalArgumentException.class, () -> QuickSort.sort(null));
        assertThrows(IllegalArgumentException.class, () -> QuickSort.sort(null, false));
    }

    @Test
    @DisplayName("已排序数组保持不变")
    void alreadySorted() {
        Integer[] arr = {1, 2, 3, 4, 5};
        QuickSort.sort(arr);
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    @DisplayName("全部相等元素")
    void allEqualElements() {
        Integer[] arr = {7, 7, 7, 7, 7};
        QuickSort.sort(arr);
        assertArrayEquals(new Integer[]{7, 7, 7, 7, 7}, arr);
    }

    @Test
    @DisplayName("含重复元素")
    void withDuplicates() {
        Integer[] arr = {3, 1, 2, 3, 1, 2};
        QuickSort.sort(arr);
        assertArrayEquals(new Integer[]{1, 1, 2, 2, 3, 3}, arr);
    }

    @Test
    @DisplayName("逆序数组升序排序")
    void reverseSorted() {
        Integer[] arr = {5, 4, 3, 2, 1};
        QuickSort.sort(arr);
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    @DisplayName("大数组升序排序")
    void largeArray() {
        Integer[] arr = new Integer[1000];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = arr.length - i;
        }
        QuickSort.sort(arr);
        for (int i = 0; i < arr.length; i++) {
            assertEquals(i + 1, arr[i]);
        }
    }
}