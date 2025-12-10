package com.px.pictureend.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * packageName: com.px.pictureend.model.dto.user
 *
 * @author: idpeng
 * @version: 1.0
 * @className: UserLoginRequest
 * @date: 2025/12/4 16:08
 * @description: 登录请求类
 */
@Data
public class UserLoginRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;
}
