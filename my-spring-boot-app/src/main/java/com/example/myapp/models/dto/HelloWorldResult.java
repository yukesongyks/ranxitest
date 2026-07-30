package com.example.myapp.models.dto;

/**
 * HelloWorld 接口出参 (I01)。
 */
public class HelloWorldResult {

    private final String message;

    public HelloWorldResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
