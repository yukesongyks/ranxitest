package com.example.myapp.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 问候服务类
 * <p>提供简单的问候消息生成功能</p>
 *
 * @author DTCoder
 */
@Service
public class HelloService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloService.class);

    private static final String DEFAULT_GREETING = "Hello, World!";
    private static final String GREETING_TEMPLATE = "Hello, %s!";

    /**
     * 获取默认问候消息
     *
     * @return 默认问候消息 "Hello, World!"
     */
    public String sayHello() {
        LOGGER.debug("生成默认问候消息");
        return DEFAULT_GREETING;
    }

    /**
     * 获取带姓名的问候消息
     *
     * @param name 用户姓名，不能为空
     * @return 带姓名的问候消息
     * @throws IllegalArgumentException 当姓名为空或null时抛出
     */
    public String sayHelloTo(String name) {
        if (name == null || name.trim().isEmpty()) {
            LOGGER.warn("尝试使用空姓名生成问候消息");
            throw new IllegalArgumentException("姓名不能为空");
        }

        String trimmedName = name.trim();
        String greeting = String.format(GREETING_TEMPLATE, trimmedName);
        LOGGER.debug("生成问候消息: {}", greeting);
        return greeting;
    }
}
