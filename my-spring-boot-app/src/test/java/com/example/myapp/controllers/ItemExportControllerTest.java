package com.example.myapp.controllers;

import com.example.myapp.docgen.DocgenErrorCode;
import com.example.myapp.docgen.DocgenExportException;
import com.example.myapp.docgen.DocgenExportMetrics;
import com.example.myapp.docgen.DocgenExportProperties;
import com.example.myapp.docgen.TxtExportService;
import com.example.myapp.docgen.TxtRow;
import com.example.myapp.services.ItemExportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ItemExportController 单元测试。
 */
@ExtendWith(MockitoExtension.class)
class ItemExportControllerTest {

    @Mock
    private ItemExportService itemExportService;

    @Mock
    private TxtExportService txtExportService;

    private DocgenExportProperties docgenExportProperties;

    private ItemExportController itemExportController;

    @BeforeEach
    void setUp() {
        docgenExportProperties = new DocgenExportProperties();
        docgenExportProperties.setEnabled(true);
        itemExportController = new ItemExportController(itemExportService, txtExportService,
                docgenExportProperties, new DocgenExportMetrics());
    }

    // ==================== W01 exportPageTxt 测试 ====================

    @Test
    void should_returnTxtAttachment_when_enabled() {
        // Arrange
        when(itemExportService.buildRows()).thenReturn(Collections.singletonList(
                new TxtRow(Collections.singletonList("1"))));
        when(txtExportService.exportTxt(any(), any())).thenReturn("ID\r\n1\r\n".getBytes());
        when(txtExportService.buildFileName(anyString())).thenReturn("items-20260820-120000.txt");

        // Act
        ResponseEntity<?> response = itemExportController.exportPageTxt();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        MediaType contentType = response.getHeaders().getContentType();
        assertThat(contentType).isEqualTo(MediaType.parseMediaType("text/plain;charset=UTF-8"));
        assertThat(response.getHeaders().getFirst("Content-Disposition")).contains("attachment");
    }

    @Test
    void should_returnServiceUnavailable_when_disabled() {
        // Arrange
        docgenExportProperties.setEnabled(false);

        // Act
        ResponseEntity<?> response = itemExportController.exportPageTxt();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Test
    void should_rethrowOverLimit_when_exportOverLimit() {
        // Arrange
        when(itemExportService.buildRows()).thenReturn(
                Collections.singletonList(new TxtRow(Collections.singletonList("1"))));
        when(txtExportService.exportTxt(any(), any()))
                .thenThrow(new DocgenExportException(DocgenErrorCode.EXPORT_OVER_LIMIT, "导出内容超限"));

        // Act & Assert
        assertThatThrownBy(() -> itemExportController.exportPageTxt())
                .isInstanceOf(DocgenExportException.class)
                .extracting(e -> ((DocgenExportException) e).getErrorCode())
                .isEqualTo("DOCGEN_002");
    }

    @Test
    void should_rethrowDataAssemblyFailed_when_buildRowsFails() {
        // Arrange
        when(itemExportService.buildRows())
                .thenThrow(new DocgenExportException(DocgenErrorCode.DATA_ASSEMBLY_FAILED, "数据组装失败"));

        // Act & Assert
        assertThatThrownBy(() -> itemExportController.exportPageTxt())
                .isInstanceOf(DocgenExportException.class)
                .extracting(e -> ((DocgenExportException) e).getErrorCode())
                .isEqualTo("DOCGEN_001");
    }

    // ==================== O01 exportOpenApiTxt 测试 ====================

    @Test
    void should_returnTxt_when_validParams() {
        // Arrange
        when(itemExportService.buildRows()).thenReturn(Collections.emptyList());
        when(txtExportService.exportTxt(any(), any())).thenReturn("ID\r\n共 0 条记录\r\n".getBytes());
        when(txtExportService.buildFileName(anyString())).thenReturn("items-20260820-120000.txt");

        // Act
        ResponseEntity<?> response = itemExportController.exportOpenApiTxt(null, null);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void should_returnInvalidParam_when_limitExceeded() {
        // Act
        ResponseEntity<?> response = itemExportController.exportOpenApiTxt(100001, null);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertThat(body.get("result")).isEqualTo("ERROR");
        assertThat((String) body.get("msg")).contains("DOCGEN_003");
    }

    @Test
    void should_returnInvalidParam_when_encodingUnsupported() {
        // Act
        ResponseEntity<?> response = itemExportController.exportOpenApiTxt(100, "iso-8859-1");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertThat((String) body.get("msg")).contains("DOCGEN_003");
    }

    @Test
    void should_returnError_when_overLimit() {
        // Arrange
        when(itemExportService.buildRows()).thenThrow(new DocgenExportException(
                "DOCGEN_002", "导出内容超限"));

        // Act
        ResponseEntity<?> response = itemExportController.exportOpenApiTxt(100, "utf-8");

        // Assert
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertThat(body.get("result")).isEqualTo("ERROR");
        assertThat((String) body.get("msg")).contains("DOCGEN_002");
    }

    @Test
    void should_truncateRows_when_limitBelowDataSize() {
        // Arrange
        when(itemExportService.buildRows()).thenReturn(Arrays.asList(
                new TxtRow(Collections.singletonList("1")),
                new TxtRow(Collections.singletonList("2")),
                new TxtRow(Collections.singletonList("3"))));
        when(txtExportService.exportTxt(any(), any())).thenReturn("ID\r\n1\r\n2\r\n".getBytes());
        when(txtExportService.buildFileName(anyString())).thenReturn("items-20260820-120000.txt");

        // Act
        ResponseEntity<?> response = itemExportController.exportOpenApiTxt(2, "utf-8");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        ArgumentCaptor<List<TxtRow>> rowsCaptor = ArgumentCaptor.forClass(List.class);
        verify(txtExportService).exportTxt(rowsCaptor.capture(), any());
        assertThat(rowsCaptor.getValue()).hasSize(2);
    }

    @Test
    void should_returnGbkContent_when_encodingIsGbk() {
        // Arrange
        when(itemExportService.buildRows()).thenReturn(Collections.emptyList());
        when(txtExportService.exportTxt(any(), any())).thenReturn("ID\r\n共 0 条记录\r\n".getBytes());
        when(txtExportService.buildFileName(anyString())).thenReturn("items-20260820-120000.txt");

        // Act
        ResponseEntity<?> response = itemExportController.exportOpenApiTxt(null, "gbk");

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        MediaType contentType = response.getHeaders().getContentType();
        assertThat(contentType.toString().toLowerCase(Locale.ROOT)).contains("charset=gbk");
    }

    @Test
    void should_returnTimeoutError_when_generationExceedsTimeout() {
        // Arrange
        docgenExportProperties.setTimeoutMs(1L);
        when(itemExportService.buildRows()).thenReturn(Collections.emptyList());
        when(txtExportService.exportTxt(any(), any())).thenAnswer(invocation -> {
            Thread.sleep(50);
            return "ID\r\n共 0 条记录\r\n".getBytes();
        });

        // Act
        ResponseEntity<?> response = itemExportController.exportOpenApiTxt(null, "utf-8");

        // Assert
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertThat(body.get("result")).isEqualTo("ERROR");
        assertThat((String) body.get("msg")).contains("DOCGEN_001");
    }
}