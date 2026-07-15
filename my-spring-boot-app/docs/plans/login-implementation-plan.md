# 用户登录功能 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为 Spring Boot + Thymeleaf 物品管理系统添加基于 Session 的用户登录/登出功能，使用 BCrypt 密码哈希。

**Architecture:** 采用轻量级 HttpSession + 拦截器方案，不引入全套 Spring Security。AuthService 负责认证逻辑（查库 → BCrypt 验密），AuthInterceptor 拦截受保护资源，AuthController 处理登录/登出请求。Thymeleaf 渲染 login.html 页面。

**Tech Stack:** Spring Boot 2.x, Java 17, JPA, Thymeleaf, H2, spring-security-crypto (BCrypt only)

---

## Global Constraints

- Java 17，javax.persistence / javax.validation（非 jakarta）
- 包名：`com.example.myapp`，新增类放在对应子包
- 密码使用 BCryptPasswordEncoder 哈希，强度 10
- 默认用户 `admin`，密码 `admin123`（BCrypt 加密存储）
- Session 属性名：`currentUser`
- 登录页面路径：`/login`，登出路径：`/logout`
- 登录成功跳转：`/items`，登出跳转：`/login`
- 不引入 spring-boot-starter-security
- 不修改现有 Controller 的已有路由行为（除 HomeController）

---

## File Structure

| 文件 | 路径 | 职责 |
|------|------|------|
| `pom.xml` | `my-spring-boot-app/pom.xml` | 添加 `spring-security-crypto` 依赖 |
| `AuthService.java` | `src/main/java/com/example/myapp/services/AuthService.java` | 认证逻辑：查用户 → BCrypt 验密 |
| `AuthController.java` | `src/main/java/com/example/myapp/controllers/AuthController.java` | GET/POST `/login`，GET `/logout` |
| `AuthInterceptor.java` | `src/main/java/com/example/myapp/interceptors/AuthInterceptor.java` | 拦截未登录请求 |
| `WebConfig.java` | `src/main/java/com/example/myapp/config/WebConfig.java` | 注册拦截器 |
| `login.html` | `src/main/resources/templates/login.html` | 登录页面 Thymeleaf 模板 |
| `UserService.java` | `src/main/java/com/example/myapp/services/UserService.java` | 修改：BCrypt 加密默认用户密码 |
| `HomeController.java` | `src/main/java/com/example/myapp/controllers/HomeController.java` | 修改：`/` 重定向到 `/login` |
| `AuthServiceTest.java` | `src/test/java/com/example/myapp/services/AuthServiceTest.java` | AuthService 单元测试 |
| `AuthControllerTest.java` | `src/test/java/com/example/myapp/controllers/AuthControllerTest.java` | AuthController 集成测试 |

---

## Task 1: 添加 BCrypt 依赖

**Files:**
- Modify: `my-spring-boot-app/pom.xml`

**Interfaces:**
- Consumes: nothing
- Produces: `org.springframework.security:spring-security-crypto` 在 classpath 可用

- [ ] **Step 1: 在 pom.xml 的 `<dependencies>` 中添加 spring-security-crypto**

```xml
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-crypto</artifactId>
</dependency>
```

位置：在现有 `<dependency>` 块最后一个 `</dependency>` 之后、`</dependencies>` 之前插入。

- [ ] **Step 2: 验证依赖可解析**

Run: `cd my-spring-boot-app && mvn dependency:resolve -q 2>&1 | tail -5`
Expected: BUILD SUCCESS，无错误

---

## Task 2: 新增 AuthService

**Files:**
- Create: `src/main/java/com/example/myapp/services/AuthService.java`
- Test: `src/test/java/com/example/myapp/services/AuthServiceTest.java`

**Interfaces:**
- Consumes: `UserRepository.findByUsername(String)` → `Optional<User>`
- Consumes: `BCryptPasswordEncoder` (from spring-security-crypto)
- Produces: `AuthService.authenticate(String username, String rawPassword)` → `User` | throws `IllegalArgumentException`

- [ ] **Step 1: 编写 AuthServiceTest（先写测试）**

