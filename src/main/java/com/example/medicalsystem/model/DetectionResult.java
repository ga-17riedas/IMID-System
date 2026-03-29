package com.example.medicalsystem.model;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;


import java.util.List;

@Data
public class DetectionResult {
    private boolean detected;
    @JsonProperty("top_class")
    private String topClass;
    @JsonProperty("top_confidence")
    private double confidence;
    private String suggestion;
    private List<BoxInfo> boxes;
    
    @JsonProperty("annotated_image_url")
    private String annotatedImageUrl;
}
