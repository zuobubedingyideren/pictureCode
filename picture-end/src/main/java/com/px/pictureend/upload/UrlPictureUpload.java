package com.px.pictureend.upload;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.px.pictureend.exception.BusinessException;
import com.px.pictureend.exception.ErrorCode;
import com.px.pictureend.exception.ThrowUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;


/**
 * packageName: com.px.pictureend.upload
 *
 * @author: idpeng
 * @version: 1.0
 * @className: UrlPictureUpload
 * @date: 2025/12/28 19:51
 * @description: 通过URL上传图片
 */
@Service
public class UrlPictureUpload extends PictureUploadTemplate{


	/**
	 * 验证图片URL的有效性，包括URL格式、协议、内容类型和文件大小
	 *
	 * @param inputSource 输入源对象，此处为图片URL字符串
	 */
	@Override
	protected void validPicture(Object inputSource) {
		String fileUrl = (String) inputSource;
		ThrowUtils.throwIf(StrUtil.isBlank(fileUrl), ErrorCode.PARAMS_ERROR, "文件URL不能为空");

		try {
			new URL(fileUrl);
		} catch (MalformedURLException e) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件URL格式错误");
		}

		ThrowUtils.throwIf(!fileUrl.startsWith("http://") && !fileUrl.startsWith("https://"), ErrorCode.PARAMS_ERROR, "文件URL格式错误");

		HttpResponse httpResponse = null;
		try {
			// 发送HEAD请求获取文件信息
			httpResponse = HttpUtil.createRequest(Method.HEAD, fileUrl).execute();
			if (httpResponse.getStatus() != HttpStatus.HTTP_OK) {
				return;
			}
			String contentType = httpResponse.header("Content-Type");
			if (StrUtil.isBlank(contentType)) {
				final List<String> ALLOW_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/png", "image/gif", "image/webp");
				// 检查内容类型是否为允许的图片格式
				ThrowUtils.throwIf(!ALLOW_CONTENT_TYPES.contains(contentType), ErrorCode.PARAMS_ERROR, "文件类型错误");
			}
			String contentLengthStr = httpResponse.header("Content-Length");
			if (StrUtil.isNotBlank(contentLengthStr)) {
				try {
					// 验证文件大小不超过10MB
					long contentLength = Long.parseLong(contentLengthStr);
					final long ONE_M = 1024 * 1024;
					ThrowUtils.throwIf(contentLength > 10 * ONE_M, ErrorCode.PARAMS_ERROR, "文件大小不能超过 10MB");
				} catch (NumberFormatException e) {
					throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小格式错误");
				}
			} 
		} finally {
			// 关闭HTTP响应资源
			if (httpResponse != null) {
				httpResponse.close();
			}
		}
	}


	/**
	 * 从文件URL中提取原始文件名
	 *
	 * @param inputSource 输入源对象，此处为图片URL字符串
	 * @return 从URL中提取的文件名（不包含扩展名）
	 */
	@Override
	protected String getOriginFileName(Object inputSource) {
		String fileUrl = ((String) inputSource);
		return FileUtil.mainName(fileUrl);
	}


	/**
	 * 将输入源（URL）下载并保存到临时文件
	 *
	 * @param inputSource 输入源对象，此处为图片URL字符串
	 * @param file 临时文件对象，用于存储从URL下载的文件内容
	 * @throws IOException 文件下载过程中可能出现的IO异常
	 */
	@Override
	protected void processFile(Object inputSource, File file) throws IOException {
		String fileUrl = (String) inputSource;
		HttpUtil.downloadFile(fileUrl, file);
	}
}
