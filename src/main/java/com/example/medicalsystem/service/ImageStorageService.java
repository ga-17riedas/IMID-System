package com.example.medicalsystem.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface ImageStorageService {
    void init();
    String storeImage(MultipartFile file) throws IOException;
    void deleteImage(String imageUrl);
} 