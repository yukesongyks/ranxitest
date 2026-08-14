# Code Review Checklist

> **Change** Service Tests · **分支/Commit** `AI/task-DEV-*` · **日期** `2025-06-11`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **自动化预扫**：`scan-all-rules.sh` 因环境限制（bwrap 不可用）无法执行，由 LLM 完成全部规则核对。

---

## Step 1 — 执行队列（产物 A）

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|-----|----|----|----|----|----|----|----|----|----|-----|-----|-----|-----|-----|-----|-----|-----|--------|
| 1 | `my-spring-boot-app/src/test/java/com/example/myapp/services/ItemServiceTest.java` | ItemService 单元测试 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 2 | `my-spring-boot-app/src/test/java/com/example/myapp/services/UserServiceTest.java` | UserService 单元测试 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |

---

## Step 2 — 功能检查（产物 B）

### REQ-1: ItemService 所有方法均被正确测试

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1.1 | `findAll` 返回所有物品 / 空列表 | 需求: `test` | `ItemServiceTest.java` | ✅ | `should_returnAllItems()` 覆盖2条物品；`should_returnEmptyList_when_noItems()` 覆盖空列表 |
| REQ-1.2 | `findById` 存在/不存在 | 需求: `test` | `ItemServiceTest.java` | ✅ | `should_returnItem_when_itemExists()` 存在分支；`should_returnEmpty_when_itemNotExists()` 不存在分支 |
| REQ-1.3 | `findByName` 存在/不存在 | 需求: `test` | `ItemServiceTest.java` | ✅ | `should_returnItem_when_nameExists()` 存在分支；`should_returnEmpty_when_nameNotExists()` 不存在分支 |
| REQ-1.4 | `save` 正常/重复名称/数据冲突 | 需求: `test` | `ItemServiceTest.java` | ✅ | `should_saveItem_when_nameNotDuplicate()` 正常保存；`should_throwException_when_nameAlreadyExistsForNewItem()` 名称重复；`should_throwException_when_dataIntegrityViolation()` DB冲突 |
| REQ-1.5 | `update` 正常/名称冲突/物品不存在/数据冲突 | 需求: `test` | `ItemServiceTest.java` | ✅ | 4个测试覆盖 update 全部4种场景 |
| REQ-1.6 | `deleteById` 存在/不存在 | 需求: `test` | `ItemServiceTest.java` | ✅ | `should_deleteItem_when_itemExists()` 删除成功；`should_throwException_when_itemNotFound()` 不存在抛异常 |
| REQ-1.7 | `searchByKeyword` 有关键词/null/空白 | 需求: `test` | `ItemServiceTest.java` | ✅ | 3个测试覆盖关键词、null、空白字符串三种输入 |
| REQ-1.8 | `searchByKeywordAndUserId` 关键词/无关键词 | 需求: `test` | `ItemServiceTest.java` | ✅ | `should_searchByKeywordAndUserId_when_keywordProvided()` 有关键词；`should_findByUserId_when_keywordIsNull()` 无关键词回退到按用户ID |
| REQ-1.9 | `findByCategory` 有结果/无结果 | 需求: `test` | `ItemServiceTest.java` | ✅ | `should_returnItemsByCategory()` 有结果；`should_returnEmptyList_when_categoryNotFound()` 无结果 |
| REQ-1.10 | `findByUserId` 按用户ID查找 | 需求: `test` | `ItemServiceTest.java` | ✅ | `should_returnItemsByUserId()` 基本路径 |
| REQ-1.11 | `findLowStockItems` 低库存阈值 | 需求: `test` | `ItemServiceTest.java` | ✅ | `should_returnItemsWithQuantityLessThanThreshold()` 验证阈值过滤 |
| REQ-1.12 | `getAllCategories` 有分类/无分类 | 需求: `test` | `ItemServiceTest.java` | ✅ | `should_returnAllCategories()` 有分类列表；`should_returnEmptyList_when_noCategories()` 无分类 |

