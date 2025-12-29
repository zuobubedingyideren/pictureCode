package com.px.pictureend.model.enums;

import lombok.Getter;

/**
 * packageName: com.px.pictureend.model.enums
 *
 * @author: idpeng
 * @version: 1.0
 * @className: PictureReviewStatusEnum
 * @date: 2025/12/28 01:05
 * @description: 图片审核状态枚举类
 */
@Getter
public enum PictureReviewStatusEnum {

	REVIEWING("待审核", 0),

	PASS("通过", 1),

	REJECT("拒绝", 2);

	/**
	 * 状态名
	 */
	private final String text;

	/**
	 * 状态值
	 */
	private final int value;

	/**
	 * 构造函数
	 *
	 * @param text  状态名
	 * @param value 状态值
	 */
	PictureReviewStatusEnum(String text, int value) {
		this.text = text;
		this.value = value;
	}


	/**
	 * 根据值获取对应的枚举对象
	 * 遍历所有枚举常量，查找与给定值匹配的枚举项
	 *
	 * @param value 枚举值
	 * @return 匹配的枚举对象，如果未找到则返回null
	 */
	public static PictureReviewStatusEnum getEnumByValue(Integer value) {
		if (value == null) {
			return null;
		}
		for (PictureReviewStatusEnum anEnum : PictureReviewStatusEnum.values()) {
			if (anEnum.value == value) {
				return anEnum;
			}
		}
		return null;
	}



}
