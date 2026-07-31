package com.example.myapp.services;

import com.example.myapp.common.BizException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ExportService 单元测试，覆盖三种导出类型、参数缺失、parseArray 异常场景。
 */
class ExportServiceTest {

    private AlgoService algoService;
    private ExportService exportService;

    @BeforeEach
    void setUp() {
        algoService = new AlgoService();
        exportService = new ExportService(algoService);
    }

    @Test
    @DisplayName("导出 helloworld 类型返回正确 CSV")
    void should_returnCsv_when_exportHelloworld() {
        ExportService.ExportResult result = exportService.export("helloworld", null, null);
        assertEquals("helloworld_export.csv", result.getFilename());
        String content = new String(result.getContent(), StandardCharsets.UTF_8);
        assertTrue(content.contains("result"));
        assertTrue(content.contains("Hello World"));
    }

    @Test
    @DisplayName("导出 hash 类型返回包含输入和哈希结果的 CSV")
    void should_returnCsv_when_exportHash() {
        ExportService.ExportResult result = exportService.export("hash", "hello", null);
        assertEquals("hash_export.csv", result.getFilename());
        String content = new String(result.getContent(), StandardCharsets.UTF_8);
        assertTrue(content.contains("input,result"));
        assertTrue(content.contains("hello"));
        assertTrue(content.contains("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824"));
    }

    @Test
    @DisplayName("导出 bubble-sort 类型返回包含排序结果的 CSV")
    void should_returnCsv_when_exportBubbleSort() {
        ExportService.ExportResult result = exportService.export("bubble-sort", null, "5,3,1");
        assertEquals("bubble_sort_export.csv", result.getFilename());
        String content = new String(result.getContent(), StandardCharsets.UTF_8);
        assertTrue(content.contains("input,result"));
        assertTrue(content.contains("[1, 3, 5]"));
    }

    @Test
    @DisplayName("type 为空时抛出 EXPORT_001")
    void should_throwException_when_typeNull() {
        BizException ex = assertThrows(BizException.class, () -> exportService.export(null, null, null));
        assertEquals("EXPORT_001", ex.getCode());
    }

    @Test
    @DisplayName("type 为空白字符串时抛出 EXPORT_001")
    void should_throwException_when_typeBlank() {
        BizException ex = assertThrows(BizException.class, () -> exportService.export("  ", null, null));
        assertEquals("EXPORT_001", ex.getCode());
    }

    @Test
    @DisplayName("type 为未知值时抛出 EXPORT_001")
    void should_throwException_when_typeUnknown() {
        BizException ex = assertThrows(BizException.class, () -> exportService.export("unknown", null, null));
        assertEquals("EXPORT_001", ex.getCode());
    }

    @Test
    @DisplayName("hash 类型缺少 input 参数时抛出 EXPORT_002")
    void should_throwException_when_hashMissingInput() {
        BizException ex = assertThrows(BizException.class, () -> exportService.export("hash", null, null));
        assertEquals("EXPORT_002", ex.getCode());
    }

    @Test
    @DisplayName("bubble-sort 类型缺少 arr 参数时抛出 EXPORT_002")
    void should_throwException_when_bubbleSortMissingArr() {
        BizException ex = assertThrows(BizException.class, () -> exportService.export("bubble-sort", null, null));
        assertEquals("EXPORT_002", ex.getCode());
    }

    @Test
    @DisplayName("parseArray 遇到非数字字符串时抛出 EXPORT_001")
    void should_throwException_when_arrContainsNonNumeric() {
        BizException ex = assertThrows(BizException.class,
                () -> exportService.export("bubble-sort", null, "1,abc,3"));
        assertEquals("EXPORT_001", ex.getCode());
    }

    @Test
    @DisplayName("CSV 字段含逗号时被双引号包裹转义")
    void should_escapeCsvField_when_inputContainsComma() {
        ExportService.ExportResult result = exportService.export("hash", "hello,world", null);
        String content = new String(result.getContent(), StandardCharsets.UTF_8);
        // 含逗号的字段应被双引号包裹
        assertTrue(content.contains("\"hello,world\""));
    }

    @Test
    @DisplayName("CSV 字段以 = 开头时被双引号包裹转义（防公式注入）")
    void should_escapeCsvField_when_inputStartsWithEquals() {
        ExportService.ExportResult result = exportService.export("hash", "=cmd", null);
        String content = new String(result.getContent(), StandardCharsets.UTF_8);
        assertTrue(content.contains("\"=cmd\""));
    }

    @Test
    @DisplayName("导出结果内容非空")
    void should_returnNonEmptyContent_when_validExport() {
        ExportService.ExportResult result = exportService.export("helloworld", null, null);
        assertNotNull(result.getContent());
        assertTrue(result.getContent().length > 0);
    }
}
