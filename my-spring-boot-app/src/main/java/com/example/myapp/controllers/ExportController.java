package com.example.myapp.controllers;

import com.example.myapp.exception.BizException;
import com.example.myapp.services.ExportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 导出接口 (I04)。
 * 按 type 复用 AlgorithmService 实时计算后序列化为 CSV/JSON，通过文件流写出。
 */
@RestController
@RequestMapping("/api/algorithm")
public class ExportController {

    private static final Logger log = LoggerFactory.getLogger(ExportController.class);

    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final ExportService exportService;

    @Autowired
    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/export")
    public void export(@RequestParam String type,
                       @RequestParam(required = false, defaultValue = "CSV") String format,
                       @RequestParam(required = false) String text,
                       @RequestParam(required = false) String algorithm,
                       @RequestParam(required = false) String array,
                       @RequestParam(required = false) String order,
                       HttpServletResponse response) throws IOException {

        try {
            String resolvedFormat = resolveFormat(format);
            String resolvedType = resolveType(type);

            List<Integer> parsedArray = null;
            if ("BUBBLE_SORT".equals(resolvedType)) {
                if (array == null || array.trim().isEmpty()) {
                    writeJsonError(response, HttpServletResponse.SC_BAD_REQUEST,
                            3, "EXPORT_003: BUBBLE_SORT 导出必须提供 array 参数");
                    return;
                }
                parsedArray = parseArray(array);
            }

            String content;
            if ("JSON".equals(resolvedFormat)) {
                content = exportService.exportJson(resolvedType, text, algorithm, parsedArray, order);
            } else {
                content = exportService.exportCsv(resolvedType, text, algorithm, parsedArray, order);
            }

            String fileExtension = resolvedFormat.toLowerCase();
            String timestamp = TIMESTAMP_FORMAT.format(LocalDateTime.now());
            String fileName = "algorithm-" + resolvedType + "-" + timestamp + "." + fileExtension;
            String contentType = "JSON".equals(resolvedFormat)
                    ? "application/json;charset=UTF-8"
                    : "text/csv;charset=UTF-8";

            response.setContentType(contentType);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            PrintWriter writer = response.getWriter();
            writer.write(content);
            writer.flush();
        } catch (BizException e) {
            log.warn("导出业务异常 code={} msg={}", e.getCode(), e.getMessage());
            writeJsonError(response, HttpServletResponse.SC_BAD_REQUEST,
                    e.getCode(), e.getMessage());
        } catch (NumberFormatException e) {
            log.warn("导出 array 解析失败", e);
            writeJsonError(response, HttpServletResponse.SC_BAD_REQUEST,
                    3, "EXPORT_003: array 包含非整数: " + e.getMessage());
        } catch (Exception e) {
            log.error("导出异常 type={} format={}", type, format, e);
            writeJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    999, "EXPORT_999: 导出异常: " + e.getMessage());
        }
    }

    private String resolveType(String type) {
        if (type == null) {
            throw new BizException(1, "EXPORT_001: type 不能为空");
        }
        String normalized = type.trim().toUpperCase();
        if (Arrays.asList("HELLOWORLD", "HASH", "BUBBLE_SORT").contains(normalized)) {
            return normalized;
        }
        throw new BizException(1, "EXPORT_001: type 不支持: " + type);
    }

    private void writeJsonError(HttpServletResponse response, int status, int code, String msg) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(status);
        response.getWriter().write("{\"code\":" + code + ",\"msg\":\"" + escapeJson(msg) + "\"}");
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

    private String resolveFormat(String format) {
        if (format == null || format.trim().isEmpty()) {
            return "CSV";
        }
        String normalized = format.trim().toUpperCase();
        if (Arrays.asList("CSV", "JSON").contains(normalized)) {
            return normalized;
        }
        // R06: format 非法时回退 CSV
        return "CSV";
    }

    private List<Integer> parseArray(String array) {
        List<Integer> result = new ArrayList<>();
        for (String part : array.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                result.add(Integer.parseInt(trimmed));
            }
        }
        return result;
    }
}
