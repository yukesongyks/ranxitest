package com.example.myapp.sort;

/**
 * 统一出参结构 {code, msg, data}。
 *
 * @param <T> data 类型
 */
public class SortResponse<T> {

    private String code;
    private String msg;
    private T data;

    public SortResponse(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> SortResponse<T> success(T data) {
        return new SortResponse<>("OK", "SUCCESS", data);
    }

    public static <T> SortResponse<T> error(String code, String msg) {
        return new SortResponse<>(code, msg, null);
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
