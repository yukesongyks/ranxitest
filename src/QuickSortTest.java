import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Arrays;

/**
 * QuickSort 单元测试
 * 覆盖设计文档中定义的所有测试场景
 */
public class QuickSortTest {
    
    private QuickSort sorter = new QuickSort();
    
    /**
     * 测试空数组
     */
    @Test
    public void testEmptyArray() {
        int[] arr = {};
        sorter.quickSort(arr);
        assertArrayEquals(new int[]{}, arr);
    }
    
    /**
     * 测试单元素数组
     */
    @Test
    public void testSingleElement() {
        int[] arr = {1};
        sorter.quickSort(arr);
        assertArrayEquals(new int[]{1}, arr);
    }
    
    /**
     * 测试已排序数组
     */
    @Test
    public void testSortedArray() {
        int[] arr = {1, 2, 3, 4, 5};
        sorter.quickSort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);
    }
    
    /**
     * 测试逆序数组
     */
    @Test
    public void testReverseArray() {
        int[] arr = {5, 4, 3, 2, 1};
        sorter.quickSort(arr);
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, arr);
    }
    
    /**
     * 测试随机数组
     */
    @Test
    public void testRandomArray() {
        int[] arr = {3, 1, 4, 1, 5, 9, 2, 6};
        sorter.quickSort(arr);
        assertArrayEquals(new int[]{1, 1, 2, 3, 4, 5, 6, 9}, arr);
    }
    
    /**
     * 测试重复元素数组
     */
    @Test
    public void testDuplicateElements() {
        int[] arr = {2, 2, 2, 1, 1, 3};
        sorter.quickSort(arr);
        assertArrayEquals(new int[]{1, 1, 2, 2, 2, 3}, arr);
    }
    
    /**
     * 测试包含负数的数组
     */
    @Test
    public void testNegativeNumbers() {
        int[] arr = {-3, -1, -2, 0, 2};
        sorter.quickSort(arr);
        assertArrayEquals(new int[]{-3, -2, -1, 0, 2}, arr);
    }
    
    /**
     * 测试所有元素相同
     */
    @Test
    public void testAllSameElements() {
        int[] arr = {5, 5, 5, 5, 5};
        sorter.quickSort(arr);
        assertArrayEquals(new int[]{5, 5, 5, 5, 5}, arr);
    }
    
    /**
     * 测试两个元素数组
     */
    @Test
    public void testTwoElements() {
        int[] arr = {2, 1};
        sorter.quickSort(arr);
        assertArrayEquals(new int[]{1, 2}, arr);
    }
    
    /**
     * 测试null数组（不抛异常）
     */
    @Test
    public void testNullArray() {
        int[] arr = null;
        sorter.quickSort(arr);
        // 不应抛出异常
    }
}