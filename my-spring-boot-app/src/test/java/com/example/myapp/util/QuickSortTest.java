package com.example.myapp.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * QuickSort 单元测试类
 *
 * @author DTCoder
 * @date 2026/01/28
 */
class QuickSortTest {

    // ==================== sort(Comparable[]) 测试 ====================

    @Test
    void should_sortArray_when_unsortedIntegers() {
        // Arrange
        Integer[] input = {5, 2, 8, 1, 9, 3};

        // Act
        QuickSort.sort(input);

        // Assert
        assertThat(input).containsExactly(1, 2, 3, 5, 8, 9);
    }

    @Test
    void should_sortArray_when_unsortedStrings() {
        // Arrange
        String[] input = {"delta", "alpha", "charlie", "bravo"};

        // Act
        QuickSort.sort(input);

        // Assert
        assertThat(input).containsExactly("alpha", "bravo", "charlie", "delta");
    }

    @Test
    void should_throwException_when_arrayIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> QuickSort.sort((Integer[]) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("array");
    }

    @Test
    void should_doNothing_when_arrayIsEmpty() {
        // Arrange
        Integer[] input = {};

        // Act
        QuickSort.sort(input);

        // Assert
        assertThat(input).isEmpty();
    }

    @Test
    void should_doNothing_when_arrayHasSingleElement() {
        // Arrange
        Integer[] input = {42};

        // Act
        QuickSort.sort(input);

        // Assert
        assertThat(input).containsExactly(42);
    }

    @Test
    void should_keepOrder_when_arrayAlreadySorted() {
        // Arrange
        Integer[] input = {1, 2, 3, 4, 5};

        // Act
        QuickSort.sort(input);

        // Assert
        assertThat(input).containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    void should_sortArray_when_reverseSorted() {
        // Arrange
        Integer[] input = {9, 8, 7, 6, 5, 4, 3, 2, 1};

        // Act
        QuickSort.sort(input);

        // Assert
        assertThat(input).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9);
    }

    @Test
    void should_sortArray_when_duplicateValues() {
        // Arrange
        Integer[] input = {3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5};

        // Act
        QuickSort.sort(input);

        // Assert
        assertThat(input).containsExactly(1, 1, 2, 3, 3, 4, 5, 5, 5, 6, 9);
    }

    @Test
    void should_sortArray_when_allElementsEqual() {
        // Arrange
        Integer[] input = {7, 7, 7, 7, 7};

        // Act
        QuickSort.sort(input);

        // Assert
        assertThat(input).containsExactly(7, 7, 7, 7, 7);
    }

    @Test
    void should_sortArray_when_largeDataset() {
        // Arrange
        int size = 1000;
        Integer[] input = new Integer[size];
        for (int i = 0; i < size; i++) {
            input[i] = size - i;
        }

        // Act
        QuickSort.sort(input);

        // Assert
        for (int i = 0; i < size; i++) {
            assertThat(input[i]).isEqualTo(i + 1);
        }
    }

    // ==================== sort(int[]) 测试 ====================

    @Test
    void should_sortIntArray_when_unsorted() {
        // Arrange
        int[] input = {5, 2, 8, 1, 9, 3};

        // Act
        QuickSort.sort(input);

        // Assert
        assertThat(input).containsExactly(1, 2, 3, 5, 8, 9);
    }

    @Test
    void should_throwException_when_intArrayIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> QuickSort.sort((int[]) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("array");
    }

    @Test
    void should_doNothing_when_intArrayIsEmpty() {
        // Arrange
        int[] input = {};

        // Act
        QuickSort.sort(input);

        // Assert
        assertThat(input).isEmpty();
    }

    @Test
    void should_doNothing_when_intArrayHasSingleElement() {
        // Arrange
        int[] input = {99};

        // Act
        QuickSort.sort(input);

        // Assert
        assertThat(input).containsExactly(99);
    }

    @Test
    void should_sortIntArray_when_reverseSorted() {
        // Arrange
        int[] input = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};

        // Act
        QuickSort.sort(input);

        // Assert
        assertThat(input).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    void should_sortIntArray_when_duplicateValues() {
        // Arrange
        int[] input = {3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5};

        // Act
        QuickSort.sort(input);

        // Assert
        assertThat(input).containsExactly(1, 1, 2, 3, 3, 4, 5, 5, 5, 6, 9);
    }
}