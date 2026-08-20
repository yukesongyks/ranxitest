package com.example.myapp.docgen;

/**
 * docgen 模块错误码枚举。
 *
 * <p>错误码格式：{MODULE}_{SEQ}，前缀 DOCGEN 对应文档生成模块。</p>
 */
public enum DocgenErrorCode {

    /** 数据组装失败（数据源异常）。 */
    DATA_ASSEMBLY_FAILED("DOCGEN_001", "数据组装失败"),

    /** 导出内容超限（行数/体积）。 */
    EXPORT_OVER_LIMIT("DOCGEN_002", "导出内容超限"),

    /** 参数非法（limit 越界、encoding 不支持）。 */
    INVALID_PARAM("DOCGEN_003", "参数非法");

    /** 错误码。 */
    private final String code;

    /** 默认提示信息。 */
    private final String defaultMessage;

    DocgenErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}