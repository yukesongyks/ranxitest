package com.example.demo.dto;

public class ApiResponse<T> {
    private int code;
    private T data;
    private String msg;
    
    public ApiResponse() {}
    
    public ApiResponse(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, data, "操作成功");
    }
    
    public static <T> ApiResponse<T> success(T data, String msg) {
        return new ApiResponse<>(200, data, msg);
    }
    
    public static <T> ApiResponse<T> error(int code, String msg) {
        return new ApiResponse<>(code, null, msg);
    }
    
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    
    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
}