```java
package com.example.myapp.services;

import com.example.myapp.models.User;
import com.example.myapp.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("admin");
        mockUser.setPassword("$2a$10$hashedpassword");
    }

    @Test
    void authenticate_shouldReturnUser_whenCredentialsAreCorrect() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("admin123", "$2a$10$hashedpassword")).thenReturn(true);

        User result = authService.authenticate("admin", "admin123");

        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        verify(userRepository).findByUsername("admin");
        verify(passwordEncoder).matches("admin123", "$2a$10$hashedpassword");
    }

    @Test
    void authenticate_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByUsername("nobody")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> authService.authenticate("nobody", "any"));
        assertEquals("用户名或密码错误", ex.getMessage());
    }

    @Test
    void authenticate_shouldThrowException_whenPasswordMismatch() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("wrong", "$2a$10$hashedpassword")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> authService.authenticate("admin", "wrong"));
        assertEquals("用户名或密码错误", ex.getMessage());
    }
}
```

- [ ] **Step 2: 运行测试验证失败**

Run: `cd my-spring-boot-app && mvn test -Dtest=AuthServiceTest -pl . 2>&1 | tail -20`
Expected: 编译失败（AuthService 不存在）

- [ ] **Step 3: 实现 AuthService**

```java
package com.example.myapp.services;

import com.example.myapp.models.User;
import com.example.myapp.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 验证用户名和密码，成功返回 User，失败抛出 IllegalArgumentException。
     */
    public User authenticate(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户名或密码错误"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        return user;
    }
}
```

- [ ] **Step 4: 运行测试验证通过**

Run: `cd my-spring-boot-app && mvn test -Dtest=AuthServiceTest -pl . 2>&1 | tail -20`
Expected: Tests run: 3, Failures: 0, Errors: 0

---

## Task 3: 新增 AuthInterceptor

**Files:**
- Create: `src/main/java/com/example/myapp/interceptors/AuthInterceptor.java`

**Interfaces:**
- Consumes: `HttpSession.getAttribute("currentUser")` → `User` or null
- Produces: `preHandle` return `boolean`（放行 true，拦截 false + redirect）

- [ ] **Step 1: 实现 AuthInterceptor**

```java
package com.example.myapp.interceptors;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        // 放行登录相关请求
        if (requestURI.startsWith("/login") || requestURI.startsWith("/logout")) {
            return true;
        }

        // 放行静态资源
        if (requestURI.startsWith("/css") || requestURI.startsWith("/js")
                || requestURI.startsWith("/images") || requestURI.startsWith("/h2-console")) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            return true;
        }

        response.sendRedirect("/login");
        return false;
    }
}
```

---

## Task 4: 新增 WebConfig 注册拦截器

**Files:**
- Create: `src/main/java/com/example/myapp/config/WebConfig.java`

**Interfaces:**
- Consumes: `AuthInterceptor`
- Produces: Spring MVC 拦截器注册，拦截 `/items/**`、`/profile/**`、`/` 等路径

- [ ] **Step 1: 实现 WebConfig**

```java
package com.example.myapp.config;

import com.example.myapp.interceptors.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/logout", "/css/**", "/js/**",
                        "/images/**", "/h2-console/**", "/error");
    }
}
```

---

## Task 5: 新增 AuthController

**Files:**
- Create: `src/main/java/com/example/myapp/controllers/AuthController.java`
- Test: `src/test/java/com/example/myapp/controllers/AuthControllerTest.java`

**Interfaces:**
- Consumes: `AuthService.authenticate(String, String)` → `User`
- Produces: `GET /login` → login.html; `POST /login` → redirect /items or login.html with error; `GET /logout` → redirect /login

- [ ] **Step 1: 编写 AuthControllerTest**

```java
package com.example.myapp.controllers;

import com.example.myapp.models.User;
import com.example.myapp.services.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    void getLogin_shouldReturnLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void postLogin_success_shouldRedirectToItems() throws Exception {
        User user = new User();
        user.setUsername("admin");
        when(authService.authenticate("admin", "admin123")).thenReturn(user);

        mockMvc.perform(post("/login")
                        .param("username", "admin")
                        .param("password", "admin123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items"));
    }

    @Test
    void postLogin_failure_shouldReturnLoginPageWithError() throws Exception {
        when(authService.authenticate(anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("用户名或密码错误"));

        mockMvc.perform(post("/login")
                        .param("username", "admin")
                        .param("password", "wrong"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("error", "用户名或密码错误"));
    }

    @Test
    void logout_shouldInvalidateSessionAndRedirect() throws Exception {
        mockMvc.perform(get("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}
```

