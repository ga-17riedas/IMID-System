package com.example.medicalsystem.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiagnosisReportDTO {
    private Long id;
    private Long patientId;
    private String patientName;
    private Long medicalImageId;
    private String diagnosis;
    private String treatmentPlan;
    private String recommendations;
    private Boolean readStatus;
    private LocalDateTime createdAt;
    private String imageUrl;
    private String imageType;
    private Double confidenceScore;
    private String aiDiagnosis;
    private DoctorDTO doctor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoctorDTO {
        private Long id;
        private String fullName;
        private String department;
        private String professionalTitle;
    }
} 