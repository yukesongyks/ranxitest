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

