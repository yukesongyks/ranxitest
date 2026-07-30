package com.example.myapp.exception;

/**
 * 业务异常，携带数字错误码与可读消息。
 * 用于算法/导出模块的参数校验与服务异常兜底。
 */
public class BizException extends RuntimeException {

    private final int code;

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
