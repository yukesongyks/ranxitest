package com.example.myapp.services;

import com.example.myapp.common.BizException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AlgoService 单元测试，覆盖三个算法方法。
 */
class AlgoServiceTest {

    private AlgoService algoService;

    @BeforeEach
    void setUp() {
        algoService = new AlgoService();
    }

    @Test
    @DisplayName("helloworld 返回固定字符串 Hello World")
    void should_returnHelloWorld_when_validRequest() {
        String result = algoService.helloworld();
        assertEquals("Hello World", result);
    }

    @Test
    @DisplayName("hash 对已知输入返回正确的 SHA-256 值")
    void should_returnCorrectHash_when_validInput() {
        String result = algoService.hash("hello");
        assertEquals("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824", result);
    }

    @Test
    @DisplayName("hash 对空输入抛出 ALGO_003")
    void should_throwException_when_inputNull() {
        BizException ex = assertThrows(BizException.class, () -> algoService.hash(null));
        assertEquals("ALGO_003", ex.getCode());
    }

    @Test
    @DisplayName("hash 对空字符串抛出 ALGO_003")
    void should_throwException_when_inputEmpty() {
        BizException ex = assertThrows(BizException.class, () -> algoService.hash(""));
        assertEquals("ALGO_003", ex.getCode());
    }

    @Test
    @DisplayName("bubbleSort 对无序数组返回升序结果")
    void should_returnSortedArray_when_validInput() {
        int[] result = algoService.bubbleSort(new int[]{5, 3, 8, 1, 9, 2});
        assertArrayEquals(new int[]{1, 2, 3, 5, 8, 9}, result);
    }

    @Test
    @DisplayName("bubbleSort 不修改原始数组")
    void should_notMutateOriginalArray_when_validInput() {
        int[] original = {5, 3, 8, 1, 9, 2};
        int[] copy = original.clone();
        algoService.bubbleSort(original);
        assertArrayEquals(copy, original);
    }

    @Test
    @DisplayName("bubbleSort 对已排序数组返回相同结果")
    void should_returnSameArray_when_alreadySorted() {
        int[] result = algoService.bubbleSort(new int[]{1, 2, 3, 4, 5});
        assertArrayEquals(new int[]{1, 2, 3, 4, 5}, result);
    }

    @Test
    @DisplayName("bubbleSort 对空数组返回空数组")
    void should_returnEmptyArray_when_emptyInput() {
        int[] result = algoService.bubbleSort(new int[]{});
        assertArrayEquals(new int[]{}, result);
    }

    @Test
    @DisplayName("bubbleSort 对 null 输入抛出 ALGO_005")
    void should_throwException_when_arrNull() {
        BizException ex = assertThrows(BizException.class, () -> algoService.bubbleSort(null));
        assertEquals("ALGO_005", ex.getCode());
    }

    @Test
    @DisplayName("bubbleSort 对超过长度限制的数组抛出 ALGO_006")
    void should_throwException_when_arrTooLong() {
        int[] tooLong = new int[1001];
        BizException ex = assertThrows(BizException.class, () -> algoService.bubbleSort(tooLong));
        assertEquals("ALGO_006", ex.getCode());
    }
}
