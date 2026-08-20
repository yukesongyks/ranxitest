package com.example.myapp.docgen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 通用 TXT 文档生成服务（docgen 模块）。
 *
 * <p>输入通用「行列表数据 + 导出选项」，输出 TXT 字节流：
 * 表头行 + 数据行 + 汇总行，UTF-8 编码、CRLF 换行；对字段值做文本注入转义。</p>
 */
@Service
public class TxtExportService {

    private static final Logger log = LoggerFactory.getLogger(TxtExportService.class);

    /** 文件名时间戳格式。 */
    private static final DateTimeFormatter FILE_NAME_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    /** 注入转义时替换的字符。 */
    private static final char ESCAPE_REPLACEMENT = ' ';

    /**
     * 将行列表数据生成为 TXT 字节流。
     *
     * @param rows    行数据列表
     * @param options 导出选项（非空）
     * @return TXT 文件字节流
     */
    public byte[] exportTxt(List<TxtRow> rows, TxtExportOptions options) {
        if (rows == null) {
            throw new DocgenExportException(DocgenErrorCode.INVALID_PARAM, "导出数据不能为空");
        }
        checkLimit(rows, options);

        StringBuilder sb = new StringBuilder();
        appendLine(sb, options.getHeaders(), options.getSeparator(), options.getLineSeparator());
        for (TxtRow row : rows) {
            appendLine(sb, row.getCells(), options.getSeparator(), options.getLineSeparator());
        }
        String summary = String.format(options.getSummaryTemplate(), rows.size());
        sb.append(summary).append(options.getLineSeparator());

        byte[] bytes = sb.toString().getBytes(options.getCharset());
        if (bytes.length > options.getMaxBytes()) {
            log.warn("导出内容体积超限, bytes={}, maxBytes={}", bytes.length, options.getMaxBytes());
            throw new DocgenExportException(DocgenErrorCode.EXPORT_OVER_LIMIT,
                    DocgenErrorCode.EXPORT_OVER_LIMIT.getDefaultMessage());
        }
        return bytes;
    }

    /**
     * 生成标准导出文件名：前缀 + 时间戳 + .txt 后缀（白名单固定格式，防路径穿越）。
     *
     * @param prefix 文件名前缀
     * @return 文件名，如 items-20260820-120000.txt
     */
    public String buildFileName(String prefix) {
        String timestamp = LocalDateTime.now().format(FILE_NAME_TIME_FORMATTER);
        return prefix + "-" + timestamp + ".txt";
    }

    private void checkLimit(List<TxtRow> rows, TxtExportOptions options) {
        if (rows.size() > options.getMaxRows()) {
            log.warn("导出行数超限, rows={}, maxRows={}", rows.size(), options.getMaxRows());
            throw new DocgenExportException(DocgenErrorCode.EXPORT_OVER_LIMIT,
                    DocgenErrorCode.EXPORT_OVER_LIMIT.getDefaultMessage());
        }
    }

    private void appendLine(StringBuilder sb, List<String> cells, String separator, String lineSeparator) {
        for (int i = 0; i < cells.size(); i++) {
            if (i > 0) {
                sb.append(separator);
            }
            sb.append(escape(cells.get(i)));
        }
        sb.append(lineSeparator);
    }

    /**
     * 文本注入转义：字段值中的制表符/换行/回车替换为空格。
     *
     * @param value 原始字段值
     * @return 转义后的值
     */
    private String escape(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\t' || c == '\r' || c == '\n') {
                sb.append(ESCAPE_REPLACEMENT);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}