package com.example.myapp.utils;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class QuickSortTest {

    // ── 基础功能测试 ──

    @Test
    void shouldSortIntegerArray() {
        Integer[] array = {5, 3, 8, 1, 9, 2, 7, 4, 6};
        QuickSort.sort(array);
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, array);
    }

    @Test
    void shouldSortStringArray() {
        String[] array = {"banana", "apple", "cherry", "date"};
        QuickSort.sort(array);
        assertArrayEquals(new String[]{"apple", "banana", "cherry", "date"}, array);
    }

    @Test
    void shouldSortWithCustomComparator() {
        String[] array = {"a", "bbb", "cc", "dddd"};
        QuickSort.sort(array, Comparator.comparingInt(String::length));
        assertArrayEquals(new String[]{"a", "cc", "bbb", "dddd"}, array);
    }

    // ── 边界条件测试 ──

    @Test
    void shouldHandleNullArray() {
        QuickSort.sort((Integer[]) null);
        // 无异常即为通过
    }

    @Test
    void shouldHandleEmptyArray() {
        Integer[] array = {};
        QuickSort.sort(array);
        assertEquals(0, array.length);
    }

    @Test
    void shouldHandleSingleElement() {
        Integer[] array = {42};
        QuickSort.sort(array);
        assertArrayEquals(new Integer[]{42}, array);
    }

    @Test
    void shouldHandleTwoElements() {
        Integer[] array = {9, 3};
        QuickSort.sort(array);
        assertArrayEquals(new Integer[]{3, 9}, array);
    }

    @Test
    void shouldHandleAlreadySorted() {
        Integer[] array = {1, 2, 3, 4, 5};
        QuickSort.sort(array);
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5}, array);
    }

    @Test
    void shouldHandleReverseSorted() {
        Integer[] array = {5, 4, 3, 2, 1};
        QuickSort.sort(array);
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5}, array);
    }

    @Test
    void shouldHandleAllEqualElements() {
        Integer[] array = {7, 7, 7, 7, 7};
        QuickSort.sort(array);
        assertArrayEquals(new Integer[]{7, 7, 7, 7, 7}, array);
    }

    @Test
    void shouldHandleDuplicates() {
        Integer[] array = {3, 1, 4, 1, 5, 9, 2, 6, 5, 3};
        QuickSort.sort(array);
        assertArrayEquals(new Integer[]{1, 1, 2, 3, 3, 4, 5, 5, 6, 9}, array);
    }

    @Test
    void shouldThrowOnNullComparator() {
        assertThrows(IllegalArgumentException.class, () ->
                QuickSort.sort(new Integer[]{1, 2, 3}, null));
    }

    // ── 随机数据压力测试 ──

    @Test
    void shouldSortLargeRandomArray() {
        int size = 10_000;
        Integer[] array = new Integer[size];
        Integer[] expected = new Integer[size];
        Random rng = new Random(42);
        for (int i = 0; i < size; i++) {
            int val = rng.nextInt();
            array[i] = val;
            expected[i] = val;
        }
        Arrays.sort(expected);
        QuickSort.sort(array);
        assertArrayEquals(expected, array);
    }
}