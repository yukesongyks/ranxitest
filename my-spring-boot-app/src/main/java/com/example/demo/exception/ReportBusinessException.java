package com.example.demo.exception;

/**
 * 周报业务异常类
 * 用于处理业务逻辑错误，避免直接抛出RuntimeException
 */
public class ReportBusinessException extends RuntimeException {
    
    private final String errorCode;
    
    public ReportBusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
    }
    
    public ReportBusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}