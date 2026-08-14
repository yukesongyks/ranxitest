package com.example.myapp.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * QuickSort 单元测试类
 *
 * @author DTCoder
 * @date 2026/08/14
 */
@DisplayName("QuickSort 快速排序算法测试")
class QuickSortTest {

    // ======================== S01: sort(int[] arr) ========================

    @Nested
    @DisplayName("sort(int[] arr) — 整数数组升序排序")
    class SortIntArrayAscending {

        @Test
        @DisplayName("正常数组升序排序")
        void should_sortAscending_when_normalArray() {
            int[] arr = {5, 3, 8, 1, 9, 2};
            QuickSort.sort(arr);
            assertArrayEquals(new int[]{1, 2, 3, 5, 8, 9}, arr);
        }

        @Test
        @DisplayName("null 数组直接返回不抛异常")
        void should_returnGracefully_when_nullArray() {
            assertDoesNotThrow(() -> QuickSort.sort((int[]) null));
        }

        @Test
        @DisplayName("空数组直接返回")
        void should_returnGracefully_when_emptyArray() {
            int[] arr = {};
            QuickSort.sort(arr);
            assertEquals(0, arr.length);
        }

        @Test
        @DisplayName("单元素数组直接返回")
        void should_returnGracefully_when_singleElement() {
            int[] arr = {42};
            QuickSort.sort(arr);
            assertArrayEquals(new int[]{42}, arr);
        }

        @Test
        @DisplayName("已升序数组排序后仍为升序")
        void should_keepOrder_when_alreadySorted() {
            int[] arr = {1, 2, 3, 4, 5};
            QuickSort.sort(arr);
            assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);
        }

