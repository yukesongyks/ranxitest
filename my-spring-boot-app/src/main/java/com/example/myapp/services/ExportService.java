package com.example.myapp.services;

import com.example.myapp.exception.BizException;
import com.example.myapp.models.dto.BubbleSortResult;
import com.example.myapp.models.dto.HashResult;
import com.example.myapp.models.dto.HelloWorldResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 导出服务：按 type 复用 AlgorithmService 实时计算后序列化为 CSV/JSON。
 * 导出结果与页面展示同源（R05），保证一致性。
 */
@Service
public class ExportService {

    private final AlgorithmService algorithmService;

    @Autowired
    public ExportService(AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
    }

    /**
     * 导出为 CSV 字符串。
     */
    public String exportCsv(String type, String text, String algorithm,
                            List<Integer> array, String order) {
        switch (type) {
            case "HELLOWORLD":
                return exportHelloWorldCsv();
            case "HASH":
                return exportHashCsv(text, algorithm);
            case "BUBBLE_SORT":
                return exportBubbleSortCsv(array, order);
            default:
                throw new BizException(1, "EXPORT_001: type 不支持: " + type);
        }
    }

    /**
     * 导出为 JSON 字符串（简化序列化，无外部依赖）。
     */
    public String exportJson(String type, String text, String algorithm,
                             List<Integer> array, String order) {
        switch (type) {
            case "HELLOWORLD": {
                HelloWorldResult result = algorithmService.hello();
                return "{\"type\":\"HELLOWORLD\",\"message\":\"" + escapeJson(result.getMessage()) + "\"}";
            }
            case "HASH": {
                HashResult result = algorithmService.hash(text, algorithm);
                return "{\"type\":\"HASH\",\"algorithm\":\"" + escapeJson(result.getAlgorithm())
                        + "\",\"digest\":\"" + escapeJson(result.getDigest())
                        + "\",\"length\":" + result.getLength() + "}";
            }
            case "BUBBLE_SORT": {
                BubbleSortResult result = algorithmService.bubbleSort(array, order);
                return "{\"type\":\"BUBBLE_SORT\",\"original\":" + toJsonArray(result.getOriginal())
                        + ",\"sorted\":" + toJsonArray(result.getSorted())
                        + ",\"swaps\":" + result.getSwaps()
                        + ",\"costMs\":" + result.getCostMs() + "}";
            }
            default:
                throw new BizException(1, "EXPORT_001: type 不支持: " + type);
        }
    }

    private String exportHelloWorldCsv() {
        HelloWorldResult result = algorithmService.hello();
        return csvRow("type", "HELLOWORLD")
                + csvRow("message", result.getMessage());
    }

    private String exportHashCsv(String text, String algorithm) {
        HashResult result = algorithmService.hash(text, algorithm);
        return csvRow("type", "HASH")
                + csvRow("algorithm", result.getAlgorithm())
                + csvRow("digest", result.getDigest())
                + csvRow("length", String.valueOf(result.getLength()));
    }

    private String exportBubbleSortCsv(List<Integer> array, String order) {
        BubbleSortResult result = algorithmService.bubbleSort(array, order);
        return csvRow("type", "BUBBLE_SORT")
                + csvRow("original", joinComma(result.getOriginal()))
                + csvRow("sorted", joinComma(result.getSorted()))
                + csvRow("swaps", String.valueOf(result.getSwaps()))
                + csvRow("costMs", String.valueOf(result.getCostMs()));
    }

    private String csvRow(String key, String value) {
        return key + "," + csvEscape(value) + "\n";
    }

    private String csvEscape(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private String joinComma(List<Integer> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(list.get(i));
        }
        return sb.toString();
    }

    private String toJsonArray(List<Integer> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(list.get(i));
        }
        sb.append("]");
        return sb.toString();
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
