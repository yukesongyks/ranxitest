# 摇号系统 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 提供一个 Web 摇号页面，用户点击「摇号」按钮后系统返回 1-99（含）之间的一个随机数字并展示。

**Architecture:** 遵循现有 Spring Boot MVC 分层：`LotteryService` 负责随机数生成业务逻辑，`LotteryController` 负责渲染摇号页面 `/lottery` 与提供 JSON 抽号接口 `/lottery/draw`。前端页面使用原生 `fetch` 调用抽号接口并把结果显示在页面上，无需额外前端框架。

**Tech Stack:** Java 17, Spring Boot 2.6.6, Spring MVC, Thymeleaf, JUnit 5, Spring Boot Test (MockMvc)

## Global Constraints

- Java 17，构建工具 Maven（`mvn`）。
- 随机数范围：1-99（含两端）。
- 遵循现有代码风格：构造器注入 `@Autowired`、`@Controller`/`@Service` 分层、Thymeleaf 模板放在 `src/main/resources/templates/`。
- 测试位于 `src/test/java/com/example/myapp/`，使用 JUnit 5 + spring-boot-starter-test（已随父 POM 提供，无需新增依赖）。
- 不修改 Git 配置；提交信息使用 conventional commits（`feat:`/`test:`）。

---

## File Structure

本计划涉及的文件及其职责：

- **Create:** `my-spring-boot-app/src/main/java/com/example/myapp/services/LotteryService.java`
  随机数生成业务逻辑。唯一职责：返回 1-99（含）之间的随机整数。
- **Create:** `my-spring-boot-app/src/main/java/com/example/myapp/controllers/LotteryController.java`
  HTTP 控制器。`GET /lottery` 返回摇号页面；`GET /lottery/draw` 返回 JSON `{ "number": <1-99> }`。
- **Create:** `my-spring-boot-app/src/main/resources/templates/lottery.html`
  摇号页面：一个「摇号」按钮，点击后通过 `fetch('/lottery/draw')` 取回数字并展示。
- **Create:** `my-spring-boot-app/src/test/java/com/example/myapp/services/LotteryServiceTest.java`
  `LotteryService` 的单元测试，验证随机数落在 1-99 范围。
- **Create:** `my-spring-boot-app/src/test/java/com/example/myapp/controllers/LotteryControllerTest.java`
  `LotteryController` 的 MockMvc 测试，验证页面路由与抽号接口。
- **Modify:** `my-spring-boot-app/src/main/resources/templates/items/list.html`
  在顶部导航加入「摇号系统」入口链接，使新页面可被发现。

---

### Task 1: LotteryService 随机数生成

**Files:**
- Create: `my-spring-boot-app/src/main/java/com/example/myapp/services/LotteryService.java`
- Test: `my-spring-boot-app/src/test/java/com/example/myapp/services/LotteryServiceTest.java`

**Interfaces:**
- Consumes: 无（首个任务）。
- Produces: `com.example.myapp.services.LotteryService#draw()` → `int`，返回 1-99（含）随机整数。

- [ ] **Step 1: Write the failing test**

Create `my-spring-boot-app/src/test/java/com/example/myapp/services/LotteryServiceTest.java`:

```java
package com.example.myapp.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LotteryServiceTest {

    private final LotteryService lotteryService = new LotteryService();

    @Test
    void draw_returnsNumberBetween1And99() {
        for (int i = 0; i < 10_000; i++) {
            int number = lotteryService.draw();
            assertTrue(number >= 1, "摇号结果不应小于 1，实际: " + number);
            assertTrue(number <= 99, "摇号结果不应大于 99，实际: " + number);
        }
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run:
```bash
cd my-spring-boot-app && mvn test -Dtest=LotteryServiceTest -q
```
Expected: 编译失败，提示 `LotteryService` 类不存在（cannot find symbol）。

- [ ] **Step 3: Write minimal implementation**

Create `my-spring-boot-app/src/main/java/com/example/myapp/services/LotteryService.java`:

```java
package com.example.myapp.services;

