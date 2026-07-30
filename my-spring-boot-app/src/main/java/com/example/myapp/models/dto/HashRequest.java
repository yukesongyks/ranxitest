package com.example.myapp.models.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 哈希计算接口入参 (I02)。
 */
public class HashRequest {

    @NotBlank(message = "text 不能为空")
    @Size(max = 10000, message = "text 长度不能超过 10000")
    private String text;

    private String algorithm;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
}
