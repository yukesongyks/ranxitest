package com.example.myapp.controllers;

import com.example.myapp.dto.CallStatsResponse;
import com.example.myapp.dto.DemoResult;
import com.example.myapp.services.ApiCallLogService;
import com.example.myapp.services.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/demo")
public class DemoApiController {

    private final DemoService demoService;
    private final ApiCallLogService apiCallLogService;

    @Autowired
    public DemoApiController(DemoService demoService, ApiCallLogService apiCallLogService) {
        this.demoService = demoService;
        this.apiCallLogService = apiCallLogService;
    }

    @GetMapping("/hello")
    public DemoResult<String> helloWorld(HttpServletRequest request) {
        String result = demoService.helloWorld();
        apiCallLogService.log("helloworld", extractCaller(request), extractHeader(request, "X-User-Type"),
                extractHeader(request, "X-User-Level"), extractHeader(request, "X-Department"));
        return DemoResult.ok("helloworld", result);
    }

    @GetMapping("/hash")
    public DemoResult<String> hash(
            @RequestParam(defaultValue = "SHA-256") String algorithm,
            @RequestParam(defaultValue = "") String input,
            HttpServletRequest request) {
        String result = demoService.hash(algorithm, input);
        apiCallLogService.log("hash", extractCaller(request), extractHeader(request, "X-User-Type"),
                extractHeader(request, "X-User-Level"), extractHeader(request, "X-Department"));
        return DemoResult.ok("hash", result);
    }

    @PostMapping("/bubble-sort")
    public DemoResult<List<Integer>> bubbleSort(
            @RequestBody Map<String, List<Integer>> body,
            HttpServletRequest request) {
        List<Integer> input = body.getOrDefault("input", List.of());
        List<Integer> result = demoService.bubbleSort(input);
        apiCallLogService.log("bubblesort", extractCaller(request), extractHeader(request, "X-User-Type"),
                extractHeader(request, "X-User-Level"), extractHeader(request, "X-Department"));
        return DemoResult.ok("bubblesort", result);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(
            @RequestParam String type,
            @RequestParam String data) {
        byte[] csv = demoService.exportToCsv(type, data);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.set("Content-Disposition", "attachment; filename=demo-export.csv");
        return ResponseEntity.ok().headers(headers).body(csv);
    }

    @GetMapping("/stats")
    public CallStatsResponse stats() {
        return apiCallLogService.getStats();
    }

    private String extractCaller(HttpServletRequest request) {
        String caller = request.getHeader("X-Caller-Name");
        return caller != null ? caller : "anonymous";
    }

    private String extractHeader(HttpServletRequest request, String name) {
        return request.getHeader(name);
    }
}
