# Heap Sort Specification

## API
```java
public class HeapSort {
    public static <T extends Comparable<T>> void sort(T[] array);
    public static <T extends Comparable<T>> void sort(T[] array, boolean ascending);
}
```

## Behavior
- `sort(T[] array)`: sorts in ascending order (default)
- `sort(T[] array, boolean ascending)`: sorts in ascending order when `ascending=true`, descending when `ascending=false`
- Null-safe: throws `IllegalArgumentException` on null input
- Empty and single-element arrays are handled as no-ops
- In-place sort, O(1) auxiliary space, O(n log n) time

## Test Cases
- Ascending sort on unsorted Integer array
- Descending sort on unsorted Integer array
- Already sorted array
- Reverse sorted array
- Array with duplicates
- Single element array
- Empty array
- Null input (expects exception)
- String array sort