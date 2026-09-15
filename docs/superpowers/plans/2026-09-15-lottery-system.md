# 摇号系统 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在现有 Spring Boot 应用中新增摇号功能：页面提供"摇号"按钮，点击后展示 1-999 之间的 5 个随机数字。

**Architecture:** 遵循仓库现有分层（Controller → Service → Thymeleaf 模板）。`LotteryService` 负责生成随机数（纯函数、可单测），`LotteryController` 提供 `/lottery` 页面和 `/api/lottery/draw` JSON 接口，前端页面用原生 JS `fetch` 调用接口并渲染结果，无新增依赖。

**Tech Stack:** Java 17, Spring Boot 2.6.6 (spring-boot-starter-web, starter-thymeleaf, starter-test), JUnit 5, MockMvc, 原生 JavaScript。

## Global Constraints

- 随机数字范围：1 ≤ n ≤ 999（含边界）。
- 每次摇号产出 5 个数字。
- 5 个数字互不重复（摇号语义；若需允许重复见计划末尾"待确认项"）。
- 结果按升序展示（便于阅读，不改变随机性语义）。
- 不新增任何 Maven 依赖；不动现有 User/Item 相关代码。
- 所有新文件放在 `my-spring-boot-app/src/` 下对应分层目录，模板放在 `templates/lottery/`。
- 构建与测试命令均在 `my-spring-boot-app/` 目录下执行：`mvn test`。
- **本计划不包含 git 提交步骤**：当前任务流程禁止一切 Git 写操作，提交由用户或后续流程处理；各 Task 结束即代表其交付物可独立验收。

---

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

### Task 2: LotteryController —— 页面路由与 JSON 接口

**Files:**
- Create: `my-spring-boot-app/src/main/java/com/example/myapp/controllers/LotteryController.java`
- Test: `my-spring-boot-app/src/test/java/com/example/myapp/controllers/LotteryControllerTest.java`

**Interfaces:**
- Consumes: `LotteryService.draw()`（Task 1 产出，签名 `public List<Integer> draw()`）。
- Produces:
  - `GET /lottery` → 渲染 Thymeleaf 视图名 `"lottery/index"`（Task 3 的模板必须放在 `templates/lottery/index.html`）。
  - `GET /api/lottery/draw` → `200 OK`，响应体为 5 个整数的 JSON 数组，如 `[123, 45, 678, 9, 500]`。Task 3 前端 JS 依赖此路径与响应结构。

- [ ] **Step 1: Write the failing test**

```java
package com.example.myapp.controllers;

import com.example.myapp.services.LotteryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LotteryController.class)
class LotteryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LotteryService lotteryService;

    @Test
    void drawEndpointReturnsFiveNumbersAsJson() throws Exception {
        when(lotteryService.draw()).thenReturn(List.of(9, 45, 123, 500, 678));

        mockMvc.perform(get("/api/lottery/draw"))
                .andExpect(status().isOk())
                .andExpect(content().json("[9,45,123,500,678]"));
    }

    @Test
    void lotteryPageIsRendered() throws Exception {
        mockMvc.perform(get("/lottery"))
                .andExpect(status().isOk())
                .andExpect(view().name("lottery/index"));
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd my-spring-boot-app && mvn test -Dtest=LotteryControllerTest`
Expected: COMPILATION ERROR，提示 `LotteryController` 类不存在。

- [ ] **Step 3: Write minimal implementation**

```java
package com.example.myapp.controllers;

import com.example.myapp.services.LotteryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class LotteryController {

    private final LotteryService lotteryService;

    public LotteryController(LotteryService lotteryService) {
        this.lotteryService = lotteryService;
    }

    @GetMapping("/lottery")
    public String lotteryPage() {
        return "lottery/index";
    }

    @GetMapping("/api/lottery/draw")
    @ResponseBody
    public List<Integer> draw() {
        return lotteryService.draw();
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd my-spring-boot-app && mvn test -Dtest=LotteryControllerTest`
Expected: `Tests run: 2, Failures: 0, Errors: 0` BUILD SUCCESS。

---

### Task 3: 摇号页面 —— Thymeleaf 模板与前端交互

**Files:**
- Create: `my-spring-boot-app/src/main/resources/templates/lottery/index.html`

**Interfaces:**
- Consumes:
  - Task 2 的视图名 `"lottery/index"`（本文件即该视图）。
  - Task 2 的接口 `GET /api/lottery/draw`，响应为 5 个整数的 JSON 数组。
