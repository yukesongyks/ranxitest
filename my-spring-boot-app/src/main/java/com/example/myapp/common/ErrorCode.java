package com.example.myapp.common;

/**
 * 业务错误码常量定义。
 */
public final class ErrorCode {

    private ErrorCode() {
    }

    // 算法服务模块错误码
    public static final String ALGO_001 = "ALGO_001";
    public static final String ALGO_002 = "ALGO_002";
    public static final String ALGO_003 = "ALGO_003";
    public static final String ALGO_004 = "ALGO_004";
    public static final String ALGO_005 = "ALGO_005";
    public static final String ALGO_006 = "ALGO_006";

    // 导出服务模块错误码
    public static final String EXPORT_001 = "EXPORT_001";
    public static final String EXPORT_002 = "EXPORT_002";
    public static final String EXPORT_003 = "EXPORT_003";

    // 埋点服务模块错误码
    public static final String TRACK_001 = "TRACK_001";
    public static final String TRACK_002 = "TRACK_002";

    public static final String MSG_USER_NOT_FOUND = "用户不存在";
    public static final String MSG_USER_ID_NULL = "userId不能为空";
    public static final String MSG_INPUT_NULL = "input不能为空";
    public static final String MSG_HASH_ERROR = "哈希计算异常";
    public static final String MSG_ARRAY_NULL = "数组不能为空";
    public static final String MSG_ARRAY_TOO_LONG = "数组长度超过限制（最大1000）";
    public static final String MSG_EXPORT_TYPE_INVALID = "type参数不合法";
    public static final String MSG_EXPORT_MISSING_PARAM = "导出类型缺少必要参数";
    public static final String MSG_EXPORT_FAIL = "导出生成失败";
    public static final String MSG_DIMENSION_INVALID = "dimension参数不合法";
    public static final String MSG_DATE_FORMAT_ERROR = "日期格式错误";
}
