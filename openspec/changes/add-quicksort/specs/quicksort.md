# Spec: QuickSort

## API
```java
package com.example.myapp.utils;

public class QuickSort {
    /**
     * Sort array in ascending order (in-place).
     * @param arr the array to sort; must not be null
     * @param <T> any Comparable type
     */
    public static <T extends Comparable<T>> void sort(T[] arr);

    /**
     * Sort array in specified order (in-place).
     * @param arr the array to sort; must not be null
     * @param ascending true for ascending, false for descending
     * @param <T> any Comparable type
     */
    public static <T extends Comparable<T>> void sort(T[] arr, boolean ascending);
}
```

## Behavior
- In-place sorting using Hoare partition scheme
- Stable for equal elements (preserves relative order)
- Time complexity: O(n log n) average, O(n²) worst case
- Space complexity: O(log n) for recursion stack
- Null input throws `IllegalArgumentException`
- Empty or single-element arrays are no-ops

## Edge Cases
- Empty array: returns immediately
- Single element: returns immediately
- Already sorted array: handles correctly
- Array with duplicates: stable partition preserves order
- Array with all equal elements: handles correctly