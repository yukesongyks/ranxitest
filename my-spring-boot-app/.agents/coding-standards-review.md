# 编码规范审查报告

> 审查日期：2025-07-18  
> 审查技能：dtazziboot-java-coding-standards v1.1.0  
> 项目：ranxitest / my-spring-boot-app  
> 技术栈：Spring Boot 2.6.6 + Java 17 + H2 + JPA + Thymeleaf

---

## 模块进度追踪

| 序号 | 模块 | READ | CHECK | 状态 |
|:----:|------|:----:|:-----:|------|
| 1 | profile (个人中心) | ✅ | ✅ | 已完成 |
| 2 | item (物品管理) | ✅ | ✅ | 已完成 |
| 3 | common (全局异常+入口) | ✅ | ✅ | 已完成 |

---

## 一、审查范围

### 已审查文件清单

| 文件 | 路径 | 行数 |
|------|------|:----:|
| MyAppApplication.java | src/main/java/com/example/myapp/ | 12 |
| User.java | src/main/java/com/example/myapp/models/ | 131 |
| Item.java | src/main/java/com/example/myapp/models/ | 143 |
| UserRepository.java | src/main/java/com/example/myapp/repositories/ | 19 |
| ItemRepository.java | src/main/java/com/example/myapp/repositories/ | 50 |
| UserService.java | src/main/java/com/example/myapp/services/ | 86 |
| ItemService.java | src/main/java/com/example/myapp/services/ | 107 |
| HomeController.java | src/main/java/com/example/myapp/controllers/ | 13 |
| ItemController.java | src/main/java/com/example/myapp/controllers/ | 130 |
| ProfileController.java | src/main/java/com/example/myapp/controllers/ | 64 |
| GlobalExceptionHandler.java | src/main/java/com/example/myapp/exception/ | 48 |
| MyAppApplicationTests.java | src/test/java/com/example/myapp/ | 13 |

---

## 二、L1 静态检查详情

### 2.1 命名规范 (naming.md)

| # | 检查项 | 结果 | 问题描述 |
|:--|--------|:----:|----------|
| N1 | 类名大驼峰 | ✅ | User, Item, HomeController 等均合规 |
| N2 | 方法名小驼峰 | ✅ | getUsername, findById, searchByKeyword 等均合规 |
| N3 | 接口方法不加public | ✅ | Repository 接口方法无多余修饰符 |
| N4 | **Service接口/实现分离** | ❌ | UserService、ItemService 是具体类，未定义接口。规范要求 Service 层应定义接口 + Impl 实现类 |
| N5 | **DO/DTO/VO 后缀** | ❌ | User、Item 实体类位于 models 包，未使用 DO 后缀。规范要求数据对象使用 `xxxDO` 命名 |
| N6 | 常量全大写 | — | 项目中未定义常量，无违规 |

**N4 修复建议**：
```java
// 定义接口
public interface UserService {
    UserDO createUser(UserDO user);
    Optional<UserDO> getUserById(Long id);
    // ...
}

// 实现类
@Service
public class UserServiceImpl implements UserService {
    // ...
}
```

**N5 修复建议**：
- `models/User.java` → `models/UserDO.java`（或移至 `entity/UserDO.java`）
- `models/Item.java` → `models/ItemDO.java`（或移至 `entity/ItemDO.java`）

---

### 2.2 异常日志规范 (exception-logging.md)

