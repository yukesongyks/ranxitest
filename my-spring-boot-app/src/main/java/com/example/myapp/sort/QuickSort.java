package com.example.myapp.sort;

/**
 * QuickSort implementation using the classic divide-and-conquer algorithm.
 * Time complexity: O(n log n) average, O(n^2) worst case
 * Space complexity: O(log n) for recursion stack
 */
public class QuickSort {

    /**
     * Sorts the given array in ascending order using quicksort algorithm.
     * 
     * @param arr the array to be sorted
     * @throws IllegalArgumentException if the array is null
     */
    public void sort(int[] arr) {
        if (arr == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        if (arr.length <= 1) {
            return; // Already sorted
        }
        quickSort(arr, 0, arr.length - 1);
    }

    private void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(arr, low, high);
            quickSort(arr, low, pivotIndex - 1);
            quickSort(arr, pivotIndex + 1, high);
        }
    }

    private int partition(int[] arr, int low, int high) {
        // Choose the rightmost element as pivot
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

    private void swap(int[] arr, int i, int j) {
        if (i != j) {
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }
}