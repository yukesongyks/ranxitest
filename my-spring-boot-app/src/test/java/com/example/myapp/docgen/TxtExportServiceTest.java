package com.example.myapp.docgen;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TxtExportService 单元测试。
 */
class TxtExportServiceTest {

    private final TxtExportService txtExportService = new TxtExportService();

    // ==================== exportTxt 测试 ====================

    @Test
    void should_exportTxt_when_validRows() {
        // Arrange
        TxtExportOptions options = new TxtExportOptions();
        options.setHeaders(Arrays.asList("ID", "名称", "描述", "价格"));
        List<TxtRow> rows = Arrays.asList(
                new TxtRow(Arrays.asList("1", "苹果", "红富士", "5.50")),
                new TxtRow(Arrays.asList("2", "香蕉", "进口香蕉", "3.20")));

        // Act
        byte[] bytes = txtExportService.exportTxt(rows, options);

        // Assert
        String content = new String(bytes, StandardCharsets.UTF_8);
        assertThat(content).isEqualTo("ID\t名称\t描述\t价格\r\n"
                + "1\t苹果\t红富士\t5.50\r\n"
                + "2\t香蕉\t进口香蕉\t3.20\r\n"
                + "共 2 条记录\r\n");
    }

    @Test
    void should_escapeTabCrLf_when_cellContainsSpecialChars() {
        // Arrange
        TxtExportOptions options = new TxtExportOptions();
        options.setHeaders(Collections.singletonList("描述"));
        List<TxtRow> rows = Collections.singletonList(
                new TxtRow(Collections.singletonList("第一行\n第二行\t制表\r回车")));

        // Act
        byte[] bytes = txtExportService.exportTxt(rows, options);

        // Assert
        String content = new String(bytes, StandardCharsets.UTF_8);
        assertThat(content).contains("第一行 第二行 制表 回车");
    }

    @Test
    void should_throwException_when_rowsExceedLimit() {
        // Arrange
        TxtExportOptions options = new TxtExportOptions();
        options.setHeaders(Collections.singletonList("ID"));
        options.setMaxRows(1);
        List<TxtRow> rows = Arrays.asList(
                new TxtRow(Collections.singletonList("1")),
                new TxtRow(Collections.singletonList("2")));

        // Act & Assert
        assertThatThrownBy(() -> txtExportService.exportTxt(rows, options))
                .isInstanceOf(DocgenExportException.class)
                .extracting(e -> ((DocgenExportException) e).getErrorCode())
                .isEqualTo("DOCGEN_002");
    }

    @Test
    void should_throwException_when_bytesExceedLimit() {
        // Arrange
        TxtExportOptions options = new TxtExportOptions();
        options.setHeaders(Collections.singletonList("ID"));
        options.setMaxBytes(10L);
        List<TxtRow> rows = Collections.singletonList(
                new TxtRow(Collections.singletonList("0123456789-very-long")));

        // Act & Assert
        assertThatThrownBy(() -> txtExportService.exportTxt(rows, options))
                .isInstanceOf(DocgenExportException.class)
                .extracting(e -> ((DocgenExportException) e).getErrorCode())
                .isEqualTo("DOCGEN_002");
    }

    @Test
    void should_exportTxt_when_emptyRows() {
        // Arrange
        TxtExportOptions options = new TxtExportOptions();
        options.setHeaders(Arrays.asList("ID", "名称"));

        // Act
        byte[] bytes = txtExportService.exportTxt(Collections.emptyList(), options);

        // Assert
        String content = new String(bytes, StandardCharsets.UTF_8);
        assertThat(content).isEqualTo("ID\t名称\r\n共 0 条记录\r\n");
    }

    @Test
    void should_throwException_when_optionsNull() {
        // Act & Assert
        assertThatThrownBy(() -> txtExportService.exportTxt(Collections.emptyList(), null))
                .isInstanceOf(DocgenExportException.class)
                .extracting(e -> ((DocgenExportException) e).getErrorCode())
                .isEqualTo("DOCGEN_003");
    }

    // ==================== buildFileName 测试 ====================

    @Test
    void should_buildFileName_when_prefixGiven() {
        // Act
        String fileName = txtExportService.buildFileName("items");

        // Assert
        assertThat(fileName).matches(Pattern.quote("items-") + "\\d{8}-\\d{6}\\.txt");
    }

    @Test
    void should_throwException_when_prefixIllegal() {
        // Act & Assert
        assertThatThrownBy(() -> txtExportService.buildFileName("../evil"))
                .isInstanceOf(DocgenExportException.class)
                .extracting(e -> ((DocgenExportException) e).getErrorCode())
                .isEqualTo("DOCGEN_003");
    }
}