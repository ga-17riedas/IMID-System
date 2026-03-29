package com.example.medicalsystem.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.constraints.NotNull;

@Data
public class ReportRequest {
    private Long patientId;
    
    @NotNull(message = "诊断结果不能为空")
    private String diagnosis;
    
    private String treatmentPlan;
    private String recommendations;
    private MultipartFile image;
    private String imageUrl;
    private String aiDiagnosis;
    private Double confidenceScore;
    private String imageType;
} 