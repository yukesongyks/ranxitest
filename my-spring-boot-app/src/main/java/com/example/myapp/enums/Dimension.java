package com.example.myapp.enums;

/**
 * 统计维度枚举。
 */
public enum Dimension {

    USER_TYPE,
    USER_LEVEL,
    DEPARTMENT,
    API_NAME;

    /**
     * 将字符串安全地转为枚举值，不匹配时返回 null。
     */
    public static Dimension fromString(String value) {
        if (value == null) {
            return null;
        }
        for (Dimension dim : values()) {
            if (dim.name().equalsIgnoreCase(value)) {
                return dim;
            }
        }
        return null;
    }
}
