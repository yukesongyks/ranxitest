# Proposal: Add QuickSort Algorithm

## Intent
Add a generic QuickSort algorithm implementation as a utility class in the project.

## Scope
- Implement a generic QuickSort utility class supporting `Comparable<T>` types
- Provide both in-place and copy-based sorting variants
- Include unit tests covering edge cases (empty array, single element, already sorted, reverse sorted, duplicates, large random arrays)

## Out of Scope
- No performance benchmarks
- No multi-threaded parallel sort variant
- No Spring component integration (plain utility class)