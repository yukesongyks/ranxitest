package com.example.myapp.sort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 排序 REST 接口。
 * <p>
 * 提供 POST /api/sort/bubble 接口，接收待排序整数数组及排序方向，
 * 使用冒泡排序算法返回排序后结果。
 *
 * @author AiWork
 * @date 2026-09-15
 */
@RestController
@RequestMapping("/api/sort")
public class SortController {

    private static final Logger log = LoggerFactory.getLogger(SortController.class);

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
    public SortResponse<SortResult> bubbleSort(@Valid @RequestBody SortRequest request) {
        long start = System.currentTimeMillis();
        int requestCount = request.getNumbers() != null ? request.getNumbers().size() : 0;
        log.info("收到排序请求，count={}, order={}", requestCount, request.getOrder());

        // R01: numbers 不可为空数组
        if (request.getNumbers() == null || request.getNumbers().isEmpty()) {
            log.warn("校验失败：待排序数组为空");
            return SortResponse.error(SortConstants.CODE_SORT_EMPTY, "待排序数组不能为空");
        }

        // R02: numbers 元素数量 ≤ 1000
        if (request.getNumbers().size() > SortConstants.MAX_INPUT_SIZE) {
            log.warn("校验失败：元素数量 {} 超过上限 {}", request.getNumbers().size(), SortConstants.MAX_INPUT_SIZE);
            return SortResponse.error(SortConstants.CODE_SORT_OVERSIZE,
                    "待排序元素数量超过上限 " + SortConstants.MAX_INPUT_SIZE);
        }

        // F03: numbers 不可包含 null 元素
        for (Integer num : request.getNumbers()) {
            if (num == null) {
                log.warn("校验失败：待排序数组包含 null 元素");
                return SortResponse.error(SortConstants.CODE_SORT_NULL_ELEMENT, "待排序数组包含 null 元素");
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
                log.warn("校验失败：排序方向非法 order={}", orderStr);
                return SortResponse.error(SortConstants.CODE_SORT_INVALID_ORDER,
                        "排序方向非法，仅支持 ASC/DESC");
            }
        }

        // 调用冒泡排序算法
        List<Integer> sorted = sortService.bubbleSort(request.getNumbers(), order);

        SortResult result = new SortResult(sorted, order.name(), sorted.size());
        long elapsed = System.currentTimeMillis() - start;
        log.info("排序完成，count={}, order={}, 耗时={}ms", sorted.size(), order, elapsed);
        return SortResponse.success(result);
    }
}
