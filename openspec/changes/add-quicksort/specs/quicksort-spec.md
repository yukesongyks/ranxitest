# Spec: QuickSort Algorithm

## API

### `QuickSort.sort(T[] array)`
- **Input**: `T[] array` where `T extends Comparable<T>`
- **Behavior**: Sorts the array in-place in ascending natural order using the quicksort algorithm
- **Output**: void (array is mutated)
- **Edge cases**:
  - `null` array → throws `IllegalArgumentException`
  - Empty array (length 0) → no-op
  - Single element → no-op
  - Array with duplicate values → sorted correctly
  - Already sorted array → remains sorted
  - Reverse sorted array → sorted correctly

### `QuickSort.sortCopy(T[] array)`
- **Input**: `T[] array` where `T extends Comparable<T>`
- **Behavior**: Returns a new sorted copy of the input array; original array is unchanged
- **Output**: `T[]` — new sorted array
- **Edge cases**: same as `sort()`, but `null` input returns empty array

## Performance
- Average time complexity: O(n log n)
- Worst-case time complexity: O(n²) with naive pivot selection (acceptable for this version)
- Space complexity: O(log n) for recursion stack