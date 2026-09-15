package com.example.myapp.sort;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SortServiceTest {

    private final SortService sortService = new SortService();

    // ===== 升序（ASC）测试 =====

    @Test
    void bubbleSort_asc_normalCase() {
        List<Integer> result = sortService.bubbleSort(Arrays.asList(5, 2, 9, 1, 5, 6), SortOrder.ASC);
        assertEquals(Arrays.asList(1, 2, 5, 5, 6, 9), result);
    }

    @Test
    void bubbleSort_asc_alreadySorted() {
        List<Integer> result = sortService.bubbleSort(Arrays.asList(1, 2, 3, 4, 5), SortOrder.ASC);
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), result);
    }

    @Test
    void bubbleSort_asc_reverseOrder() {
        List<Integer> result = sortService.bubbleSort(Arrays.asList(5, 4, 3, 2, 1), SortOrder.ASC);
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), result);
    }

    @Test
    void bubbleSort_asc_singleElement() {
        List<Integer> result = sortService.bubbleSort(Collections.singletonList(42), SortOrder.ASC);
        assertEquals(Collections.singletonList(42), result);
    }

    @Test
    void bubbleSort_asc_allEqual() {
        List<Integer> result = sortService.bubbleSort(Arrays.asList(3, 3, 3, 3), SortOrder.ASC);
        assertEquals(Arrays.asList(3, 3, 3, 3), result);
    }

    @Test
    void bubbleSort_asc_withNegatives() {
        List<Integer> result = sortService.bubbleSort(Arrays.asList(-3, 5, -1, 0, 2), SortOrder.ASC);
        assertEquals(Arrays.asList(-3, -1, 0, 2, 5), result);
    }

    // ===== 降序（DESC）测试 =====

    @Test
    void bubbleSort_desc_normalCase() {
        List<Integer> result = sortService.bubbleSort(Arrays.asList(5, 2, 9, 1, 5, 6), SortOrder.DESC);
        assertEquals(Arrays.asList(9, 6, 5, 5, 2, 1), result);
    }

    @Test
    void bubbleSort_desc_alreadySorted() {
        List<Integer> result = sortService.bubbleSort(Arrays.asList(5, 4, 3, 2, 1), SortOrder.DESC);
        assertEquals(Arrays.asList(5, 4, 3, 2, 1), result);
    }

    @Test
    void bubbleSort_desc_singleElement() {
        List<Integer> result = sortService.bubbleSort(Collections.singletonList(7), SortOrder.DESC);
        assertEquals(Collections.singletonList(7), result);
    }

    @Test
    void bubbleSort_desc_allEqual() {
        List<Integer> result = sortService.bubbleSort(Arrays.asList(8, 8, 8), SortOrder.DESC);
        assertEquals(Arrays.asList(8, 8, 8), result);
    }

    // ===== 边界与安全性测试 =====

    @Test
    void bubbleSort_emptyList() {
        List<Integer> result = sortService.bubbleSort(Collections.emptyList(), SortOrder.ASC);
        assertTrue(result.isEmpty());
    }

    @Test
    void bubbleSort_twoElements_asc() {
        List<Integer> result = sortService.bubbleSort(Arrays.asList(2, 1), SortOrder.ASC);
        assertEquals(Arrays.asList(1, 2), result);
    }

    @Test
    void bubbleSort_doesNotModifyInput() {
        List<Integer> input = Arrays.asList(5, 3, 1, 4, 2);
        sortService.bubbleSort(input, SortOrder.ASC);
        // 原始列表不应被修改
        assertEquals(Arrays.asList(5, 3, 1, 4, 2), input);
    }

    @Test
    void bubbleSort_stability_equalElementsKeepRelativeOrder() {
        // 冒泡排序是稳定排序，相等元素相对位置不变
        List<Integer> result = sortService.bubbleSort(Arrays.asList(5, 5, 5, 5), SortOrder.ASC);
        assertEquals(Arrays.asList(5, 5, 5, 5), result);
    }

    @Test
    void bubbleSort_maxSize_1000() {
        // 构造 1000 个逆序元素，验证上限规模可正常排序
        java.util.List<Integer> input = new java.util.ArrayList<>();
        for (int i = 1000; i >= 1; i--) {
            input.add(i);
        }
        List<Integer> result = sortService.bubbleSort(input, SortOrder.ASC);
        assertEquals(1000, result.size());
        assertEquals(1, result.get(0));
        assertEquals(1000, result.get(999));
    }

    // ===== null 元素防御性测试 =====

    @Test
    void bubbleSort_nullElement_throwsIllegalArgumentException() {
        // numbers 中包含 null 元素时，Service 应抛出 IllegalArgumentException 而非 NPE
        List<Integer> input = Arrays.asList(1, null, 3);
        assertThrows(IllegalArgumentException.class,
                () -> sortService.bubbleSort(input, SortOrder.ASC));
    }
}
