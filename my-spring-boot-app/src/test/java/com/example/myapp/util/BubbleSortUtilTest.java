package com.example.myapp.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("BubbleSortUtil 冒泡排序测试")
class BubbleSortUtilTest {

    @Test
    @DisplayName("正常无序数组排序")
    void shouldSortUnsortedArray() {
        int[] input = {5, 3, 8, 1, 9, 2};
        int[] expected = {1, 2, 3, 5, 8, 9};
        assertArrayEquals(expected, BubbleSortUtil.sort(input));
    }

    @Test
    @DisplayName("已排序数组不应变化")
    void shouldHandleAlreadySortedArray() {
        int[] input = {1, 2, 3, 4, 5};
        int[] expected = {1, 2, 3, 4, 5};
        assertArrayEquals(expected, BubbleSortUtil.sort(input));
    }

    @Test
    @DisplayName("逆序数组排序")
    void shouldSortReversedArray() {
        int[] input = {9, 7, 5, 3, 1};
        int[] expected = {1, 3, 5, 7, 9};
        assertArrayEquals(expected, BubbleSortUtil.sort(input));
    }

    @Test
    @DisplayName("包含重复元素的数组")
    void shouldHandleDuplicateElements() {
        int[] input = {4, 2, 4, 1, 2};
        int[] expected = {1, 2, 2, 4, 4};
        assertArrayEquals(expected, BubbleSortUtil.sort(input));
    }

    @Test
    @DisplayName("单元素数组")
    void shouldHandleSingleElement() {
        int[] input = {1};
        int[] expected = {1};
        assertArrayEquals(expected, BubbleSortUtil.sort(input));
    }

    @Test
    @DisplayName("空数组")
    void shouldHandleEmptyArray() {
        int[] input = {};
        int[] expected = {};
        assertArrayEquals(expected, BubbleSortUtil.sort(input));
    }

    @Test
    @DisplayName("null 输入应返回 null")
    void shouldHandleNullInput() {
        assertNull(BubbleSortUtil.sort(null));
    }

    @Test
    @DisplayName("两个元素的数组")
    void shouldSortTwoElementArray() {
        int[] input = {7, 3};
        int[] expected = {3, 7};
        assertArrayEquals(expected, BubbleSortUtil.sort(input));
    }
}