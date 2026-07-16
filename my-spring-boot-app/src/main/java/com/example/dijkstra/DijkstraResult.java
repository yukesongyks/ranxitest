package com.example.dijkstra;

/**
 * Dijkstra 算法结果。
 *
 * @param distances 从源点到各节点的最短距离，不可达节点为 Integer.MAX_VALUE
 * @param previous  前驱节点数组，用于路径重建；无前驱为 -1
 */
public record DijkstraResult(int[] distances, int[] previous) {
}