package com.example.myapp.algorithms;

import java.util.*;

/**
 * 最短路径算法实现，基于 Dijkstra 算法（优先队列优化）。
 *
 * <p>支持：
 * <ul>
 *   <li>单源最短路径（Dijkstra，非负权）</li>
 *   <li>最短路径还原</li>
 *   <li>无权图 BFS 最短路径</li>
 * </ul>
 */
public final class ShortestPath {

    private ShortestPath() {
        // 工具类，禁止实例化
    }

    /**
     * 图的边。
     */
    public record Edge(int to, int weight) {
        public Edge {
            if (weight < 0) {
                throw new IllegalArgumentException("Dijkstra 不支持负权边: " + weight);
            }
        }
    }

    /**
     * 单源最短路径结果。
     */
    public record Result(int[] distances, int[] predecessors) {
        /**
         * 起点到 target 是否可达。
         */
        public boolean reachable(int target) {
            return distances[target] != Integer.MAX_VALUE;
        }

        /**
         * 重建从 source 到 target 的最短路径（节点序列）。
         *
         * @param source 起点
         * @param target 终点
         * @return 路径节点列表；不可达时返回空列表
         */
        public List<Integer> reconstructPath(int source, int target) {
            if (!reachable(target)) {
                return Collections.emptyList();
            }
            List<Integer> path = new ArrayList<>();
            for (int v = target; v != source; v = predecessors[v]) {
                if (v == -1) {
                    return Collections.emptyList(); // 不可达
                }
                path.add(v);
            }
            path.add(source);
            Collections.reverse(path);
            return path;
        }
    }

    /**
     * Dijkstra 单源最短路径（优先队列优化）。
     *
     * <p>时间复杂度：O((V + E) log V)，空间复杂度：O(V)。
     *
     * @param graph  邻接表表示的图，graph[u] 为从 u 出发的所有边
     * @param source 起点编号（0-based）
     * @return 最短距离数组和前任数组
     * @throws IllegalArgumentException 若 graph 为 null 或 source 越界
     */
    public static Result dijkstra(List<Edge>[] graph, int source) {
        if (graph == null) {
            throw new IllegalArgumentException("graph 不能为 null");
        }
        int n = graph.length;
        if (n == 0) {
            return new Result(new int[0], new int[0]);
        }
        if (source < 0 || source >= n) {
            throw new IllegalArgumentException("source 越界: " + source);
        }

        int[] dist = new int[n];
        int[] prev = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);
        dist[source] = 0;

        // 小顶堆: (距离, 节点)
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        pq.offer(new int[]{0, source});

        while (!pq.isEmpty()) {
            int[] top = pq.poll();
            int d = top[0];
            int u = top[1];

            if (d > dist[u]) {
                continue; // 过期条目
            }

            if (graph[u] == null) {
                continue;
            }

            for (Edge edge : graph[u]) {
                int v = edge.to();
                int w = edge.weight();
                if (v < 0 || v >= n) {
                    continue; // 跳过无效邻居
                }
                long newDist = (long) dist[u] + w;
                if (newDist < dist[v]) {
                    dist[v] = (int) newDist;
                    prev[v] = u;
                    pq.offer(new int[]{dist[v], v});
                }
            }
        }

        return new Result(dist, prev);
    }

    /**
     * 无权图 BFS 最短路径（所有边权重视为 1）。
     *
     * <p>时间复杂度：O(V + E)，空间复杂度：O(V)。
     *
     * @param graph  邻接表（邻接节点编号，无权重）
     * @param source 起点编号（0-based）
     * @return 最短距离数组和前任数组
     */
    public static Result bfs(List<Integer>[] graph, int source) {
        if (graph == null) {
            throw new IllegalArgumentException("graph 不能为 null");
        }
        int n = graph.length;
        if (n == 0) {
            return new Result(new int[0], new int[0]);
        }
        if (source < 0 || source >= n) {
            throw new IllegalArgumentException("source 越界: " + source);
        }

        int[] dist = new int[n];
        int[] prev = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);
        dist[source] = 0;

        Queue<Integer> queue = new ArrayDeque<>();
        queue.offer(source);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            if (graph[u] == null) {
                continue;
            }
            for (int v : graph[u]) {
                if (v < 0 || v >= n) {
                    continue;
                }
                if (dist[v] == Integer.MAX_VALUE) {
                    dist[v] = dist[u] + 1;
                    prev[v] = u;
                    queue.offer(v);
                }
            }
        }

        return new Result(dist, prev);
    }
}