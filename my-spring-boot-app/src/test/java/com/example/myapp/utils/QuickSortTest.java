package com.example.myapp.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link QuickSort} 单元测试。
 *
 * <p>遵循 AAA 模式（Arrange-Act-Assert），覆盖正常路径、边界值与异常场景。
 *
 * @author DTCoder
 */
class QuickSortTest {

    @Test
    @DisplayName("给定无序数组，排序后应为升序")
    void should_sortArray_when_unsortedArray() {
        // Arrange
        int[] array = {5, 3, 8, 1, 9, 2, 7};
        // Act
        QuickSort.sort(array);
        // Assert
        assertThat(array).containsExactly(1, 2, 3, 5, 7, 8, 9);
    }

    @Test
    @DisplayName("给定已升序数组，排序后应保持不变")
    void should_keepArray_when_alreadySorted() {
        // Arrange
        int[] array = {1, 2, 3, 4, 5};
        // Act
        QuickSort.sort(array);
        // Assert
        assertThat(array).containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    @DisplayName("给定逆序数组，排序后应为升序")
    void should_sortArray_when_reverseSorted() {
        // Arrange
        int[] array = {5, 4, 3, 2, 1};
        // Act
        QuickSort.sort(array);
        // Assert
        assertThat(array).containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    @DisplayName("给定含重复元素数组，排序后应保持稳定升序")
    void should_sortArray_when_hasDuplicates() {
        // Arrange
        int[] array = {3, 1, 2, 3, 1, 2};
        // Act
        QuickSort.sort(array);
        // Assert
        assertThat(array).containsExactly(1, 1, 2, 2, 3, 3);
    }

    @Test
    @DisplayName("给定全部相同元素数组，排序后应保持不变")
    void should_keepArray_when_allSameElements() {
        // Arrange
        int[] array = {4, 4, 4, 4};
        // Act
        QuickSort.sort(array);
        // Assert
        assertThat(array).containsExactly(4, 4, 4, 4);
    }

    @Test
    @DisplayName("给定单元素数组，排序后应保持不变")
    void should_keepArray_when_singleElement() {
        // Arrange
        int[] array = {42};
        // Act
        QuickSort.sort(array);
        // Assert
        assertThat(array).containsExactly(42);
    }

    @Test
    @DisplayName("给定空数组，不应抛出异常")
    void should_notThrow_when_emptyArray() {
        // Arrange
        int[] array = {};
        // Act
        QuickSort.sort(array);
        // Assert
        assertThat(array).isEmpty();
    }

    @Test
    @DisplayName("给定 null 数组，应抛出 NullPointerException")
    void should_throwNpe_when_nullArray() {
        // Arrange
        int[] array = null;
        // Act & Assert
        assertThatThrownBy(() -> QuickSort.sort(array))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("array");
    }
}