- [ ] **Step 2: 运行测试验证失败**

Run: `cd my-spring-boot-app && mvn test -Dtest=AuthControllerTest -pl . 2>&1 | tail -20`
Expected: 编译失败或测试失败

- [ ] **Step 3: 实现 AuthController**

```java
package com.example.myapp.controllers;

import com.example.myapp.models.User;
import com.example.myapp.services.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        try {
            User user = authService.authenticate(username, password);
            session.setAttribute("currentUser", user);
            return "redirect:/items";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
```

- [ ] **Step 4: 运行测试验证通过**

Run: `cd my-spring-boot-app && mvn test -Dtest=AuthControllerTest -pl . 2>&1 | tail -20`
Expected: Tests run: 4, Failures: 0, Errors: 0

---

## Task 6: 新增 login.html 模板

**Files:**
- Create: `src/main/resources/templates/login.html`

**Interfaces:**
- Consumes: Model attribute `error` (String, optional)
- Produces: HTML 登录表单（POST /login，字段 username + password）

- [ ] **Step 1: 创建 login.html**

```html
<!DOCTYPE html>
<html lang="zh-CN" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>登录 - 物品管理系统</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        .login-box {
            background: white;
            padding: 40px;
            border-radius: 12px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            width: 360px;
        }
        .login-box h1 {
            text-align: center;
            margin-bottom: 8px;
            color: #333;
            font-size: 24px;
        }
        .login-box .subtitle {
            text-align: center;
            color: #888;
            margin-bottom: 28px;
            font-size: 14px;
        }
        .form-group {
            margin-bottom: 20px;
        }
        .form-group label {
            display: block;
            margin-bottom: 6px;
            color: #555;
            font-size: 14px;
            font-weight: 500;
        }
        .form-group input {
            width: 100%;
            padding: 10px 14px;
            border: 1px solid #ddd;
            border-radius: 6px;
            font-size: 15px;
            transition: border-color 0.2s;
        }
        .form-group input:focus {
            outline: none;
            border-color: #667eea;
        }
        .btn-login {
            width: 100%;
            padding: 12px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 6px;
            font-size: 16px;
            cursor: pointer;
            font-weight: 500;
        }
        .btn-login:hover {
            opacity: 0.9;
        }
        .error-message {
            background: #fff0f0;
            color: #e74c3c;
            padding: 10px 14px;
            border-radius: 6px;
            margin-bottom: 20px;
            font-size: 14px;
            text-align: center;
            border: 1px solid #ffd5d5;
        }
    </style>
</head>
<body>
    <div class="login-box">
        <h1>物品管理系统</h1>
        <p class="subtitle">请登录以继续</p>

        <div th:if="${error}" class="error-message" th:text="${error}"></div>

        <form th:action="@{/login}" method="post">
            <div class="form-group">
                <label for="username">用户名</label>
                <input type="text" id="username" name="username"
                       placeholder="请输入用户名" required autofocus>
            </div>
            <div class="form-group">
                <label for="password">密码</label>
                <input type="password" id="password" name="password"
                       placeholder="请输入密码" required>
            </div>
            <button type="submit" class="btn-login">登 录</button>
        </form>
    </div>
</body>
</html>
```

---

## Task 7: 修改 UserService 使用 BCrypt

**Files:**
- Modify: `src/main/java/com/example/myapp/services/UserService.java`

**Interfaces:**
- Consumes: `BCryptPasswordEncoder` (new field)
- Produces: `getOrCreateDefaultUser()` 返回的默认用户密码为 BCrypt 哈希

- [ ] **Step 1: 修改 UserService**

在 `UserService` 类中添加 `BCryptPasswordEncoder` 字段并修改 `getOrCreateDefaultUser()` 方法：

```java
// 新增字段
private final BCryptPasswordEncoder passwordEncoder;

// 修改构造函数，添加 BCryptPasswordEncoder 参数
public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
}

// 修改 getOrCreateDefaultUser() 方法中的密码设置行
// 原: defaultUser.setPassword("admin123");
// 改为:
defaultUser.setPassword(passwordEncoder.encode("admin123"));
```

具体修改：在 `getOrCreateDefaultUser()` 方法中，将 `defaultUser.setPassword("admin123");` 替换为 `defaultUser.setPassword(passwordEncoder.encode("admin123"));`。

