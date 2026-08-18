package com.example.myapp.helloworld;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * HelloWorldController 单元测试
 */
@WebMvcTest(HelloWorldController.class)
class HelloWorldControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HelloWorldService helloWorldService;

    @Test
    @DisplayName("GET /api/hello 应返回 200 和问候消息")
    void shouldReturnHelloMessage_whenGetHello() throws Exception {
        // Arrange
        String expectedMessage = "Hello, World!";
        when(helloWorldService.getGreeting()).thenReturn(expectedMessage);

        // Act & Assert
        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    @Test
    @DisplayName("GET /api/hello 应返回 200 和自定义名称问候消息")
    void shouldReturnGreetingWithName_whenNameProvided() throws Exception {
        // Arrange
        String name = "Alice";
        String expectedMessage = "Hello, Alice!";
        when(helloWorldService.getGreeting(name)).thenReturn(expectedMessage);

        // Act & Assert
        mockMvc.perform(get("/api/hello").param("name", name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }
}