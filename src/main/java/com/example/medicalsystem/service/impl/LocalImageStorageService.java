package com.example.medicalsystem.service.impl;

import com.example.medicalsystem.service.ImageStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class LocalImageStorageService implements ImageStorageService {
    
    private static final Logger log = LoggerFactory.getLogger(LocalImageStorageService.class);
    
    @Value("${upload.path}")
    private String uploadPath;
    
    @PostConstruct
    public void init() {
        try {
            Path path = Paths.get(uploadPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            log.info("Storage location initialized: {}", path);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    @Override
    public String storeImage(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + getFileExtension(file.getOriginalFilename());
        Path targetPath = Paths.get(uploadPath, fileName);
        
        Files.createDirectories(targetPath.getParent());
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        // 返回相对路径，以便前端访问
        return "/uploads/" + fileName;
    }

    @Override
    public void deleteImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                String fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
                Path path = Paths.get(uploadPath, fileName);
                Files.deleteIfExists(path);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete image: " + imageUrl, e);
            }
        }
    }
    
    private String getFileExtension(String filename) {
        if (filename == null) return "";
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex == -1 ? "" : filename.substring(lastDotIndex);
    }
} 