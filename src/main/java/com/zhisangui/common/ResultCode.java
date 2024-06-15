package com.zhisangui.common;

import lombok.Getter;

@Getter
public enum ResultCode {
    PARAMS_ERROR(40000, "请求参数错误"),
    NULL_ERROR(40001, "请求数据为空"),
    NOT_LOGIN(40100, "用户未登录"),
    NO_AUTH(40101, "无权限"),
    SYSTEM_ERROR(50000, "系统出错");
    private final int code;
    private final String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