### REQ-2: UserService 所有方法均被正确测试

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-2.1 | `createUser` 正常/用户名重复/邮箱重复 | 需求: `test` | `UserServiceTest.java` | ✅ | 3个测试覆盖正常创建、用户名已存在、邮箱已注册 |
| REQ-2.2 | `getUserById` 存在/不存在 | 需求: `test` | `UserServiceTest.java` | ✅ | `should_returnUser_when_userExists()` 存在；`should_returnEmpty_when_userNotExists()` 不存在 |
| REQ-2.3 | `getAllUsers` 有用户/无用户 | 需求: `test` | `UserServiceTest.java` | ✅ | `should_returnAllUsers()` 有用户；`should_returnEmptyList_when_noUsers()` 无用户 |
| REQ-2.4 | `updateProfile` 正常/用户名冲突/邮箱冲突/用户不存在 | 需求: `test` | `UserServiceTest.java` | ✅ | 4个测试覆盖全部4种场景 |
| REQ-2.5 | `deleteUser` 存在/不存在 | 需求: `test` | `UserServiceTest.java` | ✅ | `should_deleteUser_when_userExists()` 删除成功；`should_throwException_when_userNotFound()` 不存在 |
| REQ-2.6 | `getOrCreateDefaultUser` 已有用户/无用户创建默认 | 需求: `test` | `UserServiceTest.java` | ✅ | 已有用户返回第一个；无用户时创建默认 admin 用户并验证各字段 |

---

## Step 3 — 可读性检查（产物 C）

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | 文件名符合类名+Test，UTF-8编码，使用空格缩进 |
| A2 | 源文件结构/import 顺序 | ⚠️ | **ItemServiceTest.java**：静态 import 和非静态 import 未分组空行隔离（A2.3）。`L3-L17` 非静态 import 混在一起，`L19-L22` 静态 import 也混在一起，但两组之间无空行分隔。 |
| A3 | 代码样式 | ✅ | K&R大括号、4空格缩进、行宽≤120、运算符空格均符合规范 |
| A4 | 命名规范 | ✅ | 测试类名 `ItemServiceTest`/`UserServiceTest` 符合 A4.7；方法名 lowerCamelCase；包名全小写 |
| A5 | 编码实践 | ✅ | 无重写方法；catch 块非空有异常处理；静态方法调用正确 |
| A6 | 特定元素样式 | ✅ | 修饰符顺序正确；注解每行一个；无 long 字面量问题 |
| A7 | Javadoc 规范 | ✅ | 测试类和方法使用 `@DisplayName` 替代 Javadoc，符合 JUnit5 惯例；无 public 成员需额外 Javadoc |

---

## Step 4 — 可靠性检查（产物 D）

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

**自动化预扫说明**：由于环境限制（bwrap 不可用），`scan-all-rules.sh` 无法执行。以下由 LLM 人工逐条核对。

**B 系列（Blocker → P0）**

