package com.example.myapp.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link QuickSort} 单元测试。
 * <p>覆盖快速排序的边界条件、常规场景与异常入参，确保算法正确性与健壮性。</p>
 */
@DisplayName("快速排序 QuickSort 测试")
class QuickSortTest {

    private static final int[] EMPTY = new int[0];

    @Nested
    @DisplayName("sort(int[]) 数组排序")
    class PrimitiveArraySort {

        @Test
        @DisplayName("空数组：返回空数组且不抛异常")
        void emptyArray() {
            assertArrayEquals(EMPTY, QuickSort.sort(EMPTY));
        }

        @Test
        @DisplayName("单元素数组：保持不变")
        void singleElement() {
            int[] input = {7};
            assertArrayEquals(new int[]{7}, QuickSort.sort(input));
        }

        @Test
        @DisplayName("已升序数组：保持不变")
        void alreadySorted() {
            int[] input = {1, 2, 3, 4, 5};
            assertArrayEquals(new int[]{1, 2, 3, 4, 5}, QuickSort.sort(input));
        }

        @Test
        @DisplayName("逆序数组：升序排列")
        void reverseOrder() {
            int[] input = {5, 4, 3, 2, 1};
            assertArrayEquals(new int[]{1, 2, 3, 4, 5}, QuickSort.sort(input));
        }

        @Test
        @DisplayName("含重复元素：稳定升序排列")
        void duplicateElements() {
            int[] input = {3, 1, 2, 3, 1, 2};
            assertArrayEquals(new int[]{1, 1, 2, 2, 3, 3}, QuickSort.sort(input));
        }

        @Test
        @DisplayName("含负数与零：正确升序排列")
        void negativeAndZero() {
            int[] input = {0, -3, 5, -1, 2, -3};
            assertArrayEquals(new int[]{-3, -3, -1, 0, 2, 5}, QuickSort.sort(input));
        }

        @Test
        @DisplayName("null 入参：抛出 IllegalArgumentException")
        void nullInput() {
            assertThrows(IllegalArgumentException.class, () -> QuickSort.sort((int[]) null));
        }

        @Test
        @DisplayName("不修改原数组引用但返回新数组")
        void returnsNewArrayImmutableInput() {
            int[] input = {3, 1, 2};
            int[] result = QuickSort.sort(input);
            assertArrayEquals(new int[]{1, 2, 3}, result);
            assertEquals(3, input[0], "原数组内容应保持不变");
        }
    }

    @Nested
    @DisplayName("sort(List) 列表排序")
    class ListSort {

        @Test
        @DisplayName("空列表：返回空列表")
        void emptyList() {
            List<Integer> result = QuickSort.sort(Collections.emptyList());
            assertEquals(Collections.emptyList(), result);
        }

        @Test
        @DisplayName("单元素列表：保持不变")
        void singleElementList() {
            List<Integer> result = QuickSort.sort(Collections.singletonList(9));
            assertEquals(Collections.singletonList(9), result);
        }

        @Test
        @DisplayName("常规乱序列表：升序排列")
        void unorderedList() {
            List<Integer> result = QuickSort.sort(Arrays.asList(5, 3, 8, 1, 9, 2));
            assertEquals(Arrays.asList(1, 2, 3, 5, 8, 9), result);
        }

        @Test
        @DisplayName("null 入参：抛出 IllegalArgumentException")
        void nullList() {
            assertThrows(IllegalArgumentException.class, () -> QuickSort.sort((List<Integer>) null));
        }

        @Test
        @DisplayName("列表含 null 元素：抛出 NullPointerException")
        void listWithNullElement() {
            List<Integer> input = Arrays.asList(3, null, 1);
            assertThrows(NullPointerException.class, () -> QuickSort.sort(input));
        }
    }
}
