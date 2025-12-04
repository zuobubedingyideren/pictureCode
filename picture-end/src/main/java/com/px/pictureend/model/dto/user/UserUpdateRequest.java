package com.px.pictureend.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * packageName: com.px.pictureend.model.dto.user
 *
 * @author: idpeng
 * @version: 1.0
 * @className: UserUpdateRequest
 * @date: 2025/12/4 16:51
 * @description: 用户更新
 */
@Data
public class UserUpdateRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin
     */
    private String userRole;

    @Serial
    private static final long serialVersionUID = 1L;
}
