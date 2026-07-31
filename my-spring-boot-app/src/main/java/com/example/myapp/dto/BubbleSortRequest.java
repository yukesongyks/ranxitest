package com.example.myapp.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 冒泡排序接口请求体。
 */
public class BubbleSortRequest {

    @NotNull(message = "数组不能为空")
    @Size(max = 1000, message = "数组长度超过限制（最大1000）")
    private int[] arr;

    @NotNull(message = "userId不能为空")
    private Long userId;

    public int[] getArr() {
        return arr;
    }

    public void setArr(int[] arr) {
        this.arr = arr;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
