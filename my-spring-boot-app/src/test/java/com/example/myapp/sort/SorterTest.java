package com.example.myapp.sort;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 排序算法单元测试。
 *
 * <p>覆盖快速排序与归并排序的正常路径、边界值、异常场景。</p>
 */
class SorterTest {

    // ==================== QuickSort 测试 ====================

    @Test
    void should_sortCorrectly_when_quickSortWithMixedIntegers() {
        // Arrange
        Sorter<Integer> sorter = new QuickSort<>();
        Integer[] array = {5, 2, 8, 1, 9, 3};

        // Act
        sorter.sort(array);

        // Assert
        assertThat(array).containsExactly(1, 2, 3, 5, 8, 9);
    }

    @Test
    void should_sortCorrectly_when_quickSortWithAlreadySortedArray() {
        // Arrange
        Sorter<Integer> sorter = new QuickSort<>();
        Integer[] array = {1, 2, 3, 4, 5};

        // Act
        sorter.sort(array);

        // Assert
        assertThat(array).containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    void should_sortCorrectly_when_quickSortWithReverseSortedArray() {
        // Arrange
        Sorter<Integer> sorter = new QuickSort<>();
        Integer[] array = {5, 4, 3, 2, 1};

        // Act
        sorter.sort(array);

        // Assert
        assertThat(array).containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    void should_sortCorrectly_when_quickSortWithDuplicates() {
        // Arrange
        Sorter<Integer> sorter = new QuickSort<>();
        Integer[] array = {3, 1, 2, 1, 3, 2};

        // Act
        sorter.sort(array);

        // Assert
        assertThat(array).containsExactly(1, 1, 2, 2, 3, 3);
    }

    @Test
    void should_notChange_when_quickSortWithSingleElement() {
        // Arrange
        Sorter<Integer> sorter = new QuickSort<>();
        Integer[] array = {42};

        // Act
        sorter.sort(array);

        // Assert
        assertThat(array).containsExactly(42);
    }

    @Test
    void should_notChange_when_quickSortWithEmptyArray() {
        // Arrange
        Sorter<Integer> sorter = new QuickSort<>();
        Integer[] array = {};

        // Act
        sorter.sort(array);

        // Assert
        assertThat(array).isEmpty();
    }

    @Test
    void should_throwException_when_quickSortWithNullArray() {
        // Arrange
        Sorter<Integer> sorter = new QuickSort<>();

        // Act & Assert
        assertThatThrownBy(() -> sorter.sort(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null");
    }

    @Test
    void should_sortCorrectly_when_quickSortWithStrings() {
        // Arrange
        Sorter<String> sorter = new QuickSort<>();
        String[] array = {"banana", "apple", "cherry", "date"};

        // Act
        sorter.sort(array);

        // Assert
        assertThat(array).containsExactly("apple", "banana", "cherry", "date");
    }

    // ==================== MergeSort 测试 ====================

    @Test
    void should_sortCorrectly_when_mergeSortWithMixedIntegers() {
        // Arrange
        Sorter<Integer> sorter = new MergeSort<>();
        Integer[] array = {5, 2, 8, 1, 9, 3};

        // Act
        sorter.sort(array);

        // Assert
        assertThat(array).containsExactly(1, 2, 3, 5, 8, 9);
    }

    @Test
    void should_sortCorrectly_when_mergeSortWithAlreadySortedArray() {
        // Arrange
        Sorter<Integer> sorter = new MergeSort<>();
        Integer[] array = {1, 2, 3, 4, 5};

        // Act
        sorter.sort(array);

        // Assert
        assertThat(array).containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    void should_sortCorrectly_when_mergeSortWithReverseSortedArray() {
        // Arrange
        Sorter<Integer> sorter = new MergeSort<>();
        Integer[] array = {5, 4, 3, 2, 1};

        // Act
        sorter.sort(array);

        // Assert
        assertThat(array).containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    void should_sortCorrectly_when_mergeSortWithDuplicates() {
        // Arrange
        Sorter<Integer> sorter = new MergeSort<>();
        Integer[] array = {3, 1, 2, 1, 3, 2};

        // Act
        sorter.sort(array);

        // Assert
        assertThat(array).containsExactly(1, 1, 2, 2, 3, 3);
    }

    @Test
    void should_notChange_when_mergeSortWithSingleElement() {
        // Arrange
        Sorter<Integer> sorter = new MergeSort<>();
        Integer[] array = {42};

        // Act
        sorter.sort(array);

        // Assert
        assertThat(array).containsExactly(42);
    }

    @Test
    void should_notChange_when_mergeSortWithEmptyArray() {
        // Arrange
        Sorter<Integer> sorter = new MergeSort<>();
        Integer[] array = {};

        // Act
        sorter.sort(array);

        // Assert
        assertThat(array).isEmpty();
    }

    @Test
    void should_throwException_when_mergeSortWithNullArray() {
        // Arrange
        Sorter<Integer> sorter = new MergeSort<>();

        // Act & Assert
        assertThatThrownBy(() -> sorter.sort(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null");
    }

    @Test
    void should_sortCorrectly_when_mergeSortWithStrings() {
        // Arrange
        Sorter<String> sorter = new MergeSort<>();
        String[] array = {"banana", "apple", "cherry", "date"};

        // Act
        sorter.sort(array);

        // Assert
        assertThat(array).containsExactly("apple", "banana", "cherry", "date");
    }

    // ==================== SortUtils 测试 ====================

    @Test
    void should_swapElements_when_sortUtilsSwapCalled() {
        // Arrange
        Integer[] array = {1, 2, 3};

        // Act
        SortUtils.swap(array, 0, 2);

        // Assert
        assertThat(array).containsExactly(3, 2, 1);
    }
}
