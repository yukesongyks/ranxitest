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

    /**
     * 携带原始异常 cause 的构造器，保留原始异常栈便于排查。
     *
     * @param code    错误码
     * @param message 提示信息
     * @param cause   原始异常
     */
    public BizException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
