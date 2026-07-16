package com.example.myapp;

import com.example.myapp.models.Graph;
import com.example.myapp.services.ShortestPathService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ShortestPathService 和 Graph 的单元测试。
 */
public class ShortestPathServiceTest {

    private final ShortestPathService service = new ShortestPathService();

    // ==================== Graph 构造测试 ====================

    @Test
    @DisplayName("Graph 构造：合法节点数量")
    void graphConstructionValid() {
        Graph g = new Graph(5);
        assertEquals(5, g.getVertices());
        assertTrue(g.getNeighbors(0).isEmpty());
    }

    @Test
    @DisplayName("Graph 构造：零节点应抛异常")
    void graphConstructionZeroVertices() {
        assertThrows(IllegalArgumentException.class, () -> new Graph(0));
    }

    @Test
    @DisplayName("Graph 构造：负节点应抛异常")
    void graphConstructionNegativeVertices() {
        assertThrows(IllegalArgumentException.class, () -> new Graph(-1));
    }

    @Test
    @DisplayName("Graph 添加边：合法边")
    void addEdgeValid() {
        Graph g = new Graph(3);
        g.addEdge(0, 1, 5);
        assertEquals(5, g.getNeighbors(0).get(1));
    }

    @Test
    @DisplayName("Graph 添加边：负权重应抛异常")
    void addEdgeNegativeWeight() {
        Graph g = new Graph(3);
        assertThrows(IllegalArgumentException.class, () -> g.addEdge(0, 1, -1));
    }

    @Test
    @DisplayName("Graph 添加边：越界节点应抛异常")
    void addEdgeOutOfBounds() {
        Graph g = new Graph(3);
        assertThrows(IllegalArgumentException.class, () -> g.addEdge(0, 5, 1));
    }

    // ==================== Dijkstra 正常路径测试 ====================

    @Test
    @DisplayName("Dijkstra：简单三角形图")
    void simpleTriangleGraph() {
        Graph g = new Graph(3);
        g.addEdge(0, 1, 4);
        g.addEdge(0, 2, 2);
        g.addEdge(1, 2, 1);

        ShortestPathService.ShortestPathResult result = service.computeShortestPaths(g, 0);

        assertEquals(0, result.distances()[0]);
        assertEquals(3, result.distances()[1]); // 0->2->1 = 2+1=3
        assertEquals(2, result.distances()[2]);

        assertEquals(List.of(0, 2, 1), result.getPath(1));
        assertEquals(List.of(0, 2), result.getPath(2));
    }

    @Test
    @DisplayName("Dijkstra：经典 5 节点图")
    void classicFiveNodeGraph() {
        Graph g = new Graph(5);
        g.addEdge(0, 1, 10);
        g.addEdge(0, 2, 3);
        g.addEdge(1, 2, 1);
        g.addEdge(1, 3, 2);
        g.addEdge(2, 1, 4);
        g.addEdge(2, 3, 8);
        g.addEdge(2, 4, 2);
        g.addEdge(3, 4, 7);
        g.addEdge(4, 3, 9);

        ShortestPathService.ShortestPathResult result = service.computeShortestPaths(g, 0);

        assertEquals(0, result.distances()[0]);
        assertEquals(7, result.distances()[1]);  // 0->2->1 = 3+4=7
        assertEquals(3, result.distances()[2]);  // 0->2 = 3
        assertEquals(9, result.distances()[3]);  // 0->2->1->3 = 3+4+2=9
        assertEquals(5, result.distances()[4]);  // 0->2->4 = 3+2=5
    }

    @Test
    @DisplayName("Dijkstra：源节点到自身距离为 0")
    void sourceToSelf() {
        Graph g = new Graph(3);
        g.addEdge(0, 1, 5);
        g.addEdge(1, 2, 3);

        ShortestPathService.ShortestPathResult result = service.computeShortestPaths(g, 0);

        assertEquals(0, result.distances()[0]);
        assertEquals(List.of(0), result.getPath(0));
    }

    // ==================== 边界条件测试 ====================

