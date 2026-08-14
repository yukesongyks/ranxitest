package com.example.myapp.exception;

public class ResourceAlreadyExistsException extends BusinessException {

    public ResourceAlreadyExistsException(String message) {
        super(409, message);
    }

    public ResourceAlreadyExistsException(String resourceName, String field, Object value) {
        super(409, String.format("%s 的 %s '%s' 已存在", resourceName, field, value));
    }
}
