package com.example.myapp.services;

import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 快速排序服务 - 提供原地快速排序实现。
 * <p>
 * 算法采用经典的 Lomuto 分区方案，每次选取最右侧元素作为 pivot，
 * 将小于 pivot 的元素移到左侧，大于等于 pivot 的元素移到右侧。
 * 时间复杂度：平均 O(n log n)，最坏 O(n²)。
 * 空间复杂度：O(log n)（递归栈深度）。
 */
@Service
public class QuickSortService {

    /**
     * 对整数数组进行原地快速排序。
     *
     * @param arr 待排序的整数数组，不能为 null
     * @throws IllegalArgumentException 如果 arr 为 null
     */
    public void sort(int[] arr) {
        if (arr == null) {
            throw new IllegalArgumentException("输入数组不能为 null");
        }
        quickSort(arr, 0, arr.length - 1);
    }

    /**
     * 递归快速排序核心逻辑。
     *
     * @param arr  待排序数组
     * @param low  当前子数组的起始索引（包含）
     * @param high 当前子数组的结束索引（包含）
     */
    private void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(arr, low, high);
            quickSort(arr, low, pivotIndex - 1);
            quickSort(arr, pivotIndex + 1, high);
        }
    }

    /**
     * Lomuto 分区：将数组分为两部分，左侧元素 ≤ pivot，右侧元素 > pivot。
     *
     * @param arr  待分区数组
     * @param low  分区起始索引
     * @param high 分区结束索引（pivot 所在位置）
     * @return pivot 最终位置索引
     */
    private int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (arr[j] < pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    /**
     * 交换数组中两个位置的元素。
     */
    private void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}