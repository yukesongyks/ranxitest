package com.example.myapp.sort;

/**
 * 归并排序递归实现。
 *
 * <p>采用分治策略，将数组逐层二分至最小单元，再逐层合并有序子数组。</p>
 *
 * <p><b>时间复杂度</b>：稳定 O(n log n)</p>
 * <p><b>空间复杂度</b>：O(n)</p>
 * <p><b>稳定性</b>：稳定</p>
 *
 * @param <T> 数组元素类型
 */
public class MergeSort<T extends Comparable<T>> implements Sorter<T> {

    @Override
    public void sort(T[] array) {
        if (array == null) {
            throw new IllegalArgumentException("Input array cannot be null");
        }
        if (array.length <= 1) {
            return;
        }
        mergeSort(array, 0, array.length - 1);
    }

    /**
     * 递归执行归并排序。
     *
     * @param array 目标数组
     * @param left  排序区间左边界
     * @param right 排序区间右边界
     */
    private void mergeSort(T[] array, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(array, left, mid);
            mergeSort(array, mid + 1, right);
            merge(array, left, mid, right);
        }
    }

    /**
     * 合并两个有序子数组。
     *
     * <p>将 array[left..mid] 和 array[mid+1..right] 合并为一个有序数组。</p>
     *
     * @param array 目标数组
     * @param left  左子数组起始下标
     * @param mid   左子数组结束下标（右子数组起始下标为 mid + 1）
     * @param right 右子数组结束下标
     */
    @SuppressWarnings("unchecked")
    private void merge(T[] array, int left, int mid, int right) {
        int leftSize = mid - left + 1;
        int rightSize = right - mid;

        T[] leftArray = (T[]) java.lang.reflect.Array.newInstance(
                array.getClass().getComponentType(), leftSize);
        T[] rightArray = (T[]) java.lang.reflect.Array.newInstance(
                array.getClass().getComponentType(), rightSize);

        System.arraycopy(array, left, leftArray, 0, leftSize);
        System.arraycopy(array, mid + 1, rightArray, 0, rightSize);

        int i = 0, j = 0, k = left;

        while (i < leftSize && j < rightSize) {
            if (leftArray[i].compareTo(rightArray[j]) <= 0) {
                array[k] = leftArray[i];
                i++;
            } else {
                array[k] = rightArray[j];
                j++;
            }
            k++;
        }

        while (i < leftSize) {
            array[k] = leftArray[i];
            i++;
            k++;
        }

        while (j < rightSize) {
            array[k] = rightArray[j];
            j++;
            k++;
        }
    }
}
