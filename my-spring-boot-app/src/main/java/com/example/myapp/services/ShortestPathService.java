package com.example.myapp.services;

import com.example.myapp.models.Graph;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 最短路径服务，基于 Dijkstra 算法实现。
 * 使用优先队列优化，时间复杂度 O((V+E) log V)。
 */
@Service
public class ShortestPathService {

    /**
     * 最短路径计算结果。
     */
    public record ShortestPathResult(
            /** 从源节点到各节点的最短距离，不可达节点为 Integer.MAX_VALUE */
            int[] distances,
            /** 前驱节点数组，用于重建路径；源节点或不可达节点为 -1 */
            int[] predecessors
    ) {
        /**
         * 重建从源节点到目标节点的最短路径。
         *
         * @param target 目标节点
         * @return 路径节点列表（含源节点和目标节点），不可达时返回空列表
         */
        public List<Integer> getPath(int target) {
            if (target < 0 || target >= predecessors.length) {
                return Collections.emptyList();
            }
            if (distances[target] == Integer.MAX_VALUE) {
                return Collections.emptyList();
            }
            LinkedList<Integer> path = new LinkedList<>();
            for (int at = target; at != -1; at = predecessors[at]) {
                path.addFirst(at);
            }
            return path;
        }
    }

    /**
     * 使用 Dijkstra 算法计算从源节点到所有其他节点的最短路径。
     *
     * @param graph 图
     * @param source 源节点
     * @return 包含最短距离和前驱节点的结果
     */
    public ShortestPathResult computeShortestPaths(Graph graph, int source) {
        int vertices = graph.getVertices();

        // 距离数组，初始化为无穷大
        int[] distances = new int[vertices];
        Arrays.fill(distances, Integer.MAX_VALUE);
        distances[source] = 0;

        // 前驱节点数组
        int[] predecessors = new int[vertices];
        Arrays.fill(predecessors, -1);

        // 优先队列：(节点, 距离)，按距离升序
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.offer(new int[]{source, 0});

        // 已访问节点集合
        boolean[] visited = new boolean[vertices];

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int u = current[0];

            if (visited[u]) {
                continue;
            }
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

        return new ShortestPathResult(distances, predecessors);
    }
}