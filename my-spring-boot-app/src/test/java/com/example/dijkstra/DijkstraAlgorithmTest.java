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
}