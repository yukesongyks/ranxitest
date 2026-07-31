package com.example.myapp.controllers;

import com.example.myapp.services.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 导出服务 REST 接口。
 */
@RestController
@RequestMapping("/api")
public class ExportController {

    private final ExportService exportService;

    @Autowired
    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    /**
     * W04 导出接口：根据类型参数导出对应算法结果为 CSV 文件。
     */
    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@RequestParam String type,
                                         @RequestParam(required = false) String input,
                                         @RequestParam(required = false) String arr) {
        ExportService.ExportResult result = exportService.export(type, input, arr);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", result.getFilename());
        return ResponseEntity.ok().headers(headers).body(result.getContent());
    }
}
