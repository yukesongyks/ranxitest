package com.example.myapp;

import com.example.myapp.controllers.DemoApiController;
import com.example.myapp.dto.CallStatsResponse;
import com.example.myapp.dto.DemoResult;
import com.example.myapp.services.ApiCallLogService;
import com.example.myapp.services.DemoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DemoApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void helloWorldEndpointReturnsGreeting() throws Exception {
        mockMvc.perform(get("/api/demo/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.type").value("helloworld"))
                .andExpect(jsonPath("$.data").value("Hello, World!"));
    }

    @Test
    void hashEndpointReturnsHexResult() throws Exception {
        mockMvc.perform(get("/api/demo/hash")
                        .param("algorithm", "SHA-256")
                        .param("input", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.type").value("hash"));
    }

    @Test
    void bubbleSortEndpointReturnsSortedArray() throws Exception {
        mockMvc.perform(post("/api/demo/bubble-sort")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"input\":[3,1,2]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.type").value("bubblesort"))
                .andExpect(jsonPath("$.data[0]").value(1))
                .andExpect(jsonPath("$.data[1]").value(2))
                .andExpect(jsonPath("$.data[2]").value(3));
    }

    @Test
    void exportEndpointReturnsCsv() throws Exception {
        mockMvc.perform(get("/api/demo/export")
                        .param("type", "helloworld")
                        .param("data", "Hello, World!"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv"));
    }

    @Test
    void statsEndpointReturnsStats() throws Exception {
        mockMvc.perform(get("/api/demo/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCalls").exists());
    }
}