同时需要在 `UserService` 类的构造函数中注入 `BCryptPasswordEncoder`。

---

## Task 8: 修改 HomeController 重定向

**Files:**
- Modify: `src/main/java/com/example/myapp/controllers/HomeController.java`

**Interfaces:**
- Produces: `GET /` → redirect `/login`（替代原 redirect `/items`）

- [ ] **Step 1: 修改 HomeController**

```java
package com.example.myapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String getHome() {
        return "redirect:/login";
    }
}
```

将 `return "redirect:/items";` 改为 `return "redirect:/login";`。

---

## Task 9: 新增 BCryptPasswordEncoder Bean 配置

**Files:**
- Modify: `src/main/java/com/example/myapp/config/WebConfig.java`（或新建 `AppConfig.java`）

推荐在 `WebConfig` 同目录新建 `AppConfig.java`：

**Files:**
- Create: `src/main/java/com/example/myapp/config/AppConfig.java`

- [ ] **Step 1: 创建 AppConfig 提供 BCryptPasswordEncoder Bean**

```java
package com.example.myapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

## Task 10: 端到端验证

- [ ] **Step 1: 启动应用**

Run: `cd my-spring-boot-app && mvn spring-boot:run -q &`
Wait: 应用启动完成（约 15-30s）

- [ ] **Step 2: 验证未登录拦截**

Run: `curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/`
Expected: 302（重定向到 /login）

Run: `curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/items`
Expected: 302（重定向到 /login）

- [ ] **Step 3: 验证登录页面可访问**

Run: `curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/login`
Expected: 200

- [ ] **Step 4: 验证正确登录**

Run: `curl -s -c cookies.txt -L -d "username=admin&password=admin123" http://localhost:8080/login -o /dev/null -w "%{http_code}\n%{url_effective}"`
Expected: 最终返回 200，URL 为 `/items`

- [ ] **Step 5: 验证错误密码**

Run: `curl -s -d "username=admin&password=wrong" http://localhost:8080/login -o /dev/null -w "%{http_code}"`
Expected: 200（停留在 login 页面）

- [ ] **Step 6: 验证登出**

Run: `curl -s -b cookies.txt -c cookies.txt -L http://localhost:8080/logout -o /dev/null -w "%{url_effective}"`
Expected: 最终 URL 为 `/login`

- [ ] **Step 7: 停止应用**

Run: `pkill -f "spring-boot:run"`

- [ ] **Step 8: 清理**

Run: `rm -f cookies.txt`

---

## Self-Review

### 1. Spec Coverage
| 验收标准 | 对应 Task |
|----------|----------|
| 访问 `/` 或 `/items` 时未登录被重定向到 `/login` | Task 3, 4, 8, 10 |
| `/login` 页面展示用户名+密码表单 | Task 6 |
| 正确用户名密码 → 登录成功 → 跳转 `/items` | Task 2, 5, 10 |
| 错误密码 → 停留在 `/login` 并显示错误 | Task 2, 5, 10 |
| 登录后访问 `/items` 不再被拦截 | Task 3, 10 |
| 访问 `/logout` → 清除 session → 重定向 `/login` | Task 5, 10 |
| 默认用户 `admin/admin123` 可登录 | Task 7, 10 |

### 2. Placeholder Scan
无 TBD、TODO、占位符。

### 3. DRY / YAGNI / TDD Check
- 每个 Task 独立可测，不引入注册页面、记住我、JWT 等未需求功能
- Task 2、5 遵循 TDD：先写测试 → 验证失败 → 实现 → 验证通过

### 4. 接口一致性
- `AuthService.authenticate(String, String)` → `User` 在所有 Task 中一致
- Session 属性 `currentUser` 在 AuthController 和 AuthInterceptor 中一致
- 错误消息 `"用户名或密码错误"` 在 AuthService 和 AuthController 中一致

### 5. 文件路径
所有路径均为相对于项目根目录 `my-spring-boot-app/` 的精确路径。

---

## Execution Handoff

**Plan complete and saved to `docs/plans/login-implementation-plan.md`. Two execution options:**

**1. Subagent-Driven (recommended)** - I dispatch a fresh subagent per task, review between tasks, fast iteration

**2. Inline Execution** - Execute tasks in this session using executing-plans, batch execution with checkpoints