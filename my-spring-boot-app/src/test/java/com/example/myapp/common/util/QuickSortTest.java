package com.example.myapp.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link QuickSort} 单元测试。
 *
 * <p>覆盖正常排序、空数组、单元素、已排序、逆序、含重复元素及空值异常等场景。</p>
 *
 * @author DTCoder
 * @date 2026-07-31
 */
class QuickSortTest {

    @Test
    void sortNormalArray() {
        int[] arr = {3, 6, 8, 10, 1, 2, 1};
        QuickSort.sort(arr);
        assertArrayEquals(new int[]{1, 1, 2, 3, 6, 8, 10}, arr);
    }

    @Test
    void sortEmptyArray() {
        int[] arr = {};
        QuickSort.sort(arr);
        assertArrayEquals(new int[]{}, arr);
    }

    @Test
    void sortSingleElement() {
        int[] arr = {42};
        QuickSort.sort(arr);
        assertArrayEquals(new int[]{42}, arr);
    }

    @Test
    void sortAlreadySorted() {
        int[] arr = {1, 2, 3, 4, 5};
        QuickSort.sort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    void sortReverseSorted() {
        int[] arr = {5, 4, 3, 2, 1};
        QuickSort.sort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);
    }

    @Test
    void sortWithDuplicates() {
        int[] arr = {4, 2, 4, 2, 4};
        QuickSort.sort(arr);
        assertArrayEquals(new int[]{2, 2, 4, 4, 4}, arr);
    }

    @Test
    void sortNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> QuickSort.sort(null));
    }
}
