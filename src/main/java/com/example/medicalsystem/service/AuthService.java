package com.example.medicalsystem.service;

import com.example.medicalsystem.dto.LoginRequest;
import com.example.medicalsystem.dto.RegisterRequest;
import com.example.medicalsystem.dto.JwtAuthResponse;

public interface AuthService {
    JwtAuthResponse login(LoginRequest loginRequest);
    void register(RegisterRequest registerRequest);
} 