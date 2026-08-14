# Hello World 系统分析设计文档

## 1. 需求概述

### 1.1 需求背景
本项目为一个基于 Spring Boot 的 Web 应用程序，核心目标是提供一个可运行的 Hello World 服务，同时构建一个健壮、统一的异常处理体系，保障系统的稳定性和用户体验。

### 1.2 核心需求
- 提供一个可执行的 Hello World 入口程序
- 构建完善的异常处理机制，覆盖 API 和页面两种交互场景
- 实现统一的错误响应格式

## 2. 系统架构

### 2.1 技术栈
- **框架**: Spring Boot
- **模板引擎**: Thymeleaf
- **数据库**: H2 (内存数据库)
- **ORM**: JPA / Hibernate
- **构建工具**: Maven (推测)

### 2.2 模块划分
```
my-spring-boot-app
├── src/main/java/com/example/myapp/
│   ├── HelloWorld.java                          # 入口程序
│   ├── controllers/
│   │   └── ErrorViewController.java             # 错误页面控制器
│   └── exception/
│       ├── ApiResponse.java                     # 统一响应封装
│       ├── BusinessException.java               # 业务异常基类
│       ├── ErrorCode.java                       # 错误码枚举
│       ├── GlobalExceptionHandler.java           # 全局异常处理器
│       ├── ResourceAlreadyExistsException.java   # 资源已存在异常
│       └── ResourceNotFoundException.java        # 资源不存在异常
└── src/main/resources/
    ├── application.properties                    # 应用配置
    └── templates/
        └── error.html                            # 错误页面模板
```

## 3. 核心组件设计

### 3.1 HelloWorld 入口
- **路径**: `com.example.myapp.HelloWorld`
- **职责**: 提供程序入口，打印 "Hello World"
- **特点**: 目前为独立 main 方法，可作为独立程序运行

### 3.2 异常处理体系

#### 3.2.1 错误码定义 (ErrorCode)
定义了标准化的 HTTP 风格错误码：
- `200` - 成功
- `400` - 请求参数错误
- `401` - 未授权
- `403` - 禁止访问
- `404` - 资源不存在
- `405` - 请求方法不允许
- `409` - 资源冲突
- `422` - 参数校验失败
- `500` - 系统内部错误
- `503` - 服务不可用

#### 3.2.2 业务异常层次
```
RuntimeException
    └── BusinessException (code + message)
            ├── ResourceNotFoundException (404)
            └── ResourceAlreadyExistsException (409)
```

#### 3.2.3 统一响应封装 (ApiResponse)
```json
{
  "code": 200,
  "message": "成功",
  "data": {},
  "timestamp": "2024-01-01 12:00:00",
  "path": "/api/example"
}
```

#### 3.2.4 全局异常处理器 (GlobalExceptionHandler)
**处理器覆盖范围**:
| 异常类型 | 场景 | 响应方式 |
|---------|------|---------|
| BusinessException | 通用业务异常 | Ajax → JSON, 页面 → 重定向 |
| ResourceNotFoundException | 资源不存在 | Ajax → JSON, 页面 → 重定向 |
| ResourceAlreadyExistsException | 资源冲突 | Ajax → JSON, 页面 → 重定向 |
| IllegalArgumentException | 参数不合法 | Ajax → JSON, 页面 → 返回/items |
| MethodArgumentNotValidException | 参数校验失败 | Ajax → JSON, 页面 → 重定向 |
| BindException | 绑定异常 | Ajax → JSON, 页面 → 重定向 |
| MissingServletRequestParameterException | 缺少参数 | Ajax → JSON, 页面 → 重定向 |
| HttpMessageNotReadableException | 请求体格式错误 | Ajax → JSON, 页面 → 重定向 |
| HttpRequestMethodNotSupportedException | HTTP方法不支持 | Ajax → JSON, 页面 → 重定向 |
| NoHandlerFoundException | 页面不存在 | Ajax → JSON, 页面 → 重定向 |
| Exception | 通用异常兜底 | Ajax → JSON, 页面 → 重定向 |

**Ajax 识别逻辑**: 通过 `Accept: application/json` 请求头或 `X-Requested-With: XMLHttpRequest` 判断。

### 3.3 错误页面展示
- **ErrorViewController**: 处理 `/error` 路径，从 request attributes 提取错误信息
- **error.html**: Thymeleaf 模板，展示错误码、错误信息和返回按钮
- **安全控制**: `isInternalUrl` 方法防止开放重定向漏洞

## 4. 配置设计

### 4.1 application.properties 关键配置
```properties
server.port=8080
spring.datasource.url=jdbc:h2:mem:itemdb
spring.h2.console.enabled=true
spring.mvc.throw-exception-if-no-handler-found=true
```

### 4.2 配置说明
- 启用 `throw-exception-if-no-handler-found` 使无匹配路由时抛出 `NoHandlerFoundException`，便于全局统一处理
- H2 控制台开启便于开发调试

## 5. 接口设计

### 5.1 API 响应格式
所有 API 异常响应遵循统一的 `ApiResponse` 格式。

### 5.2 页面响应
- 错误页面路径: `/error`
- 模板: `templates/error.html`
- 支持通过 Flash Attributes 传递错误信息

## 6. 非功能设计

### 6.1 安全性
- 开放重定向防护: `isInternalUrl()` 方法校验 referer 的 host 和 port
- 错误信息脱敏: 通用异常返回 "系统错误: " + 简要信息，避免泄露敏感栈信息

### 6.2 可扩展性
- 新增业务异常只需继承 `BusinessException`
- 新增异常处理器只需在 `GlobalExceptionHandler` 中添加 `@ExceptionHandler` 方法
- 错误码通过 `ErrorCode` 枚举集中管理

### 6.3 可维护性
- 异常分类清晰，职责单一
- 统一的响应格式便于前端统一处理
- 模板化错误页面便于样式统一调整

## 7. 风险与建议

| 风险点 | 影响 | 建议措施 |
|-------|------|---------|
| HelloWorld.java 为独立 main 方法，未与 Spring Boot 整合 | 功能割裂 | 建议将其改造为 Spring Boot 的 REST Controller，提供 `/hello` 端点 |
| 缺少单元测试 | 质量风险 | 补充各异常场景的单元测试 |
| 缺少日志记录 | 排查困难 | 在全局异常处理器中添加日志记录 |

## 8. 后续迭代建议
1. 将 HelloWorld 整合为 Spring Boot REST API
2. 补充单元测试和集成测试
3. 添加日志框架 (如 SLF4J/Logback) 并在异常处理器中记录日志
4. 考虑引入 Swagger/OpenAPI 文档
