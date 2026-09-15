package com.example.myapp.sort;

/**
 * 排序业务异常。
 * <p>
 * 用于封装排序过程中的业务校验失败（如空数组、null 元素等），
 * 携带错误码与提示信息，替代直接抛出 {@link IllegalArgumentException}。
 *
 * @author AiWork
 * @date 2026-09-15
 */
public class SortException extends RuntimeException {

    /** 错误码，对应 {@link SortConstants} 中定义的常量。 */
    private final String code;

    /**
     * 构造排序业务异常。
     *
     * @param code    错误码
     * @param message 异常提示信息
     */
    public SortException(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 获取错误码。
     *
     * @return 错误码
     */
    public String getCode() {
        return code;
    }
}
