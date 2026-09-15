# Task 3 Report: 摇号页面 —— Thymeleaf 模板与前端交互

## Summary

Replaced the minimal placeholder `my-spring-boot-app/src/main/resources/templates/lottery/index.html` with the full Thymeleaf template from the task brief.

## Files Changed

- `my-spring-boot-app/src/main/resources/templates/lottery/index.html`: full replacement (128 lines, 3668 bytes)

No other files were modified.

## Verification Evidence

### Step 2a: Server startup and page serving

Server started via `nohup mvn spring-boot:run`, curl polls returned HTTP 200.

```
$ curl -s -o /dev/null -w '%{http_code}' http://localhost:8080/lottery
200
```

### Step 2b: Page content check

```
$ curl -s http://localhost:8080/lottery | grep -c '摇 号'
2
$ curl -s http://localhost:8080/lottery | grep -c 'drawNumbers'
2
```

The page contains the `<button class="btn" id="drawBtn" onclick="drawNumbers()">摇 号</button>` button and the `drawNumbers()` JavaScript function. Thymeleaf processing is functional (the `xmlns:th` namespace is stripped as expected).

### Step 2c: API endpoint verification (3 calls)

| Call | JSON Result | Size | Min | Max | Distinct | Status |
|------|-------------|------|-----|-----|----------|--------|
| 1 | [66, 141, 548, 691, 861] | 5 | 66 | 861 | ✓ | PASS |
| 2 | [16, 49, 152, 337, 412] | 5 | 16 | 412 | ✓ | PASS |
| 3 | [25, 141, 486, 506, 582] | 5 | 25 | 582 | ✓ | PASS |

All three calls validated with python3: each array has exactly 5 elements, all values are integers in [1, 999], and all values within each array are pairwise distinct.

### Step 2d: Server cleanup

Background task stopped, only a defunct zombie (PID 16217) remained — no running java/mvn processes.

### Step 3: Full test suite

```
$ cd my-spring-boot-app && mvn test
...
Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Breakdown: 1 (contextLoads) + 5 (LotteryServiceTest) + 2 (LotteryControllerTest) = 8 tests, all passing.

## Self-Review Findings

- Template written verbatim per brief — no deviations.
- Page renders correctly through Thymeleaf/Spring Boot.
- Frontend JavaScript correctly fetches `/api/lottery/draw`, iterates results with staggered animation, handles errors gracefully, and resets button state in `finally`.
- No visual inspection performed (no browser available); page structure and behavior verified via curl and python3 assertions.
- All hard constraints respected: git read-only, only `index.html` modified, no Java/pom changes.

## Fix round 1

### Changes

**File modified:** `my-spring-boot-app/src/main/resources/templates/lottery/index.html` (133 lines, 3901 bytes)

Three reviewer-identified issues fixed:

1. **Race condition (stale setTimeout callbacks)** — Added `let drawSeq = 0;` before `drawNumbers()`, `const seq = ++drawSeq;` at function start, and `if (seq !== drawSeq) return;` guard inside each setTimeout callback. If user re-clicks during the staggered ~600ms animation, old callbacks from a superseded draw silently bail out instead of appending stale balls into the freshly cleared `#result`. Staggered animation behavior preserved.

2. **Accessibility** — Added `aria-live="polite"` to `#result` div so screen readers announce changes. Button now gets `btn.setAttribute('aria-busy', 'true')` on disable and `btn.removeAttribute('aria-busy')` on re-enable in the `finally` block.

3. **Trailing newline** — File already ended with exactly one `\n`; verified with `od -c`.

### Verification evidence

**Server:** Started `mvn spring-boot:run`, HTTP 200 at `/lottery`.

**Page content (curl):**
- `drawSeq` appears 3 times (declaration, increment, guard check) ✓
- `aria-live="polite"` appears 1 time ✓
- `aria-busy` appears 2 times (set + remove) ✓

**API endpoint:** `curl -s http://localhost:8080/api/lottery/draw` → `[201,211,242,631,972]` — valid 5-element JSON array ✓

**Full test suite:**
```
Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```
(1 contextLoads + 5 LotteryServiceTest + 2 LotteryControllerTest)

**Cleanup:** Server killed; only 2 defunct zombies remain (no running java/mvn processes).

### Hard constraints
- Git read-only ✓
- Only `index.html` modified ✓
- No Java files, pom.xml, or other files touched ✓

## Final fix round

### Changes

Three reviewer Minor findings addressed, exactly as specified:

**File 1: `my-spring-boot-app/src/main/resources/templates/lottery/index.html`**

1. **Inline onclick → addEventListener**: Removed `onclick="drawNumbers()"` from `<button class="btn" id="drawBtn">` (line 96). Added `document.getElementById('drawBtn').addEventListener('click', drawNumbers);` at the end of the `<script>` block (line 133).

2. **Extract inline style to CSS class**: Added `.error-msg { color: #dc2626; }` to the `<style>` block (line 89). Changed JS error line from `'<p style="color:#dc2626">摇号失败，请重试</p>'` to `'<p class="error-msg">摇号失败，请重试</p>'` (line 126).

All other content (drawSeq guard, aria attributes, stagger animation, error handling) preserved as-is.

**File 2: `my-spring-boot-app/src/test/java/com/example/myapp/controllers/LotteryControllerTest.java`**

3. **Add Content-Type assertion**: Added `import org.springframework.http.MediaType;` (line 13) and `.andExpect(content().contentType(MediaType.APPLICATION_JSON))` to `drawEndpointReturnsFiveNumbersAsJson` (line 36). Existing assertions unchanged.

No other files touched.

### Verification evidence

**Server startup & page content check:**

```
$ curl -s http://localhost:8080/lottery > /tmp/lottery-page.html
$ grep -c 'onclick=' /tmp/lottery-page.html
0
$ grep -c "addEventListener('click'" /tmp/lottery-page.html
1
$ grep -c 'error-msg' /tmp/lottery-page.html
2
```

- `onclick=` absent ✓
- `addEventListener('click'` present (1 occurrence) ✓
- `error-msg` present (2 occurrences: CSS class definition + JS usage) ✓

**Server cleanup:** Server stopped, only defunct zombie processes remain (no running java/mvn).

**Full Maven test suite:**

```
Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Breakdown: 1 (contextLoads) + 5 (LotteryServiceTest) + 2 (LotteryControllerTest, including the new Content-Type assertion) = 8 tests, all passing.

### Hard constraints

- Git read-only ✓
- Only the 2 files listed modified (`index.html`, `LotteryControllerTest.java`) ✓
- pom.xml, User/Item code, Task 1 files, `Collectors.toList()`, `.distinct().limit(5)` all untouched ✓
- Files end with trailing newline ✓