| # | 检查项 | 结果 | 问题描述 |
|:--|--------|:----:|----------|
| E1 | **日志记录** | ❌ | 全代码零日志输出。规范要求关键业务操作（创建/更新/删除/异常）必须记录日志 |
| E2 | **自定义业务异常** | ❌ | 直接使用 `IllegalArgumentException`，规范要求使用有业务含义的自定义异常（如 `BusinessException`、`ServiceException`） |
| E3 | 预检查避免NPE | ✅ | 使用 `Optional.orElseThrow()` 和 `existsByXxx()` 预检查 |
| E4 | 异常不用于流程控制 | ⚠️ | ItemController 中 try-catch IllegalArgumentException 用于流程控制（line 53-56, 78-80, 89-91, 125-127），虽有 GlobalExceptionHandler 但 Controller 层仍做了重复捕获 |
| E5 | 禁止e.printStackTrace | ✅ | 未发现直接使用 |
| E6 | 异常信息完整性 | ⚠️ | GlobalExceptionHandler 中 `handleGeneralException` 仅输出 message，未包含堆栈信息 |

**E1 修复建议**：在 UserService 和 ItemService 的关键方法中添加 SLF4J 日志：
```java
private static final Logger logger = LoggerFactory.getLogger(UserService.class);

public User createUser(User user) {
    logger.info("创建用户, username: {}", user.getUsername());
    // ...
}
```

**E2 修复建议**：创建自定义异常类：
```java
// exception/BusinessException.java
public class BusinessException extends RuntimeException {
    private final String errorCode;
    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
```

---

### 2.3 单元测试规范 (unit-testing.md)

| # | 检查项 | 结果 | 问题描述 |
|:--|--------|:----:|----------|
| T1 | **Service层测试** | ❌ | UserService、ItemService 均无单元测试 |
| T2 | **Controller层测试** | ❌ | ItemController、ProfileController 均无单元测试 |
| T3 | 测试覆盖 | ❌ | 仅 `MyAppApplicationTests.contextLoads()` 一个空测试 |
| T4 | 测试包结构一致 | ✅ | 测试目录路径与主代码一致 |

**缺失测试清单**：

| 被测类 | 建议测试类 | 优先级 |
|--------|-----------|:------:|
| UserService | UserServiceTest.java | 🔴 高 |
| ItemService | ItemServiceTest.java | 🔴 高 |
| ItemController | ItemControllerTest.java | 🟡 中 |
| ProfileController | ProfileControllerTest.java | 🟡 中 |
| GlobalExceptionHandler | GlobalExceptionHandlerTest.java | 🟢 低 |

---

### 2.4 安全规范 (security.md)

| # | 检查项 | 结果 | 问题描述 |
|:--|--------|:----:|----------|
| S1 | @Valid 输入校验 | ✅ | Controller 使用 `@Valid @ModelAttribute` |
| S2 | SQL参数化查询 | ✅ | Spring Data JPA 方法命名查询 + @Query 使用 `:param` 占位符 |
| S3 | 禁止 `${}` 占位符 | ✅ | @Query 中未使用 `${}` |
| S4 | 白名单验证 | — | 无文件上传等场景，不适用 |
| S5 | 密码存储 | ⚠️ | User 实体无密码字段，但当前为演示项目，可接受 |

---

### 2.5 MySQL/数据库规范 (mysql.md)

| # | 检查项 | 结果 | 问题描述 |
|:--|--------|:----:|----------|
| D1 | 表名小写 | ✅ | `@Table(name = "users")`, `@Table(name = "items")` |
| D2 | 字段名小写+下划线 | ✅ | `avatar_url`, `created_at`, `updated_at`, `user_id` |
| D3 | **必备字段命名** | ⚠️ | 使用 `created_at`/`updated_at` 而非规范要求的 `gmt_create`/`gmt_modified` |
| D4 | 金额使用BigDecimal | ✅ | Item.price 使用 `BigDecimal` + `@Digits(integer=10, fraction=2)` |
| D5 | 禁止float/double | ✅ | 未使用 float/double 存储金额 |

> **说明**：D3 为 H2 内存数据库环境，字段命名与 MySQL 规范存在差异，属于可接受偏差。

---

### 2.6 工程结构规范 (project-structure.md)

