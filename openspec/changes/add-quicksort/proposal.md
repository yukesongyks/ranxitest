# Proposal: Add QuickSort Utility

## Intent
Add a generic QuickSort utility class to the project that provides an in-place, stable partitioning quicksort implementation for `Comparable` elements.

## Scope
- Add `QuickSort.java` utility in `com.example.myapp.utils` package
- Add `QuickSortTest.java` unit tests in the test source tree
- Support sorting of any `T extends Comparable<T>` array
- Provide both ascending and descending sort options

## Non-Goals
- No parallel/multi-threaded sorting
- No integration with Spring components
- No REST API exposure