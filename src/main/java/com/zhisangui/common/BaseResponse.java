package com.zhisangui.common;

import lombok.Data;

import java.io.Serializable;


/**
 * 返回类：封装相应给前端的所有数据
 *
 * @param <T>
 */
@Data
public class BaseResponse<T> implements Serializable {
    private static final long serialVersionUID = 53825974616354611L;

    private int code;

    private T data;

    private String msg;

    private String description;

    public BaseResponse(int code, T data, String msg, String description) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.description = description;
    }
}
