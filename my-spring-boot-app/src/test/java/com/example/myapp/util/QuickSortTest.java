package com.example.myapp.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link QuickSort} 单元测试。
 *
 * <p>覆盖正常排序、边界条件、稳定性场景与异常入参，遵循 FIRST 原则与 AAA 模式。</p>
 */
@DisplayName("快速排序 QuickSort 单元测试")
class QuickSortTest {

    @Test
    @DisplayName("对无序数组进行原地升序排序")
    void shouldSortUnsortedArrayInPlace() {
        // Arrange
        int[] array = {5, 3, 8, 1, 9, 2, 7};

        // Act
        QuickSort.sort(array);

        // Assert
        assertArrayEquals(new int[]{1, 2, 3, 5, 7, 8, 9}, array);
    }

    @Test
    @DisplayName("对已升序数组排序后结果不变")
    void shouldKeepSortedArrayUnchanged() {
        // Arrange
        int[] array = {1, 2, 3, 4, 5};

        // Act
        QuickSort.sort(array);

        // Assert
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, array);
    }

    @Test
    @DisplayName("对逆序数组排序后得到升序结果")
    void shouldSortDescendingArrayToAscending() {
        // Arrange
        int[] array = {9, 7, 5, 3, 1};

        // Act
        QuickSort.sort(array);

        // Assert
        assertArrayEquals(new int[]{1, 3, 5, 7, 9}, array);
    }

    @Test
    @DisplayName("对含重复元素的数组排序后保持稳定顺序")
    void shouldSortArrayWithDuplicates() {
        // Arrange
        int[] array = {4, 2, 4, 1, 2, 4};

        // Act
        QuickSort.sort(array);

        // Assert
        assertArrayEquals(new int[]{1, 2, 2, 4, 4, 4}, array);
    }

    @Test
    @DisplayName("对单元素数组排序后结果不变")
    void shouldHandleSingleElementArray() {
        // Arrange
        int[] array = {42};

        // Act
        QuickSort.sort(array);

        // Assert
        assertArrayEquals(new int[]{42}, array);
    }

    @Test
    @DisplayName("对空数组排序后仍为空数组")
    void shouldHandleEmptyArray() {
        // Arrange
        int[] array = {};

        // Act
        QuickSort.sort(array);

        // Assert
        assertArrayEquals(new int[]{}, array);
    }

    @Test
    @DisplayName("sortedCopy 返回有序副本且不修改原数组")
    void shouldReturnSortedCopyWithoutMutatingOriginal() {
        // Arrange
        int[] original = {3, 1, 2};

        // Act
        int[] result = QuickSort.sortedCopy(original);

        // Assert
        assertArrayEquals(new int[]{1, 2, 3}, result);
        assertArrayEquals(new int[]{3, 1, 2}, original);
    }

    @Test
    @DisplayName("入参为 null 时抛出 IllegalArgumentException")
    void shouldThrowWhenArrayIsNull() {
        // Arrange
        int[] array = null;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> QuickSort.sort(array)
        );
        assert exception.getMessage().contains("null") : "异常信息应包含 null 提示";
    }

    @Test
    @DisplayName("sortedCopy 入参为 null 时抛出 IllegalArgumentException")
    void shouldThrowWhenSortedCopyArrayIsNull() {
        // Arrange
        int[] array = null;

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> QuickSort.sortedCopy(array)
        );
    }
}
