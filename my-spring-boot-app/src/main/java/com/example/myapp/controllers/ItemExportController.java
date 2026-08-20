package com.example.myapp.controllers;

import com.example.myapp.docgen.DocgenErrorCode;
import com.example.myapp.docgen.DocgenExportException;
import com.example.myapp.docgen.DocgenExportMetrics;
import com.example.myapp.docgen.DocgenExportProperties;
import com.example.myapp.docgen.TxtExportOptions;
import com.example.myapp.docgen.TxtExportService;
import com.example.myapp.docgen.TxtRow;
import com.example.myapp.services.ItemExportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物品清单 TXT 导出控制器。
 *
 * <p>W01：页面导出下载；O01：对外 OpenAPI 导出。成功返回文件流，失败返回
 * {@code {result, msg, data}} JSON 结构。</p>
 */
@Controller
public class ItemExportController {

    private static final Logger log = LoggerFactory.getLogger(ItemExportController.class);

    /** limit 参数最大上限。 */
    private static final int MAX_LIMIT = 100000;

    /** limit 参数默认值。 */
    private static final int DEFAULT_LIMIT = 10000;

    /** 支持的编码白名单。 */
    private static final Map<String, Charset> SUPPORTED_ENCODINGS = new HashMap<>();

    static {
        SUPPORTED_ENCODINGS.put("utf-8", StandardCharsets.UTF_8);
        SUPPORTED_ENCODINGS.put("gbk", Charset.forName("GBK"));
    }

    private final ItemExportService itemExportService;
    private final TxtExportService txtExportService;
    private final DocgenExportProperties docgenExportProperties;
    private final DocgenExportMetrics docgenExportMetrics;

    /**
     * 构造控制器。
     *
     * @param itemExportService      物品行数据组装服务
     * @param txtExportService       通用 TXT 生成服务
     * @param docgenExportProperties 导出功能配置
     * @param docgenExportMetrics    导出监控埋点
     */
    public ItemExportController(ItemExportService itemExportService,
                                TxtExportService txtExportService,
                                DocgenExportProperties docgenExportProperties,
                                DocgenExportMetrics docgenExportMetrics) {
        this.itemExportService = itemExportService;
        this.txtExportService = txtExportService;
        this.docgenExportProperties = docgenExportProperties;
        this.docgenExportMetrics = docgenExportMetrics;
    }

    /**
     * W01 页面导出 TXT 下载（GET /items/export.txt）。
     *
     * @return 附件文件流；功能关闭时返回 503
     */
    @GetMapping("/items/export.txt")
    public ResponseEntity<?> exportPageTxt() {
        if (!docgenExportProperties.isEnabled()) {
            return maintenanceResponse();
        }
        long start = System.currentTimeMillis();
        docgenExportMetrics.recordRequest();
        try {
            List<TxtRow> rows = itemExportService.buildRows();
            TxtExportOptions options = new TxtExportOptions();
            options.setHeaders(ItemExportService.HEADERS);
            byte[] content = txtExportService.exportTxt(rows, options);
            ensureWithinTimeout(start, "物品页面导出");
            ResponseEntity<byte[]> response = attachmentResponse(content,
                    txtExportService.buildFileName(ItemExportService.FILE_NAME_PREFIX), StandardCharsets.UTF_8);
            long elapsedMs = elapsedMs(start);
            docgenExportMetrics.recordSuccess(elapsedMs, rows.size(), content.length);
            log.info("物品页面导出成功, rows={}, elapsedMs={}, bytes={}",
                    rows.size(), elapsedMs, content.length);
            return response;
        } catch (DocgenExportException e) {
            docgenExportMetrics.recordFailure(e.getErrorCode());
            log.error("物品页面导出失败, code={}", e.getErrorCode(), e);
            throw e;
        }
    }

