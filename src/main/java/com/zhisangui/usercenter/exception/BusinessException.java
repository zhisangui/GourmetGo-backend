package com.zhisangui.usercenter.exception;

import com.zhisangui.usercenter.common.ResultCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{
    private static final long serialVersionUID = 5179144018539193624L;

    private final ResultCode resultCode;

    private final String descripiton;

    public BusinessException(ResultCode resultCode, String descripiton) {
        this.resultCode = resultCode;
        this.descripiton = descripiton;
    }

    public BusinessException(ResultCode resultCode) {
        this.resultCode = resultCode;
        this.descripiton = "";
    }
}
