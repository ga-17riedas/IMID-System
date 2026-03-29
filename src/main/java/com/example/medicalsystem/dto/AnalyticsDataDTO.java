package com.example.medicalsystem.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class AnalyticsDataDTO {
    private Integer totalDiagnoses;
    private Double diagnosesTrend;
    private String aiAccuracy;
    private Double accuracyTrend;
    
    private List<String> timeLabels;
    private List<Integer> totalDiagnosesData;
    private List<Integer> aiDiagnosesData;
    
    private Map<String, Integer> accuracyDistribution;
    private Map<String, Integer> diseaseDistribution;
} 