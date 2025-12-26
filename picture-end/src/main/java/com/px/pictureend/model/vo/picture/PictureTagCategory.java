package com.px.pictureend.model.vo.picture;

import lombok.Data;

import java.util.List;

/**
 * packageName: com.px.pictureend.model.vo.picture
 *
 * @author: idpeng
 * @version: 1.0
 * @className: PictureTagCategory
 * @date: 2025/12/23 23:44
 * @description: 图片标签分类VO
 */
@Data
public class PictureTagCategory {
    /**
     * 标签列表
     */
    private List<String> tagList;

    /**
     * 分类列表
     */
    private List<String> categoryList;
}
