package com.example.myapp.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QuickSortTest {

    private List<Integer> toList(int... nums) {
        List<Integer> list = new ArrayList<>();
        for (int n : nums) {
            list.add(n);
        }
        return list;
    }

    @Test
    void sortUnsortedArray() {
        List<Integer> items = toList(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5);
        new QuickSort<Integer>().sort(items);
        assertEquals(Arrays.asList(1, 1, 2, 3, 3, 4, 5, 5, 5, 6, 9), items);
    }

    @Test
    void sortAlreadySortedArray() {
        List<Integer> items = toList(1, 2, 3, 4, 5);
        new QuickSort<Integer>().sort(items);
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), items);
    }

    @Test
    void sortReverseSortedArray() {
        List<Integer> items = toList(5, 4, 3, 2, 1);
        new QuickSort<Integer>().sort(items);
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), items);
    }

    @Test
    void sortDuplicates() {
        List<Integer> items = toList(2, 2, 2, 1, 1, 1);
        new QuickSort<Integer>().sort(items);
        assertEquals(Arrays.asList(1, 1, 1, 2, 2, 2), items);
    }

    @Test
    void sortSingleElement() {
        List<Integer> items = toList(42);
        new QuickSort<Integer>().sort(items);
        assertEquals(Collections.singletonList(42), items);
    }

    @Test
    void sortEmptyListIsNoOp() {
        List<Integer> items = new ArrayList<>();
        new QuickSort<Integer>().sort(items);
        assertEquals(Collections.emptyList(), items);
    }

    @Test
    void sortNullListIsNoOp() {
        // 空操作，不应抛异常
        new QuickSort<Integer>().sort(null);
    }

    @Test
    void sortWithCustomComparator() {
        List<String> items = new ArrayList<>(Arrays.asList("banana", "apple", "cherry", "date"));
        new QuickSort<String>(Comparator.reverseOrder()).sort(items);
        assertEquals(Arrays.asList("date", "cherry", "banana", "apple"), items);
    }

    @Test
    void sortNegativesAndZero() {
        List<Integer> items = toList(0, -3, -1, 2, -5, 4);
        new QuickSort<Integer>().sort(items);
        assertEquals(Arrays.asList(-5, -3, -1, 0, 2, 4), items);
    }

    @Test
    void sortStabilityOfComparatorEquality() {
        // 同值元素的相对顺序在 Lomuto 方案下不保证稳定，这里仅校验结果有序
        List<Integer> items = toList(3, 1, 2, 1, 3, 1);
        new QuickSort<Integer>().sort(items);
        Integer[] expected = {1, 1, 1, 2, 3, 3};
        assertArrayEquals(expected, items.toArray(new Integer[0]));
    }

    @Test
    void nullComparatorThrows() {
        assertThrows(IllegalArgumentException.class, () -> new QuickSort<Integer>(null));
    }
}
