package com.example.medicalsystem.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MedicalImageDetailDTO {
    private Long id;
    private String imageUrl;
    private String imageType;
    private Double confidenceScore;
    private String aiDiagnosis;
    private String doctorDiagnosis;
    private String doctorName;
    private String patientName;
    private LocalDateTime createdAt;
    private String treatmentPlan;
    private String recommendations;
} 