package com.example.myapp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密哈希工具类，提供 MD5、SHA-1、SHA-256、SHA-512 等常用哈希算法封装。
 *
 * <p>支持字符串、字节数组、文件的哈希计算，以及哈希值校验。</p>
 *
 * <p><b>安全提示：</b>MD5 和 SHA-1 已被证明存在碰撞攻击，不应用于安全敏感场景。
 * 新项目推荐使用 {@link #ALGORITHM_SHA_256} 或 {@link #ALGORITHM_SHA_512}。</p>
 *
 * @author DTCoder
 * @date 2026/08/24
 */
public final class HashUtil {

    /** MD5 算法名称（已不安全，仅用于兼容旧系统） */
    public static final String ALGORITHM_MD5 = "MD5";

    /** SHA-1 算法名称（不推荐用于安全场景） */
    public static final String ALGORITHM_SHA1 = "SHA-1";

    /** SHA-256 算法名称 */
    public static final String ALGORITHM_SHA_256 = "SHA-256";

    /** SHA-512 算法名称 */
    public static final String ALGORITHM_SHA_512 = "SHA-512";

    /** 文件读取缓冲区大小 */
    private static final int BUFFER_SIZE = 8192;

    private HashUtil() {
        // 工具类，禁止实例化
    }

    // ==================== MD5 ====================

    /**
     * 计算字符串的 MD5 哈希值。
     *
     * @deprecated MD5 已被证明不安全，不应用于安全敏感场景，仅用于兼容旧系统
     * @param input 输入字符串
     * @return 32 位十六进制小写哈希字符串
     * @throws IllegalArgumentException 如果 input 为 null
     */
    @Deprecated
    public static String md5(String input) {
        return hash(ALGORITHM_MD5, input);
    }

    /**
     * 计算字节数组的 MD5 哈希值。
     *
     * @deprecated MD5 已被证明不安全，不应用于安全敏感场景，仅用于兼容旧系统
     * @param input 输入字节数组
     * @return 32 位十六进制小写哈希字符串
     * @throws IllegalArgumentException 如果 input 为 null
     */
    @Deprecated
    public static String md5(byte[] input) {
        return hash(ALGORITHM_MD5, input);
    }

    // ==================== SHA-1 ====================

    /**
     * 计算字符串的 SHA-1 哈希值。
     *
     * @param input 输入字符串
     * @return 40 位十六进制小写哈希字符串
     * @throws IllegalArgumentException 如果 input 为 null
     */
    public static String sha1(String input) {
        return hash(ALGORITHM_SHA1, input);
    }

    /**
     * 计算字节数组的 SHA-1 哈希值。
     *
     * @param input 输入字节数组
     * @return 40 位十六进制小写哈希字符串
     * @throws IllegalArgumentException 如果 input 为 null
     */
    public static String sha1(byte[] input) {
        return hash(ALGORITHM_SHA1, input);
    }

    // ==================== SHA-256 ====================

    /**
     * 计算字符串的 SHA-256 哈希值。
     *
     * @param input 输入字符串
     * @return 64 位十六进制小写哈希字符串
     * @throws IllegalArgumentException 如果 input 为 null
     */
    public static String sha256(String input) {
        return hash(ALGORITHM_SHA_256, input);
    }

    /**
     * 计算字节数组的 SHA-256 哈希值。
     *
     * @param input 输入字节数组
     * @return 64 位十六进制小写哈希字符串
     * @throws IllegalArgumentException 如果 input 为 null
     */
    public static String sha256(byte[] input) {
        return hash(ALGORITHM_SHA_256, input);
    }

    // ==================== SHA-512 ====================

    /**
     * 计算字符串的 SHA-512 哈希值。
     *
     * @param input 输入字符串
     * @return 128 位十六进制小写哈希字符串
     * @throws IllegalArgumentException 如果 input 为 null
     */
    public static String sha512(String input) {
        return hash(ALGORITHM_SHA_512, input);
    }

    /**
     * 计算字节数组的 SHA-512 哈希值。
     *
     * @param input 输入字节数组
     * @return 128 位十六进制小写哈希字符串
     * @throws IllegalArgumentException 如果 input 为 null
     */
    public static String sha512(byte[] input) {
        return hash(ALGORITHM_SHA_512, input);
    }

    // ==================== 通用哈希方法 ====================

    /**
     * 使用指定算法计算字符串的哈希值。
     *
     * @param algorithm 算法名称，如 {@link #ALGORITHM_SHA_256}
     * @param input     输入字符串
     * @return 十六进制小写哈希字符串
     * @throws IllegalArgumentException 如果 algorithm 为 null/blank、input 为 null 或算法不支持
     */
    public static String hash(String algorithm, String input) {
        if (algorithm == null || algorithm.isBlank()) {
            throw new IllegalArgumentException("algorithm must not be blank");
        }
        if (input == null) {
            throw new IllegalArgumentException("input must not be null");
        }
        return hash(algorithm, input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 使用指定算法计算字节数组的哈希值。
     *
     * @param algorithm 算法名称
     * @param input     输入字节数组
     * @return 十六进制小写哈希字符串
     * @throws IllegalArgumentException 如果 algorithm 为 null/blank、input 为 null 或算法不支持
     */
    public static String hash(String algorithm, byte[] input) {
        if (algorithm == null || algorithm.isBlank()) {
            throw new IllegalArgumentException("algorithm must not be blank");
        }
        if (input == null) {
            throw new IllegalArgumentException("input must not be null");
        }

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Unsupported algorithm: " + algorithm, e);
        }

        byte[] hashBytes = digest.digest(input);
        return bytesToHex(hashBytes);
    }

    // ==================== 文件哈希 ====================

    /**
     * 计算文件的哈希值。
     *
     * @param algorithm 算法名称
     * @param file      目标文件
     * @return 十六进制小写哈希字符串
     * @throws IllegalArgumentException 如果 algorithm 为 null/blank、file 为 null、文件不存在或算法不支持
     */
    public static String fileHash(String algorithm, File file) {
        if (algorithm == null || algorithm.isBlank()) {
            throw new IllegalArgumentException("algorithm must not be blank");
        }
        if (file == null) {
            throw new IllegalArgumentException("file must not be null");
        }
        if (!file.exists()) {
            throw new IllegalArgumentException("file does not exist: " + file.getAbsolutePath());
        }

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Unsupported algorithm: " + algorithm, e);
        }

        try (InputStream is = new FileInputStream(file)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read file: " + file.getAbsolutePath(), e);
        }

        return bytesToHex(digest.digest());
    }

    // ==================== 哈希校验 ====================

    /**
     * 校验输入字符串的哈希值是否与预期匹配。
     *
     * @param input        输入字符串
     * @param algorithm    算法名称
     * @param expectedHash 预期的十六进制哈希值
     * @return true 如果哈希值匹配，否则 false
     * @throws IllegalArgumentException 如果 input 为 null
     */
    public static boolean verify(String input, String algorithm, String expectedHash) {
        if (input == null) {
            throw new IllegalArgumentException("input must not be null");
        }
        if (expectedHash == null) {
            return false;
        }
        String actualHash = hash(algorithm, input);
        return actualHash.equalsIgnoreCase(expectedHash);
    }

    // ==================== 私有工具方法 ====================

    /**
     * 将字节数组转换为十六进制小写字符串。
     *
     * @param bytes 字节数组
     * @return 十六进制小写字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}