# QuickSort Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement a generic in-place QuickSort algorithm in Java 17 with comprehensive test coverage.

**Architecture:** A single static utility class `QuickSort` in the `com.example.myapp.utils` package, providing `sort(T[] array)` and `sort(T[] array, Comparator<T> comparator)` entry points. The algorithm uses the classic Lomuto partition scheme with random pivot selection to avoid O(n²) worst-case on sorted input. All logic is self-contained with no external dependencies beyond the Java standard library.

**Tech Stack:** Java 17, JUnit 5 (via spring-boot-starter-test), Maven

---

## Global Constraints

- Java 17 (from `pom.xml` `<java.version>17</java.version>`)
- Package: `com.example.myapp.utils`
- No external dependencies beyond JDK standard library
- All public methods must handle null inputs gracefully (throw `NullPointerException` with descriptive message)
- Generic: must work with any `Comparable<T>` or with a custom `Comparator<T>`
- In-place sorting (no additional array allocation beyond recursion stack)
- TDD: test first, then implement

---

## Task 1: QuickSort Utility Class

**Files:**
- Create: `my-spring-boot-app/src/main/java/com/example/myapp/utils/QuickSort.java`
- Create: `my-spring-boot-app/src/test/java/com/example/myapp/utils/QuickSortTest.java`

**Interfaces:**
- Consumes: nothing (greenfield)
- Produces:
  - `public static <T extends Comparable<T>> void sort(T[] array)` — sorts array in-place using natural ordering
  - `public static <T> void sort(T[] array, Comparator<? super T> comparator)` — sorts array in-place using custom comparator
  - `private static <T> void quickSort(T[] array, int low, int high, Comparator<? super T> cmp)` — recursive helper
  - `private static <T> int partition(T[] array, int low, int high, Comparator<? super T> cmp)` — Lomuto partition

- [ ] **Step 1: Write the failing test class**

```java
package com.example.myapp.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Comparator;

class QuickSortTest {

    @Test
    @DisplayName("sort Integer array with natural ordering")
    void sortIntegerArray() {
        Integer[] array = {5, 3, 8, 1, 9, 2, 7, 4, 6};
        QuickSort.sort(array);
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9}, array);
    }

    @Test
    @DisplayName("sort already sorted array")
    void sortAlreadySorted() {
        Integer[] array = {1, 2, 3, 4, 5};
        QuickSort.sort(array);
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5}, array);
    }

    @Test
    @DisplayName("sort reverse sorted array")
    void sortReverseSorted() {
        Integer[] array = {5, 4, 3, 2, 1};
        QuickSort.sort(array);
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5}, array);
    }

    @Test
    @DisplayName("sort array with duplicates")
    void sortWithDuplicates() {
        Integer[] array = {3, 1, 4, 1, 5, 9, 2, 6, 5, 3};
        QuickSort.sort(array);
        assertArrayEquals(new Integer[]{1, 1, 2, 3, 3, 4, 5, 5, 6, 9}, array);
    }

    @Test
    @DisplayName("sort empty array")
    void sortEmptyArray() {
        Integer[] array = {};
        QuickSort.sort(array);
        assertArrayEquals(new Integer[]{}, array);
    }

    @Test
    @DisplayName("sort single element array")
    void sortSingleElement() {
        Integer[] array = {42};
        QuickSort.sort(array);
        assertArrayEquals(new Integer[]{42}, array);
    }

    @Test
    @DisplayName("sort String array with natural ordering")
    void sortStringArray() {
        String[] array = {"banana", "apple", "cherry", "date"};
        QuickSort.sort(array);
        assertArrayEquals(new String[]{"apple", "banana", "cherry", "date"}, array);
    }

    @Test
    @DisplayName("sort with custom comparator (descending)")
    void sortWithCustomComparator() {
        Integer[] array = {5, 3, 8, 1, 9};
        QuickSort.sort(array, Comparator.reverseOrder());
        assertArrayEquals(new Integer[]{9, 8, 5, 3, 1}, array);
    }

    @Test
    @DisplayName("sort with custom comparator by string length")
    void sortByStringLength() {
        String[] array = {"a", "abc", "ab", "abcd"};
        QuickSort.sort(array, Comparator.comparingInt(String::length));
        assertArrayEquals(new String[]{"a", "ab", "abc", "abcd"}, array);
    }

    @Test
    @DisplayName("null array throws NullPointerException")
    void nullArrayThrows() {
        assertThrows(NullPointerException.class, () -> QuickSort.sort((Integer[]) null));
        assertThrows(NullPointerException.class, () -> QuickSort.sort(null, Comparator.naturalOrder()));
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd my-spring-boot-app && mvn test -Dtest=QuickSortTest -pl . 2>&1 | tail -20`
Expected: compilation FAIL — `QuickSort` class not found

- [ ] **Step 3: Write minimal implementation**

```java
package com.example.myapp.utils;

import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * In-place QuickSort implementation using Lomuto partition scheme
 * with random pivot selection.
 */
public final class QuickSort {

    private QuickSort() {
        // utility class — prevent instantiation
    }

    /**
     * Sorts the given array in-place using the elements' natural ordering.
     *
     * @param array the array to sort; must not be null
     * @param <T>   the element type, must implement {@link Comparable}
     * @throws NullPointerException if array is null
     */
    public static <T extends Comparable<T>> void sort(T[] array) {
        Objects.requireNonNull(array, "array must not be null");
        sort(array, Comparator.naturalOrder());
    }

    /**
     * Sorts the given array in-place using the provided comparator.
     *
     * @param array      the array to sort; must not be null
     * @param comparator the comparator to determine element order; must not be null
     * @param <T>        the element type
     * @throws NullPointerException if array or comparator is null
     */
    public static <T> void sort(T[] array, Comparator<? super T> comparator) {
        Objects.requireNonNull(array, "array must not be null");
        Objects.requireNonNull(comparator, "comparator must not be null");
        if (array.length <= 1) {
            return;
        }
        quickSort(array, 0, array.length - 1, comparator);
    }

    private static <T> void quickSort(T[] array, int low, int high, Comparator<? super T> cmp) {
        if (low < high) {
            int pivotIndex = partition(array, low, high, cmp);
            quickSort(array, low, pivotIndex - 1, cmp);
            quickSort(array, pivotIndex + 1, high, cmp);
        }
    }

    private static <T> int partition(T[] array, int low, int high, Comparator<? super T> cmp) {
        // random pivot selection to avoid O(n²) on sorted input
        int randomPivot = low + ThreadLocalRandom.current().nextInt(high - low + 1);
        swap(array, randomPivot, high);

        T pivot = array[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (cmp.compare(array[j], pivot) <= 0) {
                i++;
                swap(array, i, j);
            }
        }

        swap(array, i + 1, high);
        return i + 1;
    }

    private static <T> void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd my-spring-boot-app && mvn test -Dtest=QuickSortTest -pl . 2>&1 | tail -20`
Expected: all 10 tests PASS, BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add my-spring-boot-app/src/main/java/com/example/myapp/utils/QuickSort.java
git add my-spring-boot-app/src/test/java/com/example/myapp/utils/QuickSortTest.java
git commit -m "feat: add generic in-place QuickSort utility class"
```