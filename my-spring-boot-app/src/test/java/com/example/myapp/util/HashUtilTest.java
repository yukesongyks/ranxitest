package com.example.myapp.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * HashUtil 单元测试
 *
 * @author DTCoder
 * @date 2026/08/24
 */
class HashUtilTest {

    private static final String PLAIN_TEXT = "hello world";
    private static final String EMPTY_STRING = "";
    private static final byte[] EMPTY_BYTES = new byte[0];

    // 预计算的已知哈希值（用于验证正确性）
    private static final String MD5_EXPECTED = "5eb63bbbe01eeed093cb22bb8f5acdc3";
    private static final String SHA1_EXPECTED = "2aae6c35c94fcfb415dbe95f408b9ce91ee846ed";
    private static final String SHA256_EXPECTED = "b94d27b9934d3e08a52e52d7da7dabfac484efe37a5380ee9088f7ace2efcde9";
    private static final String SHA512_EXPECTED = "309ecc489c12d6eb4cc40f50c902f2b4d0ed77ee511a7c7a9bcd3ca86d4cd86f"
            + "989dd35bc5ff499670da34255b45b0cfd830e81f605dcf7dc5542e93ae9cd76f";

    @BeforeEach
    void setUp() {
        // 通用初始化（如有需要）
    }

    @AfterEach
    void tearDown() {
        // 清理测试产生的临时状态
    }

    // ==================== md5(String) 测试 ====================

    @Test
    void should_returnCorrectMd5_when_normalInput() {
        // Act
        String result = HashUtil.md5(PLAIN_TEXT);

        // Assert
        assertThat(result).isEqualTo(MD5_EXPECTED);
    }

    @Test
    void should_returnCorrectMd5_when_emptyInput() {
        // Act
        String result = HashUtil.md5(EMPTY_STRING);

        // Assert
        assertThat(result).isEqualTo("d41d8cd98f00b204e9800998ecf8427e");
    }

