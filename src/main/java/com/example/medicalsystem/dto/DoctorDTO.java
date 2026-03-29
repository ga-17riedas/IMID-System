package com.example.medicalsystem.dto;

import lombok.Data;

@Data
public class DoctorDTO {
    private Long id;
    private String username;
    private String fullName;
    private String professionalTitle;
    private String department;
    private String hospital;
    private String licenseNumber;
    private String specialty;
    private String email;
    private String phone;
} 