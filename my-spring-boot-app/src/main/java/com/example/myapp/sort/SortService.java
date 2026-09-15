package com.example.myapp.sort;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 冒泡排序服务。
 * <p>
 * 采用经典冒泡排序（双层循环 + 单趟无交换提前终止优化），稳定排序。
 * 对入参列表做防御性拷贝，不修改调用方原始集合。
 */
@Service
public class SortService {

    /**
     * 单次排序元素数量上限。
     */
    public static final int MAX_INPUT_SIZE = 1000;

    /**
     * 对整数列表执行冒泡排序。
     *
     * @param numbers 待排序整数列表（不会修改原始列表）
     * @param order   排序方向，ASC=升序（非递减），DESC=降序（非递增）
     * @return 排序后的新列表
     */
    public List<Integer> bubbleSort(List<Integer> numbers, SortOrder order) {
        // 防御性拷贝，避免对调用方传入集合的副作用（R05）
        List<Integer> arr = new ArrayList<>(numbers);

        int n = arr.size();
        // 单元素或空列表无需排序
        if (n <= 1) {
            return arr;
        }

        // 冒泡排序：相邻元素两两比较交换，单趟无交换则提前终止
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (shouldSwap(arr.get(j), arr.get(j + 1), order)) {
                    Collections.swap(arr, j, j + 1);
                    swapped = true;
                }
            }
            // 单趟无交换 → 已有序，提前终止
            if (!swapped) {
                break;
            }
        }

        return arr;
    }

    /**
     * 判断相邻两元素是否需要交换。
     * 升序（ASC）：前 > 后才交换 → 非递减
     * 降序（DESC）：前 < 后才交换 → 非递增
     * 相等时不交换，保证稳定性（R04）。
     */
    private boolean shouldSwap(Integer current, Integer next, SortOrder order) {
        if (order == SortOrder.DESC) {
            return current < next;
        }
        // 默认 ASC
        return current > next;
    }
}
