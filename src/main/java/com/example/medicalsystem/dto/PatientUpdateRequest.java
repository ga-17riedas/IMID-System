package com.example.medicalsystem.dto;

import lombok.Data;

@Data
public class PatientUpdateRequest {
    private String fullName;
    private String gender;
    private Integer age;
    private String phone;
    private String email;
    private String emergencyContact;
    private String emergencyPhone;
} 