- Produces: 用户可见的摇号页面：一个"摇号"按钮，点击后展示 5 个数字。

- [ ] **Step 1: Write the template**

```html
<!DOCTYPE html>
<html lang="zh-CN" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>摇号系统</title>
    <style>
        :root {
            --primary: #0f766e;
            --bg: linear-gradient(135deg, #f0fdf4, #eff6ff);
        }

        * { box-sizing: border-box; }

        body {
            margin: 0;
            font-family: "Helvetica Neue", "PingFang SC", sans-serif;
            background: var(--bg);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .container {
            text-align: center;
            padding: 40px;
        }

        h1 {
            font-size: 36px;
            color: var(--primary);
            margin-bottom: 16px;
        }

        p {
            font-size: 18px;
            color: #6b7280;
            margin-bottom: 32px;
        }

        .btn {
            display: inline-block;
            background: var(--primary);
            color: white;
            padding: 14px 32px;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.2s;
        }

        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(15, 118, 110, 0.3);
        }

        .btn:disabled {
            opacity: 0.6;
            cursor: not-allowed;
            transform: none;
        }

        #result {
            display: flex;
            gap: 16px;
            justify-content: center;
            margin-top: 32px;
            min-height: 80px;
        }

        .ball {
            width: 72px;
            height: 72px;
            border-radius: 50%;
            background: white;
            border: 3px solid var(--primary);
            color: var(--primary);
            font-size: 26px;
            font-weight: 700;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 4px 10px rgba(15, 118, 110, 0.15);
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>摇号系统</h1>
        <p>点击按钮，随机摇出 1-999 之间的 5 个数字</p>
        <button class="btn" id="drawBtn" onclick="drawNumbers()">摇 号</button>
        <div id="result"></div>
    </div>

    <script>
        async function drawNumbers() {
            const btn = document.getElementById('drawBtn');
            const result = document.getElementById('result');
            btn.disabled = true;
            btn.textContent = '摇号中...';
            result.innerHTML = '';
            try {
                const response = await fetch('/api/lottery/draw');
                if (!response.ok) {
                    throw new Error('HTTP ' + response.status);
                }
                const numbers = await response.json();
                numbers.forEach(function (n, i) {
                    setTimeout(function () {
                        const ball = document.createElement('div');
                        ball.className = 'ball';
                        ball.textContent = n;
                        result.appendChild(ball);
                    }, i * 150);
                });
            } catch (e) {
                result.innerHTML = '<p style="color:#dc2626">摇号失败，请重试</p>';
            } finally {
                btn.disabled = false;
                btn.textContent = '摇 号';
            }
        }
    </script>
</body>
</html>
```

- [ ] **Step 2: Verify template renders through the real server**

Run: `cd my-spring-boot-app && mvn spring-boot:run`
然后浏览器打开 `http://localhost:8080/lottery`，点击"摇 号"按钮。
Expected: 页面无样式错乱；点击后依次弹出 5 个圆形数字球，每个数字在 1-999 之间且互不重复；再次点击可重新摇号；停止服务（Ctrl+C）。

- [ ] **Step 3: Run the full test suite**

Run: `cd my-spring-boot-app && mvn test`
Expected: 全部测试通过（原有 `MyAppApplicationTests` + 新增 `LotteryServiceTest` 5 个 + `LotteryControllerTest` 2 个），BUILD SUCCESS。

---

## 待确认项（已按默认方案编写，如需调整仅改 1 处）

- **数字是否允许重复**：默认 5 个数字互不重复。若允许重复，将 `LotteryService.draw()` 中 `.distinct()` 删除，并同步删除 `LotteryServiceTest.drawNumbersAreDistinct` 测试即可，其余不受影响。

## Self-Review 结论

- **Spec coverage**：需求"点击摇号 → 出现 1-999 之间随机 5 个数字"由 Task 3（点击交互）+ Task 1（随机 5 个数字、范围 1-999）+ Task 2（接口串联）完整覆盖。
- **Placeholder scan**：所有步骤均含完整代码/命令与预期输出，无 TBD/TODO 类占位。
- **Type consistency**：`LotteryService.draw()` 返回 `List<Integer>`，Task 2 Controller 注入并直接返回该类型，Task 3 前端消费同一路径 `/api/lottery/draw` 的 JSON 数组，签名一致。
