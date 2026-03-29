package com.example.medicalsystem.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DiagnosisRecordDTO {
    private Long id;
    private LocalDateTime diagnosisTime;
    private String doctorName;
    private String department;
    private String diagnosis;
} 