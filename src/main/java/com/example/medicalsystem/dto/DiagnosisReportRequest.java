package com.example.medicalsystem.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DiagnosisReportRequest {
    private Long patientId;  // 患者ID
    private String diagnosis;  // 诊断结果
    private String treatmentPlan;  // 治疗方案
    private String recommendations;  // 医生建议
    private MultipartFile image;  // 医学影像文件
}