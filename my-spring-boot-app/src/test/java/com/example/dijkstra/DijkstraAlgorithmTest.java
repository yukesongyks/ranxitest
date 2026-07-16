package com.example.dijkstra;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DijkstraAlgorithmTest {

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

    @Test
    void testSingleNodeGraph() {
        int[][] graph = {{0}};
        DijkstraResult result = DijkstraAlgorithm.shortestPath(graph, 0);
        assertArrayEquals(new int[]{0}, result.distances());
        assertArrayEquals(new int[]{-1}, result.previous());
    }

    @Test
    void testDisconnectedGraph() {
        int[][] graph = {
            {0, 5, 0},
            {5, 0, 0},
            {0, 0, 0}
        };
        DijkstraResult result = DijkstraAlgorithm.shortestPath(graph, 0);
        assertEquals(0, result.distances()[0]);
        assertEquals(5, result.distances()[1]);
        assertEquals(Integer.MAX_VALUE, result.distances()[2]);
    }

    @Test
    void testDifferentSource() {
        int[][] graph = {
            {0, 4, 0, 0},
            {4, 0, 8, 0},
            {0, 8, 0, 7},
            {0, 0, 7, 0}
        };
        DijkstraResult result = DijkstraAlgorithm.shortestPath(graph, 1);
        assertArrayEquals(new int[]{4, 0, 8, 15}, result.distances());
    }

    @Test
    void testPathReconstruction() {
        int[][] graph = {
            {0, 4, 0, 0},
            {4, 0, 8, 0},
            {0, 8, 0, 7},
            {0, 0, 7, 0}
        };
        DijkstraResult result = DijkstraAlgorithm.shortestPath(graph, 0);

        // 从起点 0 到节点 3 的路径回溯: 3 <- 2 <- 1 <- 0
        assertEquals(-1, result.previous()[0]);
        assertEquals(0,  result.previous()[1]);
        assertEquals(1,  result.previous()[2]);
        assertEquals(2,  result.previous()[3]);
    }

    @Test
    void testNegativeEdgeThrows() {
        int[][] graph = {
            {0, -1},
            {-1, 0}
        };
        assertThrows(IllegalArgumentException.class,
            () -> DijkstraAlgorithm.shortestPath(graph, 0));
    }

    @Test
    void testEmptyGraph() {
        int[][] graph = {};
        DijkstraResult result = DijkstraAlgorithm.shortestPath(graph, 0);
        assertEquals(0, result.distances().length);
        assertEquals(0, result.previous().length);
    }
}