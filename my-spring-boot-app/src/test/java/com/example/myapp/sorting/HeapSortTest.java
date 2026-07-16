package com.example.myapp.sorting;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HeapSortTest {

    @Test
    void shouldSortUnsortedIntegerArrayAscending() {
        Integer[] array = {5, 2, 8, 1, 9, 3};
        Integer[] expected = {1, 2, 3, 5, 8, 9};
        HeapSort.sort(array);
        assertArrayEquals(expected, array);
    }

    @Test
    void shouldSortUnsortedIntegerArrayDescending() {
        Integer[] array = {5, 2, 8, 1, 9, 3};
        Integer[] expected = {9, 8, 5, 3, 2, 1};
        HeapSort.sort(array, false);
        assertArrayEquals(expected, array);
    }

    @Test
    void shouldHandleAlreadySortedArray() {
        Integer[] array = {1, 2, 3, 4, 5};
        Integer[] expected = {1, 2, 3, 4, 5};
        HeapSort.sort(array);
        assertArrayEquals(expected, array);
    }

    @Test
    void shouldHandleReverseSortedArray() {
        Integer[] array = {5, 4, 3, 2, 1};
        Integer[] expected = {1, 2, 3, 4, 5};
        HeapSort.sort(array);
        assertArrayEquals(expected, array);
    }

    @Test
    void shouldHandleDuplicates() {
        Integer[] array = {3, 1, 3, 2, 1, 2};
        Integer[] expected = {1, 1, 2, 2, 3, 3};
        HeapSort.sort(array);
        assertArrayEquals(expected, array);
    }

    @Test
    void shouldHandleSingleElementArray() {
        Integer[] array = {42};
        Integer[] expected = {42};
        HeapSort.sort(array);
        assertArrayEquals(expected, array);
    }

    @Test
    void shouldHandleEmptyArray() {
        Integer[] array = {};
        Integer[] expected = {};
        HeapSort.sort(array);
        assertArrayEquals(expected, array);
    }

    @Test
    void shouldThrowOnNullArray() {
        assertThrows(IllegalArgumentException.class, () -> HeapSort.sort(null));
    }

    @Test
    void shouldSortStringArray() {
        String[] array = {"banana", "apple", "cherry", "date"};
        String[] expected = {"apple", "banana", "cherry", "date"};
        HeapSort.sort(array);
        assertArrayEquals(expected, array);
    }

    @Test
    void shouldSortStringArrayDescending() {
        String[] array = {"banana", "apple", "cherry", "date"};
        String[] expected = {"date", "cherry", "banana", "apple"};
        HeapSort.sort(array, false);
        assertArrayEquals(expected, array);
    }
}