| ID | 状态 | 备注（命中写 `path:line`） |
|----|------|--------------------------------------------------|
| B001 | N/A | 无 parse/of 调用 |
| B002 | N/A | 无数组 equals 比较 |
| B003 | N/A | 无 Arrays.fill 调用 |
| B004 | N/A | 无数组 toString 调用 |
| B005 | N/A | 无 Arrays.asList 传入基本类型数组 |
| B006 | ✅ | `assertThat()` 使用 AssertJ，参数顺序正确 |
| B007 | N/A | 无捕获 Throwable/Error 场景 |
| B008 | N/A | 无 Executors 使用 |
| B009 | N/A | 无移位操作 |
| B010 | N/A | 无 BigDecimal 构造 |
| B011 | N/A | 无包装类型 == 比较 |
| B012 | N/A | 无 Calendar 使用 |
| B013 | N/A | 无 Calendar 使用 |
| B014 | N/A | 无集合查询不兼容类型 |
| B015 | N/A | 无 Collection.toArray 调用 |
| B016 | N/A | 无 Comparable 实现 |
| B017 | N/A | 无 this == null 判断 |
| B018 | N/A | 无三目运算数值类型提升 |
| B019 | N/A | 无 Money 类使用 |
| B020 | N/A | 无常量溢出场景 |
| B021 | N/A | 无 Jedis 使用 |
| B022 | N/A | 无 SimpleDateFormat 使用 |
| B023 | N/A | 无 DeadException 模式 |
| B024 | N/A | 无 Thread 使用 |
| B025 | N/A | 无双括号初始化 |
| B026 | N/A | 无 equals null 调用 |
| B027 | N/A | 无 equals 方法定义 |
| B028 | N/A | 无 DateUtil 使用 |
| B029 | N/A | 无 setter 方法 |
| B030 | N/A | 无浮点数 == 比较 |
| B031 | N/A | 无 String.format 调用 |
| B032 | N/A | 无注解 getClass 调用 |
| B033 | N/A | 无 Unsafe 操作 |
| B034 | N/A | 无 Hashtable 使用 |
| B035 | N/A | 无自引用二元运算 |
| B036 | N/A | 无 IdentityHashMap 使用 |
| B037 | N/A | 无可变参数条件表达式 |
| B038 | N/A | 无递归调用 |
| B039 | N/A | 无 indexOf 调用 |
| B040 | N/A | 无 isInstance 调用 |
| B041 | N/A | 无 JDBC 连接操作 |
| B042 | N/A | JUnit5 测试，非 JUnit3 |
| B043 | N/A | `@Nested` 内部类在 JUnit5 中会被正确执行 |
| B044 | N/A | 仅使用 JUnit5 注解 |
| B045 | N/A | 无 synchronized 加锁 |
| B046 | N/A | 无循环条件 |
| B047 | N/A | 无 compare 方法 |
| B048 | N/A | 无 Math.round 调用 |
| B049 | N/A | 无日期格式字符串 |
| B050 | N/A | 无日期格式字符串 |
| B051 | N/A | 无 Boolean.getBoolean 调用 |
| B052 | N/A | 无日期格式字符串 |
| B053 | N/A | 无 try-catch 期望抛异常的测试（使用 `assertThatThrownBy` 方式） |
| B054 | N/A | 无 EqualsTester 使用 |
| B055 | ✅ | Mockito 用法正确：`when().thenReturn()` 模式正确；`verify()` 调用正确（`verify(mock,times(1)).method()`） |
| B056 | N/A | 无 Arrays.asList 返回上的 add/remove |
| B057 | N/A | 无增强 for 循环修改集合 |
| B058 | N/A | 无集合自身作为参数 |
| B059 | N/A | 无 Collections.nCopies 使用 |
| B060 | N/A | 无三目运算自动拆箱 |
| B061 | N/A | 无 sun.misc.BASE64Encoder 使用 |
| B062 | N/A | 无 ClassLoader 类型转换 |
| B063 | N/A | 无 javax.xml 使用 |
| B064 | N/A | 无 Optional == 比较 |
| B065 | N/A | 无 Pojo 自赋值 |
| B066 | N/A | 无 Math.random 使用 |
| B067 | N/A | 无 Random.nextInt 使用 |
| B068 | N/A | 无自赋值 |
| B069 | N/A | 无 compareTo 实现 |
| B070 | N/A | 无 equals 实现 |
| B071 | N/A | 无 size() >= 0 判断 |
| B072 | N/A | 无 Stream.toString 调用 |
| B073 | N/A | 无 StringBuilder 构造 |
| B074 | N/A | 无 substring(0) 调用 |
| B075 | N/A | 无 for 循环 |
| B076 | N/A | 无 @Transactional 注解 |
| B077 | N/A | 无捕获 Throwable 测试场景 |
| B078 | N/A | 无 `assertThat(x).isEqualTo(x)` 自相等 |
| B079 | ✅ | `@Mock` 字段未显式赋值，正确 |
| B080 | ✅ | 每个测试方法均包含断言（assertThat/verify/assertThatThrownBy） |
| B081 | N/A | 无集合原地修改 |

**M 系列（Major → P1）**

| ID | 状态 | 备注 |
|----|------|------|
| M001 | N/A | 无重复条件判断 |
| M002 | N/A | 无 instanceof 使用 |
| M003 | N/A | 无包装类构造器 |
| M004 | N/A | 测试代码中无异常捕获打印栈 |
| M005 | N/A | 测试类非内部类 |
| M006 | N/A | 无编译期布尔常量 |
| M007 | N/A | 无空 catch 块 |
| M008 | N/A | 无 equals/hashCode 实现 |
| M009 | N/A | 无 equals 调用 |
| M010 | N/A | 无位运算 |
| M011 | N/A | 无 switch 语句 |
| M012 | N/A | 无 finally 块 |
| M013 | N/A | 无类型转换 |
| M014 | N/A | 无枚举 getClass |
| M015 | N/A | 无继承关系 |
| M016 | N/A | 无 LocalDateTime.now 调用（在测试 helper 中有 `LocalDateTime.now()`，但测试代码不涉及时区敏感业务） |
| M017 | N/A | 所有测试方法均有 `@Test` 注解 |
| M018 | N/A | 无 Lock 使用 |
| M019 | N/A | 无枚举 switch |
| M020 | N/A | 无重写方法 |
| M021 | N/A | 无 equals 方法定义 |
| M022 | N/A | 无 Optional.of(null) 调用 |
| M023 | N/A | 无 toString 调用 |
| M024 | N/A | 无 Optional.get 在未确认时调用 |
| M025 | N/A | 测试类非 final |
| M026 | ✅ | `@Mock` 字段未声明为 static，正确 |
| M027 | N/A | 无 ThreadLocal 使用 |

