# hello 模块

## 模块职责

提供 Hello World REST API 端点，用于演示 Spring Boot REST 控制器基础用法。

## 关键类

| 类 | 类型 | 说明 |
|----|------|------|
| `HelloController` | REST 控制器 | 暴露 GET /api/hello 端点 |
| `HelloService` | 服务接口 | 定义问候语业务契约 |
| `HelloServiceImpl` | 服务实现 | 返回 "Hello, World!" |

## 依赖关系

- 无外部模块依赖
- 仅依赖 Spring Web（`spring-boot-starter-web`）

## API 接口

| 方法 | 路径 | 说明 | 响应 |
|------|------|------|------|
| GET | `/api/hello` | 返回问候语 | `200 OK` - `Hello, World!` |