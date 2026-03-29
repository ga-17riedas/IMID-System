package com.example.medicalsystem.dto;

import lombok.Data;

@Data
public class PatientHomeDTO {
    private String fullName;
    private Integer age;
    private String gender;
    private String welcomeMessage;
    private Integer pendingReports;
    private Integer totalReports;
} 