package com.example.myapp.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link SortUtils}.
 */
class SortUtilsTest {

    @Test
    @DisplayName("should sort empty array")
    void should_sortEmptyArray() {
        int[] array = {};
        SortUtils.quickSort(array);
        assertThat(array).isEmpty();
    }

    @Test
    @DisplayName("should sort single element array")
    void should_sortSingleElement() {
        int[] array = {42};
        SortUtils.quickSort(array);
        assertThat(array).containsExactly(42);
    }

    @Test
    @DisplayName("should sort already sorted array")
    void should_sortAlreadySorted() {
        int[] array = {1, 2, 3, 4, 5};
        SortUtils.quickSort(array);
        assertThat(array).containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    @DisplayName("should sort reverse sorted array")
    void should_sortReverseSorted() {
        int[] array = {5, 4, 3, 2, 1};
        SortUtils.quickSort(array);
        assertThat(array).containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    @DisplayName("should sort array with duplicate values")
    void should_sortWithDuplicates() {
        int[] array = {3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5};
        SortUtils.quickSort(array);
        assertThat(array).containsExactly(1, 1, 2, 3, 3, 4, 5, 5, 5, 6, 9);
    }

    @Test
    @DisplayName("should sort array with negative numbers")
    void should_sortWithNegativeNumbers() {
        int[] array = {-3, -1, -7, 0, 2, -5};
        SortUtils.quickSort(array);
        assertThat(array).containsExactly(-7, -5, -3, -1, 0, 2);
    }

    @Test
    @DisplayName("should sort array with all same values")
    void should_sortAllSameValues() {
        int[] array = {7, 7, 7, 7, 7};
        SortUtils.quickSort(array);
        assertThat(array).containsExactly(7, 7, 7, 7, 7);
    }

    @Test
    @DisplayName("should throw NullPointerException when input is null")
    void should_throwException_whenInputIsNull() {
        assertThatThrownBy(() -> SortUtils.quickSort(null))
                .isInstanceOf(NullPointerException.class);
    }
}