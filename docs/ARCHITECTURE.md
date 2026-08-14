# 架构文档

## 项目概述

`my-spring-boot-app` 是基于 Spring Boot 2.6.6 的 Java Web 应用。

## 技术栈

- Java 17
- Spring Boot 2.6.6
- Spring Data JPA
- Thymeleaf
- H2 Database

## 模块列表

| 模块 | 包路径 | 说明 |
|------|--------|------|
| controllers | `com.example.myapp.controllers` | Web 控制器层 |
| exception | `com.example.myapp.exception` | 异常处理 |
| models | `com.example.myapp.models` | 领域模型 |
| repositories | `com.example.myapp.repositories` | 数据访问层 |
| services | `com.example.myapp.services` | 业务服务层 |
| util | `com.example.myapp.util` | 通用工具类 |

## 分层约束

- Controller → Service → Repository → DB
- util 为纯工具层，无 Spring 依赖，可被任意层调用