package com.example.myapp.controllers;

import com.example.myapp.services.HelloService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * HelloController 单元测试。
 */
@WebMvcTest(HelloController.class)
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HelloService helloService;

    @Test
    @DisplayName("正常请求应返回 Hello, World!")
    void should_returnHelloWorld_when_validRequest() throws Exception {
        // Arrange
        when(helloService.greet()).thenReturn("Hello, World!");

        // Act & Assert
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, World!"));
    }

    @Test
    @DisplayName("服务返回自定义消息时，应正确返回")
    void should_returnCustomMessage_when_serviceReturnsCustom() throws Exception {
        // Arrange
        when(helloService.greet()).thenReturn("Hello, DTCoder!");

        // Act & Assert
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, DTCoder!"));
    }
}