package com.example.myapp.controllers;

import com.example.myapp.exception.BizException;
import com.example.myapp.models.dto.ApiResult;
import com.example.myapp.models.dto.BubbleSortRequest;
import com.example.myapp.models.dto.BubbleSortResult;
import com.example.myapp.models.dto.HashRequest;
import com.example.myapp.models.dto.HashResult;
import com.example.myapp.models.dto.HelloWorldResult;
import com.example.myapp.services.AlgorithmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 算法 REST 接口 (I01-I03)。
 * 统一返回 ApiResult{code,msg,data}，code=0 成功。
 */
@RestController
@RequestMapping("/api/algorithm")
public class AlgorithmController {

    private static final Logger log = LoggerFactory.getLogger(AlgorithmController.class);

    private final AlgorithmService algorithmService;

    @Autowired
    public AlgorithmController(AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
    }

    /**
     * I01 HelloWorld
     */
    @GetMapping("/helloworld")
    public ApiResult<HelloWorldResult> helloWorld() {
        return ApiResult.success(algorithmService.hello());
    }

    /**
     * I02 哈希计算
     */
    @PostMapping("/hash")
    public ApiResult<HashResult> hash(@Valid @RequestBody HashRequest request) {
        try {
            HashResult result = algorithmService.hash(request.getText(), request.getAlgorithm());
            return ApiResult.success(result);
        } catch (BizException e) {
            log.warn("哈希接口业务异常 code={} msg={}", e.getCode(), e.getMessage());
            return ApiResult.error(e.getCode(), e.getMessage());
        }
    }

    /**
     * I03 冒泡排序
     */
    @PostMapping("/bubble-sort")
    public ApiResult<BubbleSortResult> bubbleSort(@Valid @RequestBody BubbleSortRequest request) {
        try {
            BubbleSortResult result = algorithmService.bubbleSort(request.getArray(), request.getOrder());
            return ApiResult.success(result);
        } catch (BizException e) {
            log.warn("冒泡排序接口业务异常 code={} msg={}", e.getCode(), e.getMessage());
            return ApiResult.error(e.getCode(), e.getMessage());
        }
    }
}