**I 系列（Info → P2）**

| ID | 状态 | 备注 |
|----|------|------|
| I001 | ✅ | 异常测试使用 `assertThatThrownBy` 并验证消息内容，优于仅检测类型 |
| I002 | N/A | 无 @DoNotMock 注解 |
| I003 | N/A | 无 @AutoValue 注解 |
| I004 | N/A | 使用 `LocalDateTime` 而非 `java.util.Date` |
| I005 | N/A | 使用 JUnit5 |
| I006 | N/A | 使用 `@BeforeEach` 注解 |
| I007 | N/A | 使用 `@AfterEach` 注解 |
| I008 | N/A | 无 DataProvider 使用 |
| I009 | N/A | 统计类规则 |
| I010 | N/A | 使用 `@ExtendWith(MockitoExtension.class)`，适合纯单元测试 |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1–G1.4 | N/A | 测试代码无并发控制逻辑 |
| G2.1–G2.3 | N/A | 测试代码无幂等逻辑 |
| G3.1–G3.2 | N/A | 测试代码无事务控制 |
| G4.1–G4.3 | N/A | 测试代码无 SQL 操作 |
| G5.1 | N/A | 测试代码无 MQ 逻辑 |
| G6.1–G6.2 | N/A | 测试代码无缓存操作 |
| G7.1–G7.2 | N/A | 测试代码无调度任务 |
| G8.1–G8.6 | N/A | 测试代码无 I/O 流、连接、线程池等 |
| G9.1–G9.3 | N/A | 测试代码无外部网络调用 |
| G10.1–G10.2 | N/A | 测试代码无接口契约逻辑 |
| G11.1 | ✅ | 所有新增逻辑均有对应单测，每个单测均包含断言 |
| G11.2 | ⚠️ | **ItemServiceTest.java**：`save` 方法中 `item.getId() != null`（更新场景）的 save 分支未覆盖；`findByQuantityLessThan` 的边界值（如阈值=0或负数）未覆盖；`update` 方法中 `item.getName().equals(itemDetails.getName())` 名称未变更分支虽覆盖，但仅验证了正常路径。**UserServiceTest.java**：`createUser` 方法中用户名为 null/空字符串、邮箱格式不合法等边界未覆盖 |
| G11.3 | N/A | 测试代码不涉及入参校验（入参由 Mock 控制） |
| G11.4 | N/A | 测试代码无数值运算 |
| G12.1–G12.2 | N/A | 测试代码不涉及资损 |
| G13.1 | N/A | 测试代码无日志输出 |
| G14.1–G14.4 | N/A | 测试代码不涉及国际化和时区 |
| G15.1–G15.3 | N/A | 测试代码不涉及 DB 变更 |
| G16.1–G16.4 | N/A | 测试代码不涉及日志和监控 |
| G17.1–G17.3 | N/A | 测试代码不涉及应急开关 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1–S1.3 | N/A | 测试代码无 SQL 操作 |
| S2.1–S2.3 | N/A | 测试代码无 XSS 风险 |
| S3.1–S3.3 | N/A | 测试代码无外部 URL 请求 |
| S4.1–S4.2 | N/A | 测试代码无命令执行 |
| S5.1–S5.2 | N/A | 测试代码无 XML 解析 |
| S6.1–S6.3 | N/A | 测试代码无反序列化 |
| S7.1–S7.3 | N/A | 测试代码无文件上传 |
| S8.1–S8.4 | N/A | 测试代码无访问控制逻辑 |
| S9.1–S9.4 | N/A | 测试代码无密钥或敏感数据 |
| S10.1–S10.3 | N/A | 测试代码无 CSRF/CORS 逻辑 |

---

## Step 5 — 自定义扩展检查（产物 E）

### 5.1 自定义扩展（`customized-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | N/A | 示例项，测试代码不涉及 Controller 入参校验 |
| U1.2 | N/A | 未启用 |
| U1.3 | N/A | 未启用 |
| U2.1 | N/A | 未启用 |
| U2.2 | N/A | 未启用 |
| U2.3 | N/A | 未启用 |

**整体结论**：`N/A(未启用自定义规则)` — 该清单仅包含示例项，团队未配置自定义扩展规则。

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、**S1–S10 / G1–G17** 各列均非 `⬜`（跳过文件除外）；
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`（允许 `N/A`，但有原因）
- [x] Step 5 全部 U* ID 均非 `⬜`（允许 `N/A(未启用自定义规则)`）
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`