        @Test
        @DisplayName("已降序数组排序后为升序")
        void should_sortAscending_when_reverseSorted() {
            int[] arr = {5, 4, 3, 2, 1};
            QuickSort.sort(arr);
            assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);
        }

        @Test
        @DisplayName("包含重复元素的数组排序")
        void should_handleDuplicates_when_duplicateElements() {
            int[] arr = {3, 1, 3, 2, 1, 2};
            QuickSort.sort(arr);
            assertArrayEquals(new int[]{1, 1, 2, 2, 3, 3}, arr);
        }

        @Test
        @DisplayName("所有元素相同的数组排序")
        void should_handleAllSame_when_allElementsEqual() {
            int[] arr = {7, 7, 7, 7, 7};
            QuickSort.sort(arr);
            assertArrayEquals(new int[]{7, 7, 7, 7, 7}, arr);
        }

        @Test
        @DisplayName("大数组排序")
        void should_sortLargeArray() {
            int size = 1000;
            int[] arr = new int[size];
            for (int i = 0; i < size; i++) {
                arr[i] = size - i;
            }
            QuickSort.sort(arr);
            for (int i = 0; i < size - 1; i++) {
                assertTrue(arr[i] <= arr[i + 1],
                        "Element at " + i + " should be <= element at " + (i + 1));
            }
        }
    }

    // ======================== S02: sort(int[] arr, boolean ascending) ========================

    @Nested
    @DisplayName("sort(int[] arr, boolean ascending) — 整数数组带方向排序")
    class SortIntArrayWithDirection {

        @Test
        @DisplayName("升序排序")
        void should_sortAscending() {
            int[] arr = {5, 3, 8, 1, 9, 2};
            QuickSort.sort(arr, true);
            assertArrayEquals(new int[]{1, 2, 3, 5, 8, 9}, arr);
        }

        @Test
        @DisplayName("降序排序")
        void should_sortDescending() {
            int[] arr = {5, 3, 8, 1, 9, 2};
            QuickSort.sort(arr, false);
            assertArrayEquals(new int[]{9, 8, 5, 3, 2, 1}, arr);
        }

        @Test
        @DisplayName("null 数组直接返回")
        void should_returnGracefully_when_nullArray() {
            assertDoesNotThrow(() -> QuickSort.sort((int[]) null, true));
        }

        @Test
        @DisplayName("空数组降序不抛异常")
        void should_returnGracefully_when_emptyArray() {
            int[] arr = {};
            QuickSort.sort(arr, false);
            assertEquals(0, arr.length);
        }
    }

    // ======================== S03: sort(T[] arr) — Comparable 泛型 ========================

    @Nested
    @DisplayName("sort(T[] arr) — 泛型 Comparable 升序排序")
    class SortComparableArray {

        @Test
        @DisplayName("String 数组升序排序")
        void should_sortStringsAscending() {
            String[] arr = {"banana", "apple", "cherry", "date"};
            QuickSort.sort(arr);
            assertArrayEquals(new String[]{"apple", "banana", "cherry", "date"}, arr);
        }

        @Test
        @DisplayName("Integer 数组升序排序")
        void should_sortIntegersAscending() {
            Integer[] arr = {5, 3, 8, 1, 9, 2};
            QuickSort.sort(arr);
            assertArrayEquals(new Integer[]{1, 2, 3, 5, 8, 9}, arr);
        }

        @Test
        @DisplayName("null 数组直接返回")
        void should_returnGracefully_when_nullArray() {
            assertDoesNotThrow(() -> QuickSort.sort((String[]) null));
        }

        @Test
        @DisplayName("空数组直接返回")
        void should_returnGracefully_when_emptyArray() {
            String[] arr = {};
            QuickSort.sort(arr);
            assertEquals(0, arr.length);
        }

        @Test
        @DisplayName("单元素数组直接返回")
        void should_returnGracefully_when_singleElement() {
            String[] arr = {"hello"};
            QuickSort.sort(arr);
            assertArrayEquals(new String[]{"hello"}, arr);
        }
    }

    // ======================== S04: sort(T[] arr, boolean ascending) ========================

    @Nested
    @DisplayName("sort(T[] arr, boolean ascending) — 泛型带方向排序")
    class SortComparableWithDirection {

        @Test
        @DisplayName("String 数组降序排序")
        void should_sortStringsDescending() {
            String[] arr = {"banana", "apple", "cherry", "date"};
            QuickSort.sort(arr, false);
            assertArrayEquals(new String[]{"date", "cherry", "banana", "apple"}, arr);
        }

        @Test
        @DisplayName("Integer 数组降序排序")
        void should_sortIntegersDescending() {
            Integer[] arr = {5, 3, 8, 1, 9, 2};
            QuickSort.sort(arr, false);
            assertArrayEquals(new Integer[]{9, 8, 5, 3, 2, 1}, arr);
        }
    }

    // ======================== S05: sort(T[] arr, Comparator<T> comparator) ========================

    @Nested
    @DisplayName("sort(T[] arr, Comparator<T> comparator) — 自定义比较器排序")
    class SortWithComparator {

        @Test
        @DisplayName("自定义比较器按字符串长度排序")
        void should_sortByLength_when_customComparator() {
            String[] arr = {"apple", "pie", "banana", "a", "cherry"};
            QuickSort.sort(arr, Comparator.comparingInt(String::length));
            assertArrayEquals(new String[]{"a", "pie", "apple", "banana", "cherry"}, arr);
        }

        @Test
        @DisplayName("自定义比较器降序排序")
        void should_sortDescending_when_reverseComparator() {
            Integer[] arr = {5, 3, 8, 1, 9, 2};
            QuickSort.sort(arr, Comparator.reverseOrder());
            assertArrayEquals(new Integer[]{9, 8, 5, 3, 2, 1}, arr);
        }

        @Test
        @DisplayName("null 比较器抛出 IllegalArgumentException")
        void should_throwException_when_nullComparator() {
            Integer[] arr = {5, 3, 8};
            assertThrows(IllegalArgumentException.class,
                    () -> QuickSort.sort(arr, null));
        }

        @Test
        @DisplayName("null 数组直接返回（不抛异常）")
        void should_returnGracefully_when_nullArray() {
            assertDoesNotThrow(() -> QuickSort.sort((String[]) null, Comparator.naturalOrder()));
        }

        @Test
        @DisplayName("自定义对象按字段排序")
        void should_sortCustomObjects() {
            Person[] people = {
                    new Person("Charlie", 30),
                    new Person("Alice", 25),
                    new Person("Bob", 35)
            };
            QuickSort.sort(people, Comparator.comparingInt(Person::getAge));
            assertEquals("Alice", people[0].getName());
            assertEquals("Charlie", people[1].getName());
            assertEquals("Bob", people[2].getName());
        }
    }

    /**
     * 测试用 Person 类
     */
    static class Person {
        private final String name;
        private final int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        String getName() {
            return name;
        }

        int getAge() {
            return age;
        }
    }
}