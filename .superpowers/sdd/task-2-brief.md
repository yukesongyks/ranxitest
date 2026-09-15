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

