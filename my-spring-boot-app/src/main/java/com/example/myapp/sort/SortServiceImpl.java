package com.example.myapp.sort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 冒泡排序服务实现。
 * <p>
 * 采用经典冒泡排序（双层循环 + 单趟无交换提前终止优化），稳定排序。
 * 对入参列表做防御性拷贝，不修改调用方原始集合。
 *
 * @author AiWork
 * @date 2026-09-15
 */
@Service
public class SortServiceImpl implements SortService {

    private static final Logger log = LoggerFactory.getLogger(SortServiceImpl.class);

    /**
     * 对整数列表执行冒泡排序。
     *
     * @param numbers 待排序整数列表（不会修改原始列表）
     * @param order   排序方向，ASC=升序（非递减），DESC=降序（非递增）
     * @return 排序后的新列表
     */
    @Override
    public List<Integer> bubbleSort(List<Integer> numbers, SortOrder order) {
        if (numbers == null) {
            throw new SortException(SortConstants.CODE_SORT_EMPTY, "待排序数组不能为 null");
        }

        // 防御性拷贝，避免对调用方传入集合的副作用（R05）
        List<Integer> arr = new ArrayList<>(numbers);

        // 校验是否存在 null 元素（F03：非法元素校验）
        for (int k = 0; k < arr.size(); k++) {
            if (arr.get(k) == null) {
                throw new SortException(SortConstants.CODE_SORT_NULL_ELEMENT,
                        "待排序数组包含 null 元素，索引: " + k);
            }
        }

        int n = arr.size();
        // 单元素或空列表无需排序
        if (n <= 1) {
            log.debug("排序元素数量 ≤ 1，直接返回，count={}, order={}", n, order);
            return arr;
        }

        long start = System.currentTimeMillis();

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

        long elapsed = System.currentTimeMillis() - start;
        log.info("冒泡排序完成，count={}, order={}, 耗时={}ms", n, order, elapsed);

        return arr;
    }

    /**
     * 判断相邻两元素是否需要交换。
     * 升序（ASC）：前 > 后才交换 → 非递减
     * 降序（DESC）：前 < 后才交换 → 非递增
     * 相等时不交换，保证稳定性（R04）。
     *
     * @param current 当前元素
     * @param next    下一个元素
     * @param order   排序方向
     * @return true 表示需要交换
     */
    private boolean shouldSwap(Integer current, Integer next, SortOrder order) {
        if (order == SortOrder.DESC) {
            return current < next;
        }
        // 默认 ASC
        return current > next;
    }
}
