package com.example.myapp.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * {@link BubbleSort} 单元测试。
 *
 * <p>覆盖正常、逆序、重复元素、空数组、null、单元素等场景，
 * 并验证方法的无副作用语义（不修改入参）。
 */
class BubbleSortTest {

    @Test
    @DisplayName("正常乱序数组应被正确排序")
    void shouldSortUnorderedArray() {
        int[] input = {5, 3, 8, 1, 9, 2, 7};
        int[] expected = {1, 2, 3, 5, 7, 8, 9};

        int[] actual = BubbleSort.sort(input);

        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("完全逆序数组应被正确排序")
    void shouldSortReverseOrderedArray() {
        int[] input = {9, 8, 7, 6, 5, 4, 3, 2, 1};
        int[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9};

        int[] actual = BubbleSort.sort(input);

        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("含重复元素的数组应被正确排序且保持稳定")
    void shouldSortArrayWithDuplicates() {
        int[] input = {4, 2, 4, 1, 2, 3};
        int[] expected = {1, 2, 2, 3, 4, 4};

        int[] actual = BubbleSort.sort(input);

        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("已排序数组应直接返回有序结果")
    void shouldReturnSortedForAlreadySortedArray() {
        int[] input = {1, 2, 3, 4, 5};
        int[] expected = {1, 2, 3, 4, 5};

        int[] actual = BubbleSort.sort(input);

        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("单元素数组应保持不变")
    void shouldHandleSingleElementArray() {
        int[] input = {42};

        int[] actual = BubbleSort.sort(input);

        assertArrayEquals(new int[]{42}, actual);
    }

    @Test
    @DisplayName("空数组应返回空数组")
    void shouldReturnEmptyArrayForEmptyInput() {
        int[] input = {};

        int[] actual = BubbleSort.sort(input);

        assertArrayEquals(new int[0], actual);
    }

    @Test
    @DisplayName("null 入参应返回空数组而非抛出异常")
    void shouldReturnEmptyArrayForNullInput() {
        int[] actual = BubbleSort.sort(null);

        assertArrayEquals(new int[0], actual);
    }

    @Test
    @DisplayName("方法不应修改原始入参数组")
    void shouldNotMutateInputArray() {
        int[] input = {5, 3, 1, 4, 2};
        int[] inputCopy = {5, 3, 1, 4, 2};

        BubbleSort.sort(input);

        assertArrayEquals(inputCopy, input);
    }

    @Test
    @DisplayName("方法应返回新数组实例而非入参本身")
    void shouldReturnNewArrayInstance() {
        int[] input = {3, 1, 2};

        int[] actual = BubbleSort.sort(input);

        assertNotSame(input, actual);
    }
}
