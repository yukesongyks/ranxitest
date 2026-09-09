package com.example.myapp.utils;

import java.util.List;

/**
 * Generic in-place quicksort implementation.
 * Supports int[] and List&lt;T extends Comparable&gt;.
 */
public final class Quicksort {

    private Quicksort() {
        // utility class
    }

    // ── int[] ────────────────────────────────────────────────────

    public static void sort(int[] arr) {
        if (arr == null) {
            return;
        }
        sort(arr, 0, arr.length - 1);
    }

    private static void sort(int[] arr, int lo, int hi) {
        if (lo >= hi) {
            return;
        }
        int p = partition(arr, lo, hi);
        sort(arr, lo, p - 1);
        sort(arr, p + 1, hi);
    }

    private static int partition(int[] arr, int lo, int hi) {
        // median-of-three pivot selection
        int mid = lo + (hi - lo) / 2;
        int pivot = median(arr[lo], arr[mid], arr[hi]);

        int i = lo - 1;
        int j = hi + 1;

        while (true) {
            do { i++; } while (arr[i] < pivot);
            do { j--; } while (arr[j] > pivot);

            if (i >= j) {
                return j;
            }
            swap(arr, i, j);
        }
    }

    private static int median(int a, int b, int c) {
        if (a > b) { int t = a; a = b; b = t; }
        if (a > c) { int t = a; a = c; c = t; }
        if (b > c) { int t = b; b = c; c = t; }
        return b;
    }

    private static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    // ── List<T extends Comparable<T>> ─────────────────────────────

    public static <T extends Comparable<T>> void sort(List<T> list) {
        if (list == null) {
            return;
        }
        sort(list, 0, list.size() - 1);
    }

    private static <T extends Comparable<T>> void sort(List<T> list, int lo, int hi) {
        if (lo >= hi) {
            return;
        }
        int p = partition(list, lo, hi);
        sort(list, lo, p - 1);
        sort(list, p + 1, hi);
    }

    private static <T extends Comparable<T>> int partition(List<T> list, int lo, int hi) {
        int mid = lo + (hi - lo) / 2;
        T pivot = median(list.get(lo), list.get(mid), list.get(hi));

        int i = lo - 1;
        int j = hi + 1;

        while (true) {
            do { i++; } while (list.get(i).compareTo(pivot) < 0);
            do { j--; } while (list.get(j).compareTo(pivot) > 0);

            if (i >= j) {
                return j;
            }
            swap(list, i, j);
        }
    }

    private static <T extends Comparable<T>> T median(T a, T b, T c) {
        if (a.compareTo(b) > 0) { T t = a; a = b; b = t; }
        if (a.compareTo(c) > 0) { T t = a; a = c; c = t; }
        if (b.compareTo(c) > 0) { T t = b; b = c; c = t; }
        return b;
    }

    private static <T> void swap(List<T> list, int i, int j) {
        T tmp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, tmp);
    }
}