package com.example.myapp.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HelloService 单元测试类。
 *
 * @author DTCoder
 */
class HelloServiceTest {

    private HelloService helloService;

    @BeforeEach
    void setUp() {
        helloService = new HelloService();
    }

    @Test
    void should_returnDefaultGreeting_when_noNameProvided() {
        // Arrange & Act
        String result = helloService.greet(null);

        // Assert
        assertEquals("Hello, World!", result);
    }

    @Test
    void should_returnDefaultGreeting_when_emptyNameProvided() {
        // Arrange & Act
        String result = helloService.greet("");

        // Assert
        assertEquals("Hello, World!", result);
    }

    @Test
    void should_returnDefaultGreeting_when_blankNameProvided() {
        // Arrange & Act
        String result = helloService.greet("   ");

        // Assert
        assertEquals("Hello, World!", result);
    }

    @Test
    void should_returnPersonalizedGreeting_when_validNameProvided() {
        // Arrange
        String name = "Alice";

        // Act
        String result = helloService.greet(name);

        // Assert
        assertEquals("Hello, Alice!", result);
    }

    @Test
    void should_trimName_when_nameHasLeadingTrailingSpaces() {
        // Arrange
        String name = "  Bob  ";

        // Act
        String result = helloService.greet(name);

        // Assert
        assertEquals("Hello, Bob!", result);
    }
}
