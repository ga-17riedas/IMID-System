package com.example.medicalsystem.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminDTO {
    private Long id;
    private String username;
    private String email;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
} 