# 代码评审报告 — 最短路径算法

> **需求**: 湘阳测试：写一个最短路径算法  
> **评审日期**: 2026-07-16  
> **技术栈**: Java 17+, Spring Boot, JUnit 5  
> **算法**: Dijkstra（优先队列优化，O((V+E) log V)）  
> **验证方式**: 静态代码审查（Maven 构建环境不可用，已触发降级协议）

---

## 1. 评审范围

| 文件 | 路径 | 行数 | 类型 |
|------|------|------|------|
| Graph.java | `my-spring-boot-app/src/main/java/com/example/myapp/models/Graph.java` | 69 | 领域模型 |
| ShortestPathService.java | `my-spring-boot-app/src/main/java/com/example/myapp/services/ShortestPathService.java` | 97 | 核心服务 |
| ShortestPathServiceTest.java | `my-spring-boot-app/src/test/java/com/example/myapp/ShortestPathServiceTest.java` | 219 | 单元测试 |
| IMPLEMENTATION_PLAN.md | `my-spring-boot-app/IMPLEMENTATION_PLAN.md` | 166 | 实施计划 |

---

## 2. 评审总结

| 维度 | 评分 | 说明 |
|------|------|------|
| 算法正确性 | ⭐⭐⭐⭐ | Dijkstra 实现正确，使用 long 防溢出 |
| 代码质量 | ⭐⭐⭐⭐ | 结构清晰，命名规范，注释充分 |
| 异常处理 | ⭐⭐⭐ | 缺少 source 参数校验和 null 检查 |
| 测试覆盖 | ⭐⭐⭐⭐⭐ | 15 个测试用例，覆盖正常/边界/异常场景 |
| 文档完整性 | ⭐⭐⭐⭐⭐ | 实施计划详尽，Javadoc 完整 |

**Blocker 数量**: 2  
**Major 数量**: 2  
**Minor 数量**: 3

---

## 3. Blocker 问题

### B1. ShortestPathService.computeShortestPaths 缺少 source 参数校验

- **文件**: `ShortestPathService.java:52-58`
- **严重级别**: 🔴 Blocker
- **描述**: `computeShortestPaths(Graph graph, int source)` 方法未对 `source` 参数进行边界校验。当 `source < 0` 或 `source >= vertices` 时，第 58 行 `distances[source] = 0` 将抛出 `ArrayIndexOutOfBoundsException`，导致运行时崩溃。
- **影响**: 调用方传入非法 source 节点时服务直接崩溃，无友好错误提示。
- **修复建议**:
```java
public ShortestPathResult computeShortestPaths(Graph graph, int source) {
    if (graph == null) {
        throw new IllegalArgumentException("graph 不能为 null");
    }
    int vertices = graph.getVertices();
    if (source < 0 || source >= vertices) {
        throw new IllegalArgumentException(
            "source 节点越界: " + source + "，有效范围 [0, " + (vertices - 1) + "]");
    }
    // ... 原有逻辑
}
```

### B2. ShortestPathService.computeShortestPaths 缺少 graph null 检查

- **文件**: `ShortestPathService.java:52-53`
- **严重级别**: 🔴 Blocker
- **描述**: 当 `graph` 参数为 `null` 时，第 53 行 `graph.getVertices()` 将抛出 `NullPointerException`。
- **影响**: 调用方传入 null 时无明确错误信息。
- **修复建议**: 在方法开头添加 null 检查（见 B1 修复代码）。

---

## 4. Major 问题

### M1. getNeighbors 返回的不可变视图可能产生 ConcurrentModificationException

- **文件**: `Graph.java:59-62`
- **严重级别**: 🟠 Major
- **描述**: `getNeighbors` 返回 `Collections.unmodifiableMap(adjacencyList.get(vertex))`，该不可变 Map 是对内部 HashMap 的实时视图。如果在迭代遍历邻居时，其他线程通过 `addEdge` 修改了该节点的邻接表，将抛出 `ConcurrentModificationException`。
- **影响**: 多线程并发场景下可能崩溃。
- **修复建议**: 考虑返回 `new HashMap<>(adjacencyList.get(vertex))` 做防御性拷贝，或在类文档中明确标注"非线程安全"。

