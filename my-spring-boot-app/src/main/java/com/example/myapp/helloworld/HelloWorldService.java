package com.example.myapp.helloworld;

import org.springframework.stereotype.Service;

/**
 * 问候语业务服务
 */
@Service
public class HelloWorldService {

    private static final String DEFAULT_GREETING = "Hello, World!";
    private static final String GREETING_TEMPLATE = "Hello, %s!";

    /**
     * 获取默认问候语
     *
     * @return 默认问候消息
     */
    public String getGreeting() {
        return DEFAULT_GREETING;
    }

    /**
     * 获取包含指定名称的问候语
     *
     * @param name 被问候人名称
     * @return 包含名称的问候消息
     */
    public String getGreeting(String name) {
        if (name == null || name.trim().isEmpty()) {
            return DEFAULT_GREETING;
        }
        return String.format(GREETING_TEMPLATE, name.trim());
    }
}