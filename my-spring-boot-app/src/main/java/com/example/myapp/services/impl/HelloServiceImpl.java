package com.example.myapp.services.impl;

import com.example.myapp.services.HelloService;
import org.springframework.stereotype.Service;

/**
 * Hello 服务实现。
 */
@Service
public class HelloServiceImpl implements HelloService {

    @Override
    public String greet() {
        return "Hello, World!";
    }
}