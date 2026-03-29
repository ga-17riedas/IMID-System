package com.example.medicalsystem.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
} 