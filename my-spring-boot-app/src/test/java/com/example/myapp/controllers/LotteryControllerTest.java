package com.example.myapp.controllers;

import com.example.myapp.services.LotteryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LotteryController.class)
class LotteryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LotteryService lotteryService;

    @Test
    void lotteryPage_returnsLotteryView() throws Exception {
        mockMvc.perform(get("/lottery"))
                .andExpect(status().isOk())
                .andExpect(view().name("lottery"));
    }

    @Test
    void draw_returnsJsonWithNumber() throws Exception {
        when(lotteryService.draw()).thenReturn(42);

        mockMvc.perform(get("/lottery/draw"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(42));
    }
}
