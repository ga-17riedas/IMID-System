package com.example.medicalsystem.dto;

import lombok.Data;

@Data
public class DoctorProfileDTO {
    private String username;
    private String email;
    private String fullName;
    private Integer age;
    private String gender;
    private String phone;
    private String professionalTitle;
    private String hospital;
    private String department;
    private String licenseNumber;
    private String specialty;
    private Integer practiceYears;
} 