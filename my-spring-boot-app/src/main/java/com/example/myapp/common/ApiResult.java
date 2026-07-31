package com.example.myapp.common;

/**
 * 统一响应结果封装。
 *
 * @param <T> data 字段的业务数据类型
 */
public class ApiResult<T> {

    private String code;
    private String msg;
    private T data;

    public ApiResult() {
    }

    private ApiResult(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>("OK", "SUCCESS", data);
    }

    public static <T> ApiResult<T> success() {
        return new ApiResult<>("OK", "SUCCESS", null);
    }

    public static <T> ApiResult<T> fail(String code, String msg) {
        return new ApiResult<>(code, msg, null);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
