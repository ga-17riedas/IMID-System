package com.example.medicalsystem.dto;

import lombok.Data;

@Data
public class MedicalImageDTO {
    private Long id;
    private Long patientId;
    private String patientName;
    private String imageUrl;
    private String imageType;
    private String bodyPart;
    private String aiDiagnosis;
    private Double confidenceScore;
    private String doctorDiagnosis;
    private String createdAt;
} 