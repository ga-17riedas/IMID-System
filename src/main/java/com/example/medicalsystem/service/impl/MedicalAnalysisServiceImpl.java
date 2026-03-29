package com.example.medicalsystem.service.impl;

import com.example.medicalsystem.dto.AnalysisResult;
import com.example.medicalsystem.model.DetectionResult;
import com.example.medicalsystem.model.DiagnosisResult;
import com.example.medicalsystem.repository.DiagnosisResultRepository;
import com.example.medicalsystem.service.MedicalAnalysisService;
import com.example.medicalsystem.service.ImageStorageService;
import com.example.medicalsystem.service.PythonScriptExecutor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@Service
@Transactional
public class MedicalAnalysisServiceImpl implements MedicalAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(MedicalAnalysisServiceImpl.class);

    @Value("${upload.path}")
    private String uploadPath;

    @Value("${python.script.path}")
    private String scriptPath;

    @Value("${model.path.brain-tumor}")
    private String brainTumorModelPath;

    @Value("${model.path.covid}")
    private String covidModelPath;

    @Autowired
    private PythonScriptExecutor pythonScriptExecutor;
    
    @Autowired
    private DiagnosisResultRepository diagnosisResultRepository;
    
    @Autowired
    private ImageStorageService imageStorageService;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String saveImage(MultipartFile file) throws IOException {
        try {
            return imageStorageService.storeImage(file);
        } catch (IOException e) {
            logger.error("保存图像失败", e);
            throw e;
        }
    }

    @Override
    public AnalysisResult analyzeImage(String imageUrl, String analysisType) {
        try {
            String modelPath = getModelPath(analysisType);
            String imagePath = getImagePath(imageUrl);
            
            // 构建Python脚本路径
            String scriptPath = new File(this.scriptPath, "analyze.py").getAbsolutePath();
            
            logger.info("Analyzing image: {} with model: {}", imagePath, modelPath);
            
            // 检查文件是否存在
            File modelFile = new File(modelPath);
            File imageFile = new File(imagePath);
            File scriptFile = new File(scriptPath);
            
            if (!modelFile.exists()) {
                throw new RuntimeException("Model file not found: " + modelPath);
            }
            if (!imageFile.exists()) {
                throw new RuntimeException("Image file not found: " + imagePath);
            }
            if (!scriptFile.exists()) {
                throw new RuntimeException("Script file not found: " + scriptPath);
            }

            // 执行Python脚本进行分析
            String scriptOutput = pythonScriptExecutor.executeScript(
                scriptPath,
                "--model", modelPath,
                "--image", imagePath,
                "--type", analysisType
            );

            // 从输出中提取JSON结果
            String jsonResult = extractJsonFromOutput(scriptOutput);

            System.out.println(321);
            System.out.println(jsonResult);

            // 解析Python返回的JSON结果
            DetectionResult pythonResult = objectMapper.readValue(
                jsonResult,
                DetectionResult.class
            );

            // 创建诊断结果记录
            DiagnosisResult diagnosisResult = new DiagnosisResult();
            diagnosisResult.setImageId(imageUrl);
            diagnosisResult.setCreatedAt(LocalDateTime.now());
            diagnosisResult.setAnalysisType(analysisType);
            diagnosisResult.setDiagnosis(pythonResult.getSuggestion());
            
            // 保存诊断结果
            diagnosisResultRepository.save(diagnosisResult);

            // 构建返回结果
            AnalysisResult analysisResult = new AnalysisResult();
            analysisResult.setDetected(pythonResult.isDetected());
            analysisResult.setSuggestion(pythonResult.getSuggestion());
            analysisResult.setImageUrl(imageUrl);
            analysisResult.setConfidence(pythonResult.getConfidence());
            analysisResult.setBoxes(pythonResult.getBoxes());
            analysisResult.setTopClass(pythonResult.getTopClass());
            analysisResult.setAnalysisTime(LocalDateTime.now());
            
            // 设置标注图像URL
            if (pythonResult.getAnnotatedImageUrl() != null && !pythonResult.getAnnotatedImageUrl().isEmpty()) {
                String annotatedUrl = pythonResult.getAnnotatedImageUrl();
                // 清理URL，确保不包含双斜杠等可能被安全机制拦截的字符
                annotatedUrl = cleanImageUrl(annotatedUrl);
                analysisResult.setAnalyzedImageUrl(annotatedUrl);
            } else {
                analysisResult.setAnalyzedImageUrl(imageUrl);
            }
            
            return analysisResult;

        } catch (Exception e) {
            logger.error("Image analysis failed", e);
            throw new RuntimeException("Image analysis failed: " + e.getMessage());
        }
    }

//    private String extractJsonFromOutput(String output) {
//        // 查找最后一个JSON对象
//        int jsonStart = output.lastIndexOf("{");
//        int jsonEnd = output.lastIndexOf("}") + 1;
//
//        if (jsonStart == -1 || jsonEnd == -1 || jsonEnd <= jsonStart) {
//            throw new RuntimeException("No valid JSON found in Python script output");
//        }
//
//        return output.substring(jsonStart, jsonEnd);
//    }

    public String extractJsonFromOutput(String output) {
        int startIndex = output.indexOf("{");
        int endIndex = output.lastIndexOf("}");

        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            return output.substring(startIndex, endIndex + 1);
        }

        return null; // or throw error
    }


    private String getModelPath(String analysisType) {
        switch (analysisType.toUpperCase()) {
            case "BRAIN_TUMOR":
                return brainTumorModelPath;
            case "COVID":
                return covidModelPath;
            default:
                throw new IllegalArgumentException("Unsupported analysis type: " + analysisType);
        }
    }

    private String getImagePath(String imageUrl) {
        return new File(uploadPath, imageUrl.replace("/uploads/", "")).getAbsolutePath();
    }

    /**
     * 清理图像URL，确保其格式正确且可访问
     */
    private String cleanImageUrl(String url) {
        if (url == null || url.isEmpty()) {
            return url;
        }
        
        // 移除连续的多个斜杠
        while (url.contains("//")) {
            url = url.replace("//", "/");
        }
        
        // 如果URL不是以"/"开头的相对路径，则添加前导斜杠
        if (!url.startsWith("/") && !url.startsWith("http")) {
            url = "/" + url;
        }
        
        logger.debug("Cleaned image URL: {}", url);
        return url;
    }

    private static class PythonAnalysisResult {
        private boolean detected;
        private double confidence;
        private String suggestion;

        public boolean isDetected() {
            return detected;
        }

        public void setDetected(boolean detected) {
            this.detected = detected;
        }

        public double getConfidence() {
            return confidence;
        }

        public void setConfidence(double confidence) {
            this.confidence = confidence;
        }

        public String getSuggestion() {
            return suggestion;
        }

        public void setSuggestion(String suggestion) {
            this.suggestion = suggestion;
        }
    }
} 