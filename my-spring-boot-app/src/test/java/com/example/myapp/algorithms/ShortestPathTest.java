package com.example.myapp.algorithms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShortestPathTest {

    // ── Dijkstra 正向测试 ──

    @Test
    @DisplayName("Dijkstra: 基本有向加权图")
    void dijkstraBasicDirected() {
        @SuppressWarnings("unchecked")
        List<ShortestPath.Edge>[] graph = new List[4];
        for (int i = 0; i < 4; i++) graph[i] = new ArrayList<>();
        // 0 -> 1 (2), 0 -> 2 (6), 1 -> 2 (3), 1 -> 3 (1), 2 -> 3 (5)
        graph[0].add(new ShortestPath.Edge(1, 2));
        graph[0].add(new ShortestPath.Edge(2, 6));
        graph[1].add(new ShortestPath.Edge(2, 3));
        graph[1].add(new ShortestPath.Edge(3, 1));
        graph[2].add(new ShortestPath.Edge(3, 5));

        ShortestPath.Result result = ShortestPath.dijkstra(graph, 0);

        assertArrayEquals(new int[]{0, 2, 5, 3}, result.distances());
        assertArrayEquals(new int[]{-1, 0, 1, 1}, result.predecessors());
    }

    @Test
    @DisplayName("Dijkstra: 单节点图")
    void dijkstraSingleNode() {
        @SuppressWarnings("unchecked")
        List<ShortestPath.Edge>[] graph = new List[1];
        graph[0] = new ArrayList<>();

        ShortestPath.Result result = ShortestPath.dijkstra(graph, 0);

        assertArrayEquals(new int[]{0}, result.distances());
        assertArrayEquals(new int[]{-1}, result.predecessors());
    }

    @Test
    @DisplayName("Dijkstra: 不连通图，部分节点不可达")
    void dijkstraDisconnected() {
        @SuppressWarnings("unchecked")
        List<ShortestPath.Edge>[] graph = new List[3];
        for (int i = 0; i < 3; i++) graph[i] = new ArrayList<>();
        graph[0].add(new ShortestPath.Edge(1, 3));
        // 节点 2 不可达

        ShortestPath.Result result = ShortestPath.dijkstra(graph, 0);

        assertEquals(0, result.distances()[0]);
        assertEquals(3, result.distances()[1]);
        assertEquals(Integer.MAX_VALUE, result.distances()[2]);
        assertFalse(result.reachable(2));
    }

    // ── Dijkstra 边界/异常测试 ──

    @Test
    @DisplayName("Dijkstra: graph 为 null 抛异常")
    void dijkstraNullGraph() {
        assertThrows(IllegalArgumentException.class, () -> ShortestPath.dijkstra(null, 0));
    }

    @Test
    @DisplayName("Dijkstra: source 越界抛异常")
    void dijkstraSourceOutOfBounds() {
        @SuppressWarnings("unchecked")
        List<ShortestPath.Edge>[] graph = new List[3];
        for (int i = 0; i < 3; i++) graph[i] = new ArrayList<>();

        assertThrows(IllegalArgumentException.class, () -> ShortestPath.dijkstra(graph, -1));
        assertThrows(IllegalArgumentException.class, () -> ShortestPath.dijkstra(graph, 3));
    }

    @Test
    @DisplayName("Dijkstra: Edge 负权抛异常")
    void edgeNegativeWeight() {
        assertThrows(IllegalArgumentException.class, () -> new ShortestPath.Edge(1, -1));
    }

    @Test
    @DisplayName("Dijkstra: 空图")
    void dijkstraEmptyGraph() {
        @SuppressWarnings("unchecked")
        List<ShortestPath.Edge>[] graph = new List[0];

        ShortestPath.Result result = ShortestPath.dijkstra(graph, 0);
        assertEquals(0, result.distances().length);
    }

    // ── 路径还原测试 ──

    @Test
    @DisplayName("路径还原: 正常可达路径")
    void reconstructPathReachable() {
        @SuppressWarnings("unchecked")
        List<ShortestPath.Edge>[] graph = new List[4];
        for (int i = 0; i < 4; i++) graph[i] = new ArrayList<>();
        graph[0].add(new ShortestPath.Edge(1, 2));
        graph[1].add(new ShortestPath.Edge(2, 3));
        graph[2].add(new ShortestPath.Edge(3, 1));

        ShortestPath.Result result = ShortestPath.dijkstra(graph, 0);
        List<Integer> path = result.reconstructPath(0, 3);

        assertEquals(Arrays.asList(0, 1, 2, 3), path);
    }

    @Test
    @DisplayName("路径还原: 不可达节点返回空列表")
    void reconstructPathUnreachable() {
        @SuppressWarnings("unchecked")
        List<ShortestPath.Edge>[] graph = new List[3];
        for (int i = 0; i < 3; i++) graph[i] = new ArrayList<>();
        graph[0].add(new ShortestPath.Edge(1, 2));

        ShortestPath.Result result = ShortestPath.dijkstra(graph, 0);
        List<Integer> path = result.reconstructPath(0, 2);

        assertTrue(path.isEmpty());
    }

    // ── BFS 无权图测试 ──

    @Test
    @DisplayName("BFS: 基本无权图")
    void bfsBasic() {
        @SuppressWarnings("unchecked")
        List<Integer>[] graph = new List[5];
        for (int i = 0; i < 5; i++) graph[i] = new ArrayList<>();
        graph[0].addAll(Arrays.asList(1, 2));
        graph[1].addAll(Arrays.asList(3));
        graph[2].addAll(Arrays.asList(3, 4));
        graph[3].addAll(Arrays.asList(4));

        ShortestPath.Result result = ShortestPath.bfs(graph, 0);

        assertArrayEquals(new int[]{0, 1, 1, 2, 2}, result.distances());
    }

    @Test
    @DisplayName("BFS: graph 为 null 抛异常")
    void bfsNullGraph() {
        assertThrows(IllegalArgumentException.class, () -> ShortestPath.bfs(null, 0));
    }

    @Test
    @DisplayName("BFS: source 越界抛异常")
    void bfsSourceOutOfBounds() {
        @SuppressWarnings("unchecked")
        List<Integer>[] graph = new List[2];
        graph[0] = new ArrayList<>();
        graph[1] = new ArrayList<>();

        assertThrows(IllegalArgumentException.class, () -> ShortestPath.bfs(graph, 2));
    }

    @Test
    @DisplayName("BFS: 空图")
    void bfsEmptyGraph() {
        @SuppressWarnings("unchecked")
        List<Integer>[] graph = new List[0];

        ShortestPath.Result result = ShortestPath.bfs(graph, 0);
        assertEquals(0, result.distances().length);
    }
}