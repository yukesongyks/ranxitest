package com.example.myapp.enums;

/**
 * 接口名称枚举，用于埋点记录。
 */
public enum ApiName {

    HELLOWORLD,
    HASH,
    BUBBLE_SORT;

    /**
     * 将字符串安全地转为枚举值，不匹配时返回 null。
     */
    public static ApiName fromString(String value) {
        if (value == null) {
            return null;
        }
        for (ApiName name : values()) {
            if (name.name().equalsIgnoreCase(value)) {
                return name;
            }
        }
        return null;
    }
}
