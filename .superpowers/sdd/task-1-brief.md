### Task 1: LotteryService —— 随机数生成核心

**Files:**
- Create: `my-spring-boot-app/src/main/java/com/example/myapp/services/LotteryService.java`
- Test: `my-spring-boot-app/src/test/java/com/example/myapp/services/LotteryServiceTest.java`

**Interfaces:**
- Consumes: 无（首个任务，不依赖其他新代码）。
- Produces: `public List<Integer> draw()` —— 返回 5 个互不重复、升序排列的整数，每个整数在 `[1, 999]`（含边界）。后续 Task 2 的 Controller 依赖此方法名与返回类型。

- [ ] **Step 1: Write the failing test**

```java
package com.example.myapp.services;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LotteryServiceTest {

    private final LotteryService lotteryService = new LotteryService();

    @Test
    void drawReturnsFiveNumbers() {
        List<Integer> result = lotteryService.draw();
        assertEquals(5, result.size());
    }

    @Test
    void drawNumbersAreWithinRange() {
        List<Integer> result = lotteryService.draw();
        for (Integer n : result) {
            assertTrue(n >= 1 && n <= 999, "数字越界: " + n);
        }
    }

    @Test
    void drawNumbersAreDistinct() {
        List<Integer> result = lotteryService.draw();
        assertEquals(5, result.stream().distinct().count(), "存在重复数字");
    }

    @Test
    void drawNumbersAreSortedAscending() {
        List<Integer> result = lotteryService.draw();
        for (int i = 1; i < result.size(); i++) {
            assertTrue(result.get(i - 1) < result.get(i), "结果未升序排列");
        }
    }

    @Test
    void drawIsRandomAcrossInvocations() {
        // 多次抽取，至少出现两组不同结果，验证随机性（理论上 1-999 取 5 个组合极多，碰撞概率可忽略）
        boolean differ = false;
        List<Integer> first = lotteryService.draw();
        for (int i = 0; i < 10 && !differ; i++) {
            differ = !first.equals(lotteryService.draw());
        }
        assertTrue(differ, "多次摇号结果完全相同，疑似未随机");
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd my-spring-boot-app && mvn test -Dtest=LotteryServiceTest`
Expected: COMPILATION ERROR，提示 `LotteryService` 类不存在。

- [ ] **Step 3: Write minimal implementation**

```java
package com.example.myapp.services;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class LotteryService {

    private static final int COUNT = 5;
    private static final int MIN = 1;
    private static final int MAX_INCLUSIVE = 999;

    /**
     * 摇号：返回 5 个互不重复、升序排列的随机整数，范围 [1, 999]。
     */
    public List<Integer> draw() {
        return ThreadLocalRandom.current()
                .ints(MIN, MAX_INCLUSIVE + 1)
                .distinct()
                .limit(COUNT)
                .boxed()
                .sorted()
                .collect(Collectors.toList());
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd my-spring-boot-app && mvn test -Dtest=LotteryServiceTest`
Expected: `Tests run: 5, Failures: 0, Errors: 0` BUILD SUCCESS。

---

