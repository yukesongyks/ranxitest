package com.example.myapp.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 哈希算法接口请求体。
 */
public class HashRequest {

    @NotBlank(message = "input不能为空")
    private String input;

    @NotNull(message = "userId不能为空")
    private Long userId;

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
