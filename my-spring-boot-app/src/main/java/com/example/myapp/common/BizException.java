package com.example.myapp.common;

/**
 * 业务异常，携带错误码与提示信息。
 */
public class BizException extends RuntimeException {

    private final String code;

    public BizException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
