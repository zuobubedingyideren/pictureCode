package com.px.pictureend.upload;

import cn.hutool.core.io.FileUtil;
import com.px.pictureend.exception.ErrorCode;
import com.px.pictureend.exception.ThrowUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * packageName: com.px.pictureend.upload
 *
 * @author: idpeng
 * @version: 1.0
 * @className: FilePictureUpload
 * @date: 2025/12/28 19:01
 * @description: 文件上传
 */
@Service
public class FilePictureUpload extends PictureUploadTemplate{



    /**
     * 验证上传的图片文件
     *
     * @param inputSource 输入源对象，此处应为MultipartFile类型
     */
    @Override
    protected void validPicture(Object inputSource) {
        // 将输入源转换为MultipartFile对象
        MultipartFile multipartFile = (MultipartFile) inputSource;
        // 检查文件是否为空
        ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "文件不能为空");

        // 获取文件大小
        long fileSize = multipartFile.getSize();

        // 定义1MB的字节数
        final long ONE_M = 1024 * 1024;

        // 检查文件大小是否超过10MB限制
        ThrowUtils.throwIf(fileSize > 10 * ONE_M, ErrorCode.PARAMS_ERROR, "文件大小不能超过 10MB");

        // 获取文件后缀名
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());

        // 定义允许的文件格式列表
        final List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");

        // 检查文件格式是否在允许的格式列表中
        ThrowUtils.throwIf(!ALLOW_FORMAT_LIST.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "文件类型错误");
    }



	/**
	 * 获取原始文件名
	 *
	 * @param inputSource 输入源对象，此处为MultipartFile类型
	 * @return 原始文件名
	 */
	@Override
	protected String getOriginFileName(Object inputSource) {
		MultipartFile multipartFile = (MultipartFile) inputSource;
		return multipartFile.getOriginalFilename();
	}


	/**
	 * 将输入源（MultipartFile）处理并保存到临时文件
	 *
	 * @param inputSource 输入源对象，此处为MultipartFile类型
	 * @param file 临时文件对象，用于存储上传的文件内容
	 * @throws IOException 文件传输过程中可能出现的IO异常
	 */
	@Override
	protected void processFile(Object inputSource, File file) throws IOException {
		MultipartFile multipartFile = (MultipartFile) inputSource;
		multipartFile.transferTo(file);
	}
}
