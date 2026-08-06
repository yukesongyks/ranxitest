package com.example.myapp.util;

/**
 * 冒泡排序工具类
 *
 * <p>提供对整型数组进行冒泡排序（升序）的静态方法。</p>
 */
public final class BubbleSortUtil {

    private BubbleSortUtil() {
        // 工具类，禁止实例化
    }

    /**
     * 对整型数组执行冒泡排序（升序），原地排序。
     *
     * @param array 待排序数组，允许为空或 null
     * @return 排序后的数组（与输入为同一引用）
     */
    public static int[] sort(int[] array) {
        if (array == null || array.length <= 1) {
            return array;
        }

        for (int i = 0; i < array.length - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < array.length - 1 - i; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    swapped = true;
                }
            }
            // 未发生交换说明已有序，提前终止
            if (!swapped) {
                break;
            }
        }
        return array;
    }
}