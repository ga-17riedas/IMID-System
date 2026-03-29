package com.example.medicalsystem.dto;

import lombok.Data;

@Data
public class AnalysisStatisticsDTO {
    private Integer totalDiagnosis;
    private Double diagnosisTrend;
    private Double aiAccuracy;
    private Double aiAccuracyTrend;
} 