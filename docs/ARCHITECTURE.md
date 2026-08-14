# 架构文档

## 项目概述

`ranxitest` - Spring Boot 示例应用，基于 Java 17、Spring Boot 2.6.6、Spring Data JPA、Thymeleaf、H2。

## 技术栈

| 技术 | 版本 |
|------|------|
| Java | 17 |
| Spring Boot | 2.6.6 |
| Spring Data JPA | via Spring Boot |
| Thymeleaf | via Spring Boot |
| H2 | in-memory |
| Maven | build tool |

## 模块列表

| 模块 | 路径 | 说明 |
|------|------|------|
| user | `src/main/java/com/example/myapp/models/User.java` | 用户管理模块 |
| hello | `docs/modules/hello/README.md` | Hello World 演示模块 |

## 分层架构

```
com.example.myapp
├── controllers/     # Web 层（控制器）
├── services/        # Service 层（业务逻辑）
│   └── impl/        # Service 实现
├── models/          # 数据模型层（JPA Entity）
├── repositories/    # DAO 层（数据访问）
├── exception/       # 全局异常处理
└── utils/           # 工具类
```

## 模块文档

- [hello 模块](docs/modules/hello/README.md)