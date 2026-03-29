package com.example.medicalsystem.dto;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import java.io.Serializable;

@Data
public class RegisterRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 50, message = "用户名长度必须在4到50个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "请确认密码")
    private String confirmPassword;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "请选择用户类型")
    private String role;  // ROLE_DOCTOR or ROLE_PATIENT

    // 共用字段
    @NotBlank(message = "姓名不能为空")
    @Size(min = 2, max = 50, message = "姓名长度必须在2到50个字符之间")
    private String fullName;

    // 医生特有字段
    private String professionalTitle;
    private String hospital;
    private String department;
    private String licenseNumber;
    private String specialty;

    // 患者特有字段
    @Min(value = 0, message = "年龄不能为负数")
    @Max(value = 150, message = "年龄超出合理范围")
    private Integer age;
    
    private String gender;
    
    @Size(max = 50, message = "紧急联系人姓名不能超过50个字符")
    private String emergencyContact;
    
    @Pattern(regexp = "^(|1[3-9]\\d{9})$", message = "紧急联系人电话格式不正确")
    private String emergencyPhone;
    
    private String address;
    private String medicalHistory;
    private String allergies;
} 