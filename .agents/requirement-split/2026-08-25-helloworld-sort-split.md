# 需求梳理文档

> 需求描述极度模糊，以下为基于当前信息的初步梳理，待关键问题澄清后再进入完整评估流程。

## 当前理解

用户需求为三个独立功能点：

1. **HelloWorld** — 一个输出 "Hello World" 的程序或接口
2. **冒泡排序** — 实现冒泡排序算法
3. **最短排序** — 含义不明确，可能指：最短路径排序、选择排序（每次选最小元素）、最短代码实现、或其他排序算法

**仓库上下文**：当前仓库为 Spring Boot 2.6.6 + Java 17 + Maven 项目，已有 Controller/Service/Model/Repository 分层结构，使用 Thymeleaf 模板和 H2 内存数据库。合理的实现方式可能为：新增 REST Controller 端点、新增 Service 工具方法、或新增 Thymeleaf 页面。

## 待回答的关键问题

按优先级排列：

1. ~~**目标用户与预期结果**~~ ✅ 已确认：纯 Java 工具类，通过单元测试验证
2. ~~**"最短排序"的具体含义**~~ ✅ 已确认：选择排序（Selection Sort）
3. ~~**成功衡量方式**~~ ✅ 已确认：默认约定 — 包路径 `com.example.myapp.utils`，类名 `HelloWorld` / `BubbleSort` / `SelectionSort`，通过 JUnit 测试验证

## 影响地图（已确认）

| 领域 | 证据（路径:符号） | 预期变化 | 置信度 |
|---|---|---|---|
| utils 包 | `my-spring-boot-app/src/main/java/com/example/myapp/`（新建 `utils/` 子包） | 新增 `HelloWorld.java`、`BubbleSort.java`、`SelectionSort.java` | 高 |
| 测试 | `my-spring-boot-app/src/test/java/com/example/myapp/` | 新增对应 JUnit 测试类 | 高 |

## 复杂度评估

| 维度 | 评分 | 依据 |
|---|---|---|
| 范围与触点 | 1 | 一个模块及测试 |
| 领域/业务规则 | 0 | 纯算法展示，无业务规则 |
| 数据与状态 | 0 | 无持久化，仅内存计算 |
| 集成与依赖 | 0 | 无外部依赖 |
| 非功能/安全/运维 | 0 | 无 |
| 未知项与歧义 | 0 | 已全部澄清 |
| 测试与上线难度 | 1 | 新增单测 |
| **总分** | **2/21** | **极低复杂度** |

## 拆分建议

**不拆分。** 未触发任何强制拆分条件，三个类为同一模块下的独立工具类，可在一个任务中完成。

### 子任务清单

| # | 子任务 | 依赖 |
|---|---|---|
| 1 | 创建 `HelloWorld.java` + 测试 | 无 |
| 2 | 创建 `BubbleSort.java` + 测试 | 无 |
| 3 | 创建 `SelectionSort.java` + 测试 | 无 |

三个子任务可并行执行，无相互依赖。

---

*生成时间：2026-08-25 | 状态：已澄清，可进入执行*