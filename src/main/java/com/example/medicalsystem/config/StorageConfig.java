package com.example.medicalsystem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.FileSystemUtils;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StorageConfig {

    @Value("${model.storage.path}")
    private String modelStoragePath;

    @Value("${upload.path}")
    private String uploadPath;

    @PostConstruct
    public void init() {
        try {
            // 创建模型存储目录
            Path modelPath = Paths.get(modelStoragePath);
            if (!Files.exists(modelPath)) {
                Files.createDirectories(modelPath);
            }

            // 创建上传目录
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not create storage directories", e);
        }
    }
} 