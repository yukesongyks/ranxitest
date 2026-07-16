# Dijkstra 最短路径算法 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在 Spring Boot 项目中实现 Dijkstra 最短路径算法，包含核心算法类与完整单元测试。

**Architecture:** 采用纯算法层实现，不依赖 Spring 容器。核心类 `DijkstraAlgorithm` 提供静态方法 `shortestPath`，接受邻接矩阵/邻接表形式的图输入，返回最短路径距离数组及路径追踪。测试覆盖正常图、单节点图、不连通图、负权边（预期抛异常）等边界场景。

**Tech Stack:** Java 17+, JUnit 5, Maven

---

## 范围检查

需求仅涉及单一算法实现，无需拆分为多个子系统计划。一个计划即可产出可独立测试的软件。

---

## Task 1: DijkstraAlgorithm 核心实现 + 单元测试

**Files:**
- Create: `my-spring-boot-app/src/main/java/com/example/dijkstra/DijkstraAlgorithm.java`
- Create: `my-spring-boot-app/src/test/java/com/example/dijkstra/DijkstraAlgorithmTest.java`

**Interfaces:**
- Consumes: 无（独立任务，无上游依赖）
- Produces:
  - `DijkstraAlgorithm.shortestPath(int[][] graph, int source)` → `DijkstraResult`
  - `DijkstraResult` record: `int[] distances`, `int[] previous` (前驱节点数组，用于路径重建)
  - 支持邻接矩阵输入，`Integer.MAX_VALUE` 表示无边

- [ ] **Step 1: 编写失败测试**

```java
@Test
void testSimpleGraph() {
    int[][] graph = {
        {0, 4, 0, 0},
        {4, 0, 8, 0},
        {0, 8, 0, 7},
        {0, 0, 7, 0}
    };
    DijkstraResult result = DijkstraAlgorithm.shortestPath(graph, 0);
    assertArrayEquals(new int[]{0, 4, 12, 19}, result.distances());
}
```

- [ ] **Step 2: 运行测试确认失败**

Run: `mvn test -pl my-spring-boot-app -Dtest=DijkstraAlgorithmTest -DfailIfNoTests=false`
Expected: FAIL — 类未定义

- [ ] **Step 3: 编写最小实现**

```java
public record DijkstraResult(int[] distances, int[] previous) {}

public class DijkstraAlgorithm {
    public static DijkstraResult shortestPath(int[][] graph, int source) {
        int n = graph.length;
        int[] dist = new int[n];
        int[] prev = new int[n];
        boolean[] visited = new boolean[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);
        dist[source] = 0;

        for (int i = 0; i < n; i++) {
            int u = -1;
            for (int j = 0; j < n; j++) {
                if (!visited[j] && (u == -1 || dist[j] < dist[u])) {
                    u = j;
                }
            }
            if (dist[u] == Integer.MAX_VALUE) break;
            visited[u] = true;
            for (int v = 0; v < n; v++) {
                if (graph[u][v] != 0 && !visited[v]) {
                    long newDist = (long) dist[u] + graph[u][v];
                    if (newDist < dist[v]) {
                        dist[v] = (int) newDist;
                        prev[v] = u;
                    }
                }
            }
        }
        return new DijkstraResult(dist, prev);
    }
}
```

- [ ] **Step 4: 运行测试确认通过**

Run: `mvn test -pl my-spring-boot-app -Dtest=DijkstraAlgorithmTest -DfailIfNoTests=false`
Expected: PASS

- [ ] **Step 5: 补充边界测试用例**

测试覆盖：
- 单节点图 → 距离为 `[0]`
- 不连通图 → 不可达节点距离为 `Integer.MAX_VALUE`
- 多源场景验证不同起点
- 路径重建验证（通过 `previous` 数组回溯）

---

## 验证清单

| 验证项 | 命令 | 预期 |
|--------|------|------|
| 编译通过 | `mvn compile -pl my-spring-boot-app` | BUILD SUCCESS |
| 测试通过 | `mvn test -pl my-spring-boot-app -Dtest=DijkstraAlgorithmTest` | Tests run: 5+, Failures: 0 |