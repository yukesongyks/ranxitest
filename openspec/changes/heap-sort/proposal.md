# Heap Sort Implementation

## Summary
Implement a generic heap sort algorithm in Java, supporting both ascending and descending order.

## Motivation
Heap sort is a comparison-based sorting algorithm with O(n log n) time complexity and O(1) auxiliary space. It is a fundamental algorithm needed in the project's algorithm library.

## Scope
- New package: `com.example.myapp.sorting`
- New class: `HeapSort.java` with a generic static sort method
- New test class: `HeapSortTest.java`

## Design Decisions
- Generic implementation using `Comparable<T>` for type safety
- Static method API for simple usage
- In-place sorting for O(1) space complexity
- Support both ascending and descending order via a boolean flag