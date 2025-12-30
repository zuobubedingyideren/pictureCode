package com.px.pictureend.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.px.pictureend.config.CosClientConfig;
import com.px.pictureend.exception.BusinessException;
import com.px.pictureend.exception.ErrorCode;
import com.px.pictureend.exception.ThrowUtils;
import com.px.pictureend.model.dto.file.UploadPictureResult;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * packageName: com.px.pictureend.manager
 *
 * @author: idpeng
 * @version: 1.0
 * @className: FileManager
 * @date: 2025/12/23 23:28
 * @description: 文件上传管理器，提供文件上传、删除等操作
 */

@Slf4j
@Service
@Deprecated
public class FileManager {
    @Resource
    private CosManager cosManager;

    @Resource
    private CosClientConfig cosClientConfig;

    /**
     * 上传图片到对象存储
     *
     * @param multipartFile 上传的图片文件
     * @param uploadPathPrefix 上传路径前缀
     * @return UploadPictureResult 上传结果，包含图片URL、名称、尺寸等信息
     */
    public UploadPictureResult uploadPicture(MultipartFile multipartFile, String uploadPathPrefix) {
        // 校验文件
        validPicture(multipartFile);

        String uuid = RandomUtil.randomString(16);
        String originalFilename = multipartFile.getOriginalFilename();

        // 生成上传文件名
        String uploadFileName = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid, FileUtil.getSuffix(originalFilename));
        // 生成上传路径
        String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFileName);

        File file = null;

        try {
            file = File.createTempFile(uploadPath, null);
            multipartFile.transferTo(file);
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);

            // 获取图片信息并计算宽高比
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            int picWidth = imageInfo.getWidth();
            int picHeight = imageInfo.getHeight();
            double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();

            // 返回上传结果
            return UploadPictureResult.builder()
                    .url(cosClientConfig.getHost() + "/" + uploadPath)
                    .picName(FileUtil.mainName(originalFilename))
                    .picSize(FileUtil.size(file))
                    .picWidth(picWidth)
                    .picHeight(picHeight)
                    .picScale(picScale)
                    .picFormat(imageInfo.getFormat())
                    .build();

        } catch (Exception e) {
            log.error("图片上传到对象存储失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            // 临时文件清理
            this.deleteTempFile(file);
        }


    }


    /**
     * 校验文件
     *
     * @param multipartFile  文件
     */
    private void validPicture(MultipartFile multipartFile) {
        ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "文件不能为空");
        // 1. 校验文件大小
        long fileSize = multipartFile.getSize();
        final long ONE_M = 1024 * 1024;
        ThrowUtils.throwIf(fileSize > 2 * ONE_M, ErrorCode.PARAMS_ERROR, "文件大小不能超过 2MB");
        // 2. 校验文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        // 允许上传的文件后缀列表（或者集合）
        final List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpeg", "png", "jpg", "webp");
        ThrowUtils.throwIf(!ALLOW_FORMAT_LIST.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "文件类型错误");
    }

    /**
     * 删除临时文件
     * 注意：当前方法的逻辑存在错误，如果file为null会直接返回，不会执行删除操作
     * 
     * @param file 要删除的临时文件对象，可以为null
     */
    public void deleteTempFile(File file) {
        if (file != null) {
            return;
        }
        boolean deleteResult = file.delete();
        if (!deleteResult) {
            log.error("删除临时文件失败, 文件路径 = {}", file.getAbsolutePath());
        }
    }
}
