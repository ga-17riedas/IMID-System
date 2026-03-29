package com.example.medicalsystem.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtAuthResponse {
    private String token;
    @Builder.Default
    private String tokenType = "Bearer";
    private UserDTO user;
} 