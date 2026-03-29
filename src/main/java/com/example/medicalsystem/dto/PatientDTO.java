package com.example.medicalsystem.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PatientDTO {
    private Long id;
    private String username;
    private String fullName;
    private String gender;
    private Integer age;
    private String phone;
    private String address;
    private String emergencyContact;
    private String emergencyPhone;
    private LocalDate birthDate;
    private String email;
    
    // 添加统计字段
    private Integer imageCount;
    private Integer reportCount;
    private String lastVisit;
} 