    /**
     * O01 对外导出物品 TXT 文档（GET /openapi/items/export）。
     *
     * @param limit    导出行数上限，默认 10000，最大 100000
     * @param encoding 编码，默认 utf-8，仅支持 utf-8/gbk
     * @return 附件文件流或失败 JSON
     */
    @GetMapping("/openapi/items/export")
    public ResponseEntity<?> exportOpenApiTxt(@RequestParam(required = false) Integer limit,
                                              @RequestParam(required = false) String encoding) {
        if (!docgenExportProperties.isEnabled()) {
            return maintenanceResponse();
        }
        Charset charset = SUPPORTED_ENCODINGS.get(encoding == null ? "utf-8" : encoding.toLowerCase());
        if (charset == null) {
            return errorResponse(DocgenErrorCode.INVALID_PARAM,
                    "参数非法：encoding 不支持，仅支持 utf-8/gbk");
        }
        int effectiveLimit = limit == null ? DEFAULT_LIMIT : limit;
        if (effectiveLimit < 1 || effectiveLimit > MAX_LIMIT) {
            return errorResponse(DocgenErrorCode.INVALID_PARAM,
                    "参数非法：limit 超出最大限制 " + MAX_LIMIT);
        }
        int maxRows = Math.min(effectiveLimit, TxtExportOptions.DEFAULT_MAX_ROWS);
        long start = System.currentTimeMillis();
        docgenExportMetrics.recordRequest();
        try {
            List<TxtRow> rows = itemExportService.buildRows();
            if (rows.size() > maxRows) {
                rows = rows.subList(0, maxRows);
            }
            TxtExportOptions options = new TxtExportOptions();
            options.setHeaders(ItemExportService.HEADERS);
            options.setMaxRows(maxRows);
            options.setCharset(charset);
            byte[] content = txtExportService.exportTxt(rows, options);
            ensureWithinTimeout(start, "物品 OpenAPI 导出");
            ResponseEntity<byte[]> response = attachmentResponse(content,
                    txtExportService.buildFileName(ItemExportService.FILE_NAME_PREFIX), charset);
            long elapsedMs = elapsedMs(start);
            docgenExportMetrics.recordSuccess(elapsedMs, rows.size(), content.length);
            log.info("物品 OpenAPI 导出成功, rows={}, elapsedMs={}, bytes={}",
                    rows.size(), elapsedMs, content.length);
            return response;
        } catch (DocgenExportException e) {
            docgenExportMetrics.recordFailure(e.getErrorCode());
            log.error("物品 OpenAPI 导出失败, code={}", e.getErrorCode(), e);
            return errorResponse(e.getErrorCode(), e.getMessage());
        }
    }

    private long elapsedMs(long start) {
        return System.currentTimeMillis() - start;
    }

    /**
     * 生成耗时兜底：超过配置阈值（docgen.export.timeout-ms）视为超时，返回 DOCGEN_001。
     *
     * @param start      开始时间戳
     * @param sceneName  场景名（日志用）
     */
    private void ensureWithinTimeout(long start, String sceneName) {
        long elapsedMs = elapsedMs(start);
        if (elapsedMs > docgenExportProperties.getTimeoutMs()) {
            log.error("{}超时, elapsedMs={}, timeoutMs={}", sceneName, elapsedMs,
                    docgenExportProperties.getTimeoutMs());
            throw new DocgenExportException(DocgenErrorCode.DATA_ASSEMBLY_FAILED, "导出超时，请稍后重试");
        }
    }

    private ResponseEntity<byte[]> attachmentResponse(byte[] content, String fileName, Charset charset) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "plain", charset));
        headers.setContentDisposition(ContentDisposition.attachment().filename(fileName).build());
        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }

    private ResponseEntity<Map<String, Object>> errorResponse(String errorCode, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("result", "ERROR");
        body.put("msg", errorCode + " " + message);
        body.put("data", null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    private ResponseEntity<Map<String, Object>> errorResponse(DocgenErrorCode errorCode, String message) {
        return errorResponse(errorCode.getCode(), message);
    }

    private ResponseEntity<Map<String, Object>> maintenanceResponse() {
        Map<String, Object> body = new HashMap<>();
        body.put("result", "ERROR");
        body.put("msg", "导出功能维护中");
        body.put("data", null);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }
}