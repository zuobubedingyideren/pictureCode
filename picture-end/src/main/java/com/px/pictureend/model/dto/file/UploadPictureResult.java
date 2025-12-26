package com.px.pictureend.model.dto.file;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * packageName: com.px.pictureend.model.dto.file
 *
 * @author: idpeng
 * @version: 1.0
 * @className: UploadPictureResult
 * @date: 2025/12/23 23:36
 * @description: 图片上传结果
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadPictureResult {
    /**
     * 图片地址
     */
    private String url;

    /**
     * 图片名称
     */
    private String picName;

    /**
     * 文件体积
     */
    private Long picSize;

    /**
     * 图片宽度
     */
    private int picWidth;

    /**
     * 图片高度
     */
    private int picHeight;

    /**
     * 图片宽高比
     */
    private Double picScale;

    /**
     * 图片格式
     */
    private String picFormat;

}
