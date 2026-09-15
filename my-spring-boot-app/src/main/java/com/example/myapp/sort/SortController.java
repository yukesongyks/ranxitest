package com.example.myapp.sort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 排序 REST 接口。
 * <p>
 * 提供 POST /api/sort/bubble 接口，接收待排序整数数组及排序方向，
 * 使用冒泡排序算法返回排序后结果。
 */
@RestController
@RequestMapping("/api/sort")
public class SortController {

    private final SortService sortService;

    @Autowired
    public SortController(SortService sortService) {
        this.sortService = sortService;
    }

    /**
     * 冒泡排序接口。
     *
     * @param request 包含 numbers（整数数组）和 order（ASC/DESC，可选）
     * @return 统一出参 {code, msg, data}
     */
    @PostMapping("/bubble")
    public SortResponse<SortResult> bubbleSort(@RequestBody SortRequest request) {
        // R01: numbers 不可为 null 且不可为空数组
        if (request.getNumbers() == null || request.getNumbers().isEmpty()) {
            return SortResponse.error("SORT_001", "待排序数组不能为空");
        }

        // R02: numbers 元素数量 ≤ 1000
        if (request.getNumbers().size() > SortService.MAX_INPUT_SIZE) {
            return SortResponse.error("SORT_002", "待排序元素数量超过上限 " + SortService.MAX_INPUT_SIZE);
        }

        // R04: numbers 不可包含 null 元素（F03：非法元素校验）
        for (Integer num : request.getNumbers()) {
            if (num == null) {
                return SortResponse.error("SORT_004", "待排序数组包含 null 元素");
            }
        }

        // R03: order 为空时默认 ASC；非空时仅允许 ASC/DESC
        SortOrder order;
        String orderStr = request.getOrder();
        if (orderStr == null || orderStr.trim().isEmpty()) {
            order = SortOrder.ASC;
        } else {
            try {
                order = SortOrder.valueOf(orderStr.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return SortResponse.error("SORT_003", "排序方向非法，仅支持 ASC/DESC");
            }
        }

        // 调用冒泡排序算法
        List<Integer> sorted = sortService.bubbleSort(request.getNumbers(), order);

        SortResult result = new SortResult(sorted, order.name(), sorted.size());
        return SortResponse.success(result);
    }
}
