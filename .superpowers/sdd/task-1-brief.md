# Task 1: Implement Quick Sort Algorithm

## Requirement

Implement a quick sort algorithm as a utility class in the existing Java Spring Boot project.

## Details

Create a `SortUtil` class under `com.example.myapp.util` package with a **quick sort** implementation.

### Method Signature

- `public static <T extends Comparable<T>> void quickSort(T[] array)` — sorts the given array in-place using the quick sort algorithm.

### Algorithm Requirements

- Use the **Lomuto partition scheme** (pivot as the last element)
- Handle edge cases gracefully: null array, empty array, single-element array
- The algorithm should sort in **ascending order** (natural ordering via Comparable)
- Must be a stable variant (note: quick sort is not naturally stable; no stability requirement)

### Package

`com.example.myapp.util`

### File Location

- Source: `my-spring-boot-app/src/main/java/com/example/myapp/util/SortUtil.java`
- Test: `my-spring-boot-app/src/test/java/com/example/myapp/util/SortUtilTest.java`

### Test Requirements (TDD)

Write comprehensive tests covering:
1. Null array → no exception, no-op
2. Empty array → no-op
3. Single-element array → unchanged
4. Already sorted array → remains sorted
5. Reverse sorted array → correctly sorted
6. Array with duplicates → correctly sorted with duplicates preserved
7. Array with negative numbers → correctly sorted
8. Array with mixed positive/negative/zero → correctly sorted
9. Array with all identical elements → unchanged
10. Large array (1000+ elements) → correctly sorted and performs reasonably

### Acceptance Criteria

- All tests pass
- The algorithm correctly sorts Comparable objects
- In-place sorting (does not create a new array)
- Follows Java conventions for utility classes (private constructor)