package com.example.medicalsystem.controller;

import com.example.medicalsystem.dto.AnalysisRequest;
import com.example.medicalsystem.dto.AnalysisResult;
import com.example.medicalsystem.dto.ErrorResponse;
import com.example.medicalsystem.dto.UploadResponse;
import com.example.medicalsystem.service.MedicalAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/medical")
@Slf4j
public class MedicalAnalysisController {
    
    private final MedicalAnalysisService medicalAnalysisService;
    
    public MedicalAnalysisController(MedicalAnalysisService medicalAnalysisService) {
        this.medicalAnalysisService = medicalAnalysisService;
    }
    
    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String url = medicalAnalysisService.saveImage(file);
            return ResponseEntity.ok(new UploadResponse(url));
        } catch (Exception e) {
            log.error("Failed to upload image", e);
            throw new RuntimeException("上传失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/analyze")
    public ResponseEntity<AnalysisResult> analyzeImage(@RequestBody AnalysisRequest request) {
        try {
            AnalysisResult result = medicalAnalysisService.analyzeImage(
                request.getImageUrl(),
                request.getAnalysisType()
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Analysis failed", e);
            throw new RuntimeException("分析失败: " + e.getMessage());
        }
    }
} 