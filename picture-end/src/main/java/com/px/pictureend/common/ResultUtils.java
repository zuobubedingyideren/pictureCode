package com.px.pictureend.common;

import com.px.pictureend.exception.ErrorCode;

/**
 * packageName: com.px.pictureend.common
 *
 * @author: idpeng
 * @version: 1.0
 * @className: ResultUtils
 * @date: 2025/12/4 13:22
 * @description: 响应工具类，提供成功、失败的响应构建
 */
public class ResultUtils {
    /**
     * 成功
     * @param data  数据
     * @param <T> 数据类型
     * @return BaseResponse
     */
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 失败
     * @param errorCode 错误码
     * @return BaseResponse
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode){
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     * @param code 状态码
     * @param message 错误信息
     * @return BaseResponse
     */
    public static <T> BaseResponse<T> error(int code, String message){
        return new BaseResponse<>(code, null, message);
    }

    /**
     * 失败
     * @param errorCode 错误码
     * @param message 错误信息
     * @return BaseResponse
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode, String message){
        return new BaseResponse<>(errorCode.getCode(), null, message);
    }
}
