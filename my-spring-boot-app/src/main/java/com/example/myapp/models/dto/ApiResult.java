package com.example.myapp.models.dto;

/**
 * REST 接口通用返回结构。
 * code=0 表示成功，非 0 表示失败；msg 为可读消息；data 为业务数据。
 */
public class ApiResult<T> {

    private final int code;
    private final String msg;
    private final T data;

    private ApiResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(0, "success", data);
    }

    public static <T> ApiResult<T> error(int code, String msg) {
        return new ApiResult<>(code, msg, null);
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
}