    @Test
    @DisplayName("Dijkstra：单节点图")
    void singleNodeGraph() {
        Graph g = new Graph(1);
        ShortestPathService.ShortestPathResult result = service.computeShortestPaths(g, 0);

        assertEquals(0, result.distances()[0]);
        assertEquals(List.of(0), result.getPath(0));
    }

    @Test
    @DisplayName("Dijkstra：不可达节点")
    void unreachableNode() {
        Graph g = new Graph(4);
        g.addEdge(0, 1, 3);
        // 节点 2 和 3 从 0 不可达

        ShortestPathService.ShortestPathResult result = service.computeShortestPaths(g, 0);

        assertEquals(0, result.distances()[0]);
        assertEquals(3, result.distances()[1]);
        assertEquals(Integer.MAX_VALUE, result.distances()[2]);
        assertEquals(Integer.MAX_VALUE, result.distances()[3]);

        assertTrue(result.getPath(2).isEmpty());
        assertTrue(result.getPath(3).isEmpty());
    }

    @Test
    @DisplayName("Dijkstra：零权重边")
    void zeroWeightEdge() {
        Graph g = new Graph(3);
        g.addEdge(0, 1, 0);
        g.addEdge(1, 2, 5);

        ShortestPathService.ShortestPathResult result = service.computeShortestPaths(g, 0);

        assertEquals(0, result.distances()[0]);
        assertEquals(0, result.distances()[1]);
        assertEquals(5, result.distances()[2]);
    }

    @Test
    @DisplayName("Dijkstra：稠密完全图")
    void denseCompleteGraph() {
        int n = 4;
        Graph g = new Graph(n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    g.addEdge(i, j, i + j + 1);
                }
            }
        }

        ShortestPathService.ShortestPathResult result = service.computeShortestPaths(g, 0);

        // 0->1: 直接权重 2，或 0->2->1 = 3+3=6，最短是 2
        assertEquals(2, result.distances()[1]);
        // 0->2: 直接权重 3
        assertEquals(3, result.distances()[2]);
        // 0->3: 0->1->3 = 2+4=6，0->2->3 = 3+5=8，直接 4，最短是 4
        assertEquals(4, result.distances()[3]);
    }

    // ==================== 路径重建测试 ====================

    @Test
    @DisplayName("路径重建：合法目标节点")
    void pathReconstruction() {
        Graph g = new Graph(4);
        g.addEdge(0, 1, 1);
        g.addEdge(1, 2, 2);
        g.addEdge(2, 3, 3);

        ShortestPathService.ShortestPathResult result = service.computeShortestPaths(g, 0);

        assertEquals(List.of(0, 1, 2, 3), result.getPath(3));
    }

    @Test
    @DisplayName("路径重建：越界目标节点返回空列表")
    void pathReconstructionOutOfBounds() {
        Graph g = new Graph(3);
        ShortestPathService.ShortestPathResult result = service.computeShortestPaths(g, 0);

        assertTrue(result.getPath(5).isEmpty());
        assertTrue(result.getPath(-1).isEmpty());
    }

    // ==================== 异常测试 ====================

    @Test
    @DisplayName("Graph getNeighbors：越界节点应抛异常")
    void getNeighborsOutOfBounds() {
        Graph g = new Graph(3);
        assertThrows(IllegalArgumentException.class, () -> g.getNeighbors(5));
    }

    @Test
    @DisplayName("computeShortestPaths：null graph 应抛异常")
    void computeShortestPathsNullGraph() {
        assertThrows(IllegalArgumentException.class, () -> service.computeShortestPaths(null, 0));
    }

    @Test
    @DisplayName("computeShortestPaths：source 为负数应抛异常")
    void computeShortestPathsSourceNegative() {
        Graph g = new Graph(3);
        assertThrows(IllegalArgumentException.class, () -> service.computeShortestPaths(g, -1));
    }

    @Test
    @DisplayName("computeShortestPaths：source 越界应抛异常")
    void computeShortestPathsSourceOutOfBounds() {
        Graph g = new Graph(3);
        assertThrows(IllegalArgumentException.class, () -> service.computeShortestPaths(g, 3));
    }
}