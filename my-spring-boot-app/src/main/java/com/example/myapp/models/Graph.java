package com.example.myapp.models;

import java.util.*;

/**
 * 加权有向图的邻接表表示。
 * 支持添加边、获取邻接节点和边权重。
 *
 * <p><b>线程安全说明</b>：此类不是线程安全的。如果在多线程环境中使用，
 * 需要外部同步。特别地，{@link #getNeighbors(int)} 返回的不可变 Map
 * 是对内部数据结构的实时视图，若在迭代邻居时并发修改图结构，
 * 可能抛出 {@link java.util.ConcurrentModificationException}。
 */
public class Graph {

    /** 邻接表：节点 -> (邻居节点 -> 边权重) */
    private final Map<Integer, Map<Integer, Integer>> adjacencyList;

    /** 图中节点数量 */
    private final int vertices;

    /**
     * 构造一个包含指定数量节点的图。
     *
     * @param vertices 节点数量（节点编号从 0 到 vertices-1）
     */
    public Graph(int vertices) {
        if (vertices <= 0) {
            throw new IllegalArgumentException("节点数量必须大于 0");
        }
        this.vertices = vertices;
        this.adjacencyList = new HashMap<>();
        for (int i = 0; i < vertices; i++) {
            adjacencyList.put(i, new HashMap<>());
        }
    }

    /**
     * 添加一条有向加权边。
     *
     * @param source 源节点
     * @param target 目标节点
     * @param weight 边权重（必须非负，Dijkstra 算法要求）
     */
    public void addEdge(int source, int target, int weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("边权重不能为负数，source=" + source + ", target=" + target + ", weight=" + weight);
        }
        validateVertex(source);
        validateVertex(target);
        adjacencyList.get(source).put(target, weight);
    }

    /**
     * 获取节点数量。
     */
    public int getVertices() {
        return vertices;
    }

    /**
     * 获取指定节点的所有邻居及其边权重。
     */
    public Map<Integer, Integer> getNeighbors(int vertex) {
        validateVertex(vertex);
        return Collections.unmodifiableMap(adjacencyList.get(vertex));
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= vertices) {
            throw new IllegalArgumentException("节点编号越界: " + vertex + "，有效范围 [0, " + (vertices - 1) + "]");
        }
    }
}