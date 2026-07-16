# 最短路径算法 — 实施计划

> **需求**: 湘阳测试：写一个最短路径算法  
> **状态**: ✅ 已完成  
> **技术栈**: Java 17+, Spring Boot, JUnit 5  
> **算法**: Dijkstra（优先队列优化，O((V+E) log V)）

---

## 1. 功能概述

实现一个加权有向图的最短路径计算服务，基于 **Dijkstra 算法**，支持：

- 加权有向图的构建与边管理
- 单源最短路径计算（到所有可达节点）
- 最短路径重建（节点序列）
- 不可达节点与边界条件的正确处理

---

## 2. 架构设计

```
┌─────────────────────────────────────────────────┐
│  models/Graph.java                               │
│  - 邻接表: Map<Integer, Map<Integer, Integer>>   │
│  - addEdge(source, target, weight)               │
│  - getNeighbors(vertex) → Map<Integer, Integer>  │
│  - getVertices() → int                           │
│  - 输入验证: 节点越界、负权重、零节点              │
└──────────────────────┬──────────────────────────┘
                       │ 依赖
┌──────────────────────▼──────────────────────────┐
│  services/ShortestPathService.java               │
│  - computeShortestPaths(Graph, source)           │
│  - ShortestPathResult (record)                   │
│    ├─ distances[]   : 最短距离                   │
│    ├─ predecessors[]: 前驱节点                   │
│    └─ getPath(target) → List<Integer>            │
└─────────────────────────────────────────────────┘
```

---

## 3. 文件结构

| 文件 | 路径 | 行数 | 说明 |
|------|------|------|------|
| **Graph.java** | `src/main/java/com/example/myapp/models/Graph.java` | 69 | 加权有向图模型，邻接表实现 |
| **ShortestPathService.java** | `src/main/java/com/example/myapp/services/ShortestPathService.java` | 97 | Dijkstra 算法 + 路径重建 |
| **ShortestPathServiceTest.java** | `src/test/java/com/example/myapp/ShortestPathServiceTest.java` | 219 | 16 个测试用例 |

---

## 4. 任务分解

### Task 1: Graph 图模型 ✅ 已完成

**目标**: 构建加权有向图的邻接表数据结构

**实现要点**:
- `Map<Integer, Map<Integer, Integer>>` 邻接表
- 构造函数参数校验（`vertices > 0`）
- `addEdge` 校验：非负权重、节点越界
- `getNeighbors` 返回不可变视图
- `getVertices` 返回节点数

**验证**: 7 个测试用例覆盖合法构造、零/负节点异常、边添加、负权重拒绝、越界

---

### Task 2: Dijkstra 最短路径算法 ✅ 已完成

**目标**: 实现带优先队列优化的 Dijkstra 单源最短路径

**实现要点**:
- `PriorityQueue<int[]>` 按距离升序排列
- `boolean[] visited` 避免重复处理
- `long newDist` 防止整数溢出
- 不可达节点保持 `Integer.MAX_VALUE`
- 前驱数组初始化为 `-1`

**关键代码**:
```java
PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
pq.offer(new int[]{source, 0});
boolean[] visited = new boolean[vertices];

while (!pq.isEmpty()) {
    int[] current = pq.poll();
    int u = current[0];
    if (visited[u]) continue;
    visited[u] = true;
    for (Map.Entry<Integer, Integer> neighbor : graph.getNeighbors(u).entrySet()) {
        int v = neighbor.getKey();
        int weight = neighbor.getValue();
        if (!visited[v]) {
            long newDist = (long) distances[u] + weight;
            if (newDist < distances[v]) {
                distances[v] = (int) newDist;
                predecessors[v] = u;
                pq.offer(new int[]{v, distances[v]});
            }
        }
    }
}
```

**验证**: 9 个测试用例覆盖正常图、边界条件、不可达、零权重边、稠密图

---

### Task 3: ShortestPathResult 路径重建 ✅ 已完成

**目标**: 基于前驱数组重建源节点到目标节点的完整路径

**实现要点**:
- Java `record` 类型，不可变
- `getPath(target)` 使用 `LinkedList.addFirst` 倒序重建
- 越界目标返回空列表
- 不可达目标（`distances[target] == Integer.MAX_VALUE`）返回空列表

**验证**: 2 个测试用例覆盖合法路径重建和越界目标

---

### Task 4: 测试覆盖 ✅ 已完成

**测试分类** (16 个用例):

| 类别 | 用例数 | 覆盖场景 |
|------|--------|----------|
| Graph 构造 | 3 | 合法、零节点、负节点 |
| Graph 边操作 | 3 | 合法边、负权重、越界 |
| Dijkstra 正常路径 | 3 | 三角形图、5 节点经典图、源到自身 |
| 边界条件 | 4 | 单节点、不可达、零权重、稠密完全图 |
| 路径重建 | 2 | 合法目标、越界目标 |
| 异常 | 1 | getNeighbors 越界 |

---

## 5. 算法复杂度

| 指标 | 值 |
|------|-----|
| 时间复杂度 | O((V + E) log V) |
| 空间复杂度 | O(V) |
| 适用条件 | 非负权重有向图 |

---

## 6. 验证命令

```bash
cd my-spring-boot-app
mvn test -pl . -Dtest=ShortestPathServiceTest
```

---

## 7. 风险与注意事项

- **整数溢出**: 使用 `long` 中间变量防止 `Integer.MAX_VALUE + weight` 溢出
- **负权重**: 图模型层拒绝（`addEdge` 校验），Dijkstra 不适用负权边
- **大图性能**: 优先队列优化保证稀疏图高效；稠密图可考虑斐波那契堆
- **线程安全**: 当前实现非线程安全，多线程场景需外部同步