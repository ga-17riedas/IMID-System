package com.example.medicalsystem.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class AdminStatusUpdateRequest {
    @NotBlank(message = "状态不能为空")
    private String status;
} 