package com.px.pictureend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.px.pictureend.model.dto.picture.PictureQueryRequest;
import com.px.pictureend.model.dto.picture.PictureReviewRequest;
import com.px.pictureend.model.dto.picture.PictureUploadByBatchRequest;
import com.px.pictureend.model.dto.picture.PictureUploadRequest;
import com.px.pictureend.model.entity.Picture;
import com.px.pictureend.model.entity.User;
import com.px.pictureend.model.vo.picture.PictureVO;

import javax.servlet.http.HttpServletRequest;

/**
 * packageName: com.px.pictureend.service
 *
 * @author: idpeng
 * @version: 1.0
 * @interfaceName: PictureService
 * @date: 2025/12/23 23:35
 * @description: 图片服务
 */
public interface PictureService extends IService<Picture> {
    /**
     * 校验图片
     *
     * @param picture  图片
     */
    void validPicture(Picture picture);

    /**
     * 上传图片
     *
     * @param inputSource          多个文件
     * @param pictureUploadRequest 图片上传请求
     * @param loginUser            登录用户
     * @return 图片包装类
     */
    PictureVO uploadPicture(Object inputSource,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);

    /**
     * 获取图片包装类（单条）
     *
      * @param picture  图片
      * @param request  请求
      * @return  图片包装类
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    /**
     * 获取图片包装类（分页）
     *
      * @param picturePage  图片分页
      * @param request  请求
      * @return  图片包装类分页
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

	/**
	 * 获取查询对象
	 *
	 * @param pictureQueryRequest 图片查询请求
	 * @return 查询对象
	 */
	QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

	/**
	 * 图片审核
	 *
	 * @param pictureReviewRequest  图片审核请求
	 * @param loginUser 登录用户
	 */
	void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

	/**
	 * 填充审核参数
	 *
	 * @param picture  图片
	 * @param loginUser 登录用户
	 */
	void fillReviewParams(Picture picture, User loginUser);

	/**
	 * 图片批量上传
	 *
	 * @param pictureUploadByBatchRequest 图片批量上传请求
	 * @param loginUser 登录用户
	 * @return 图片数量
	 */
	Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser);


}