import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 摇号业务逻辑：返回 1-99（含）之间的随机整数。
 */
@Service
public class LotteryService {

    public int draw() {
        return ThreadLocalRandom.current().nextInt(1, 100);
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run:
```bash
cd my-spring-boot-app && mvn test -Dtest=LotteryServiceTest -q
```
Expected: PASS（`Tests run: 1, Failures: 0, Errors: 0`）。

- [ ] **Step 5: Commit**

```bash
git add my-spring-boot-app/src/main/java/com/example/myapp/services/LotteryService.java \
        my-spring-boot-app/src/test/java/com/example/myapp/services/LotteryServiceTest.java
git commit -m "feat: add LotteryService to generate random number 1-99"
```

---

### Task 2: LotteryController 页面与抽号接口

**Files:**
- Create: `my-spring-boot-app/src/main/java/com/example/myapp/controllers/LotteryController.java`
- Test: `my-spring-boot-app/src/test/java/com/example/myapp/controllers/LotteryControllerTest.java`

**Interfaces:**
- Consumes: `com.example.myapp.services.LotteryService#draw()` → `int`（来自 Task 1）。
- Produces:
  - `GET /lottery` → 视图名 `"lottery"`（渲染 `templates/lottery.html`，Task 3 创建）。
  - `GET /lottery/draw` → JSON `{"number": <int 1-99>}`，HTTP 200。

- [ ] **Step 1: Write the failing test**

Create `my-spring-boot-app/src/test/java/com/example/myapp/controllers/LotteryControllerTest.java`:

```java
package com.example.myapp.controllers;

import com.example.myapp.services.LotteryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

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
    void lotteryPage_returnsLotteryView() throws Exception {
        mockMvc.perform(get("/lottery"))
                .andExpect(status().isOk())
                .andExpect(view().name("lottery"));
    }

    @Test
    void draw_returnsJsonWithNumber() throws Exception {
        when(lotteryService.draw()).thenReturn(42);

        mockMvc.perform(get("/lottery/draw"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(42));
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run:
```bash
cd my-spring-boot-app && mvn test -Dtest=LotteryControllerTest -q
```
Expected: 编译失败，提示 `LotteryController` 类不存在。

- [ ] **Step 3: Write minimal implementation**

Create `my-spring-boot-app/src/main/java/com/example/myapp/controllers/LotteryController.java`:

```java
package com.example.myapp.controllers;

import com.example.myapp.services.LotteryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/lottery")
public class LotteryController {

    private final LotteryService lotteryService;

    @Autowired
    public LotteryController(LotteryService lotteryService) {
        this.lotteryService = lotteryService;
    }

    @GetMapping
    public String lotteryPage() {
        return "lottery";
    }

    @GetMapping("/draw")
    @ResponseBody
    public Map<String, Object> draw() {
        return Map.of("number", lotteryService.draw());
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run:
```bash
cd my-spring-boot-app && mvn test -Dtest=LotteryControllerTest -q
```
Expected: PASS（2 个测试通过）。注意：`@WebMvcTest` 默认会尝试解析 Thymeleaf 视图 `lottery`，若模板尚未创建可能报 404/视图解析错误。如果 `lotteryPage_returnsLotteryView` 因模板缺失而失败，先执行 Task 3 创建 `lottery.html`，再回到本步骤重跑测试。

- [ ] **Step 5: Commit**

```bash
git add my-spring-boot-app/src/main/java/com/example/myapp/controllers/LotteryController.java \
        my-spring-boot-app/src/test/java/com/example/myapp/controllers/LotteryControllerTest.java
git commit -m "feat: add LotteryController with page and draw endpoint"
```

---

### Task 3: 摇号页面模板

**Files:**
- Create: `my-spring-boot-app/src/main/resources/templates/lottery.html`

**Interfaces:**
- Consumes: `GET /lottery` 路由（Task 2）与 `GET /lottery/draw` JSON 接口（Task 2）。
- Produces: 渲染摇号按钮与结果展示的 HTML 页面，视觉风格与 `items/list.html` 保持一致（同样的 CSS 变量与卡片布局）。

- [ ] **Step 1: 创建摇号页面**

Create `my-spring-boot-app/src/main/resources/templates/lottery.html`:

```html
<!DOCTYPE html>
<html lang="zh-CN" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>摇号系统</title>
    <style>
        :root {
            --bg: #f6f8fb;
            --card: #ffffff;
            --text: #1f2937;
            --muted: #6b7280;
            --primary: #0f766e;
            --border: #e5e7eb;
        }

        * { box-sizing: border-box; }

        body {
            margin: 0;
            font-family: "Helvetica Neue", "PingFang SC", sans-serif;
            background: linear-gradient(135deg, #eef7f4, #f7fafc);
            color: var(--text);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .container {
            max-width: 520px;
            margin: 40px auto;
            background: var(--card);
            border: 1px solid var(--border);
            border-radius: 12px;
            padding: 40px;
            text-align: center;
            box-shadow: 0 10px 30px rgba(15, 23, 42, 0.06);
        }

        h1 {
            margin: 0 0 8px;
            font-size: 28px;
            color: var(--primary);
        }

        .desc {
            color: var(--muted);
            font-size: 14px;
            margin-bottom: 32px;
        }

        .result {
            font-size: 96px;
            font-weight: 700;
            color: var(--primary);
            line-height: 1;
            margin: 24px 0;
            min-height: 96px;
        }

        .result.pending {
            color: var(--muted);
            font-size: 18px;
            font-weight: 400;
        }

        .btn {
            display: inline-block;
            border: none;
            border-radius: 8px;
            padding: 14px 36px;
            background: var(--primary);
            color: #ffffff;
            font-size: 16px;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.2s;
        }

        .btn:hover:not(:disabled) {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(15, 118, 110, 0.3);
        }

        .btn:disabled {
            opacity: 0.6;
            cursor: not-allowed;
        }

        .back {
            display: inline-block;
            margin-top: 24px;
            color: var(--muted);
            text-decoration: none;
            font-size: 14px;
        }

        .back:hover {
            color: var(--primary);
        }
    </style>
</head>
<body>
<div class="container">
    <h1>摇号系统</h1>
    <div class="desc">点击下方按钮，随机抽取 1-99 之间的数字</div>

    <div id="result" class="result pending">等待摇号</div>

    <button id="drawBtn" class="btn" type="button">摇号</button>
    <br>
    <a class="back" th:href="@{/items}">返回物品管理</a>
</div>

<script>
    (function () {
        var btn = document.getElementById('drawBtn');
        var result = document.getElementById('result');

        btn.addEventListener('click', function () {
            btn.disabled = true;
            result.textContent = '摇号中...';
            result.className = 'result pending';

            fetch('/lottery/draw')
                .then(function (res) {
                    if (!res.ok) {
                        throw new Error('摇号失败');
                    }
                    return res.json();
                })
                .then(function (data) {
                    result.textContent = data.number;
                    result.className = 'result';
                })
                .catch(function (err) {
                    result.textContent = '出错了：' + err.message;
                    result.className = 'result pending';
                })
                .finally(function () {
                    btn.disabled = false;
                });
        });
    })();
</script>
</body>
</html>
```

- [ ] **Step 2: 重跑控制器测试以确认模板可被解析**

Run:
```bash
cd my-spring-boot-app && mvn test -Dtest=LotteryControllerTest -q
```
Expected: PASS（`lotteryPage_returnsLotteryView` 现可正确解析 Thymeleaf 视图）。

- [ ] **Step 3: Commit**

```bash
git add my-spring-boot-app/src/main/resources/templates/lottery.html
git commit -m "feat: add lottery page with draw button"
```

---

### Task 4: 物品列表页加入摇号入口

**Files:**
- Modify: `my-spring-boot-app/src/main/resources/templates/items/list.html`（顶部导航区，约第 199-202 行）

**Interfaces:**
- Consumes: `GET /lottery` 路由（Task 2）。
- Produces: 在物品列表页右上角导航增加「摇号系统」链接按钮。

- [ ] **Step 1: 在顶部导航加入链接**

在 `my-spring-boot-app/src/main/resources/templates/items/list.html` 中，将顶部导航区的 div（`style="display:flex;gap:10px;align-items:center;"`）内容修改为：

原代码（第 199-202 行）：
```html
        <div style="display:flex;gap:10px;align-items:center;">
            <a class="btn btn-secondary" th:href="@{/profile}">个人中心</a>
            <a class="btn btn-primary" th:href="@{/items/new}">+ 新增物品</a>
        </div>
```

替换为：
```html
        <div style="display:flex;gap:10px;align-items:center;">
            <a class="btn btn-secondary" th:href="@{/lottery}">摇号系统</a>
            <a class="btn btn-secondary" th:href="@{/profile}">个人中心</a>
            <a class="btn btn-primary" th:href="@{/items/new}">+ 新增物品</a>
        </div>
```

- [ ] **Step 2: 运行全部摇号相关测试，确认无回归**

Run:
```bash
cd my-spring-boot-app && mvn test -Dtest=LotteryServiceTest,LotteryControllerTest -q
```
Expected: PASS（模板修改不影响控制器/服务测试）。

- [ ] **Step 3: Commit**

```bash
git add my-spring-boot-app/src/main/resources/templates/items/list.html
git commit -m "feat: add lottery entry link in items list navigation"
```

---

### Task 5: 全量测试与端到端冒烟验证

**Files:**
- 无新增文件；验证整个摇号功能端到端可用。

- [ ] **Step 1: 运行项目全部测试**

Run:
```bash
cd my-spring-boot-app && mvn test -q
```
Expected: 所有测试通过（含原有的 `MyAppApplicationTests.contextLoads` 与新增的摇号测试）。

- [ ] **Step 2: 启动应用并冒烟验证抽号接口**

启动应用（后台）：
```bash
cd my-spring-boot-app && mvn spring-boot:run
```

等待应用启动后，调用抽号接口多次验证返回值范围：
```bash
for i in 1 2 3 4 5; do curl -s http://localhost:8080/lottery/draw; echo; done
```
Expected: 每次返回形如 `{"number":<N>}` 的 JSON，其中 `<N>` 为 1-99 之间的整数。

验证页面可访问：
```bash
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/lottery
```
Expected: `200`。

验证首页跳转链路未受影响：
```bash
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/items
```
Expected: `200`。

- [ ] **Step 3: 停止应用**

停止后台运行的 `mvn spring-boot:run` 进程。

- [ ] **Step 4: 最终提交（如有未提交变更）**

```bash
git status
```
若工作区干净，则无需提交；若有遗漏变更，按文件补提交：
```bash
git add -A && git commit -m "test: verify lottery system end-to-end"
```

---

## Self-Review

**1. Spec coverage（需求覆盖）：**
- 「点击摇号」 → Task 3 的 `lottery.html` 提供「摇号」按钮，点击触发 `fetch('/lottery/draw')`。✅
- 「出现 1-99 之间随机一个数字」 → Task 1 `LotteryService.draw()` 使用 `ThreadLocalRandom.current().nextInt(1, 100)` 返回 1-99（含）。Task 1 测试以 10000 次循环验证范围。✅
- 可发现性 → Task 4 在物品列表页加入入口链接。✅

**2. Placeholder scan（占位符扫描）：**
- 全部步骤均含完整代码与确切命令，无 TBD/TODO/“类似 Task N”等占位。✅

**3. Type consistency（类型一致性）：**
- `LotteryService.draw()` 在 Task 1 定义为 `int draw()`，Task 2 控制器调用 `lotteryService.draw()` 返回 `int`，测试 mock 返回 `42`（int）——一致。✅
- 路由 `/lottery`、`/lottery/draw` 在 Task 2、Task 3、Task 4 中引用一致。✅
- 视图名 `"lottery"` 与模板文件 `templates/lottery.html` 匹配。✅

计划完整，可交付执行。
