package com.example.myapp.enums;

/**
 * 枚举工具类，提供通用的字符串转枚举方法。
 */
public final class EnumUtil {

    private EnumUtil() {
    }

    /**
     * 将字符串安全地转为枚举值，不匹配时返回 null。
     * 匹配规则：忽略大小写比较。
     *
     * @param enumValues 枚举值数组
     * @param value      待匹配的字符串
     * @param <T>        枚举类型
     * @return 匹配的枚举值，或 null
     */
    public static <T extends Enum<T>> T fromString(T[] enumValues, String value) {
        if (value == null) {
            return null;
        }
        for (T e : enumValues) {
            if (e.name().equalsIgnoreCase(value)) {
                return e;
            }
        }
        return null;
    }
}
