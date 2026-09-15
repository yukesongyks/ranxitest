package com.example.myapp;

import com.example.myapp.controllers.GreetingController;
import com.example.myapp.services.GreetingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 问候模块测试：验证 GreetingService 逻辑与 GreetingController 两个端点。
 */
@SpringBootTest
@AutoConfigureMockMvc
class GreetingControllerTests {

    @Autowired
    private GreetingService greetingService;

    @Autowired
    private GreetingController greetingController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void serviceReturnsGreeting() {
        assertEquals("你好", greetingService.getGreeting());
    }

    @Test
    void apiHelloReturnsPlainText() throws Exception {
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("你好"));
    }

    @Test
    void openapiHelloReturnsJson() throws Exception {
        mockMvc.perform(get("/openapi/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.msg").value("SUCCESS"))
                .andExpect(jsonPath("$.data.greeting").value("你好"));
    }
}
