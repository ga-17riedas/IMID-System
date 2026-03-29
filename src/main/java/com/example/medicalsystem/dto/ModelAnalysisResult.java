package com.example.medicalsystem.dto;

import lombok.Data;

@Data
public class ModelAnalysisResult {
    private Long modelSize;
    private String modelSizeFormatted;
    private String framework;
    private String modelType;
    private Long parameterCount;
    private String accuracy;
    private String precision;
    private String recall;
    private String f1Score;
    private String[] classes;
} 