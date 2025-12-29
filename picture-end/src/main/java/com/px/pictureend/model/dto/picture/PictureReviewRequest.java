package com.px.pictureend.model.dto.picture;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * packageName: com.px.pictureend.model.dto.picture
 *
 * @author: idpeng
 * @version: 1.0
 * @className: PictureReviewRequest
 * @date: 2025/12/28 00:40
 * @description: 图片审核请求
 */
@Data
public class PictureReviewRequest implements Serializable {
	/**
	 * id
	 */
	private Long id;

	/**
	 * 审核状态：0-待审核; 1-通过; 2-拒绝
	 */
	private Integer reviewStatus;

	/**
	 * 审核信息
	 */
	private String reviewMessage;

	@Serial
	private static final long serialVersionUID = 1L;
}
