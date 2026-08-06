package com.example.myapp.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link QuickSort} 单元测试类。
 *
 * @author DTCoder
 * @date 2026/08/06
 */
class QuickSortTest {

    /**
     * 排序整数数组的常规场景，验证结果与自然升序一致。
     */
    @Test
    @DisplayName("给定乱序整数数组，排序后应与期望升序一致")
    void givenUnsortedIntegerArray_whenSort_thenResultIsAscending() {
        // given
        Integer[] array = {5, 2, 9, 1, 5, 6};

        // when
        Integer[] result = QuickSort.sort(array);

        // then
        assertThat(result).containsExactly(1, 2, 5, 5, 6, 9);
    }

    /**
     * 入参为 null 时应原样返回，不应抛出空指针异常。
     */
    @Test
    @DisplayName("给定 null 入参，排序应原样返回 null")
    void givenNullArray_whenSort_thenReturnNull() {
        // given
        Integer[] array = null;

        // when
        Integer[] result = QuickSort.sort(array);

        // then
        assertThat(result).isNull();
    }

    /**
     * 空数组无需排序，应原样返回且不抛异常。
     */
    @Test
    @DisplayName("给定空数组，排序应原样返回空数组")
    void givenEmptyArray_whenSort_thenReturnEmptyArray() {
        // given
        Integer[] array = new Integer[0];

        // when
        Integer[] result = QuickSort.sort(array);

        // then
        assertThat(result).isEmpty();
    }

    /**
     * 单元素数组已天然有序，应原样返回。
     */
    @Test
    @DisplayName("给定单元素数组，排序应原样返回该数组")
    void givenSingleElementArray_whenSort_thenReturnSameArray() {
        // given
        Integer[] array = {42};

        // when
        Integer[] result = QuickSort.sort(array);

        // then
        assertThat(result).containsExactly(42);
    }

    /**
     * 已升序数组经过排序后应保持不变（稳定性边界）。
     */
    @Test
    @DisplayName("给定已升序数组，排序后应保持不变")
    void givenAlreadySortedArray_whenSort_thenRemainsUnchanged() {
        // given
        Integer[] array = {1, 2, 3, 4, 5};

        // when
        Integer[] result = QuickSort.sort(array);

        // then
        assertThat(result).containsExactly(1, 2, 3, 4, 5);
    }

    /**
     * 逆序数组是最易触发最坏时间复杂度的场景，验证可正确翻转。
     */
    @Test
    @DisplayName("给定逆序数组，排序后应得到升序结果")
    void givenDescendingArray_whenSort_thenResultIsAscending() {
        // given
        Integer[] array = {5, 4, 3, 2, 1};

        // when
        Integer[] result = QuickSort.sort(array);

        // then
        assertThat(result).containsExactly(1, 2, 3, 4, 5);
    }

    /**
     * 包含重复元素的数组，验证重复元素在排序后相对位置合理、数量保持一致。
     */
    @Test
    @DisplayName("给定含重复元素的数组，排序后重复元素数量与升序结果均正确")
    void givenArrayWithDuplicates_whenSort_thenDuplicatesPreservedAndOrdered() {
        // given
        Integer[] array = {3, 1, 2, 3, 1, 2};

        // when
        Integer[] result = QuickSort.sort(array);

        // then
        assertThat(result).containsExactly(1, 1, 2, 2, 3, 3);
    }

    /**
     * 排序应为原地操作，返回的引用与入参应为同一对象。
     */
    @Test
    @DisplayName("排序后返回的数组引用应与入参为同一对象")
    void givenArray_whenSort_thenReturnedReferenceIsSameInstance() {
        // given
        Integer[] array = {3, 1, 2};

        // when
        Integer[] result = QuickSort.sort(array);

        // then
        assertThat(result).isSameAs(array);
    }

    /**
     * 快速排序为泛型实现，验证对字符串类型的支持。
     */
    @Test
    @DisplayName("给定字符串数组，排序后应按字典序升序排列")
    void givenStringArray_whenSort_thenResultIsLexicographicallyAscending() {
        // given
        String[] array = {"banana", "apple", "cherry"};

        // when
        String[] result = QuickSort.sort(array);

        // then
        assertThat(result).containsExactly("apple", "banana", "cherry");
    }
}
