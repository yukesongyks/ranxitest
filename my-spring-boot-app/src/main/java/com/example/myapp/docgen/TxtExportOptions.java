package com.example.myapp.docgen;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * TXT 导出选项。
 */
public class TxtExportOptions {

    /** 默认字段分隔符（制表符）。 */
    public static final String DEFAULT_SEPARATOR = "\t";

    /** 默认换行符（CRLF）。 */
    public static final String DEFAULT_LINE_SEPARATOR = "\r\n";

    /** 默认最大行数。 */
    public static final int DEFAULT_MAX_ROWS = 10000;

    /** 默认最大体积（10MB）。 */
    public static final long DEFAULT_MAX_BYTES = 10L * 1024L * 1024L;

    /** 默认汇总行模板。 */
    public static final String DEFAULT_SUMMARY_TEMPLATE = "共 %d 条记录";

    private List<String> headers = new ArrayList<>();
    private String separator = DEFAULT_SEPARATOR;
    private String lineSeparator = DEFAULT_LINE_SEPARATOR;
    private long maxBytes = DEFAULT_MAX_BYTES;
    private int maxRows = DEFAULT_MAX_ROWS;
    private Charset charset = StandardCharsets.UTF_8;
    private String summaryTemplate = DEFAULT_SUMMARY_TEMPLATE;

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers == null ? new ArrayList<>() : new ArrayList<>(headers);
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String getLineSeparator() {
        return lineSeparator;
    }

    public void setLineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

    public long getMaxBytes() {
        return maxBytes;
    }

    public void setMaxBytes(long maxBytes) {
        this.maxBytes = maxBytes;
    }

    public int getMaxRows() {
        return maxRows;
    }

    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public String getSummaryTemplate() {
        return summaryTemplate;
    }

    public void setSummaryTemplate(String summaryTemplate) {
        this.summaryTemplate = summaryTemplate;
    }
}