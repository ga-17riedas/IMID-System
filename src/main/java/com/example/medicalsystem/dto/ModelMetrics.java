package com.example.medicalsystem.dto;

import lombok.Data;

@Data
public class ModelMetrics {
    private Double accuracy;
    private Double precision;
    private Double recall;
    private Double f1Score;
    private Long totalParams;
    private Long modelSize;
    private String[] classes;
    private Double[] classAccuracies;
} 