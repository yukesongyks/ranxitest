/**
 * 快速排序算法实现
 * 采用分治策略，支持三数取中基准选择以优化性能
 * 
 * 时间复杂度：平均 O(n log n)，最坏 O(n²)
 * 空间复杂度：O(log n)（递归调用栈）
 * 稳定性：不稳定排序
 */
public class QuickSort {
    
    /**
     * 对整数数组进行升序排序
     * @param arr 待排序的数组
     */
    public void quickSort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        quickSort(arr, 0, arr.length - 1);
    }
    
    /**
     * 对数组指定范围进行排序
     * @param arr 待排序的数组
     * @param low 起始索引
     * @param high 结束索引
     */
    public void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }
    
    /**
     * 分区操作，返回基准最终位置
     * 采用三数取中策略选择基准，将基准交换到最后位置
     * 使用 Lomuto 分区方案
     * 
     * @param arr 待分区数组
     * @param low 起始索引
     * @param high 结束索引
     * @return 基准的最终位置
     */
    private int partition(int[] arr, int low, int high) {
        // 三数取中选择基准
        int mid = low + (high - low) / 2;
        
        // 对 low, mid, high 三个位置的元素进行排序
        if (arr[low] > arr[mid]) {
            swap(arr, low, mid);
        }
        if (arr[low] > arr[high]) {
            swap(arr, low, high);
        }
        if (arr[mid] > arr[high]) {
            swap(arr, mid, high);
        }
        
        // 将中位数（基准）交换到最后位置
        swap(arr, mid, high);
        
        int pivot = arr[high];
        int i = low - 1;
        
        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        
        swap(arr, i + 1, high);
        return i + 1;
    }
    
    /**
     * 交换数组中两个元素的位置
     * @param arr 数组
     * @param i 第一个元素的索引
     * @param j 第二个元素的索引
     */
    private void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}