package com.zhisangui.usercenter.common;



public class ResponseUtil {

    /**
     * 简化创建返回结果类的操作（success or error）
     * @param data 返回数据
     * @param msg 返回信息
     * @param description 返回信息的具体描述
     * @return 返回的结果类
     * @param <T> 泛型，适配返回任意类型
     */
    public static <T> BaseResponse<T> success(T data, String msg, String description) {
        return new BaseResponse<T>(0, data, msg, description);
    }
    public static <T> BaseResponse<T> success(T data, String msg) {
        return new BaseResponse<T>(0, data, msg, "");
    }

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<T>(0, data, "success", "");
    }


    public static <T> BaseResponse<T> error(ResultCode resultCode, String description) {
        return new BaseResponse<T>(resultCode.getCode(), null, resultCode.getMsg(), description);
    }

    public static <T> BaseResponse<T> error(ResultCode resultCode) {
        return new BaseResponse<T>(resultCode.getCode(), null, resultCode.getMsg(), "");
    }

}
