package com.example.medicalsystem.dto;

import com.example.medicalsystem.model.BoxInfo;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AnalysisResult {
    private boolean detected;
    private double confidence;
    private String topClass;
    private String suggestion;
    private String imageUrl;
    private String analyzedImageUrl;
    private List<BoxInfo> boxes;
    private LocalDateTime analysisTime;
}