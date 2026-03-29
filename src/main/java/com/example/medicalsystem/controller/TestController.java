package com.example.medicalsystem.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/config")
    public Map<String, Object> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("uploadPath", uploadPath);
        
        File uploadDir = new File(uploadPath);
        config.put("uploadDirExists", uploadDir.exists());
        config.put("uploadDirIsDirectory", uploadDir.isDirectory());
        config.put("uploadDirCanRead", uploadDir.canRead());
        config.put("uploadDirCanWrite", uploadDir.canWrite());
        
        if (uploadDir.exists() && uploadDir.isDirectory()) {
            File[] files = uploadDir.listFiles();
            config.put("fileCount", files != null ? files.length : 0);
            
            if (files != null && files.length > 0) {
                List<Map<String, Object>> fileInfos = new ArrayList<>();
                for (File file : files) {
                    Map<String, Object> fileInfo = new HashMap<>();
                    fileInfo.put("name", file.getName());
                    fileInfo.put("size", file.length());
                    fileInfo.put("lastModified", new Date(file.lastModified()));
                    fileInfo.put("url", "/uploads/" + file.getName());
                    fileInfos.add(fileInfo);
                }
                config.put("files", fileInfos);
            }
        }
        
        return config;
    }
    
    @GetMapping("/images")
    public Map<String, Object> getImages() {
        Map<String, Object> result = new HashMap<>();
        
        File uploadDir = new File(uploadPath);
        if (uploadDir.exists() && uploadDir.isDirectory()) {
            File[] files = uploadDir.listFiles((dir, name) -> 
                name.toLowerCase().endsWith(".jpg") || 
                name.toLowerCase().endsWith(".jpeg") || 
                name.toLowerCase().endsWith(".png") || 
                name.toLowerCase().endsWith(".gif"));
            
            if (files != null && files.length > 0) {
                List<Map<String, Object>> imageInfos = new ArrayList<>();
                for (File file : files) {
                    Map<String, Object> imageInfo = new HashMap<>();
                    imageInfo.put("name", file.getName());
                    imageInfo.put("size", file.length());
                    imageInfo.put("lastModified", new Date(file.lastModified()));
                    imageInfo.put("url", "/uploads/" + file.getName());
                    imageInfos.add(imageInfo);
                }
                result.put("images", imageInfos);
                result.put("count", imageInfos.size());
            } else {
                result.put("images", Collections.emptyList());
                result.put("count", 0);
            }
        } else {
            result.put("error", "Upload directory does not exist or is not a directory");
        }
        
        return result;
    }
} 