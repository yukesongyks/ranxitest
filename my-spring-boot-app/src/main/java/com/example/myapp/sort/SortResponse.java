package com.example.myapp.sort;

/**
 * 统一出参结构 {code, msg, data}。
 *
 * @param <T> data 类型
 * @author AiWork
 * @date 2026-09-15
 */
public class SortResponse<T> {

    /** 结果码，OK 表示成功，其余为错误码。 */
    private String code;

    /** 提示信息。 */
    private String msg;

    /** 业务数据。 */
    private T data;

    /**
     * 构造统一出参。
     *
     * @param code 结果码
     * @param msg  提示信息
     * @param data 业务数据
     */
    public SortResponse(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 构造成功响应。
     *
     * @param data 业务数据
     * @param <T>  data 类型
     * @return 成功出参
     */
    public static <T> SortResponse<T> success(T data) {
        return new SortResponse<>(SortConstants.CODE_SUCCESS, SortConstants.MSG_SUCCESS, data);
    }

    /**
     * 构造错误响应。
     *
     * @param code 错误码
     * @param msg  提示信息
     * @param <T>  data 类型
     * @return 错误出参
     */
    public static <T> SortResponse<T> error(String code, String msg) {
        return new SortResponse<>(code, msg, null);
    }

    /**
     * 获取结果码。
     *
     * @return 结果码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置结果码。
     *
     * @param code 结果码
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取提示信息。
     *
     * @return 提示信息
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 设置提示信息。
     *
     * @param msg 提示信息
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 获取业务数据。
     *
     * @return 业务数据
     */
    public T getData() {
        return data;
    }

    /**
     * 设置业务数据。
     *
     * @param data 业务数据
     */
    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SortResponse{code='" + code + "', msg='" + msg + "', data=" + data + "}";
    }
}
