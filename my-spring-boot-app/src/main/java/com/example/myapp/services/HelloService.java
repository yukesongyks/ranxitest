package com.example.myapp.services;

import org.springframework.stereotype.Service;

/**
 * 问候服务，提供问候消息生成能力。
 *
 * @author DTCoder
 */
@Service
public class HelloService {

    private static final String DEFAULT_NAME = "World";
    private static final String GREETING_TEMPLATE = "Hello, %s!";

    /**
     * 根据传入的名字生成问候消息。
     * 当名字为空或空白时，返回默认问候 "Hello, World!"。
     *
     * @param name 用户名字，可为 null 或空
     * @return 问候消息字符串
     */
    public String greet(String name) {
        String effectiveName = (name == null || name.isBlank()) ? DEFAULT_NAME : name.trim();
        return String.format(GREETING_TEMPLATE, effectiveName);
    }
}
