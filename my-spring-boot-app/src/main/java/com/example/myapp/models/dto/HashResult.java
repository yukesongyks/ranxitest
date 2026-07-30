package com.example.myapp.models.dto;

/**
 * 哈希计算接口出参 (I02)。
 */
public class HashResult {

    private final String algorithm;
    private final String digest;
    private final int length;

    public HashResult(String algorithm, String digest, int length) {
        this.algorithm = algorithm;
        this.digest = digest;
        this.length = length;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getDigest() {
        return digest;
    }

    public int getLength() {
        return length;
    }
}
