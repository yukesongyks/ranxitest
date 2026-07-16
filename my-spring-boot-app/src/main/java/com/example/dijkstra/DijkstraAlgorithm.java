package com.example.dijkstra;

import java.util.Arrays;

/**
 * Dijkstra 最短路径算法实现。
 * 使用邻接矩阵表示图，0 表示无边（或自环），正值表示边权。
 * 不支持负权边 —— 若检测到负权边将抛出 IllegalArgumentException。
 */
public class DijkstraAlgorithm {

    /**
     * 计算从 source 出发到所有节点的最短路径。
     *
     * @param graph  邻接矩阵，graph[i][j] 表示 i→j 的边权，0 表示无边
     * @param source 起点索引
     * @return DijkstraResult 包含 distances 和 previous 数组
     * @throws IllegalArgumentException 若检测到负权边
     */
    public static DijkstraResult shortestPath(int[][] graph, int source) {
        int n = graph.length;
        if (n == 0) {
            return new DijkstraResult(new int[0], new int[0]);
        }

        // 检测负权边
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (graph[i][j] < 0) {
                    throw new IllegalArgumentException(
                        "Dijkstra 算法不支持负权边: graph[" + i + "][" + j + "] = " + graph[i][j]);
                }
            }
        }

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