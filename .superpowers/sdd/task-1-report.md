# Task 1 Report: LotteryService —— 随机数生成核心

## Status: DONE

## Files Created

| File | Path |
|------|------|
| Test | `my-spring-boot-app/src/test/java/com/example/myapp/services/LotteryServiceTest.java` |
| Implementation | `my-spring-boot-app/src/main/java/com/example/myapp/services/LotteryService.java` |

## TDD Evidence

### RED Phase — Step 2: Compilation failure

**Command:**
```
cd my-spring-boot-app && mvn test -Dtest=LotteryServiceTest
```

**Result: BUILD FAILURE — COMPILATION ERROR**
```
[ERROR] cannot find symbol
  symbol:   class LotteryService
  location: class com.example.myapp.services.LotteryServiceTest
```

Confirmed: `LotteryService` class did not exist yet, test compilation failed as expected.

### GREEN Phase — Step 4: All tests pass

**Command:**
```
cd my-spring-boot-app && mvn test -Dtest=LotteryServiceTest
```

**Result: BUILD SUCCESS**
```
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
```

All 5 test cases passed:
1. `drawReturnsFiveNumbers` — verifies exactly 5 numbers returned
2. `drawNumbersAreWithinRange` — verifies each number ∈ [1, 999]
3. `drawNumbersAreDistinct` — verifies no duplicates
4. `drawNumbersAreSortedAscending` — verifies ascending order
5. `drawIsRandomAcrossInvocations` — verifies randomness (at least 1 differing result in 10 draws)

## Implementation Summary

`LotteryService` is a `@Service` Spring bean exposing `public List<Integer> draw()`:

- Uses `ThreadLocalRandom.current().ints(MIN, MAX_INCLUSIVE + 1)` for lock-free concurrent-safe random generation
- `.distinct()` ensures uniqueness
- `.limit(COUNT)` takes exactly 5 values
- `.sorted()` produces natural ascending order
- Returns `List<Integer>` as specified for Task 2's Controller to consume

## Self-Review

- No other files modified; only the two specified files were created.
- `@Service` annotation present for Spring component scanning (Task 2's Controller will inject it).
- The interface contract (`public List<Integer> draw()`) matches the brief exactly.
- Thread safety: `ThreadLocalRandom` is used instead of shared `Random`, suitable for concurrent web requests.
- No concerns.