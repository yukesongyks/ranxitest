# Hello World — 命令行程序 设计文档

## 需求来源

用户需求：写个 hello world

## 最终确认方案

| 项 | 决策 |
|---|---|
| 功能 | 命令行程序，输出 "Hello World" |
| 语言 | Shell 脚本（POSIX `/bin/sh`） |
| 位置 | `my-spring-boot-app/hello.sh` |
| 文件名 | `hello.sh` |

## 设计方案

### 文件：`my-spring-boot-app/hello.sh`

```sh
#!/bin/sh
echo "Hello World"
```

### 权限

```sh
chmod +x my-spring-boot-app/hello.sh
```

### 运行方式

- `./my-spring-boot-app/hello.sh`
- `sh my-spring-boot-app/hello.sh`

## 设计要点

- **零依赖**：仅使用 POSIX shell 内置 `echo`，无外部工具依赖
- **最小化**：单文件，2 行有效代码
- **可移植**：兼容所有 POSIX 兼容 shell（dash、bash、zsh 等）

## 验收标准

1. 执行 `sh my-spring-boot-app/hello.sh` 输出 `Hello World`
2. 脚本具有可执行权限，可直接 `./hello.sh` 运行
3. 退出码为 0