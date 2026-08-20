package com.example.myapp.docgen;

/**
 * docgen 模块业务异常。
 */
public class DocgenExportException extends RuntimeException {

    /** 错误码。 */
    private final String errorCode;

    /**
     * 构造异常。
     *
     * @param errorCode 错误码
     * @param message   提示信息
     */
    public DocgenExportException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 构造异常。
     *
     * @param errorCode 错误码枚举
     * @param message   提示信息
     */
    public DocgenExportException(DocgenErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode.getCode();
    }

    public String getErrorCode() {
        return errorCode;
    }
}