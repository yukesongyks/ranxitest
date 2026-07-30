package com.example.myapp.models.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 冒泡排序接口入参 (I03)。
 */
public class BubbleSortRequest {

    @NotEmpty(message = "array 不能为空")
    @Size(max = 1000, message = "array 长度不能超过 1000")
    private List<Integer> array;

    private String order;

    public List<Integer> getArray() {
        return array;
    }

    public void setArray(List<Integer> array) {
        this.array = array;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
