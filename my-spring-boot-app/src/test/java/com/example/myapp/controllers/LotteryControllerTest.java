package com.example.myapp.controllers;

import com.example.myapp.services.LotteryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import org.springframework.http.MediaType;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LotteryController.class)
@TestPropertySource(properties = "spring.thymeleaf.check-template=false")
class LotteryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LotteryService lotteryService;

    @Test
    void drawEndpointReturnsFiveNumbersAsJson() throws Exception {
        when(lotteryService.draw()).thenReturn(List.of(9, 45, 123, 500, 678));

        mockMvc.perform(get("/api/lottery/draw"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[9,45,123,500,678]"))
                .andExpect(jsonPath("$", hasSize(5)));
    }

    @Test
    void lotteryPageIsRendered() throws Exception {
        mockMvc.perform(get("/lottery"))
                .andExpect(status().isOk())
                .andExpect(view().name("lottery/index"));
    }
}
