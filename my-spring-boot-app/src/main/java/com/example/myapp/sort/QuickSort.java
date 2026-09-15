package com.example.myapp.sort;

/**
 * 快速排序递归实现。
 *
 * <p>采用分治策略，选取基准值将数组划分为左右两部分，递归排序子区间。</p>
 *
 * <p><b>时间复杂度</b>：平均 O(n log n)，最坏 O(n²)</p>
 * <p><b>空间复杂度</b>：O(log n)</p>
 * <p><b>稳定性</b>：不稳定</p>
 *
 * @param <T> 数组元素类型
 */
public class QuickSort<T extends Comparable<T>> implements Sorter<T> {

    @Override
    public void sort(T[] array) {
        if (array == null) {
            throw new IllegalArgumentException("Input array cannot be null");
        }
        if (array.length <= 1) {
            return;
        }
        quickSort(array, 0, array.length - 1);
    }

    /**
     * 递归执行快速排序。
     *
     * @param array 目标数组
     * @param low   排序区间左边界
     * @param high  排序区间右边界
     */
    private void quickSort(T[] array, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(array, low, high);
            quickSort(array, low, pivotIndex - 1);
            quickSort(array, pivotIndex + 1, high);
        }
    }

    /**
     * 分区操作：将数组按基准值划分为左右两部分。
     *
     * <p>默认采用末尾元素作为 pivot。所有 &lt; pivot 的元素移到左侧，&gt;= pivot 的元素移到右侧。</p>
     *
     * @param array 目标数组
     * @param low   分区区间左边界
     * @param high  分区区间右边界
     * @return 基准值的最终位置
     */
    private int partition(T[] array, int low, int high) {
        T pivot = array[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (array[j].compareTo(pivot) < 0) {
                i++;
                SortUtils.swap(array, i, j);
            }
        }

        SortUtils.swap(array, i + 1, high);
        return i + 1;
    }
}
