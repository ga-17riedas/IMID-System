package com.example.medicalsystem.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Email;

@Data
public class DoctorUpdateRequest {
    @NotBlank(message = "姓名不能为空")
    private String fullName;
    private String professionalTitle;
    private String department;
    private String hospital;
    private String licenseNumber;
    private String specialty;
    
    @Email(message = "邮箱格式不正确")
    private String email;
} 