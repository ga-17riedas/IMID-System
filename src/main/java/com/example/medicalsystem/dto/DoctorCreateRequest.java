package com.example.medicalsystem.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Email;

@Data
public class DoctorCreateRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @NotBlank(message = "姓名不能为空")
    private String fullName;
    
    private String professionalTitle;
    private String department;
    private String hospital;
    private String licenseNumber;
    private String specialty;
} 