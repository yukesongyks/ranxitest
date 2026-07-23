package com.example.myapp.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * QuickSortService 单元测试。
 */
class QuickSortServiceTest {

    private QuickSortService quickSortService;

    @BeforeEach
    void setUp() {
        quickSortService = new QuickSortService();
    }

    @Test
    @DisplayName("正常乱序数组应正确排序")
    void shouldSortUnsortedArray() {
        int[] arr = {5, 2, 8, 1, 9, 3};
        quickSortService.sort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 5, 8, 9}, arr);
    }

    @Test
    @DisplayName("已排序数组应保持不变")
    void shouldHandleAlreadySortedArray() {
        int[] arr = {1, 2, 3, 4, 5};
        quickSortService.sort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    @DisplayName("逆序数组应正确排序")
    void shouldHandleReverseSortedArray() {
        int[] arr = {5, 4, 3, 2, 1};
        quickSortService.sort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    @DisplayName("包含重复元素的数组应正确排序")
    void shouldHandleArrayWithDuplicates() {
        int[] arr = {3, 1, 3, 2, 2, 1};
        quickSortService.sort(arr);
        assertArrayEquals(new int[]{1, 1, 2, 2, 3, 3}, arr);
    }

    @Test
    @DisplayName("空数组应正常处理")
    void shouldHandleEmptyArray() {
        int[] arr = {};
        quickSortService.sort(arr);
        assertArrayEquals(new int[]{}, arr);
    }

    @Test
    @DisplayName("单元素数组应保持不变")
    void shouldHandleSingleElementArray() {
        int[] arr = {42};
        quickSortService.sort(arr);
        assertArrayEquals(new int[]{42}, arr);
    }

    @Test
    @DisplayName("null 输入应抛出 IllegalArgumentException")
    void shouldThrowOnNullInput() {
        assertThrows(IllegalArgumentException.class, () -> quickSortService.sort(null));
    }

    @Test
    @DisplayName("大数组排序正确性")
    void shouldSortLargeArray() {
        int[] arr = new int[1000];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = arr.length - i;
        }
        quickSortService.sort(arr);
        for (int i = 0; i < arr.length; i++) {
            assertEquals(i + 1, arr[i]);
        }
    }
}