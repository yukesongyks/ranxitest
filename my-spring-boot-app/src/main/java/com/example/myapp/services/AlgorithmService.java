package com.example.myapp.services;

import com.example.myapp.exception.BizException;
import com.example.myapp.models.dto.BubbleSortResult;
import com.example.myapp.models.dto.HashResult;
import com.example.myapp.models.dto.HelloWorldResult;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 算法服务：HelloWorld / 哈希 / 冒泡排序。
 * 无状态纯函数计算，天然线程安全。
 */
@Service
public class AlgorithmService {

    private static final String DEFAULT_HASH_ALGORITHM = "SHA-256";
    private static final long MAX_ARRAY_ELEMENT = 1_000_000_000L;

    private static final Set<String> SUPPORTED_HASH = new HashSet<>(
            Arrays.asList("SHA-256", "SHA-512", "MD5"));
    private static final Set<String> SUPPORTED_ORDER = new HashSet<>(
            Arrays.asList("ASC", "DESC"));

    /**
     * HelloWorld (I01)：返回固定问候串。
     */
    public HelloWorldResult hello() {
        return new HelloWorldResult("Hello, World!");
    }

    /**
     * 哈希计算 (I02)。
     *
     * @param text      待哈希文本（非空，长度 <= 10000，由调用方/校验保证）
     * @param algorithm 算法名，为空或非法时回退 SHA-256（R01）
     */
    public HashResult hash(String text, String algorithm) {
        if (text == null || text.isEmpty()) {
            throw new BizException(1, "ALGO_001: text 不能为空");
        }
        if (text.length() > 10000) {
            throw new BizException(2, "ALGO_002: text 长度不能超过 10000");
        }

        String normalizedAlg = resolveAlgorithm(algorithm);

        try {
            MessageDigest md = MessageDigest.getInstance(normalizedAlg);
            byte[] digestBytes = md.digest(text.getBytes(StandardCharsets.UTF_8));
            String digest = toHex(digestBytes);
            return new HashResult(toEnumName(normalizedAlg), digest, text.length());
        } catch (NoSuchAlgorithmException e) {
            // 正常不应走到，resolveAlgorithm 已过滤；兜底
            throw new BizException(999, "ALGO_999: 哈希算法不可用: " + normalizedAlg);
        }
    }

    /**
     * 冒泡排序 (I03)。
     *
     * @param array 整数数组（非空，长度 <= 1000，由调用方/校验保证）
     * @param order ASC/DESC，为空默认 ASC（R02）
     */
    public BubbleSortResult bubbleSort(List<Integer> array, String order) {
        if (array == null || array.isEmpty()) {
            throw new BizException(4, "ALGO_004: array 不能为空");
        }
        if (array.size() > 1000) {
            throw new BizException(5, "ALGO_005: array 长度不能超过 1000");
        }

        String resolvedOrder = resolveOrder(order);
        boolean descending = "DESC".equals(resolvedOrder);

        // 校验元素越界（ALGO_006）
        for (Integer num : array) {
            if (num != null && Math.abs((long) num) > MAX_ARRAY_ELEMENT) {
                throw new BizException(6, "ALGO_006: 数组元素绝对值不能超过 " + MAX_ARRAY_ELEMENT);
            }
        }

        List<Integer> original = new ArrayList<>(array);
        int[] work = new int[original.size()];
        for (int i = 0; i < original.size(); i++) {
            work[i] = original.get(i);
        }

        long start = System.nanoTime();
        int swaps = bubbleSortInPlace(work, descending);
        long costMs = (System.nanoTime() - start) / 1_000_000;

        List<Integer> sorted = new ArrayList<>(work.length);
        for (int value : work) {
            sorted.add(value);
        }

        return new BubbleSortResult(sorted, swaps, costMs, Collections.unmodifiableList(original));
    }

    /**
     * 原地冒泡排序，返回交换次数。若某轮无交换则提前终止。
     */
    private int bubbleSortInPlace(int[] arr, boolean descending) {
        int n = arr.length;
        int swapCount = 0;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                boolean needSwap = descending ? arr[j] < arr[j + 1] : arr[j] > arr[j + 1];
                if (needSwap) {
                    int tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                    swapCount++;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
        return swapCount;
    }

    private String resolveAlgorithm(String algorithm) {
        if (algorithm == null || algorithm.trim().isEmpty()) {
            return DEFAULT_HASH_ALGORITHM;
        }
        // 兼容枚举风格 SHA_256 -> SHA-256
        String normalized = algorithm.trim().toUpperCase().replace("_", "-");
        if (SUPPORTED_HASH.contains(normalized)) {
            return normalized;
        }
        // R01: 非法时回退默认
        return DEFAULT_HASH_ALGORITHM;
    }

    private String resolveOrder(String order) {
        if (order == null || order.trim().isEmpty()) {
            return "ASC";
        }
        String normalized = order.trim().toUpperCase();
        if (SUPPORTED_ORDER.contains(normalized)) {
            return normalized;
        }
        return "ASC";
    }

    private String toEnumName(String algorithm) {
        return algorithm.replace("-", "_");
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
