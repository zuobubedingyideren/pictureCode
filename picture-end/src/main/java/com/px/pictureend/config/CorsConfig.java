package com.px.pictureend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * packageName: com.px.pictureend.config
 *
 * @author: idpeng
 * @version: 1.0
 * @className: CorsConfig
 * @date: 2025/12/4 14:16
 * @description: 跨域配置类
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

        /**
     * 添加跨域配置
     *
     * @param registry 跨域注册器，用于配置跨域访问规则
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 配置所有路径的跨域访问规则
        registry.addMapping("/**")
                // 允许携带认证信息
                .allowCredentials(true)
                // 允许携带认证信息
                .allowedOriginPatterns("*")
                // 允许的HTTP方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许所有请求头
                .allowedHeaders("*")
                // 暴露所有响应头给客户端
                .exposedHeaders("*");
    }

}
