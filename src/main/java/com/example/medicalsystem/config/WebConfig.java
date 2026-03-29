package com.example.medicalsystem.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Web配置类
 * 
 * 定义Web MVC相关的配置，如跨域资源共享策略、静态资源处理等。
 * 实现WebMvcConfigurer接口，自定义Spring MVC的配置。
 * 处理上传文件目录的映射，使文件可通过URL直接访问。
 * 
 * @author Medical System Team
 * @version 1.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    /**
     * 日志记录器
     * 用于记录配置过程中的信息
     */
    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);
    
    /**
     * 上传路径
     * 从配置文件中注入，指定文件上传的目标目录
     */
    @Value("${upload.path}")
    private String uploadPath;
    
    /**
     * 配置跨域资源共享
     * 
     * 允许来自特定源的跨域请求访问API。
     * 设置允许的HTTP方法、请求头以及凭据。
     * 配置预检请求的缓存时间。
     * 
     * @param registry CORS注册表，用于添加CORS配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 添加资源处理器
     * 
     * 配置静态资源的访问路径映射。
     * 将上传目录映射到/uploads/**路径，使上传的文件可通过URL访问。
     * 配置静态资源的缓存策略和资源链。
     * 
     * @param registry 资源处理器注册表，用于添加资源处理器
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将/uploads/**的请求映射到文件系统中的上传目录
        Path uploadDir = Paths.get(uploadPath);
        String uploadAbsolutePath = uploadDir.toFile().getAbsolutePath();
        
        log.info("配置上传目录资源映射: /uploads/** -> {}", uploadAbsolutePath);
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadAbsolutePath + "/")
                .setCachePeriod(3600)
                .resourceChain(true);
                
        // 添加其他静态资源路径
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
} 