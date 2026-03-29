package com.example.medicalsystem.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HealthRecordDTO {
    private Long id;
    private String recordType = "REGULAR";
    private Double value1;
    private Double value2;
    private String unit;
    private LocalDateTime measureTime;
    private LocalDateTime recordTime;
    private Double systolicPressure;
    private Double diastolicPressure;
    private Double heartRate;
    private Double bloodSugar;
    private Double temperature;
    private Double weight;
    private String symptoms;
    private String medications;
    private String notes;
} 