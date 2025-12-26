package com.px.pictureend.manager;

import com.px.pictureend.config.CosClientConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.*;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

/**
 * packageName: com.px.pictureend.manager
 *
 * @author: idpeng
 * @version: 1.0
 * @className: CosManager
 * @date: 2025/12/23 23:19
 * @description: cos管理器，用于上传文件和图片、获取文件和图片信息
 */
@Component
public class CosManager {
    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;

    /**
     * 上传文件
     *
     * @param key  文件名
     * @param file 文件
     * @return 响应结果
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 获取文件
     *
     * @param key 文件名
     * @return 响应结果
     */
    public COSObject getObject(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        return cosClient.getObject(getObjectRequest);
    }

    /**
     * 上传图片到对象存储服务
     * 该方法将本地文件上传到指定的存储桶，并设置图片处理参数
     *
     * @param key  文件在存储桶中的唯一标识（文件名）
     * @param file 需要上传的本地文件对象
     * @return PutObjectResult 上传操作的响应结果，包含上传状态和相关信息
     */
    public PutObjectResult putPictureObject(String key, File file) {
        // 创建上传对象请求，指定存储桶、文件名和文件对象
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);

        // 创建图片操作对象，用于设置图片处理相关参数
        PicOperations picOperations = new PicOperations();
        picOperations.setIsPicInfo(1);
        putObjectRequest.setPicOperations(picOperations);

        // 执行上传操作并返回结果
        return cosClient.putObject(putObjectRequest);
    }

}
