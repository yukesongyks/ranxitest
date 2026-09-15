package com.example.myapp.services;

import org.springframework.stereotype.Service;

/**
 * 问候模块 Service 层。
 * 无状态、只读，不依赖数据库或外部服务。
 */
@Service
public class GreetingService {

    /**
     * 返回固定问候语 "你好"。
     *
     * @return 问候语字符串
     */
    public String getGreeting() {
        return "你好";
    }
}
