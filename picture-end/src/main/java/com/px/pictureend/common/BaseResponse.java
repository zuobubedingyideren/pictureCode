package com.px.pictureend.common;

import com.px.pictureend.exception.ErrorCode;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * packageName: com.px.pictureend.common
 *
 * @author: idpeng
 * @version: 1.0
 * @className: BaseResponse
 * @date: 2025/12/4 13:18
 * @description: 响应结果类
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    /**
     * 默认构造函数
     */
    public BaseResponse() {
    }

    /**
     * 构造函数
     *
     * @param code 状态码
     * @param data 数据
     */
    public BaseResponse(int code, T data) {
        this.code = code;
        this.data = data;
    }

    /**
     * 构造函数
     *
     * @param errorCode 错误码
     */
    public BaseResponse(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.data = null;
        this.message = errorCode.getMessage();
    }

    /**
     * 构造函数
     *
     * @param code 状态码
     * @param data 数据
     * @param message 错误信息
     */
    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    @Serial
    private static final long serialVersionUID = 1L;

}
