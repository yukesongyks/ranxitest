package com.example.myapp.helloworld;

import java.util.Objects;

/**
 * 问候语响应视图对象
 */
public class HelloWorldVO {

    private String message;

    public HelloWorldVO() {
    }

    public HelloWorldVO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HelloWorldVO that = (HelloWorldVO) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }
}