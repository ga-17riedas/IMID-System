package com.example.medicalsystem.model;

import lombok.Data;

@Data
public class BoxInfo {
    private double x1;
    private double y1;
    private double x2;
    private double y2;
    private double confidence;
    private String label;
}
