package com.example.myapp.services;

import com.example.myapp.common.ErrorCode;
import com.example.myapp.common.BizException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 算法服务，提供 helloworld、哈希计算、冒泡排序三个纯内存计算方法。
 */
@Service
public class AlgoService {

    private static final String HELLO_WORLD_RESULT = "Hello World";

    /**
     * 返回固定字符串 "Hello World"。
     *
     * @return 固定字符串
     */
    public String helloworld() {
        return HELLO_WORLD_RESULT;
    }

    /**
     * 对输入字符串计算 SHA-256 哈希值，返回小写十六进制字符串。
     *
     * @param input 待哈希的原始字符串
     * @return 64 位十六进制哈希值
     */
    public String hash(String input) {
        if (input == null || input.isEmpty()) {
            throw new BizException(ErrorCode.ALGO_003, ErrorCode.MSG_INPUT_NULL);
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new BizException(ErrorCode.ALGO_004, ErrorCode.MSG_HASH_ERROR);
        }
    }

    /**
     * 对输入整数数组执行冒泡排序（标准双层循环，非 Arrays.sort），返回升序数组。
     *
     * @param arr 待排序整数数组
     * @return 升序排列后的数组
     */
    public int[] bubbleSort(int[] arr) {
        if (arr == null) {
            throw new BizException(ErrorCode.ALGO_005, ErrorCode.MSG_ARRAY_NULL);
        }
        if (arr.length > 1000) {
            throw new BizException(ErrorCode.ALGO_006, ErrorCode.MSG_ARRAY_TOO_LONG);
        }
        int[] result = arr.clone();
        int n = result.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (result[j] > result[j + 1]) {
                    int temp = result[j];
                    result[j] = result[j + 1];
                    result[j + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
        return result;
    }

    /**
     * 将字节数组转为小写十六进制字符串。
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
