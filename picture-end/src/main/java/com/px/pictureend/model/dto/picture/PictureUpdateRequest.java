package com.px.pictureend.model.dto.picture;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * packageName: com.px.pictureend.model.dto.picture
 *
 * @author: idpeng
 * @version: 1.0
 * @className: PictureUpdateRequest
 * @date: 2025/12/23 23:40
 * @description: 图片修改请求体
 */
@Data
public class PictureUpdateRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签
     */
    private List<String> tags;

    @Serial
    private static final long serialVersionUID = 1L;
}
