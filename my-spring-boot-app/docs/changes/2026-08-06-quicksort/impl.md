# 快速排序算法 - 编码实现报告

> 技能：dtazziboot-java-coding-standards
> 阶段：编码实现
> 模块：quicksort
> 日期：2026-08-06

## 1. 需求描述

实现一个快速排序算法。

## 2. 模块职责

对整型数组进行原地升序排序的纯算法工具类，不依赖 Spring 容器，归入 `utils` 工具包。

## 3. 关键类说明

### QuickSort（工具类）

- 算法：快速排序，Lomuto 分区方案，pivot 取分区区间末位元素
- 复杂度：平均时间 O(n log n)，空间 O(log n)
- 线程安全：无状态静态方法，天然线程安全

### 关键方法

| 方法 | 可见性 | 说明 |
|------|:------:|------|
| `sort(int[])` | public static | 入口方法，null 校验 + 递归排序 |
| `quickSort(int[], int, int)` | private static | 递归分区 |
| `partition(int[], int, int)` | private static | Lomuto 分区，返回 pivot 最终位置 |
| `swap(int[], int, int)` | private static | 元素交换 |

## 4. 依赖关系

- 仅依赖 JDK `java.util.Objects`（null 校验）
- 不依赖 Spring/JPA/业务模块
- 测试依赖 JUnit 5 + AssertJ（spring-boot-starter-test 自带）

## 5. 五阶段执行记录

| 阶段 | 状态 | 说明 |
|------|:----:|------|
| READ | ✅ | 读取 pom.xml、现有分层结构、命名/单测规范 |
| TEST | ✅ | 生成 8 个单测用例（AAA 模式），先于实现 |
| IMPL | ✅ | 实现 QuickSort 工具类 |
| CHECK | ✅ | L1 静态检查全通过；L2 动态验证因环境无 mvn/javac 跳过 |
| DOCS | ✅ | 本报告 |

## 6. 单元测试用例

| 方法 | 测试场景 | 状态 |
|------|----------|:----:|
| should_sortArray_when_unsortedArray | 无序数组升序排序 | ✅ |
| should_keepArray_when_alreadySorted | 已升序保持不变 | ✅ |
| should_sortArray_when_reverseSorted | 逆序转升序 | ✅ |
| should_sortArray_when_hasDuplicates | 含重复元素 | ✅ |
| should_keepArray_when_allSameElements | 全部相同元素 | ✅ |
| should_keepArray_when_singleElement | 单元素 | ✅ |
| should_notThrow_when_emptyArray | 空数组不异常 | ✅ |
| should_throwNpe_when_nullArray | null 抛 NPE | ✅ |

## 7. 变更文件清单

| 文件 | 操作 | 说明 |
|------|:----:|------|
| `src/main/java/com/example/myapp/utils/QuickSort.java` | 新增 | 快速排序实现 |
| `src/test/java/com/example/myapp/utils/QuickSortTest.java` | 新增 | 单元测试 |

## 8. L1 静态检查清单

| 检查项 | 规范要求 | 符合情况 |
|--------|----------|:--------:|
| 命名规范 | 类名大驼峰、方法名小驼峰、常量全大写 | ✅ |
| 工具类构造 | 工具类隐藏构造器 | ✅ |
| 空值检查 | 参数校验、快速失败 | ✅ |
| 魔法值禁止 | 无未定义常量 | ✅ |
| 注释规范 | 类/方法使用 Javadoc | ✅ |
| 控制语句 | if/for 大括号完整 | ✅ |
| 异常处理 | NPE 带明确 message | ✅ |
| 单测规范 | AAA 模式 + should_xxx_when_yyy | ✅ |
| 单测覆盖 | 正常/边界/异常路径 | ✅ |

## 9. L2 动态验证

[降级说明] 运行环境无 `mvn`、无 `mvnw` wrapper、`javac`/`java` 不在 PATH，属环境问题而非代码问题，依降级协议转为静态审查。

| 验证项 | 状态 | 说明 |
|--------|:----:|------|
| 编译验证 | ⚠️ 跳过 | 环境无 mvn/javac |
| 单测验证 | ⚠️ 跳过 | 同上 |

**待人工验证命令**：

```bash
cd my-spring-boot-app
mvn compile -DskipTests
mvn test -Dtest=QuickSortTest
```

## 10. 残留风险

1. 动态编译/单测未在 CI 环境验证，需人工本地执行确认
2. Lomuto 分区在已排序/逆序输入下退化为 O(n²)，若后续有大规模排序需求可改用三数取中或随机化 pivot
