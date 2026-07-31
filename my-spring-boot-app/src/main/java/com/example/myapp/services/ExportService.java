package com.example.myapp.services;

import com.example.myapp.common.BizException;
import com.example.myapp.common.ErrorCode;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 导出服务，根据类型参数复用算法逻辑生成 CSV 字节流。
 */
@Service
public class ExportService {

    private final AlgoService algoService;

    public ExportService(AlgoService algoService) {
        this.algoService = algoService;
    }

    /**
     * 根据导出类型生成对应算法结果的 CSV 字节流。
     *
     * @param type  导出类型：helloworld / hash / bubble-sort
     * @param input 哈希算法的输入字符串（type=hash 时必填）
     * @param arr   冒泡排序的输入数组字符串（type=bubble-sort 时必填，逗号分隔）
     * @return CSV 文件字节流
     */
    public ExportResult export(String type, String input, String arr) {
        if (type == null || type.trim().isEmpty()) {
            throw new BizException(ErrorCode.EXPORT_001, ErrorCode.MSG_EXPORT_TYPE_INVALID);
        }
        String normalizedType = type.trim().toLowerCase();
        switch (normalizedType) {
            case "helloworld":
                return buildCsv("helloworld_export.csv", "result", algoService.helloworld());
            case "hash":
                if (input == null || input.isEmpty()) {
                    throw new BizException(ErrorCode.EXPORT_002, ErrorCode.MSG_EXPORT_MISSING_PARAM);
                }
                String hashResult = algoService.hash(input);
                return buildCsv("hash_export.csv", "input,result",
                        input + "," + hashResult);
            case "bubble-sort":
                if (arr == null || arr.isEmpty()) {
                    throw new BizException(ErrorCode.EXPORT_002, ErrorCode.MSG_EXPORT_MISSING_PARAM);
                }
                int[] parsedArr = parseArray(arr);
                int[] sortedArr = algoService.bubbleSort(parsedArr);
                return buildCsv("bubble_sort_export.csv", "input,result",
                        arr + "," + arrayToString(sortedArr));
            default:
                throw new BizException(ErrorCode.EXPORT_001, ErrorCode.MSG_EXPORT_TYPE_INVALID);
        }
    }

    private ExportResult buildCsv(String filename, String header, String data) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write((header + "\n").getBytes(StandardCharsets.UTF_8));
            baos.write((data + "\n").getBytes(StandardCharsets.UTF_8));
            return new ExportResult(filename, baos.toByteArray());
        } catch (IOException e) {
            throw new BizException(ErrorCode.EXPORT_003, ErrorCode.MSG_EXPORT_FAIL);
        }
    }

    private int[] parseArray(String arrStr) {
        String[] parts = arrStr.split(",");
        int[] result = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Integer.parseInt(parts[i].trim());
        }
        return result;
    }

    private String arrayToString(int[] arr) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(arr[i]);
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * 导出结果，包含文件名和 CSV 字节流。
     */
    public static class ExportResult {

        private final String filename;
        private final byte[] content;

        public ExportResult(String filename, byte[] content) {
            this.filename = filename;
            this.content = content;
        }

        public String getFilename() {
            return filename;
        }

        public byte[] getContent() {
            return content;
        }
    }
}
