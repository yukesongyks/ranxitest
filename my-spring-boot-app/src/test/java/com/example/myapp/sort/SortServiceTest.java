package com.example.myapp.sort;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 冒泡排序服务单元测试。
 *
 * @author AiWork
 * @date 2026-09-15
 */
class SortServiceTest {

    private final SortService sortService = new SortServiceImpl();

    // ===== 升序（ASC）测试 =====

    @Test
    void should_sortAsc_when_normalCase() {
        // Arrange
        List<Integer> input = Arrays.asList(5, 2, 9, 1, 5, 6);
        List<Integer> expected = Arrays.asList(1, 2, 5, 5, 6, 9);

        // Act
        List<Integer> result = sortService.bubbleSort(input, SortOrder.ASC);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void should_returnSorted_when_alreadySortedAsc() {
        // Arrange
        List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);

        // Act
        List<Integer> result = sortService.bubbleSort(input, SortOrder.ASC);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void should_sortAsc_when_reverseOrder() {
        // Arrange
        List<Integer> input = Arrays.asList(5, 4, 3, 2, 1);
        List<Integer> expected = Arrays.asList(1, 2, 3, 4, 5);

        // Act
        List<Integer> result = sortService.bubbleSort(input, SortOrder.ASC);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void should_returnSingleElement_when_singleElementAsc() {
        // Arrange
        List<Integer> input = Collections.singletonList(42);
        List<Integer> expected = Collections.singletonList(42);

        // Act
        List<Integer> result = sortService.bubbleSort(input, SortOrder.ASC);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void should_keepOrder_when_allEqualAsc() {
        // Arrange
        List<Integer> input = Arrays.asList(3, 3, 3, 3);
        List<Integer> expected = Arrays.asList(3, 3, 3, 3);

        // Act
        List<Integer> result = sortService.bubbleSort(input, SortOrder.ASC);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void should_sortAsc_when_withNegatives() {
        // Arrange
        List<Integer> input = Arrays.asList(-3, 5, -1, 0, 2);
        List<Integer> expected = Arrays.asList(-3, -1, 0, 2, 5);

        // Act
        List<Integer> result = sortService.bubbleSort(input, SortOrder.ASC);

        // Assert
        assertEquals(expected, result);
    }

    // ===== 降序（DESC）测试 =====

    @Test
    void should_sortDesc_when_normalCase() {
        // Arrange
        List<Integer> input = Arrays.asList(5, 2, 9, 1, 5, 6);
        List<Integer> expected = Arrays.asList(9, 6, 5, 5, 2, 1);

        // Act
        List<Integer> result = sortService.bubbleSort(input, SortOrder.DESC);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void should_returnSorted_when_alreadySortedDesc() {
        // Arrange
        List<Integer> input = Arrays.asList(5, 4, 3, 2, 1);
        List<Integer> expected = Arrays.asList(5, 4, 3, 2, 1);

        // Act
        List<Integer> result = sortService.bubbleSort(input, SortOrder.DESC);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void should_returnSingleElement_when_singleElementDesc() {
        // Arrange
        List<Integer> input = Collections.singletonList(7);
        List<Integer> expected = Collections.singletonList(7);

        // Act
        List<Integer> result = sortService.bubbleSort(input, SortOrder.DESC);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void should_keepOrder_when_allEqualDesc() {
        // Arrange
        List<Integer> input = Arrays.asList(8, 8, 8);
        List<Integer> expected = Arrays.asList(8, 8, 8);

        // Act
        List<Integer> result = sortService.bubbleSort(input, SortOrder.DESC);

        // Assert
        assertEquals(expected, result);
    }

    // ===== 边界与安全性测试 =====

    @Test
    void should_returnEmpty_when_emptyList() {
        // Arrange
        List<Integer> input = Collections.emptyList();

        // Act
        List<Integer> result = sortService.bubbleSort(input, SortOrder.ASC);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void should_sortAsc_when_twoElements() {
        // Arrange
        List<Integer> input = Arrays.asList(2, 1);
        List<Integer> expected = Arrays.asList(1, 2);

        // Act
        List<Integer> result = sortService.bubbleSort(input, SortOrder.ASC);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void should_notModifyInput_when_sorting() {
        // Arrange
        List<Integer> input = Arrays.asList(5, 3, 1, 4, 2);
        List<Integer> expected = Arrays.asList(5, 3, 1, 4, 2);

        // Act
        sortService.bubbleSort(input, SortOrder.ASC);

        // Assert
        assertEquals(expected, input);
    }

    @Test
    void should_keepRelativeOrder_when_equalElements() {
        // Arrange
        List<Integer> input = Arrays.asList(5, 5, 5, 5);
        List<Integer> expected = Arrays.asList(5, 5, 5, 5);

        // Act
        List<Integer> result = sortService.bubbleSort(input, SortOrder.ASC);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void should_sortCorrectly_when_maxSize1000() {
        // Arrange
        List<Integer> input = new java.util.ArrayList<>();
        for (int i = 1000; i >= 1; i--) {
            input.add(i);
        }

        // Act
        List<Integer> result = sortService.bubbleSort(input, SortOrder.ASC);

        // Assert
        assertEquals(1000, result.size());
        assertEquals(1, result.get(0));
        assertEquals(1000, result.get(999));
    }

    // ===== null 入参防御性测试 =====

    @Test
    void should_throwSortException_when_inputIsNull() {
        // Arrange
        List<Integer> input = null;

        // Act & Assert
        SortException ex = assertThrows(SortException.class,
                () -> sortService.bubbleSort(input, SortOrder.ASC));
        assertEquals(SortConstants.CODE_SORT_EMPTY, ex.getCode());
    }

    @Test
    void should_throwSortException_when_containsNullElement() {
        // Arrange
        List<Integer> input = Arrays.asList(1, null, 3);

        // Act & Assert
        SortException ex = assertThrows(SortException.class,
                () -> sortService.bubbleSort(input, SortOrder.ASC));
        assertEquals(SortConstants.CODE_SORT_NULL_ELEMENT, ex.getCode());
    }
}
