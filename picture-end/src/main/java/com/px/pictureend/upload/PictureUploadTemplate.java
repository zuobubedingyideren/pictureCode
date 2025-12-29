package com.px.pictureend.upload;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.px.pictureend.config.CosClientConfig;
import com.px.pictureend.exception.BusinessException;
import com.px.pictureend.exception.ErrorCode;
import com.px.pictureend.manager.CosManager;
import com.px.pictureend.model.dto.file.UploadPictureResult;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * packageName: com.px.pictureend.upload
 *
 * @author: idpeng
 * @version: 1.0
 * @className: PictureUploadTemplate
 * @date: 2025/12/28 18:25
 * @description: 图片上传模板
 */
@Slf4j
public abstract class PictureUploadTemplate {
	@Resource
	private CosClientConfig cosClientConfig;

	@Resource
	private CosManager cosManager;

    /**
     * 上传图片到对象存储
     *
     * @param inputSource 输入源对象，可以是多种类型，如文件、字节数组等
     * @param uploadPathPrefix 上传路径前缀，用于指定存储位置
     * @return UploadPictureResult 上传结果对象，包含上传成功后的相关信息
     */
    public UploadPictureResult uploadPicture(Object inputSource, String uploadPathPrefix) {
        // 验证图片格式和内容
        validPicture(inputSource);
        // 生成16位随机UUID作为文件名的一部分，避免重复
        String uuid = RandomUtil.randomString(16);
        // 获取原始文件名
        String originFileName = getOriginFileName(inputSource);

        // 构造上传文件名，格式为：日期_随机字符串.原扩展名
        String uploadFileName = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid, FileUtil.getSuffix(originFileName));

        // 构造上传路径
        String uploadPath = String.format("/*%s/%s", uploadPathPrefix, uploadFileName);
        File file = null;

        try {
            // 创建临时文件
            file = File.createTempFile(uploadPath, null);

            // 处理输入源到临时文件
            processFile(inputSource, file);

            // 上传图片到对象存储服务
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            // 获取上传图片的元信息
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            // 构建并返回上传结果
            return buildResult(originFileName, file, uploadPath, imageInfo);
        } catch (IOException e) {
            log.error("图片上传到对象存储失败",e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            // 清理临时文件
            this.deleteTempFile(file);
        }
    }



	/**
	 * 验证图片
	 *
	 * @param inputSource 输入源对象，可以是多种类型，如文件、字节数组等
	 */
	protected abstract void validPicture(Object inputSource);

	/**
	 * 获取原始文件名
	 *
	 * @param inputSource 输入源对象，可以是多种类型，如文件、字节数组等
	 * @return 原始文件名
	 */
	protected abstract String getOriginFileName(Object inputSource);

	/**
	 * 处理输入源到临时文件
	 *
	 * @param inputSource 输入源对象，可以是多种类型，如文件、字节数组等
	 * @param file 临时文件
	 */
	protected abstract void processFile(Object inputSource, File file) throws IOException;

    /**
     * 构建图片上传结果对象
     *
     * @param originFileName 原始文件名
     * @param file 本地文件对象
     * @param uploadPath 上传路径
     * @param imageInfo 图片信息对象
     * @return UploadPictureResult 包含上传图片详细信息的结果对象
     */
    private UploadPictureResult buildResult(String originFileName, File file, String uploadPath, ImageInfo imageInfo) {
        // 获取图片宽度和高度
        int picWith = imageInfo.getWidth();
        int picHeight = imageInfo.getHeight();
        // 计算图片宽高比
        double picScale = NumberUtil.round(picWith * 1.0 / picHeight, 2).doubleValue();

        // 构建上传结果对象
        return UploadPictureResult.builder()
                .url(cosClientConfig.getHost() + "/" + uploadPath)
                .picName(originFileName)
                .picSize(file.length())
                .picWidth(picWith)
                .picHeight(picHeight)
                .picScale(picScale)
                .picFormat(imageInfo.getFormat())
                .build();
    }


	/**
	 * 删除临时文件
	 *
	 * @param file 临时文件
	 */
	public void deleteTempFile(File file) {
		if (file == null) {
			return;
		}

		// 删除临时文件
		boolean deleteResult = file.delete();
		if (!deleteResult) {
			log.error("file delete error, filepath = {}", file.getAbsolutePath());
		}
	}
}
