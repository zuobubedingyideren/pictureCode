package com.px.pictureend.model.dto.picture;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * packageName: com.px.pictureend.model.dto.picture
 *
 * @author: idpeng
 * @version: 1.0
 * @className: PictureUploadByBatchRequest
 * @date: 2025/12/28 01:00
 * @description: 批量导入图片请求
 */
@Data
public class PictureUploadByBatchRequest implements Serializable {
	/**
	 * 搜索文本
	 */
	private String searchText;
	/**
	 * 图片数量
	 */
	private Integer count = 10;
	/**
	 * 图片名称前缀
	 */
	private String namePrefix;

	@Serial
	private static final long serialVersionUID = 1L;
}
