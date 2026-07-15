# 用户登录功能 — 需求澄清与设计方案

> 阶段: clarify | 日期: 2026-07-15 | 技能: brainstorming

---

## 1. 现状分析 (SSOT)

| 维度 | 现状 |
|------|------|
| 项目栈 | Spring Boot 2.x + Java 17 + JPA + Thymeleaf + H2 (内存库) |
| User 实体 | 已有 `id / username / email / password / phone / bio / location / avatarUrl / createdAt / updatedAt` |
| UserRepository | 已有 `findByUsername` / `findByEmail` / `existsByUsername` / `existsByEmail` |
| UserService | 已有 CRUD + `getOrCreateDefaultUser()`，注释标明"实际项目应接入认证系统" |
| 认证状态 | **无任何认证机制**，默认用户 `admin` 无密码 |
| 前端 | Thymeleaf 服务端渲染，首页 `index.html` 直通 `/items` |
| 安全依赖 | pom.xml 中**无 Spring Security** 依赖 |

---

## 2. 需求澄清 Q&A（自动推断决策）

| # | 问题 | 推断决策 | 依据 |
|---|------|----------|------|
| Q1 | 认证方式：Session 还是 JWT？ | **Session-based** | Thymeleaf 服务端渲染天然适配 Session；无需引入额外 JWT 依赖 |
| Q2 | 密码如何存储？ | **BCrypt 哈希** | 行业标准，Spring Security Crypto 提供 `BCryptPasswordEncoder` |
| Q3 | 登录凭证：用户名还是邮箱？ | **用户名 + 密码** | User 实体已有 `username` 字段，`UserRepository.findByUsername` 已就绪 |
| Q4 | 是否需要 Spring Security？ | **否，轻量实现** | 当前项目无 Security 依赖，引入全套 Security 配置成本高；用 HttpSession + 拦截器即可满足需求 |
| Q5 | 登录页面形式？ | **Thymeleaf 模板 `login.html`** | 与现有 `index.html` 风格一致 |
| Q6 | 登录成功跳转？ | **重定向到 `/items`** | 保持现有首页行为一致 |
| Q7 | 登录失败提示？ | **页面内错误消息** | 使用 Thymeleaf `Model` 传参 + `${error}` 显示 |
| Q8 | 是否需要注册页面？ | **本次不需要** | 需求仅描述"用户登陆"，注册可通过 H2 Console 或已有 `createUser` 手动完成 |
| Q9 | 是否需要"记住我"？ | **不需要** | 超出最小可行范围 |
| Q10 | 是否需要登出？ | **需要** | 登录与登出是对称功能，且实现成本极低 |

---

## 3. 方案设计

### 3.1 架构概览

```
浏览器 → LoginController (GET/POST /login)
              ↓
         AuthService.authenticate(username, password)
              ↓ 成功
         HttpSession.setAttribute("currentUser", user)
              ↓
         重定向 → /items
              ↓
         AuthInterceptor (preHandle 拦截 /items/**)
              ↓ 无 session → 重定向 /login
```

### 3.2 新增/变更文件清单

| 文件 | 操作 | 说明 |
|------|------|------|
| `pom.xml` | 修改 | 添加 `spring-security-crypto` 依赖（仅 BCrypt，非全套 Security） |
| `AuthService.java` | 新增 | 登录认证逻辑：查用户 → BCrypt 验密 → 返回 User 或抛异常 |
| `AuthController.java` | 新增 | `GET /login` 展示登录页；`POST /login` 处理登录表单 |
| `AuthInterceptor.java` | 新增 | 拦截 `/items/**`，未登录则重定向到 `/login` |
| `WebConfig.java` | 新增 | 注册拦截器 |
| `login.html` | 新增 | Thymeleaf 登录页面模板 |
| `UserService.java` | 修改 | 移除 `getOrCreateDefaultUser()` 的默认密码逻辑；初始化时用 BCrypt 加密默认用户密码 |
| `HomeController.java` | 修改 | `/` 重定向改为 `/login`（未登录用户先进登录页） |

### 3.3 数据流

```
POST /login
  body: username=admin&password=123456
    → AuthService.authenticate("admin", "123456")
      → UserRepository.findByUsername("admin")
        → User{password="$2a$10$..."}  // BCrypt hash
      → BCryptPasswordEncoder.matches("123456", hash) → true
      → return User
    → session.setAttribute("currentUser", user)
    → redirect:/items
```

### 3.4 拦截器逻辑

```
AuthInterceptor.preHandle:
  if (requestURI starts with "/login") → true (放行)
  if (session.getAttribute("currentUser") != null) → true (放行)
  else → response.sendRedirect("/login") → false (拦截)
```

---

## 4. 风险与约束

| 风险 | 缓解措施 |
|------|----------|
| H2 内存库重启后用户数据丢失 | 初始化时自动创建默认用户 `admin/admin123`（BCrypt 加密） |
| 无 CSRF 保护 | 对当前演示级项目可接受；后续可加 Spring Security |
| 密码明文传输 | 演示项目，HTTP 明文可接受；生产环境需 HTTPS |

---

## 5. 验收标准

- [ ] 访问 `/` 或 `/items` 时，未登录用户被重定向到 `/login`
- [ ] `/login` 页面展示用户名+密码表单
- [ ] 输入正确用户名密码 → 登录成功 → 跳转 `/items`
- [ ] 输入错误密码 → 停留在 `/login` 并显示"用户名或密码错误"
- [ ] 登录后访问 `/items` 不再被拦截
- [ ] 访问 `/logout` → 清除 session → 重定向到 `/login`
- [ ] 默认用户 `admin / admin123` 可登录