package com.example.myapp.utils;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * QuickSort 单元测试，覆盖边界条件和典型场景。
 */
class QuickSortTest {

    // ─── 空数组 / null ──────────────────────────────────

    @Test
    void shouldHandleNullArray() {
        QuickSort.sort((Integer[]) null);
        // 不抛异常即为通过
    }

    @Test
    void shouldHandleEmptyArray() {
        Integer[] arr = {};
        QuickSort.sort(arr);
        assertEquals(0, arr.length);
    }

    // ─── 单元素 ─────────────────────────────────────────

    @Test
    void shouldHandleSingleElement() {
        Integer[] arr = {42};
        QuickSort.sort(arr);
        assertArrayEquals(new Integer[]{42}, arr);
    }

    // ─── 已排序数组 ─────────────────────────────────────

    @Test
    void shouldSortAlreadySortedArray() {
        Integer[] arr = {1, 2, 3, 4, 5};
        QuickSort.sort(arr);
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5}, arr);
    }

    // ─── 逆序数组 ───────────────────────────────────────

    @Test
    void shouldSortReverseSortedArray() {
        Integer[] arr = {5, 4, 3, 2, 1};
        QuickSort.sort(arr);
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5}, arr);
    }

    // ─── 包含重复元素 ───────────────────────────────────

    @Test
    void shouldSortArrayWithDuplicates() {
        Integer[] arr = {3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5};
        Integer[] expected = arr.clone();
        Arrays.sort(expected);
        QuickSort.sort(arr);
        assertArrayEquals(expected, arr);
    }

    @Test
    void shouldSortAllSameElements() {
        Integer[] arr = {7, 7, 7, 7, 7};
        QuickSort.sort(arr);
        assertArrayEquals(new Integer[]{7, 7, 7, 7, 7}, arr);
    }

    // ─── 随机数组（与 Java 内置排序对比）────────────────

    @Test
    void shouldSortRandomArray() {
        Random random = new Random(42);
        Integer[] arr = new Integer[1000];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = random.nextInt(10000);
        }
        Integer[] expected = arr.clone();
        Arrays.sort(expected);
        QuickSort.sort(arr);
        assertArrayEquals(expected, arr);
    }

    // ─── 字符串排序 ─────────────────────────────────────

    @Test
    void shouldSortStringArray() {
        String[] arr = {"banana", "apple", "cherry", "date", "elderberry"};
        String[] expected = arr.clone();
        Arrays.sort(expected);
        QuickSort.sort(arr);
        assertArrayEquals(expected, arr);
    }

    // ─── 双元素 ─────────────────────────────────────────

    @Test
    void shouldSortTwoElements() {
        Integer[] arr = {2, 1};
        QuickSort.sort(arr);
        assertArrayEquals(new Integer[]{1, 2}, arr);
    }

    // ─── 大数组（奇偶长度） ─────────────────────────────

    @Test
    void shouldSortLargeOddLengthArray() {
        Integer[] arr = new Integer[999];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = arr.length - i;
        }
        Integer[] expected = arr.clone();
        Arrays.sort(expected);
        QuickSort.sort(arr);
        assertArrayEquals(expected, arr);
    }
}