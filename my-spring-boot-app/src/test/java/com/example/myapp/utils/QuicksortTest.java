package com.example.myapp.utils;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class QuicksortTest {

    // ── int[] tests ───────────────────────────────────────────────

    @Test
    void intArray_null_shouldReturn() {
        Quicksort.sort((int[]) null);
        // no NPE
    }

    @Test
    void intArray_empty_shouldRemainEmpty() {
        int[] arr = {};
        Quicksort.sort(arr);
        assertEquals(0, arr.length);
    }

    @Test
    void intArray_singleElement_shouldRemainUnchanged() {
        int[] arr = {42};
        Quicksort.sort(arr);
        assertArrayEquals(new int[]{42}, arr);
    }

    @Test
    void intArray_alreadySorted_shouldRemainSorted() {
        int[] arr = {1, 2, 3, 4, 5};
        Quicksort.sort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    void intArray_reverseSorted_shouldSortAscending() {
        int[] arr = {5, 4, 3, 2, 1};
        Quicksort.sort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    void intArray_unsorted_shouldSort() {
        int[] arr = {3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5};
        Quicksort.sort(arr);
        int[] expected = {1, 1, 2, 3, 3, 4, 5, 5, 5, 6, 9};
        assertArrayEquals(expected, arr);
    }

    @Test
    void intArray_allEqual_shouldRemainUnchanged() {
        int[] arr = {7, 7, 7, 7, 7};
        Quicksort.sort(arr);
        assertArrayEquals(new int[]{7, 7, 7, 7, 7}, arr);
    }

    @Test
    void intArray_negativeValues_shouldSort() {
        int[] arr = {-3, 0, -1, 5, -5, 2};
        Quicksort.sort(arr);
        assertArrayEquals(new int[]{-5, -3, -1, 0, 2, 5}, arr);
    }

    @Test
    void intArray_largeArray_shouldSort() {
        int n = 1000;
        int[] arr = new int[n];
        int[] expected = new int[n];
        for (int i = 0; i < n; i++) {
            expected[i] = i;
            arr[n - 1 - i] = i;
        }
        Quicksort.sort(arr);
        assertArrayEquals(expected, arr);
    }

    // ── List<T> tests ─────────────────────────────────────────────

    @Test
    void list_null_shouldReturn() {
        Quicksort.sort((List<Integer>) null);
        // no NPE
    }

    @Test
    void list_empty_shouldRemainEmpty() {
        List<Integer> list = new ArrayList<>();
        Quicksort.sort(list);
        assertEquals(0, list.size());
    }

    @Test
    void list_singleElement_shouldRemainUnchanged() {
        List<Integer> list = new ArrayList<>(List.of(42));
        Quicksort.sort(list);
        assertEquals(List.of(42), list);
    }

    @Test
    void list_unsorted_shouldSort() {
        List<Integer> list = new ArrayList<>(List.of(3, 1, 4, 1, 5, 9, 2, 6));
        Quicksort.sort(list);
        assertEquals(List.of(1, 1, 2, 3, 4, 5, 6, 9), list);
    }

    @Test
    void list_strings_shouldSortAlphabetically() {
        List<String> list = new ArrayList<>(List.of("banana", "apple", "cherry", "date"));
        Quicksort.sort(list);
        assertEquals(List.of("apple", "banana", "cherry", "date"), list);
    }

    @Test
    void list_reverseSorted_shouldSortAscending() {
        List<Integer> list = new ArrayList<>(List.of(5, 4, 3, 2, 1));
        Quicksort.sort(list);
        assertEquals(List.of(1, 2, 3, 4, 5), list);
    }

    @Test
    void list_allEqual_shouldRemainUnchanged() {
        List<Integer> list = new ArrayList<>(Collections.nCopies(5, 7));
        Quicksort.sort(list);
        assertEquals(Collections.nCopies(5, 7), list);
    }
}