package com.example.myapp.exception;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String message) {
        super(404, message);
    }

    public ResourceNotFoundException(String resourceName, Object id) {
        super(404, String.format("%s 不存在，ID: %s", resourceName, id));
    }
}
