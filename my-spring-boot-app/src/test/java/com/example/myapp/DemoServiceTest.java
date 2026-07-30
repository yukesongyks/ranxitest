package com.example.myapp;

import com.example.myapp.services.DemoService;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DemoServiceTest {

    private final DemoService demoService = new DemoService();

    @Test
    void helloWorldReturnsGreeting() {
        assertEquals("Hello, World!", demoService.helloWorld());
    }

    @Test
    void hashReturnsCorrectSha256() {
        String result = demoService.hash("SHA-256", "hello");
        assertEquals(64, result.length());
    }

    @Test
    void hashThrowsForUnsupportedAlgorithm() {
        assertThrows(IllegalArgumentException.class, () -> demoService.hash("INVALID-ALGO", "test"));
    }

    @Test
    void bubbleSortReturnsSortedAscending() {
        List<Integer> input = Arrays.asList(5, 3, 8, 1, 9, 2);
        List<Integer> result = demoService.bubbleSort(input);
        assertEquals(Arrays.asList(1, 2, 3, 5, 8, 9), result);
    }

    @Test
    void bubbleSortReturnsEmptyForEmptyInput() {
        List<Integer> result = demoService.bubbleSort(List.of());
        assertTrue(result.isEmpty());
    }

    @Test
    void exportToCsvContainsHeader() {
        byte[] csv = demoService.exportToCsv("helloworld", "Hello, World!");
        String content = new String(csv);
        assertTrue(content.contains("type,value"));
        assertTrue(content.contains("helloworld"));
    }
}