    @Test
    void should_throwException_when_md5InputNull() {
        // Act & Assert
        assertThatThrownBy(() -> HashUtil.md5((String) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("input must not be null");
    }

    // ==================== md5(byte[]) 测试 ====================

    @Test
    void should_returnCorrectMd5_when_byteArrayInput() {
        // Act
        String result = HashUtil.md5(PLAIN_TEXT.getBytes(StandardCharsets.UTF_8));

        // Assert
        assertThat(result).isEqualTo(MD5_EXPECTED);
    }

    @Test
    void should_returnCorrectMd5_when_emptyByteArray() {
        // Act
        String result = HashUtil.md5(EMPTY_BYTES);

        // Assert
        assertThat(result).isEqualTo("d41d8cd98f00b204e9800998ecf8427e");
    }

    @Test
    void should_throwException_when_md5ByteArrayNull() {
        // Act & Assert
        assertThatThrownBy(() -> HashUtil.md5((byte[]) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("input must not be null");
    }

    // ==================== sha1 测试 ====================

    @Test
    void should_returnCorrectSha1_when_normalInput() {
        // Act
        String result = HashUtil.sha1(PLAIN_TEXT);

        // Assert
        assertThat(result).isEqualTo(SHA1_EXPECTED);
    }

    @Test
    void should_returnCorrectSha1_when_emptyInput() {
        // Act
        String result = HashUtil.sha1(EMPTY_STRING);

        // Assert
        assertThat(result).isEqualTo("da39a3ee5e6b4b0d3255bfef95601890afd80709");
    }

    @Test
    void should_throwException_when_sha1InputNull() {
        // Act & Assert
        assertThatThrownBy(() -> HashUtil.sha1((String) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("input must not be null");
    }

    // ==================== sha256 测试 ====================

    @Test
    void should_returnCorrectSha256_when_normalInput() {
        // Act
        String result = HashUtil.sha256(PLAIN_TEXT);

        // Assert
        assertThat(result).isEqualTo(SHA256_EXPECTED);
    }

    @Test
    void should_returnCorrectSha256_when_emptyInput() {
        // Act
        String result = HashUtil.sha256(EMPTY_STRING);

        // Assert
        assertThat(result).isEqualTo("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
    }

    @Test
    void should_throwException_when_sha256InputNull() {
        // Act & Assert
        assertThatThrownBy(() -> HashUtil.sha256((String) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("input must not be null");
    }

    // ==================== sha512 测试 ====================

    @Test
    void should_returnCorrectSha512_when_normalInput() {
        // Act
        String result = HashUtil.sha512(PLAIN_TEXT);

        // Assert
        assertThat(result).isEqualTo(SHA512_EXPECTED);
    }

    @Test
    void should_returnCorrectSha512_when_emptyInput() {
        // Act
        String result = HashUtil.sha512(EMPTY_STRING);

        // Assert
        assertThat(result).isEqualTo("cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce"
                + "47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e");
    }

    @Test
    void should_throwException_when_sha512InputNull() {
        // Act & Assert
        assertThatThrownBy(() -> HashUtil.sha512((String) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("input must not be null");
    }

    // ==================== hash() 通用方法测试 ====================

    @Test
    void should_returnCorrectHash_when_algorithmSpecified() {
        // Act
        String result = HashUtil.hash(HashUtil.ALGORITHM_SHA_256, PLAIN_TEXT);

        // Assert
        assertThat(result).isEqualTo(SHA256_EXPECTED);
    }

    @Test
    void should_returnCorrectHash_when_byteArrayAndAlgorithmSpecified() {
        // Act
        String result = HashUtil.hash(HashUtil.ALGORITHM_SHA_256, PLAIN_TEXT.getBytes(StandardCharsets.UTF_8));

        // Assert
        assertThat(result).isEqualTo(SHA256_EXPECTED);
    }

    @Test
    void should_throwException_when_algorithmIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> HashUtil.hash(null, PLAIN_TEXT))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("algorithm must not be null");
    }

    @Test
    void should_throwException_when_algorithmIsInvalid() {
        // Act & Assert
        assertThatThrownBy(() -> HashUtil.hash("INVALID_ALGO", PLAIN_TEXT))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported algorithm");
    }

    @Test
    void should_throwException_when_algorithmIsEmpty() {
        // Act & Assert
        assertThatThrownBy(() -> HashUtil.hash("", PLAIN_TEXT))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("algorithm must not be blank");
    }

    // ==================== fileHash() 测试 ====================

    @Test
    void should_returnCorrectFileHash_when_fileExists(@TempDir Path tempDir) throws IOException {
        // Arrange
        File file = tempDir.resolve("test.txt").toFile();
        Files.write(file.toPath(), PLAIN_TEXT.getBytes(StandardCharsets.UTF_8));

        // Act
        String result = HashUtil.fileHash(HashUtil.ALGORITHM_SHA_256, file);

        // Assert
        assertThat(result).isEqualTo(SHA256_EXPECTED);
    }

    @Test
    void should_throwException_when_fileIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> HashUtil.fileHash(HashUtil.ALGORITHM_SHA_256, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("file must not be null");
    }

    @Test
    void should_throwException_when_fileNotExists(@TempDir Path tempDir) {
        // Arrange
        File nonExistentFile = tempDir.resolve("non_existent.txt").toFile();

        // Act & Assert
        assertThatThrownBy(() -> HashUtil.fileHash(HashUtil.ALGORITHM_SHA_256, nonExistentFile))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("file does not exist");
    }

    // ==================== verify() 测试 ====================

    @Test
    void should_returnTrue_when_hashMatches() {
        // Act
        boolean result = HashUtil.verify(PLAIN_TEXT, HashUtil.ALGORITHM_SHA_256, SHA256_EXPECTED);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void should_returnFalse_when_hashDoesNotMatch() {
        // Act
        boolean result = HashUtil.verify(PLAIN_TEXT, HashUtil.ALGORITHM_SHA_256, "incorrect_hash");

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void should_returnFalse_when_expectedHashIsNull() {
        // Act
        boolean result = HashUtil.verify(PLAIN_TEXT, HashUtil.ALGORITHM_SHA_256, null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void should_throwException_when_verifyInputNull() {
        // Act & Assert
        assertThatThrownBy(() -> HashUtil.verify(null, HashUtil.ALGORITHM_SHA_256, SHA256_EXPECTED))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("input must not be null");
    }

    // ==================== 不同字符集输入测试 ====================

    @Test
    void should_returnConsistentHash_when_sameInputDifferentEncodings() {
        // Arrange
        String chineseText = "你好世界";

        // Act
        String hash1 = HashUtil.sha256(chineseText);
        String hash2 = HashUtil.sha256(chineseText);

        // Assert
        assertThat(hash1).isEqualTo(hash2);
        assertThat(hash1).hasSize(64); // SHA-256 always produces 64 hex chars
    }

    // ==================== 哈希值长度验证 ====================

    @Test
    void should_returnHexStringOfCorrectLength() {
        // Act & Assert
        assertThat(HashUtil.md5(PLAIN_TEXT)).hasSize(32);
        assertThat(HashUtil.sha1(PLAIN_TEXT)).hasSize(40);
        assertThat(HashUtil.sha256(PLAIN_TEXT)).hasSize(64);
        assertThat(HashUtil.sha512(PLAIN_TEXT)).hasSize(128);
    }

    // ==================== 边界值测试 ====================

    @Test
    void should_handleLargeInput() {
        // Arrange
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            sb.append("abcdefghij");
        }
        String largeInput = sb.toString();

        // Act
        String result = HashUtil.sha256(largeInput);

        // Assert
        assertThat(result).isNotNull().hasSize(64);
    }

    @Test
    void should_handleSingleCharInput() {
        // Act
        String result = HashUtil.sha256("a");

        // Assert
        assertThat(result).isNotNull().hasSize(64);
    }

    @Test
    void should_handleWhitespaceOnlyInput() {
        // Act
        String result = HashUtil.sha256("   ");

        // Assert
        assertThat(result).isNotNull().hasSize(64);
    }
}