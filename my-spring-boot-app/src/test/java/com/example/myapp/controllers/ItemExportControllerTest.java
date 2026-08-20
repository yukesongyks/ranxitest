package com.example.myapp.controllers;

import com.example.myapp.docgen.DocgenExportProperties;
import com.example.myapp.docgen.TxtExportService;
import com.example.myapp.docgen.TxtRow;
import com.example.myapp.services.ItemExportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
        itemExportController = new ItemExportController(itemExportService, txtExportService, docgenExportProperties);
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
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.parseMediaType("text/plain;charset=UTF-8"));
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
        when(itemExportService.buildRows()).thenThrow(new com.example.myapp.docgen.DocgenExportException(
                "DOCGEN_002", "导出内容超限"));

        // Act
        ResponseEntity<?> response = itemExportController.exportOpenApiTxt(100, "utf-8");

        // Assert
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertThat(body.get("result")).isEqualTo("ERROR");
        assertThat((String) body.get("msg")).contains("DOCGEN_002");
    }
}