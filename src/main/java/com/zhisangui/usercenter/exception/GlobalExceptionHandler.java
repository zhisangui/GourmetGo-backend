package com.zhisangui.usercenter.exception;

import com.zhisangui.usercenter.common.BaseResponse;
import com.zhisangui.usercenter.common.ResponseUtil;
import com.zhisangui.usercenter.common.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BussinessException:" + e.getResultCode().getMsg(), e);
        return ResponseUtil.error(e.getResultCode(), e.getDescripiton());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimException", e);
        return ResponseUtil.error(ResultCode.SYSTEM_ERROR);
    }
}
