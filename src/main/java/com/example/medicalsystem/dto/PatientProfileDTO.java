package com.example.medicalsystem.dto;

import lombok.Data;

@Data
public class PatientProfileDTO {
    private String username;
    private String fullName;
    private Integer age;
    private String gender;
    private String phone;
    private String email;
    private String emergencyContact;
    private String emergencyPhone;
} 