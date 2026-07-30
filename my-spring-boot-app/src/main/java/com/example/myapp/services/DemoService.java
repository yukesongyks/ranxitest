package com.example.myapp.services;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DemoService {

    public String helloWorld() {
        return "Hello, World!";
    }

    public String hash(String algorithm, String input) {
        if (algorithm == null || algorithm.trim().isEmpty()) {
            throw new IllegalArgumentException("哈希算法不能为空");
        }
        if (input == null) {
            throw new IllegalArgumentException("输入不能为空");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm.trim().toUpperCase());
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("不支持的哈希算法: " + algorithm, e);
        }
    }

    public List<Integer> bubbleSort(List<Integer> input) {
        if (input == null) {
            throw new IllegalArgumentException("排序列表不能为空");
        }
        List<Integer> arr = new ArrayList<>(input);
        int n = arr.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr.get(j) > arr.get(j + 1)) {
                    int temp = arr.get(j);
                    arr.set(j, arr.get(j + 1));
                    arr.set(j + 1, temp);
                }
            }
        }
        return arr;
    }

    public byte[] exportToCsv(String type, Object data) {
        StringBuilder csv = new StringBuilder();
        csv.append("type,value\n");
        if ("helloworld".equals(type)) {
            csv.append("helloworld,").append(data).append("\n");
        } else if ("hash".equals(type)) {
            csv.append("hash_result,").append(data).append("\n");
        } else if ("bubblesort".equals(type)) {
            csv.append("sorted_result,").append(data).append("\n");
        } else {
            csv.append("unknown,").append(String.valueOf(data)).append("\n");
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }
}
