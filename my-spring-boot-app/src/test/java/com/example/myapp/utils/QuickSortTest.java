package com.example.myapp.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Comparator;

class QuickSortTest {

    @Test
    @DisplayName("sort Integer array with natural ordering")
    void sortIntegerArray() {
        Integer[] array = {5, 3, 8, 1, 9, 2, 7, 4, 6};
        QuickSort.sort(array);
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, array);
    }

    @Test
    @DisplayName("sort already sorted array")
    void sortAlreadySorted() {
        Integer[] array = {1, 2, 3, 4, 5};
        QuickSort.sort(array);
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5}, array);
    }

    @Test
    @DisplayName("sort reverse sorted array")
    void sortReverseSorted() {
        Integer[] array = {5, 4, 3, 2, 1};
        QuickSort.sort(array);
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5}, array);
    }

    @Test
    @DisplayName("sort array with duplicates")
    void sortWithDuplicates() {
        Integer[] array = {3, 1, 4, 1, 5, 9, 2, 6, 5, 3};
        QuickSort.sort(array);
        assertArrayEquals(new Integer[]{1, 1, 2, 3, 3, 4, 5, 5, 6, 9}, array);
    }

    @Test
    @DisplayName("sort empty array")
    void sortEmptyArray() {
        Integer[] array = {};
        QuickSort.sort(array);
        assertArrayEquals(new Integer[]{}, array);
    }

    @Test
    @DisplayName("sort single element array")
    void sortSingleElement() {
        Integer[] array = {42};
        QuickSort.sort(array);
        assertArrayEquals(new Integer[]{42}, array);
    }

    @Test
    @DisplayName("sort String array with natural ordering")
    void sortStringArray() {
        String[] array = {"banana", "apple", "cherry", "date"};
        QuickSort.sort(array);
        assertArrayEquals(new String[]{"apple", "banana", "cherry", "date"}, array);
    }

    @Test
    @DisplayName("sort with custom comparator (descending)")
    void sortWithCustomComparator() {
        Integer[] array = {5, 3, 8, 1, 9};
        QuickSort.sort(array, Comparator.reverseOrder());
        assertArrayEquals(new Integer[]{9, 8, 5, 3, 1}, array);
    }

    @Test
    @DisplayName("sort with custom comparator by string length")
    void sortByStringLength() {
        String[] array = {"a", "abc", "ab", "abcd"};
        QuickSort.sort(array, Comparator.comparingInt(String::length));
        assertArrayEquals(new String[]{"a", "ab", "abc", "abcd"}, array);
    }

    @Test
    @DisplayName("null array throws NullPointerException")
    void nullArrayThrows() {
        assertThrows(NullPointerException.class, () -> QuickSort.sort((Integer[]) null));
        assertThrows(NullPointerException.class, () -> QuickSort.sort(null, Comparator.naturalOrder()));
    }
}