| # | 检查项 | 结果 | 问题描述 |
|:--|--------|:----:|----------|
| P1 | 包名小写 | ✅ | `com.example.myapp` 全小写 |
| P2 | **接口/实现分离** | ❌ | Service 无 impl 子包，无接口定义 |
| P3 | 公共类位置 | ⚠️ | `GlobalExceptionHandler` 位于 `exception/` 而非 `common/exception/` |
| P4 | Maven标准结构 | ✅ | 遵循标准目录布局 |
| P5 | 配置文件按环境分离 | ⚠️ | 仅一个 `application.properties`，无 dev/test/prod 分离 |

---

### 2.7 格式规范 (formatting.md)

| # | 检查项 | 结果 | 问题描述 |
|:--|--------|:----:|----------|
| F1 | 4空格缩进 | ✅ | 统一使用4空格 |
| F2 | 大括号格式 | ✅ | 左大括号不换行，右大括号前换行 |
| F3 | 运算符空格 | ✅ | 运算符左右均有空格 |
| F4 | 行长度≤120 | ✅ | 无超长行 |
| F5 | 方法长度≤80行 | ✅ | 最长方法约50行 |
| F6 | 注释格式 | ⚠️ | 仅有 ProfileController 有 Javadoc，其他类缺少类级别 Javadoc |

---

## 三、L2 动态验证

| 验证项 | 状态 | 说明 |
|--------|:----:|------|
| 编译验证 | ⚠️ 跳过 | 运行环境无 Maven (`mvn: not found`) |
| 单测验证 | ⚠️ 跳过 | 同上 |

---

## 四、待人工验证命令

请在本地开发环境执行以下命令：

```bash
cd my-spring-boot-app
mvn compile -DskipTests
mvn test
```

---

## 五、问题汇总与优先级

### 🔴 高优先级（必须修复）

| # | 问题 | 涉及文件 | 规范引用 |
|:--|------|----------|----------|
| 1 | Service 层未定义接口 | UserService.java, ItemService.java | naming.md §2.2, project-structure.md §4.2 |
| 2 | 全代码零日志输出 | UserService.java, ItemService.java, 全部Controller | exception-logging.md §3 |
| 3 | 缺少单元测试 | 全部Service/Controller | unit-testing.md §2.1 |

### 🟡 中优先级（建议修复）

| # | 问题 | 涉及文件 | 规范引用 |
|:--|------|----------|----------|
| 4 | 实体类未使用DO后缀 | User.java, Item.java | naming.md §4.2 |
| 5 | 直接抛出IllegalArgumentException | UserService.java, ItemService.java | exception-logging.md §2.14 |
| 6 | Controller层重复异常捕获 | ItemController.java | exception-logging.md §2.3 |

### 🟢 低优先级（可选优化）

| # | 问题 | 涉及文件 | 规范引用 |
|:--|------|----------|----------|
| 7 | 缺少类级别Javadoc | 多个文件 | comments.md |
| 8 | 配置文件未按环境分离 | application.properties | project-structure.md §3.2 |
| 9 | 异常处理器未记录堆栈 | GlobalExceptionHandler.java | exception-logging.md §3.6 |

---

## 六、合规亮点

1. ✅ **输入校验完善**：Entity 层使用 Bean Validation 注解（`@NotBlank`、`@Size`、`@Email`、`@Min`、`@Digits`），Controller 层使用 `@Valid` 触发校验
2. ✅ **SQL注入防护**：全部使用 Spring Data JPA 方法命名查询 + `@Query` 参数化，无字符串拼接
3. ✅ **金额类型正确**：`Item.price` 使用 `BigDecimal` + `@Digits` 约束
4. ✅ **NPE 防护**：使用 `Optional.orElseThrow()` 而非直接 `.get()`
5. ✅ **格式规范**：缩进、大括号、空格使用均符合规范
6. ✅ **Repository 接口简洁**：接口方法无多余 public 修饰符

---

*报告由 DTCoder 基于 dtazziboot-java-coding-standards v1.1.0 自动生成*