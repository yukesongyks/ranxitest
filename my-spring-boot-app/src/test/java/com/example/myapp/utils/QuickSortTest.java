package com.example.myapp.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link QuickSort} 单元测试。
 *
 * <p>覆盖边界条件：空数组、单元素、已升序、逆序、重复元素、全相同、含负数、
 * 普通无序、大数组随机校验、null 校验。
 *
 * @author example
 * @since 1.0
 */
@DisplayName("快速排序工具类测试")
class QuickSortTest {

    @Test
    @DisplayName("空数组排序后保持为空")
    void shouldHandleEmptyArray() {
        int[] arr = new int[0];
        QuickSort.sort(arr);
        assertArrayEquals(new int[0], arr);
    }

    @Test
    @DisplayName("单元素数组排序后不变")
    void shouldHandleSingleElementArray() {
        int[] arr = {42};
        QuickSort.sort(arr);
        assertArrayEquals(new int[]{42}, arr);
    }

    @Test
    @DisplayName("已升序数组排序后不变")
    void shouldHandleAlreadySortedArray() {
        int[] arr = {1, 2, 3, 4, 5};
        QuickSort.sort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    @DisplayName("逆序数组排序后升序")
    void shouldSortDescendingArray() {
        int[] arr = {5, 4, 3, 2, 1};
        QuickSort.sort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    @DisplayName("含重复元素的数组排序稳定为升序")
    void shouldSortArrayWithDuplicates() {
        int[] arr = {3, 1, 4, 1, 5, 9, 2, 6, 5, 3};
        QuickSort.sort(arr);
        assertArrayEquals(new int[]{1, 1, 2, 3, 3, 4, 5, 5, 6, 9}, arr);
    }

    @Test
    @DisplayName("全相同元素数组排序后不变")
    void shouldHandleAllEqualArray() {
        int[] arr = {7, 7, 7, 7, 7};
        QuickSort.sort(arr);
        assertArrayEquals(new int[]{7, 7, 7, 7, 7}, arr);
    }

    @Test
    @DisplayName("含负数数组正确排序")
    void shouldSortArrayWithNegatives() {
        int[] arr = {3, -1, -4, 0, 2, -5};
        QuickSort.sort(arr);
        assertArrayEquals(new int[]{-5, -4, -1, 0, 2, 3}, arr);
    }

    @Test
    @DisplayName("普通无序数组正确排序")
    void shouldSortRandomArray() {
        int[] arr = {9, 3, 7, 1, 8, 2, 6, 4, 5};
        QuickSort.sort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, arr);
    }

    @Test
    @DisplayName("大数组排序结果与标准库一致")
    void shouldSortLargeRandomArray() {
        Random random = new Random(20260730L);
        int[] arr = random.ints(1000, Integer.MIN_VALUE, Integer.MAX_VALUE).toArray();
        int[] expected = Arrays.copyOf(arr, arr.length);
        Arrays.sort(expected);
        QuickSort.sort(arr);
        assertArrayEquals(expected, arr);
    }

    @Test
    @DisplayName("null 入参抛出 IllegalArgumentException")
    void shouldThrowWhenInputIsNull() {
        assertThrows(IllegalArgumentException.class, () -> QuickSort.sort(null));
    }
}
