package com.px.pictureend.exception;

import lombok.Getter;

/**
 * packageName: com.px.pictureend.exception
 *
 * @author: idpeng
 * @version: 1.0
 * @className: BusinessException
 * @date: 2025/12/4 13:11
 * @description: 业务异常类，用于表示业务逻辑错误，继承自RuntimeException类
 */
@Getter
public class BusinessException extends RuntimeException{

    /**
     * 错误码
     */
    private final int code;


    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

}
