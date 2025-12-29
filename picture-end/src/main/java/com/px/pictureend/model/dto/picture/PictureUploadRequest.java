package com.px.pictureend.model.dto.picture;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * packageName: com.px.pictureend.model.dto.picture
 *
 * @author: idpeng
 * @version: 1.0
 * @className: PictureUploadRequest
 * @date: 2025/12/23 23:42
 * @description: 图片上传请求体
 */
@Data
public class PictureUploadRequest implements Serializable {
    /**
     * 图片 id（用于修改）
     */
    private Long id;


	/**
	 * 文件地址
	 */
	private String fileUrl;

	/**
	 * 图片名称
	 */
	private String picName;

    @Serial
    private static final long serialVersionUID = 1L;
}
