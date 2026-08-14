package com.example.myapp.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HelloService 单元测试类
 *
 * @author DTCoder
 */
@DisplayName("HelloService 单元测试")
class HelloServiceTest {

    private HelloService helloService;

    @BeforeEach
    void setUp() {
        helloService = new HelloService();
    }

    @Test
    @DisplayName("sayHello - 正常路径：返回默认问候消息")
    void should_returnDefaultGreeting_when_sayHello() {
        // Arrange
        // Act
        String result = helloService.sayHello();

        // Assert
        assertNotNull(result, "返回结果不应为空");
        assertEquals("Hello, World!", result, "应返回默认问候消息");
    }

    @Test
    @DisplayName("sayHelloTo - 正常路径：返回带姓名的问候消息")
    void should_returnGreetingWithName_when_validNameProvided() {
        // Arrange
        String name = "Alice";

        // Act
        String result = helloService.sayHelloTo(name);

        // Assert
        assertNotNull(result, "返回结果不应为空");
        assertEquals("Hello, Alice!", result, "应返回带姓名的问候消息");
    }

    @Test
    @DisplayName("sayHelloTo - 参数校验：姓名为空时抛出异常")
    void should_throwException_when_nameIsBlank() {
        // Arrange
        String blankName = "   ";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> helloService.sayHelloTo(blankName),
                "姓名为空时应抛出 IllegalArgumentException"
        );
        assertEquals("姓名不能为空", exception.getMessage(), "异常消息应正确");
    }

    @Test
    @DisplayName("sayHelloTo - 参数校验：姓名为null时抛出异常")
    void should_throwException_when_nameIsNull() {
        // Arrange
        String nullName = null;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> helloService.sayHelloTo(nullName),
                "姓名为null时应抛出 IllegalArgumentException"
        );
        assertEquals("姓名不能为空", exception.getMessage(), "异常消息应正确");
    }

    @Test
    @DisplayName("sayHelloTo - 边界值：姓名包含前后空格时应去除空格")
    void should_trimName_when_nameHasLeadingOrTrailingSpaces() {
        // Arrange
        String nameWithSpaces = "  Bob  ";

        // Act
        String result = helloService.sayHelloTo(nameWithSpaces);

        // Assert
        assertEquals("Hello, Bob!", result, "应去除姓名前后空格");
    }
}