### M2. ShortestPathResult 直接暴露内部数组引用

- **文件**: `ShortestPathService.java:18-23`
- **严重级别**: 🟠 Major
- **描述**: `ShortestPathResult` record 的 `distances` 和 `predecessors` 数组直接通过 accessor 方法暴露给调用方。调用方可以修改这些数组内容，破坏结果的不变性。
- **影响**: 调用方误修改数组后可能导致后续 `getPath` 行为异常。
- **修复建议**: 在 `getPath` 等方法中已有防御逻辑，但 record accessor 返回原始引用。建议在构造时做防御性拷贝：
```java
public ShortestPathResult(int[] distances, int[] predecessors) {
    this.distances = distances.clone();
    this.predecessors = predecessors.clone();
}
```

---

## 5. Minor 问题

### m1. 缺少 equals/hashCode/toString 实现

- **文件**: `Graph.java`, `ShortestPathService.java`
- **严重级别**: 🟡 Minor
- **描述**: `Graph` 类和 `ShortestPathResult` record 均未重写 `equals`/`hashCode`/`toString`。`ShortestPathResult` 作为 record 自动获得这些方法，但使用的是数组引用比较（`Arrays.equals` 未被自动使用）。
- **建议**: 为 `ShortestPathResult` 显式声明 compact constructor 以使用 `Arrays.equals` 和 `Arrays.hashCode`。

### m2. 测试用例缺少 source 越界和 null graph 场景

- **文件**: `ShortestPathServiceTest.java`
- **严重级别**: 🟡 Minor
- **描述**: 测试覆盖了 Graph 构造异常和 getNeighbors 越界，但缺少对 `computeShortestPaths` 的 source 越界测试和 null graph 测试。
- **建议**: 添加对应测试用例，与修复 B1/B2 同步进行。

### m3. 测试类缺少 public 修饰符

- **文件**: `ShortestPathServiceTest.java:15`
- **严重级别**: 🟡 Minor
- **描述**: 测试类声明为 `class ShortestPathServiceTest`（包级私有），而非 `public class`。JUnit 5 支持包级私有，但不符合 Spring Boot 项目惯例。
- **建议**: 添加 `public` 修饰符以保持一致性。

---

## 6. 亮点总结

1. **Dijkstra 算法实现正确**: 使用优先队列优化的标准实现，`long newDist` 防止整数溢出。
2. **输入验证**: `Graph` 类对节点数、边权重、节点编号均做了完整的参数校验。
3. **不可变设计**: `getNeighbors` 返回不可变视图，`ShortestPathResult` 使用 record 类型。
4. **测试覆盖全面**: 15 个测试用例覆盖了正常图、边界条件（单节点、不可达、零权重、稠密图）、路径重建、异常场景。
5. **文档完整**: 实施计划详尽（含需求澄清、设计决策、测试策略），Javadoc 注释充分。
6. **代码结构清晰**: 分层合理（models/services），方法职责单一，命名规范。

---

## 7. 降级说明

> **[降级说明]** 构建环境 Maven 不可用（`mvn: not found`），无法执行编译和单元测试。已切换为静态代码审查，审查范围覆盖：
> - Dijkstra 算法逻辑正确性（包括 visited 标记、优先队列松弛、整数溢出防护）
> - 参数校验完整性（source 越界、null 检查缺失）
> - 测试用例逻辑覆盖（预期距离值手动验证通过）
> - 线程安全性和 API 契约审查

---

## 8. 评审结论

代码整体质量良好，Dijkstra 算法实现正确，测试覆盖充分。**2 个 Blocker 问题**需要在合入前修复：
1. 添加 `source` 参数边界校验
2. 添加 `graph` null 检查

修复上述问题后，建议同时处理 Major 问题中的线程安全说明和防御性拷贝。