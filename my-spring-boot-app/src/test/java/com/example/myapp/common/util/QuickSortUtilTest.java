package com.example.myapp.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * {@link QuickSortUtil} 单元测试。
 *
 * <p>覆盖空数组、单元素、已序、逆序、含重复元素、乱序、{@code null} 等边界与常规场景，
 * 以及泛型 {@link List} 排序路径。</p>
 *
 * @author dtcoder
 * @date 2026/07/31
 */
class QuickSortUtilTest {

    @Test
    @DisplayName("null数组应直接返回不抛异常")
    void sortNullArrayShouldDoNothing() {
        assertDoesNotThrow(() -> QuickSortUtil.sort((int[]) null));
    }

    @Test
    @DisplayName("空数组应直接返回不抛异常")
    void sortEmptyArrayShouldDoNothing() {
        int[] array = new int[0];
        assertDoesNotThrow(() -> QuickSortUtil.sort(array));
        assertArrayEquals(new int[0], array);
    }

    @Test
    @DisplayName("单元素数组排序后保持不变")
    void sortSingleElementArrayShouldRemainUnchanged() {
        int[] array = {42};
        QuickSortUtil.sort(array);
        assertArrayEquals(new int[]{42}, array);
    }

    @Test
    @DisplayName("已序数组排序后保持有序")
    void sortAlreadySortedArrayShouldRemainSorted() {
        int[] array = {1, 2, 3, 4, 5};
        QuickSortUtil.sort(array);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, array);
    }

    @Test
    @DisplayName("逆序数组排序后应变为升序")
    void sortReverseOrderArrayShouldBecomeAscending() {
        int[] array = {5, 4, 3, 2, 1};
        QuickSortUtil.sort(array);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, array);
    }

    @Test
    @DisplayName("含重复元素数组排序后应稳定有序")
    void sortArrayWithDuplicatesShouldBecomeOrdered() {
        int[] array = {3, 1, 2, 3, 1, 2};
        QuickSortUtil.sort(array);
        assertArrayEquals(new int[]{1, 1, 2, 2, 3, 3}, array);
    }

    @Test
    @DisplayName("乱序数组排序后应为升序")
    void sortUnorderedArrayShouldBecomeAscending() {
        int[] array = {9, 3, 7, 1, 8, 2, 6, 5, 4};
        QuickSortUtil.sort(array);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, array);
    }

    @Test
    @DisplayName("含负数数组排序后应为升序")
    void sortArrayWithNegativesShouldBecomeAscending() {
        int[] array = {-3, 7, -1, 0, 5, -9};
        QuickSortUtil.sort(array);
        assertArrayEquals(new int[]{-9, -3, -1, 0, 5, 7}, array);
    }

    @Test
    @DisplayName("null列表应直接返回不抛异常")
    void sortNullListShouldDoNothing() {
        assertDoesNotThrow(() -> QuickSortUtil.sort((List<Integer>) null));
    }

    @Test
    @DisplayName("空列表应直接返回不抛异常")
    void sortEmptyListShouldDoNothing() {
        List<Integer> list = Collections.emptyList();
        assertDoesNotThrow(() -> QuickSortUtil.sort(list));
        assertEquals(Collections.emptyList(), list);
    }

    @Test
    @DisplayName("单元素列表排序后保持不变")
    void sortSingleElementListShouldRemainUnchanged() {
        List<Integer> list = Collections.singletonList(42);
        QuickSortUtil.sort(list);
        assertEquals(Collections.singletonList(42), list);
    }

    @Test
    @DisplayName("乱序列表排序后应为升序")
    void sortUnorderedListShouldBecomeAscending() {
        List<Integer> list = Arrays.asList(9, 3, 7, 1, 8, 2, 6, 5, 4);
        QuickSortUtil.sort(list);
        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9), list);
    }

    @Test
    @DisplayName("含重复元素列表排序后应为升序")
    void sortListWithDuplicatesShouldBecomeOrdered() {
        List<Integer> list = Arrays.asList(3, 1, 2, 3, 1, 2);
        QuickSortUtil.sort(list);
        assertEquals(Arrays.asList(1, 1, 2, 2, 3, 3), list);
    }

    @Test
    @DisplayName("字符串列表排序后应按自然顺序升序")
    void sortStringListShouldBecomeNaturalOrder() {
        List<String> list = Arrays.asList("banana", "apple", "cherry", "date");
        QuickSortUtil.sort(list);
        assertEquals(Arrays.asList("apple", "banana", "cherry", "date"), list);
    }
}
