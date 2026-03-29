package com.example.medicalsystem.service;

import com.example.medicalsystem.dto.AnalysisResult;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface MedicalAnalysisService {
    /**
     * 分析医学图像
     */
    AnalysisResult analyzeImage(String imageUrl, String analysisType);

    /**
     * 保存上传的图像
     */
    String saveImage(MultipartFile file) throws IOException;
} 