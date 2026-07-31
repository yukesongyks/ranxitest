package com.example.myapp.common;

import com.example.myapp.controllers.AlgoController;
import com.example.myapp.controllers.ExportController;
import com.example.myapp.controllers.TrackController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * REST 接口全局异常处理，将异常转换为统一 ApiResult 响应。
 * 通过 assignableTypes 限定只处理新增 REST Controller，避免与现有 MVC GlobalExceptionHandler 冲突。
 */
@RestControllerAdvice(assignableTypes = {AlgoController.class, ExportController.class, TrackController.class})
public class RestExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(BizException.class)
    public ResponseEntity<ApiResult<Void>> handleBizException(BizException ex) {
        log.warn("业务异常: code={}, msg={}", ex.getCode(), ex.getMessage());
        return ResponseEntity.ok(ApiResult.fail(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResult<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("参数异常: {}", ex.getMessage());
        return ResponseEntity.ok(ApiResult.fail(ErrorCode.ALGO_002, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<Void>> handleGeneralException(Exception ex) {
        log.error("系统异常", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResult.fail("SYSTEM_ERROR", "系统异常: " + ex.getMessage()));
    }
}
