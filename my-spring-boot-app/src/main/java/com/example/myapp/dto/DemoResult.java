package com.example.myapp.dto;

import java.time.LocalDateTime;

public class DemoResult<T> {

    private boolean success;
    private String type;
    private T data;
    private LocalDateTime timestamp;

    public DemoResult() {
    }

    public DemoResult(boolean success, String type, T data) {
        this.success = success;
        this.type = type;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> DemoResult<T> ok(String type, T data) {
        return new DemoResult<>(true, type, data);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
