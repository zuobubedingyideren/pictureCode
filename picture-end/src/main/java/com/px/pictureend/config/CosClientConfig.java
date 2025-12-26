package com.px.pictureend.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * packageName: com.px.pictureend.config
 *
 * @author: idpeng
 * @version: 1.0
 * @className: CosClientConfig
 * @date: 2025/12/23 23:11
 * @description: cos客户端配置类
 */
@Configuration
@ConfigurationProperties(prefix = "cos.client")
@Data
public class CosClientConfig {

    /**
     * 域名
     */
    private String host;

    /**
     * secretId
     */
    private String secretId;

    /**
     * 密钥（注意不要泄露）
     */
    private String secretKey;

    /**
     * 区域
     */
    private String region;

    /**
     * 桶名
     */
    private String bucket;


    /**
     * 创建COS客户端Bean
     *
     * 该方法用于初始化和配置腾讯云对象存储服务(COS)的客户端实例，
     * 通过提供的密钥信息和区域配置来创建安全的HTTPS连接
     *
     * @return 配置完成的COSClient实例，用于后续的对象存储操作
     */
    @Bean
    public COSClient cosClient() {
        // 创建COS凭证对象
        BasicCOSCredentials cosCredentials = new BasicCOSCredentials(secretId, secretKey);

        // 创建客户端配置并设置区域
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        // 设置HTTP协议为HTTPS，确保数据传输安全
        clientConfig.setHttpProtocol(HttpProtocol.https);
        // 创建COS客户端并传入凭证和配置
        return new COSClient(cosCredentials, clientConfig);
    }



}
